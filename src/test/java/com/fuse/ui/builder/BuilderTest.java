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

public class BuilderTest {
  private ModelCollection col;

  public BuilderTest(){
    Node.setPGraphics(new PGraphics());
  }

  @Test public void builder(){
    Builder builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile("testdata/BuilderTest.1.json");

    Node n = builder.createNode("NodeBuilderTest.page");
    assertEquals(n.getChildNodes().get(0).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(1).getClass(), TextNode.class);
    assertEquals(n.getChildNodes().get(2).getClass(), ImageNode.class);
    assertEquals(n.getChildNodes().size(), 3);
  }
}
