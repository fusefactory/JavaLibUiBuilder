package com.fuse.ui.builder;

import com.fuse.cms.Model;
import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;

public class NodeBuilder {
  private Instantiator instantiator;
  private ModelCollection collection;
  private String nodeId;

  public NodeBuilder(ModelCollection col, String nodeId){
    collection = col;
    this.nodeId = nodeId;
    this.instantiator = new Instantiator();
  }

  public Node createNode(){
    Model m = collection.findById(nodeId, true/* create-if-doesnt-exist */);
    Node n = instantiator.createNode(m);

    // create content of this node; loop over each child record in the collection
    // if any is a _direct_ child of this node, build it, including it's sub-structure
    // (recursive!) and add it to our node.
    collection.each((Model mod) -> {
      // child of us?
      if(this.isDirectChild(mod, m)){
        // create builder
        NodeBuilder childBuilder = new NodeBuilder(this.collection, mod.getId());
        // let builder generate node
        Node childNode = childBuilder.createNode();
        // add built node as child to our node
        n.addChild(childNode);
      }
    });

    return n;
  }

  private boolean isDirectChild(Model potentialChild, Model potentialParent){
    return potentialChild.get("parent", "").equals(potentialParent.getId());
  }
}
