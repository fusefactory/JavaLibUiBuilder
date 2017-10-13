# JavaLibUi

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

### Dependencies
This repo uses [maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) for dependency management (see the pom.xml file).

Compile Dependencies are:
* [fusefactory](http://fuseinteractive.it/)'s [JavaLibUi package](https://github.com/fusefactory/JavaLibUi) [(jitpack)](https://jitpack.io/#fusefactory/JavaLibUi) - obviously
* [fusefactory](http://fuseinteractive.it/)'s [JavaLibCms package](https://github.com/fusefactory/JavaLibCms) [(jitpack)](https://jitpack.io/#fusefactory/JavaLibCms) - for data management

### USAGE: Generating UI structues from json data using the default builder

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
  }
```

# USAGE: NON-implicit builder
_TODO_
