package com.fuse.ui.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.fuse.cms.Model;
import com.fuse.ui.EllipseNode;
import com.fuse.ui.EventNode;
import com.fuse.ui.ImageNode;
import com.fuse.ui.LambdaNode;
import com.fuse.ui.Node;
import com.fuse.ui.RectNode;
import com.fuse.ui.ShadowImageNode;
import com.fuse.ui.TextNode;
import com.fuse.ui.extensions.Constrain;
import com.fuse.ui.extensions.DoubleClickZoom;
import com.fuse.ui.extensions.Draggable;
import com.fuse.ui.extensions.PinchZoom;
import com.fuse.ui.extensions.Swiper;
import com.fuse.ui.extensions.TouchEventForwarder;

public class Instantiator {
  private Map<String, Function<Model, Node>> typeInstantiators = null;
  private Map<String, BiConsumer<Node, Model>> typeExtenders = null;
  private boolean bDefaultNodesToNotInteractive = false;
  private Configurator configurator = new Configurator();

  public Instantiator(){
    this.registerDefaultTypeInstantiators();
    this.registerDefaultExtenders();
  }

  // operation methods

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

    if(bDefaultNodesToNotInteractive)
      n.setInteractive(false);

    if(this.configurator != null)
      this.configurator.cfg(n, model);

    return n;
  }

  public void extend(Node n, Model m) {
	  this.typeExtenders.get(m.get("type", "BaseExtension")).accept(n, m);
  }

  // config methods

  public Configurator getConfigurator(){
    return this.configurator;
  }

  public void setConfigurator(Configurator configurator){
    this.configurator = configurator;
  }

  public void setTypeInstantiator(String typeValue, Function<Model, Node> func){
    if(typeInstantiators == null)
      typeInstantiators = new HashMap<>();

    typeInstantiators.put(typeValue, func);
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

  public void setDefaultNodesToNotInteractive(boolean set){
    bDefaultNodesToNotInteractive = set;
  }

  public boolean getDefaultNodesToNotInteractive(){
    return bDefaultNodesToNotInteractive;
  }

  // defaults

  private Node defaultInstantiator(Model model){
    Node n = new Node(); //typ.equals("Node")){
    if(this.configurator != null)
      this.configurator.cfg(n, model);
    return n;
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

    this.setTypeInstantiator("ShadowImageNode", (Model model) -> {
      ShadowImageNode node = new ShadowImageNode();
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

    this.setTypeInstantiator("EllipseNode", (Model model) -> {
      EllipseNode node = new EllipseNode();
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

    this.setTypeExtender("TouchEventForwarder", (Node node, Model model) -> {
      TouchEventForwarder.enableForChildNames(node, model.get("children", "").split(","));
    });
  }
}
