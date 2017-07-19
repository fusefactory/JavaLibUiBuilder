package com.fuse.ui.builder;

import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;
import com.fuse.cms.Model;

import com.fuse.ui.Node;
import com.fuse.ui.LineNode;
import com.fuse.ui.TextNode;
import com.fuse.ui.ImageNode;

public class Instantiator {
  private Map<String, Function<Model, Node>> typeInstatiators = null;

  public Node createNode(Model model){
    String typ = model.get("type", "Node");

    if(typeInstatiators != null){
      Function<Model, Node> func = typeInstatiators.get(typ);
      if(func != null){
        return func.apply(model);
      }
    }

    if(typ.equals("TextNode")){
      TextNode n = new TextNode();
      // configurator?
      return n;
    }

    if(typ.equals("LineNode")){
      LineNode n = new LineNode();
      // configurator?
      return n;
    }

    if(typ.equals("ImageNode")){
      ImageNode n = new ImageNode();
      // configurator?
      return n;
    }

    //if(typ.equals("Node")){
      Node n = new Node();
      // configurator?
      return n;
    // }
  }

  public void setTypeInstantiator(String typeValue, Function<Model, Node> func){
    if(typeInstatiators == null)
      typeInstatiators = new HashMap<>();

    typeInstatiators.put(typeValue, func);
  }
}
