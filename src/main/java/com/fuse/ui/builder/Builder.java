package com.fuse.ui.builder;

import java.util.List;
import java.util.ArrayList;
import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;

public class Builder {
  private ModelCollection layoutCollection;
  private List<NodeBuilder> activeNodeBuilders;

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

    // if active store NodeBuilder internally so it doesn't expire
    if(activeBuilder){
      if(activeNodeBuilders == null)
        activeNodeBuilders = new ArrayList<>();
      activeNodeBuilders.add(nodeBuilder);
    }

    return nodeBuilder.createNode();
  }
}
