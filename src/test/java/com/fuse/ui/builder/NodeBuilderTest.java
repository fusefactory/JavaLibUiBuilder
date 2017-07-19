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

public class NodeBuilderTest {
  private ModelCollection col;

  public NodeBuilderTest(){
    Node.setPGraphics(new PGraphics());

    col = new ModelCollection();
    col.loadJsonFromFile("testdata/NodeBuilderTest.json");
  }

  @Test public void createNode(){
    NodeBuilder nb = new NodeBuilder(col, "NodeBuilderTest.page");
    Node n = nb.createNode();
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);
  }
}
