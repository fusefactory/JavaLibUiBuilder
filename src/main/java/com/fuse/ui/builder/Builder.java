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
    return createNode(nodeId, false /* not active */);
  }

  public Node createNode(String nodeId, boolean activeBuilder){
    NodeBuilder nodeBuilder = new NodeBuilder(layoutCollection, nodeId);
    nodeBuilder.setActive(activeBuilder);
    return nodeBuilder.createNode();
  }
}
