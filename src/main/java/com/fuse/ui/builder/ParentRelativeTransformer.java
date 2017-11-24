package com.fuse.ui.builder;

import processing.core.PVector;
import com.fuse.ui.Node;
import com.fuse.ui.extensions.ExtensionBase;
import com.fuse.ui.extensions.TransformerExtension;

public class ParentRelativeTransformer extends TransformerExtension {

  private Float sizeFactorX,sizeFactorY,sizeFactorZ;
  private Float posFactorX,posFactorY,posFactorZ;
  private Node activeParent;

  public ParentRelativeTransformer(){
    this.maxTransformationsPerUpdate = 25;
    this.setSmoothValue(0.0f); // disable smoothing by default
  }

  @Override public void setup(){
    if(this.node.getParent() != null)
      this.enableForParent(this.node.getParent());

    this.node.newParentEvent.whenTriggered(() -> { this.enableForParent(this.node.getParent()); }, this);
  }

  private void enableForParent(Node parent){
    if(this.activeParent != null)
      this.disableForParent(this.activeParent);

    this.activeParent = parent;

    if(this.activeParent != null){
      this.activeParent.transformationEvent.whenTriggered(() -> { this.apply(); }, this);
      this.apply();
    }
  }

  @Override public void teardown(){
    this.node.newParentEvent.stopWhenTriggeredCallbacks(this);
    if(this.activeParent != null)
      this.disableForParent(this.activeParent);
  }

  private void disableForParent(Node parent){
    parent.transformationEvent.stopWhenTriggeredCallbacks(this);
  }


  public ParentRelativeTransformer setPosFactorX(Float factor){
    this.posFactorX = factor; this.apply(); return this;
  }

  public ParentRelativeTransformer setPosFactorY(Float factor){
    this.posFactorY = factor; this.apply(); return this;
  }

  public ParentRelativeTransformer setPosFactorZ(Float factor){
    this.posFactorZ = factor; this.apply(); return this;
  }

  public ParentRelativeTransformer setSizeFactorX(Float factor){
    this.sizeFactorX = factor; this.apply(); return this;
  }

  public ParentRelativeTransformer setSizeFactorY(Float factor){
    this.sizeFactorY = factor; this.apply(); return this;
  }

  public ParentRelativeTransformer setSizeFactorZ(Float factor){
    this.sizeFactorZ = factor; this.apply(); return this;
  }


  protected void apply(){
    Node parent = this.activeParent;
    if(parent == null)
      return;

    PVector size = this.node.getSize();
    if(this.sizeFactorX != null) size.x = this.sizeFactorX * parent.getSize().x;
    if(this.sizeFactorY != null) size.y = this.sizeFactorY * parent.getSize().y;
    if(this.sizeFactorZ != null) size.z = this.sizeFactorZ * parent.getSize().z;
    this.transformSize(size);

    PVector pos = this.node.getPosition();
    if(this.posFactorX != null) pos.x = this.posFactorX * parent.getSize().x;
    if(this.posFactorY != null) pos.y = this.posFactorY * parent.getSize().y;
    if(this.posFactorZ != null) pos.z = this.posFactorZ * parent.getSize().z;
    this.transformPosition(pos);
  }

  // static factory methods // // // // //

  public static ParentRelativeTransformer enableFor(Node n){
    ParentRelativeTransformer d = getFor(n);

    if(d == null){
      d = new ParentRelativeTransformer();
      n.use(d);
    }

    return d;
  }

  public static ParentRelativeTransformer getFor(Node n){
    for(ExtensionBase ext : n.getExtensions())
      if(ParentRelativeTransformer.class.isInstance(ext))
        return (ParentRelativeTransformer)ext;
    return null;
  }

  public static void disableFor(Node n){
    for(ExtensionBase ext : n.getExtensions()) {
      if(ParentRelativeTransformer.class.isInstance(ext))
          n.stopUsing(ext);
    }
  }
}
