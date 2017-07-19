package com.fuse.ui.builder;

import com.fuse.cms.Model;

import com.fuse.ui.Node;
import com.fuse.ui.LineNode;
import com.fuse.ui.TextNode;
import com.fuse.ui.ImageNode;

public class Instantiator {
  public Node createNode(Model model){
    String typ = model.get("type", "Node");

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
}
