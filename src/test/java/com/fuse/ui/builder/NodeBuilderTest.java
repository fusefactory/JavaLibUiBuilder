package com.fuse.ui.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

import com.fuse.cms.ModelCollection;

public class NodeBuilderTest {
  private ModelCollection col;

  public NodeBuilderTest(){
    col = new ModelCollection();
    col.loadJsonFromFile("testdata/NodeBuilderTest.json");
  }
  @Test public void builder(){
    NodeBuilder nb = new NodeBuilder(col, "");
    assertFalse(nb.getNode() == null);
  }
}
