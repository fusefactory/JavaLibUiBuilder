package com.fuse.ui.builder;

import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.Map;
import java.util.HashMap;
import com.fuse.cms.Model;

import com.fuse.ui.Node;
import com.fuse.ui.LineNode;
import com.fuse.ui.TextNode;
import com.fuse.ui.ImageNode;

public class Instantiator {
  private Map<String, Function<Model, Node>> typeInstatiators = null;
  private Map<String, BiConsumer<Node, Model>> typeExtenders = null;

  public Node createNode(Model model){
    String typ = model.get("type", "Node");

    if(typeInstatiators != null){
      Function<Model, Node> func = typeInstatiators.get(typ);
      if(func != null){
        return func.apply(model);
      }
    }

    return Instantiator.defaultInstantiator(model);
  }

  public void setTypeInstantiator(String typeValue, Function<Model, Node> func){
    if(typeInstatiators == null)
      typeInstatiators = new HashMap<>();

    typeInstatiators.put(typeValue, func);
  }

  public static Node defaultInstantiator(Model model){
    String typ = model.get("type", "Node");

    Node n = null;

    if(typ.equals("TextNode")){
      n = new TextNode();
    }

    if(n == null && typ.equals("LineNode")){
      n = new LineNode();
    }

    if(n == null && typ.equals("ImageNode")){
      n = new ImageNode();
      // configurator?
      return n;
    }

    if(n == null){ //typ.equals("Node")){
      n = new Node();
    }

    if(model.has("name"))
      n.setName(model.get("name"));

    return n;
  }

  public void setTypeExtender(String typeValue, BiConsumer<Node, Model> func) {
	  if(this.typeExtenders == null)
		  this.typeExtenders = new HashMap<>();
	  this.typeExtenders.put(typeValue, func);
  }

  public boolean isExtender(Model model) {
	  boolean result = this.typeExtenders != null && this.typeExtenders.get(model.get("type", "Node")) != null;
	  return result;
  }
  
  public void extend(Node n, Model m) {
	  this.typeExtenders.get(m.get("type", "Node")).accept(n, m);
  }
}
