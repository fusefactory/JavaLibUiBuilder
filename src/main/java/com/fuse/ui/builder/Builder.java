package com.fuse.ui.builder;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.BiConsumer;
import com.fuse.cms.Model;
import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;

public class Builder {
  private ModelCollection layoutCollection;
  private List<NodeBuilder> activeNodeBuilders;
  private Instantiator instantiator;
  private boolean bUseImplicitBuilder = false;

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
    NodeBuilder nodeBuilder = bUseImplicitBuilder ?
      new NodeBuilderImplicit(layoutCollection, nodeId, instantiator, activeBuilder) :
      new NodeBuilder(layoutCollection, nodeId, instantiator, activeBuilder);

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
  
  public void setTypeExtender(String typeValue, BiConsumer<Node, Model> func) {
	  this.instantiator.setTypeExtender(typeValue, func);
  }

  public void setUseImplicitBuilder(boolean enable){
    bUseImplicitBuilder = enable;
  }

  public boolean getUseImplicitBuilder(){
    return bUseImplicitBuilder;
  }
}
