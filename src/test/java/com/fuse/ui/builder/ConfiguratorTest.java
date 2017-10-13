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
}
