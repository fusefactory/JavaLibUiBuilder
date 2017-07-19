package com.fuse.ui.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import processing.core.PGraphics;
import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;
import com.fuse.ui.TextNode;
import com.fuse.ui.ImageNode;
import com.fuse.ui.LineNode;

public class BuilderTest {
  private ModelCollection col;

  public BuilderTest(){
    Node.setPGraphics(new PGraphics());
  }

  @Test public void createNode(){
    Builder builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.1.json");

    Node n = builder.createNode("NodeBuilderTest.page");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);
  }

  @Test public void createNode_active(){
    // creat builder and load initial data
    Builder builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.1.json");

    // create a node with active flag=true
    Node n = builder.createNode("NodeBuilderTest.page", true /* active! changes to the layout data are actively applied to the created node */);
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);

    // update layout data, verify new layout is applied to earlier created Node
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.2.json");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), LineNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().get(3).getClass(), LineNode.class);
    assertEquals(n.getChildNodes().get(4).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().size(), 5);
  }
}
