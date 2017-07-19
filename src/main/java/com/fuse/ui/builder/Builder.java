package com.fuse.ui.builder;

import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;

public class Builder {
  private ModelCollection layoutCollection;

  public Builder(){
    layoutCollection = new ModelCollection();
  }

  public ModelCollection getLayoutCollection(){
    return layoutCollection;
  }

  public Node createNode(String nodeId){
    NodeBuilder nodeBuilder = new NodeBuilder(layoutCollection, nodeId);
    return nodeBuilder.createNode();
  }
}
