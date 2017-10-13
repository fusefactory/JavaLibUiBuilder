# JavaLibUiBuilder

[![Build Status](https://travis-ci.org/fusefactory/JavaLibUiBuilder.svg?branch=master)](https://travis-ci.org/fusefactory/JavaLibUi)

_Java package that extends the JavaLibUi package and provides a -heavily opiniated- framework for fast and flexible UI-layout development_



## Installation
Use as maven/gradle/sbt/leiningen dependency with [JitPack](https://github.com/jitpack/maven-modular)
* https://jitpack.io/#fusefactory/JavaLibUiBuilder

For more info on jitpack see;
* https://github.com/jitpack/maven-simple
* https://jitpack.io/docs/?#building-with-jitpack



## Documentation
* javadocs: https://fusefactory.github.io/JavaLibUiBuilder/site/apidocs/index.html
* to run unit tests: ```mvn test```



## Classes
This package consists of four small classes (see the [javadocs](https://fusefactory.github.io/JavaLibUiBuilder/site/apidocs/index.html) for more information), but there is only one you really need to know about to use it;
* com.fuse.ui.builder.Builder



## Dependencies
This repo uses [maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) for dependency management (see the pom.xml file).

Compile Dependencies are:
* [fusefactory](http://fuseinteractive.it/)'s [JavaLibUi package](https://github.com/fusefactory/JavaLibUi) [(jitpack)](https://jitpack.io/#fusefactory/JavaLibUi) - obviously
* [fusefactory](http://fuseinteractive.it/)'s [JavaLibCms package](https://github.com/fusefactory/JavaLibCms) [(jitpack)](https://jitpack.io/#fusefactory/JavaLibCms) - for data management



## USAGE: Generating UI structures from json data using the default builder

The Builder class is designed to use json data (or any other data format that can be read by the ModelCollection class of the JavaLibCms package) to generated UI hierarchy.


Assuming you have a file called ```data/ui.json``` with the following content:
```json
  [
    {"id": "InfoPage.page"},
    {"id": "InfoPage.page.title", "type": "TextNode"},
    {"id": "InfoPage.page.subtitle", "type": "TextNode"},
    {"id": "InfoPage.page.image", "type": "ImageNode"},
    {"id": "InfoPage.page.image.overlay", "type": "TextNode"},

    {"id": "InfoPage.menu", "type": "RectNode"},
    {"id": "InfoPage.menu.btnOk", "type": "RectNode", "name":"btnOk"},
    {"id": "InfoPage.menu.btnOk.text", "type": "TextNode"},
    {"id": "InfoPage.menu.btnCancel", "type": "RectNode", "name":"btnCancel"},
    {"id": "InfoPage.menu.btnCancel.text", "type": "TextNode"},
  ]
```

You can use the builder class to load and use that data for generating UI structures:

```java
  public void setup(){
    // create our builder instance
    this.builder = new Builder();
    // load our json data into the builder's ModelCollection
    this.builder.getLayoutCollection().loadJsonFromFile("data/ui.json");

    // this configuration should become default behaviour, but is necessary for now
    // see USAGE: NON-implicit builder section below
    this.builder.setUseImplicitBuilder(true);
  }

  public void openInfoPage(){
    // let's start building!
    Node pageNode = builder.createNode("InfoPage.page");

    // The pageNode instance will be a Node instance, which has the following child-hierarchy:
    // - TextNode
    // - TextNode
    // - ImageNode
    //   - TextNode (child of the last ImageNode)

    Node menuNode = builder.createNode("InfoPage.menu");

    // The menuNode instance will be a RectNode instance, which has the following child-hierarchy:
    // - RectNode (with name "btnOk")
    //   - TextNode
    // - RectNode (with name "btnCancel")
    //   - TextNode

    // of course you will still need to add them to your scene:
    mySceneNode.addChild(pageNode);
    mySceneNode.addChild(menuNode);

    // don't forget to populate your nodes with data and to add your event-handlers
  }
```

## USAGE: Supporting custom Node types
The default builder in this package supports the Node classes in the JavaLibUi package. To support your own custom Node classes you'll have to register new instantiators.

```java
  class Builder extends com.fuse.ui.builder.Builder {

    public Builder(){
      this.getLayoutCollection().loadJsonFromFile("data/ui.json");
      this.setUseImplicitBuilder(true); // this should become default

      // register custom instantiator lambda for Node
      this.setTypeInstantiator("Node", (Model model) -> {
        return new Node();
      });

      // overwrite the default instantiator for the ImageNode  type
      this.setTypeInstantiator("ImageNode", (Model model) -> {
        return new ImageNode();
      });

      // create an instantiator for a custom type.
      this.setTypeInstantiator("MyCustomNode", (Model model) -> {
        return new MyCustomNode(); // obviously, MyCustomNode has to extend the Node;
      });

      // Note that it is technically perfectly possible to use type values that do not
      // match with the class name, but for readability I strongly discouraged
      // So JUST DON'T DO THIS, for your own sake:
      this.setTypeInstantiator("Video", (Model model) -> {
        return new VideoViewerNode();
      });
    }
  }
```

As the last instantiator in the above example demonstrates; it might be tempting to use shorter names sometimes, but since the data in the json is tightly coupled to the code, using this builder technique is already a debatable practice; you should try to do anything you can to make it clear how the json data relates to your code.


## Usage suggestion: a configurator class
```java

  class Configurator {
    public void cfg(Node n, Model m){
      // TODO
    }

    public void cfg(ImageNode n, Model m){
      this.cfg((Node)n); // first apply all Node configurations

      m.transform((ModelBase m) -> {
        n.setImage(customImageLoaderMethod(m.get("image")));
      });
    }
  }

  class Builder extends com.fuse.ui.builder.Builder {

    private Configurator configurator;

    public Builder(){
      this.getLayoutCollection().loadJsonFromFile("data/ui.json");
      this.setUseImplicitBuilder(true); // this should become default

      this.configurator = new Configurator();

      // register custom instantiator lambda for
      this.setTypeInstantiator("Node", (Model model) -> {
        // create our Node instance
        Node n = new Node();
        // make non-interactive by default
        n.setInteractive(false);
        // configure the node
        this.configurator.cfg(n, model.getId());
        // return the instantiated node
        return n;
      });

      // overwrite the default instantiator for the ImageNode  type
      this.setTypeInstantiator("ImageNode", (Model model) -> {
        // create our Node instance
        Node n = new ImageNode();
        // make non-interactive by default
        n.setInteractive(false);
        // configure the node
        this.configurator.cfg(n, model.getId());
        // return the instantiated node
        return n;
      });

      // create an instantiator for a custom type. For readability It is strongly recommended
      // to keep the 'type' value and the instantiated class name the same
      this.setTypeInstantiator("MyCustomNode", (Model model) -> {
        MyCustomNode n = new MyCustomNode(); // obviously, MyCustomNode has to extend the Node class
        n.setInteractive(false);

        this.configurator.cfg(n, model.getId());

      // etc.
      // etc.
    }
  }
```

## About implicit builders and non-implicit builders

#### Implicit builder
In the below data, according to the _implicit builder_, button1, button2 and button3 are considered children of HomePage.menu because they start with the
full HomePage.menu ID, followed by a dot (.). Therefore button4 is not a child of HomePage.menu, but a child of ```HomePage```.

HomePage is not explicitly defined but trying to build it will just default to Node and find the button4 child to add to it.

```json
  [
    {"id":"HomePage.menu"},
    {"id":"HomePage.menu.button1"},
    {"id":"HomePage.menu.button2"},
    {"id":"HomePage.menu.button3"},
    {"id":"HomePage.button4"},
  ]
```

#### Non-implicit (aka explicit) builder (not recommended but default for now)
In the below data, according to the _NON-implicit builder_, only button1, button2 and button4 are considered children of the ```HomePage.menu``` node configuration (for reasons that should be obvious).

```json
  [
    {"id":"HomePage.menu"},
    {"id":"HomePage.menu.button1", "parent":"HomePage.menu"},
    {"id":"HomePage.menu.button2", "parent":"HomePage.menu"},
    {"id":"HomePage.menu.button3"},
    {"id":"HomePage.button4", "parent":"HomePage.menu"},
  ]
```

Note that though the explicit style helps avoiding unintentional relationships, it also causes a LOT more redundant data that clutters the json. Therefore the implicit-style is recommended, which has the added advantage of enforcing clean and consistent naming conventions.
