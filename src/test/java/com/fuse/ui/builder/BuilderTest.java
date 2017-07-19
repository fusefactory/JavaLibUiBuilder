package com.fuse.ui.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.Ignore;

import processing.core.PGraphics;
import com.fuse.cms.Model;
import com.fuse.ui.Node;
import com.fuse.ui.TextNode;
import com.fuse.ui.ImageNode;
import com.fuse.ui.LineNode;

public class BuilderTest {

  public BuilderTest(){
    Node.setPGraphics(new PGraphics());
  }

  @Test public void createNode(){
    Builder builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.createNode.1.json");

    Node n = builder.createNode("NodeBuilderTest.page");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);
  }

  @Ignore @Test public void createNode_active(){
    // creat builder and load initial data
    Builder builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.createNode.1.json");

    // create a node with active flag=true
    Node n = builder.createNode("NodeBuilderTest.page", true /* active! changes to the layout data are actively applied to the created node */);
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);

    // update layout data, verify new layout is applied to earlier created Node
    assertTrue(builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.createNode.2.addOne.json"));
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().get(3).getClass(), LineNode.class);
    assertEquals(n.getChildNodes().size(), 4);

    // update layout data, verify new layout is applied to earlier created Node
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.createNode.3.removeOne.json");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), LineNode.class);
    assertEquals(n.getChildNodes().size(), 3);
  }

  @Test public void addTypeInstantiator(){
    Builder builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.createNode.1.json");

    Node n = builder.createNode("NodeBuilderTest.page");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(((TextNode)n.getChildNodes().get(0)).getText(), "");
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(((TextNode)n.getChildNodes().get(1)).getText(), "");
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);

    builder.setTypeInstantiator("TextNode", (Model layoutModel) -> {
      TextNode tn = new TextNode();
      tn.setText("customly instantiated!");
      return tn;
    });

    n = builder.createNode("NodeBuilderTest.page");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(((TextNode)n.getChildNodes().get(0)).getText(), "customly instantiated!");
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(((TextNode)n.getChildNodes().get(1)).getText(), "customly instantiated!");
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);

    builder.setTypeInstantiator("TextNode", (Model layoutModel) -> {
      TextNode tn = new TextNode();
      tn.setText("simple");
      return tn;
    });

    n = builder.createNode("NodeBuilderTest.page");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(((TextNode)n.getChildNodes().get(0)).getText(), "simple");
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(((TextNode)n.getChildNodes().get(1)).getText(), "simple");
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);
  }
}
