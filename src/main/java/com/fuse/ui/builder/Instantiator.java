package com.fuse.ui.builder;

import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.Map;
import java.util.HashMap;
import com.fuse.cms.Model;

import com.fuse.ui.*;
import com.fuse.ui.extensions.*;

public class Instantiator {
  private Map<String, Function<Model, Node>> typeInstantiators = null;
  private Map<String, BiConsumer<Node, Model>> typeExtenders = null;
  private Configurator configurator = new Configurator();

  public Instantiator(){
    this.registerDefaultExtenders();
  }

  public Node createNode(Model model){
    Node n = null;

    String typ = model.get("type", "Node");

    if(typeInstantiators != null){
      Function<Model, Node> func = typeInstantiators.get(typ);
      if(func != null){
        n = func.apply(model);
      }
    }

    if(n == null)
      n = Instantiator.defaultInstantiator(model);

    if(this.configurator != null)
      this.configurator.cfg(n, model);

    return n;
  }

  public void setTypeInstantiator(String typeValue, Function<Model, Node> func){
    if(typeInstantiators == null)
      typeInstantiators = new HashMap<>();

    typeInstantiators.put(typeValue, func);
  }

  private static Node defaultInstantiator(Model model){
    String typ = model.get("type", "Node");

    Node n = null;
    if(typ.equals("TextNode")) n = new TextNode();
    if(n == null && typ.equals("LineNode")) n = new LineNode();
    if(n == null && typ.equals("ImageNode")) n = new ImageNode();
    if(n == null && typ.equals("RectNode")) n = new RectNode();
    if(n == null && typ.equals("LambdaNode")) n = new LambdaNode();
    if(n == null && typ.equals("EventNode")) n = new EventNode();
    if(n == null) n = new Node(); //typ.equals("Node")){

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
	  this.typeExtenders.get(m.get("type", "BaseExtension")).accept(n, m);
  }


  private void registerDefaultExtenders(){
    this.setTypeExtender("Draggable", (Node node, Model model) -> {
      Draggable ext = Draggable.enableFor(node);
      if(this.configurator != null)
        this.configurator.cfg(ext, model);
    });

    this.setTypeExtender("Constrain", (Node node, Model model) -> {
      Constrain ext = Constrain.enableFor(node);
      if(this.configurator != null)
        this.configurator.cfg(ext, model);
    });

    this.setTypeExtender("PinchZoom", (Node node, Model model) -> {
      PinchZoom ext = PinchZoom.enableFor(node);
      if(this.configurator != null)
        this.configurator.cfg(ext, model);
    });

    this.setTypeExtender("DoubleClickZoom", (Node node, Model model) -> {
      DoubleClickZoom ext = DoubleClickZoom.enableFor(node);
      if(this.configurator != null)
        this.configurator.cfg(ext, model);
    });

    this.setTypeExtender("Swiper", (Node node, Model model) -> {
      Swiper ext = Swiper.enableFor(node);
      if(this.configurator != null)
        this.configurator.cfg(ext, model);
    });
  }
}
