package com.fuse.ui.builder;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import com.fuse.cms.Model;
import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;

public class Builder {
  private ModelCollection layoutCollection;
  private List<NodeBuilder> activeNodeBuilders;
  private Instantiator instantiator;

  public Builder(){
    layoutCollection = new ModelCollection();
    instantiator = new Instantiator();
  }

  public ModelCollection getLayoutCollection(){
    return layoutCollection;
  }

  public void setLayoutCollection(ModelCollection newCol){
    layoutCollection = newCol;
  }

  public Node createNode(String nodeId){
    return createNode(nodeId, false /* not active */);
  }

  public Node createNode(String nodeId, boolean activeBuilder){
    NodeBuilder nodeBuilder = new NodeBuilder(layoutCollection, nodeId);
    nodeBuilder.setInstantiator(instantiator);
    nodeBuilder.setActive(activeBuilder);

    // if active store NodeBuilder internally so it doesn't expire
    if(activeBuilder){
      if(activeNodeBuilders == null)
        activeNodeBuilders = new ArrayList<>();
      activeNodeBuilders.add(nodeBuilder);
    }

    return nodeBuilder.createNode();
  }

  public void setTypeInstantiator(String typeValue, Function<Model, Node> func){
    instantiator.setTypeInstantiator(typeValue, func);
  }
}
