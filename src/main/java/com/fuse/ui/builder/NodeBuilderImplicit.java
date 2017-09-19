package com.fuse.ui.builder;

import com.fuse.cms.Model;
import com.fuse.cms.ModelCollection;

public class NodeBuilderImplicit extends NodeBuilder {

  public NodeBuilderImplicit(ModelCollection col, String nodeId, Instantiator instantiator){
    this(col, nodeId, instantiator, false);
  }

  public NodeBuilderImplicit(ModelCollection col, String nodeId, Instantiator instantiator, boolean active){
    super(col, nodeId, instantiator, active);
  }

  @Override
  protected boolean isDirectChild(Model potentialChild, Model potentialParent){
    String prefix = potentialParent.getId()+".";

    if(potentialChild.getId().length() <= prefix.length() || !potentialChild.getId().startsWith(prefix))
      return false;

    String rest = potentialChild.getId().substring(prefix.length());
    boolean result = !rest.contains(".");
    // System.out.println(potentialChild.getId() +" of? "+potentialParent.getId()+" ?? " +Boolean.toString(result));
    return result;
  }

  @Override
  protected NodeBuilder getChildBuilder(String childId){
	  return new NodeBuilderImplicit(this.collection, childId, this.instantiator, this.bActive);
  }
}
