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
  private Instantiator instantiator = new Instantiator();
  private List<NodeBuilder> activeNodeBuilders = null;

  // configurables
  private boolean bUseImplicitBuilder = true;

  // lifecycle methods

  public Builder(){
    this.setLayoutCollection(new ModelCollection());
  }

  public Builder(ModelCollection layoutCollection){
    this.setLayoutCollection(layoutCollection);
  }

  public Builder(ModelCollection layoutCollection, Configurator configurator){
    this.setLayoutCollection(layoutCollection);
    this.setConfigurator(configurator);
  }

  // operation methods

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

  // config methods

  public void setTypeInstantiator(String typeValue, Function<Model, Node> func){
    instantiator.setTypeInstantiator(typeValue, func);
  }

  public void setTypeExtender(String typeValue, BiConsumer<Node, Model> func) {
	  this.instantiator.setTypeExtender(typeValue, func);
  }

  public void setLayoutCollection(ModelCollection newCol){
    this.layoutCollection = newCol;
    this.instantiator.getConfigurator().setDataCollection(newCol);
  }

  public ModelCollection getLayoutCollection(){
    return layoutCollection;
  }

  public void setUseImplicitBuilder(boolean enable){
    bUseImplicitBuilder = enable;
  }

  public boolean getUseImplicitBuilder(){
    return bUseImplicitBuilder;
  }

  public void setUseActiveTransformations(boolean active){
    instantiator.getConfigurator().setUseActiveTransformations(active);
  }

  public void setDefaultNodesToNotInteractive(boolean active){
    instantiator.getConfigurator().setDefaultNodesToNotInteractive(active);
  }

  public void setConfigurator(Configurator configurator){
    this.instantiator.setConfigurator(configurator);
  }
}
