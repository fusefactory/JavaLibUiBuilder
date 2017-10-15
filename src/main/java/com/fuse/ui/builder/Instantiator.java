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
    this.registerDefaultTypeInstantiators();
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
      n = this.defaultInstantiator(model);

    if(this.configurator != null)
      this.configurator.cfg(n, model);

    return n;
  }

  public void setTypeInstantiator(String typeValue, Function<Model, Node> func){
    if(typeInstantiators == null)
      typeInstantiators = new HashMap<>();

    typeInstantiators.put(typeValue, func);
  }

  private Node defaultInstantiator(Model model){
    Node n = new Node(); //typ.equals("Node")){
    if(this.configurator != null)
      this.configurator.cfg(n, model);
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

  public Configurator getConfigurator(){
    return this.configurator;
  }


  private void registerDefaultTypeInstantiators(){
    this.setTypeInstantiator("Node", (Model model) -> {
      Node node = new Node();
      if(this.configurator != null)
        this.configurator.cfg(node, model);
      return node;
    });

    this.setTypeInstantiator("TextNode", (Model model) -> {
      TextNode node = new TextNode();
      if(this.configurator != null)
        this.configurator.cfg(node, model);
      return node;
    });

    this.setTypeInstantiator("ImageNode", (Model model) -> {
      ImageNode node = new ImageNode();
      if(this.configurator != null)
        this.configurator.cfg(node, model);
      return node;
    });

    this.setTypeInstantiator("RectNode", (Model model) -> {
      RectNode node = new RectNode();
      if(this.configurator != null)
        this.configurator.cfg(node, model);
      return node;
    });

    this.setTypeInstantiator("LambdaNode", (Model model) -> {
      LambdaNode node = new LambdaNode();
      if(this.configurator != null)
        this.configurator.cfg(node, model);
      return node;
    });

    this.setTypeInstantiator("EventNode", (Model model) -> {
      EventNode node = new EventNode();
      if(this.configurator != null)
        this.configurator.cfg(node, model);
      return node;
    });
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
