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
This package consists of five small classes (see the [javadocs](https://fusefactory.github.io/JavaLibUiBuilder/site/apidocs/index.html) for more information), but there are only two you really need to know about to use it;
* com.fuse.ui.builder.Builder
* com.fuse.ui.builder.Configurator


## Dependencies
This repo uses [maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) for dependency management (see the pom.xml file).

Compile Dependencies are:
* [fusefactory](http://fuseinteractive.it/)'s [JavaLibUi package](https://github.com/fusefactory/JavaLibUi) [(jitpack)](https://jitpack.io/#fusefactory/JavaLibUi) - obviously
* [fusefactory](http://fuseinteractive.it/)'s [JavaLibCms package](https://github.com/fusefactory/JavaLibCms) [(jitpack)](https://jitpack.io/#fusefactory/JavaLibCms) - for data management



## USAGE: Generating UI json data using the default builder

The Builder class is designed to use json data (or any other data format that can be read by the ModelCollection class of the JavaLibCms package) to generate a UI hierarchy of Node instances (see [JavaLibUi package](https://github.com/fusefactory/JavaLibUi) package).

The default builder by default also uses the default ```Configurator``` class that takes any supported attributes (like position, size, color, etc.) in the JSON data and uses it to "configure" the created Node instances.


Assuming you have a file called ```data/ui.json``` with the following content (the menu only has structure data, the page also has layout and some content data):
```json
  [
    {"id": "InfoPage.menu", "type": "RectNode"},
    {"id": "InfoPage.menu.btnOk", "type": "RectNode", "name":"btnOk"},
    {"id": "InfoPage.menu.btnOk.text", "type": "TextNode"},
    {"id": "InfoPage.menu.btnCancel", "type": "RectNode", "name":"btnCancel"},
    {"id": "InfoPage.menu.btnCancel.text", "type": "TextNode"},

    {"id": "InfoPage.page","pos":"100,100","size":"800,600"},
    {"id": "InfoPage.page.title", "type": "TextNode", "pos":"10,10", "size":"790,30", "text":"TITLE HERE"},
    {"id": "InfoPage.page.subtitle", "type": "TextNode", "pos":"10,50", "size":"790,25", "text":"SUBTITLE HERE"},
    {"id": "InfoPage.page.image", "type": "ImageNode", "pos":"10,100", "size":"780,490", "image": "data/placeholder/loading.png"},
    {"id": "InfoPage.page.image.overlay", "type": "TextNode", "size":"800,600"},
  ]
```

You can use the builder class to load and use that data for generating UI structures:

```java
  public void setup(){
    // create our builder instance
    this.builder = new Builder();
    // load our json data into the builder's ModelCollection
    this.builder.getLayoutCollection().loadJsonFromFile("data/ui.json");
  }

  public void openInfoPage(){
    // let's start building!
    Node pageNode = builder.createNode("InfoPage.page");

    // The pageNode instance will be a Node instance, which has the following child-hierarchy:
    // - TextNode (at relative position 10,10 with size: 790,30 and pre-populated with the text "TITLE HERE")
    // - TextNode (etc.)
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


## Usage: Configurator and custom Builder class for CSS-like dynamic configurations

Create your own Configurator class (this package includes a 'example' Configurator class which supports configuration of basic Node properties), which you could extend to support your own custom Node classes.

Note that of course you are also free to create your own Configurator class, it's not necessary at all to inherit from the Configurator class in this package.

Note also that the configurator class can very well be used for non-Node type objects as well...

```java
  class Configurator extends com.fuse.ui.builder.Configurator {

    private PApplet papplet;

    public Configurator(PApplet papplet){
      this.papplet = papplet;
    }

    public void cfg(ImageNode n, String configId){
      // first apply all the default configurator's configruations
      super.cfg(n, configId);

      // now for the more intersting stuff...
      this.apply(configId, (ModelBase m) -> {
        // you'd probably want to make a smarter and safer image loader...
        m.with("image", (String val) -> n.setImage(this.papplet.loadImage(val));
      });
    }

    // configuration method for our custom node
    public void cfg(MyCustomNode n, String configId){
      // first apply all the default Configurator's Node-specific configurations
      super.cfg((Node)n, configId);

      // now our MyCustomNode-specific configs
      this.apply(configId, (ModelBase m) -> {
        // you'd probably want to make a smarter and safer image loader...
        m.with("foo", (String val) -> n.setFoo(val);
      });
    }
  }

  class Builder extends com.fuse.ui.builder.Builder {

    private Configurator configurator;

    public Builder(){
      this.getLayoutCollection().loadJsonFromFile("data/ui.json");

      // register custom instantiator lambda for
      this.setTypeInstantiator("Node", (Model model) -> {
        // create our Node instance
        Node n = new Node();
        // configure the node
        if(this.configurator != null)
          this.configurator.cfg(n, model.getId());
        // return the instantiated node
        return n;
      });

      // overwrite the default instantiator for the ImageNode  type
      this.setTypeInstantiator("ImageNode", (Model model) -> {
        // create our Node instance
        ImageNode n = new ImageNode();
        // configure the node
        if(this.configurator != null)
          this.configurator.cfg(n, model.getId());
        // return the instantiated node
        return n;
      });

      // create an instantiator for a custom type. For readability It is strongly recommended
      // to keep the 'type' value and the instantiated class name the same
      this.setTypeInstantiator("MyCustomNode", (Model model) -> {
        // instantiate
        MyCustomNode n = new MyCustomNode(); // obviously, MyCustomNode has to extend
        // configure
        if(this.configurator != null)
          this.configurator.cfg(n, model.getId());
        // return
        return n;
      });

      // etc.
      // etc.
    }

    public void setConfigurator(Configurator configurator){
      this.configurator = configurator;
    }
  }
```

The above Builder/Configurator example will allow you to have the following data which not only provides a UI-hierarchy structure, but also configures basic properties of all nodes AND allows you to dynamically update those properties at runtime when you reload the json into the data collection.

```json
  [
    {"id":"SomeScreen.menu","pos":"800,600"},
    {"id":"SomeScreen.menu.msg", "type":"TextNode", "pos":"100,30", "size":"600,400", "name":"msg"},
    {"id":"SomeScreen.menu.buttonOk", "type":"RectNode", "pos":"100,450", "size":"200,50", "color":"0,255,0", "interactive":true, "name":"buttonOk"},
    {"id":"SomeScreen.menu.buttonOk.text", "type":"TextNode", "pos":"10,10", "size":"80,30", "text":"Ok"},
    {"id":"SomeScreen.menu.buttonCancel", "type":"RectNode", "pos":"320,450", "size":"200,50", "color":"255,0,0", "interactive":true, "name":"buttonCancel"},
    {"id":"SomeScreen.menu.buttonCancel.text", "type":"TextNode", "pos":"10,10", "size":"80,30", "text":"Abort"},
  ]
```

Instantiate the above structure + preconfigured nodes using: ```builder.createNode("SomeScreen.menu"); ```, then populate it with data and add event handlers and you are good to go:

```java
  class Menu {
    void create(String message){
      Node menuNode = builder.createNode("SomeScreen.menu");
      menuNode.withChild("msg", (Node n) -> ((TextNode)n).setText(message));
      menuNode.withChild("buttonOk", (Node n) -> n.touchClickEvent.whenTriggered(() -> this.submit()));
      menuNode.withChild("buttonCancel", (Node n) -> n.touchClickEvent.whenTriggered(() -> this.cancel()));
      return menuNode;
    }

    void submit(){
      // trigger notification, perform action, whatever
    }

    void cancel(){
      // trigger notification, perform action, whatever
    }
  }
```
