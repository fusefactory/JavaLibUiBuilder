package com.fuse.ui.builder;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.IdentityHashMap;

import com.fuse.cms.Model;
import com.fuse.cms.ModelBase;
import com.fuse.cms.ModelCollection;
import com.fuse.ui.Node;

public class NodeBuilder {
  private Instantiator instantiator;
  private ModelCollection collection;
  private String nodeId;
  private boolean bActive = false;
  private Node createdNode = null;
  private Model nodeModel = null;
  private List<NodeBuilder> activeChildNodeBuilders;

  public NodeBuilder(ModelCollection col, String nodeId){
    collection = col;
    this.nodeId = nodeId;
    this.instantiator = new Instantiator();
    activeChildNodeBuilders = new ArrayList<>();
  }

  public Node createNode(){
    if(createdNode != null)
      return createdNode;

    nodeModel = collection.findById(nodeId, true/* create-if-doesnt-exist */);
    createdNode = instantiator.createNode(nodeModel);

    // create content of this node; loop over each child record in the collection
    // if any is a _direct_ child of this node, build it, including it's sub-structure
    // (recursive!) and add it to our node.
    collection.each((Model mod) -> {
      // child of us?
      if(this.isDirectChild(mod, nodeModel))
        createChildStructure(mod);
    });

    return createdNode;
  }

  public void setActive(boolean newActive){
    // enable active?
    if(newActive && !this.bActive){
      this.collection.addEvent.addListener((Model newModel) -> {
        newModel.changeEvent.addListener((ModelBase mbase) -> { this.changedModelCallback((Model)mbase); }, this);

        if(this.createdNode == null)
          return;

        if(this.isDirectChild(newModel, this.collection.findById(this.nodeId, true)))
          createChildStructure(newModel);
      }, this);

      this.collection.removeEvent.addListener((Model removeModel) -> {
        // this our model?
        if(this.nodeModel == removeModel){
          if(this.createdNode != null){
            Node parentNode = this.createdNode.getParent();
            if(parentNode != null)
              parentNode.removeChild(this.createdNode);
          }

          // don't do this.createdNode = null, because any parent
          // builder might still need to find us using our node
        }

        // this a child builder's model?
        NodeBuilder childBuilder = getActiveChildBuilderForModel(removeModel);
        if(childBuilder != null){
          // orphan our child builder
          activeChildNodeBuilders.remove(childBuilder);
        }

        // cleanup any listeners we might have registered on this model
        removeModel.changeEvent.removeListeners(this);
      });

      this.collection.each((Model m) -> {
        m.changeEvent.addListener((ModelBase mbase) -> { this.changedModelCallback((Model)mbase); }, this);
      });
    }

    // disable active?
    if(this.bActive && !newActive){
      this.collection.addEvent.removeListeners(this);
      this.collection.removeEvent.removeListeners(this);
      this.collection.each((Model m) -> m.changeEvent.removeListeners(this));
    }

    this.bActive = newActive;
  }

  public boolean isActive(){ return bActive; }

  protected Model getNodeModel(){
    return nodeModel;
  }

  private boolean isDirectChild(Model potentialChild, Model potentialParent){
    return potentialChild.get("parent", "").equals(potentialParent.getId()) && !potentialChild.get("parent", "").equals("");
  }

  private boolean isChild(Model potentialChild, Model potentialParent){
    for(Model curModel = potentialChild; curModel != null; curModel = this.collection.findById(curModel.get("parent", ""))){
      if(curModel.get("parent").equals(potentialParent.getId()))
        return true;
    }

    return false;
  }

  protected NodeBuilder getActiveChildBuilderForModel(Model childModel){
    if(!isActive())
      return null;

    for(NodeBuilder activeChildBuilder : activeChildNodeBuilders){
      if(activeChildBuilder.getNodeModel() == childModel)
        return activeChildBuilder;

      NodeBuilder bb = activeChildBuilder.getActiveChildBuilderForModel(childModel);

      if(bb != null)
        return bb;
    }

    return null;
  }

  private void createChildStructure(Model childModel){
    // System.out.println("CREATE CHILD STRUCTURE: "+childModel.getId());

    // create builder
    NodeBuilder childBuilder = new NodeBuilder(this.collection, childModel.getId());
    childBuilder.setActive(isActive());

    // let builder generate node
    Node childNode = childBuilder.createNode();
    // add built node as child to our node
    createdNode.addChild(childNode);

    if(isActive())
      activeChildNodeBuilders.add(childBuilder);
  }

  private void changedModelCallback(Model changedModel){
    if(createdNode == null)
      return;

    // is out our node's model
    if(this.nodeModel == changedModel){
      // apply changes?
      Node newNode = instantiator.createNode(changedModel);
      if(newNode.getClass() != createdNode.getClass()){
        // transfer all child nodes to newNode
        List<Node> childNodes = new ArrayList<>();
        childNodes.addAll(createdNode.getChildNodes());
        for(Node n : childNodes){
          createdNode.removeChild(n);
          newNode.addChild(n);
        }

        Node parentNode = createdNode.getParent();
        if(parentNode != null){
          parentNode.removeChild(createdNode);
          parentNode.addChild(newNode);
        }

        createdNode = newNode;
      }
    }

    if(isDirectChild(changedModel, nodeModel)){
      NodeBuilder existingChildBuilder = null;
      for(NodeBuilder childBuilder : activeChildNodeBuilders){
        if(childBuilder.getNodeModel() == changedModel){
          existingChildBuilder = childBuilder;
          break;
        }
      }

      // just became our child?
      if(existingChildBuilder == null){
        createChildStructure(changedModel);
      }
    } else { // no our child
      for(int i=activeChildNodeBuilders.size()-1; i>=0; i--){
        NodeBuilder childBuilder = activeChildNodeBuilders.get(i);
        // was our child, but no longer?
        if(childBuilder.getNodeModel() == changedModel){
          createdNode.removeChild(childBuilder.createNode());
          activeChildNodeBuilders.remove(childBuilder);
        }
      }
    }
  }
}
