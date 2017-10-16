package com.fuse.ui.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Ignore;

import processing.core.PVector;
import com.fuse.cms.Model;
import com.fuse.ui.*;

public class ConfiguratorTest {

  @Test public void Node(){
    Configurator c = new Configurator();
    c.getDataCollection().loadJsonFromFile("testdata/ConfiguratorTest.json");

    Node n = new Node();
    assertEquals(n.getPosition(), new PVector(0.0f, 0.0f, 0.0f));
    c.cfg(n, "Node.1");
    assertEquals(n.getPosition(), new PVector(10.0f, 20.0f, 0.0f));
  }

  @Test public void TextNode(){
    Configurator c = new Configurator();
    c.getDataCollection().loadJsonFromFile("testdata/ConfiguratorTest.json");

    TextNode n = new TextNode();
    assertEquals(n.getText(), "");
    c.cfg(n, "TextNode.1");
    assertEquals(n.getText(), "foo_bar");
  }

  @Test public void Node_percentageSize(){
    Configurator c = new Configurator();
    c.getDataCollection().loadJsonFromFile("testdata/ConfiguratorTest.json");

    Node parent = new Node();
    parent.setSize(800,600);
    Node n = new Node();
    parent.addChild(n);

    Model m = new Model();
    m.set("width", "80%");
    m.set("height", "50%");
    c.cfg(n, m);
    assertEquals(n.getSize(), new PVector(640.0f, 300.0f, 0.0f));
    parent.setSize(1920,1080);
    assertEquals(n.getSize(), new PVector(1536.0f, 540.0f, 0.0f));

    Node newParent = new Node();
    newParent.setSize(1000,700);
    newParent.addChild(n);
    assertEquals(n.getSize(), new PVector(800.0f, 350.0f, 0.0f));
  }
}
