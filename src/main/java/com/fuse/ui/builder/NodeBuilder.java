package com.fuse.ui.builder;

import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;

public class NodeBuilder {
  private ModelCollection collection;
  private String nodeId;

  public NodeBuilder(ModelCollection col, String nodeId){
    collection = col;
    this.nodeId = nodeId;
  }

  public Node getNode(){
    return null;
  }
}
