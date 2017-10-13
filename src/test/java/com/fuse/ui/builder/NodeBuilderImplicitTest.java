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

public class NodeBuilderImplicitTest {
  private ModelCollection col;

  public NodeBuilderImplicitTest(){
    Node.setPGraphics(new PGraphics());

    col = new ModelCollection();
    col.loadJsonFromFile("testdata/NodeBuilderImplicitTest.json");
  }

  @Test public void createNode(){
    NodeBuilderImplicit nb = new NodeBuilderImplicit(col, "NodeBuilderImplicitTest.page", null);
    Node n = nb.createNode();
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);
    assertEquals(n.getChildNodes().get(2).getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getChildNodes().size(), 1);
  }
}
