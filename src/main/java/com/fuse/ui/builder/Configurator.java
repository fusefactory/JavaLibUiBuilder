package com.fuse.ui.builder;

import java.util.Random;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PGraphics;

import com.fuse.cms.*;
import com.fuse.ui.*;
import com.fuse.ui.extensions.*;

public class Configurator {

  private ModelCollection configs = new ModelCollection();
  private static PGraphics pg = new PGraphics(); // only needed for color stuff
  private boolean bActive = false;

  // config methods

  public void setDataCollection(ModelCollection collection){
    this.configs = collection;
  }

  public ModelCollection getDataCollection(){
    return this.configs;
  }

  public void setUseActiveTransformations(boolean active){
    this.bActive = active;
    if(this.bActive){
      System.err.println("ConfiguratorsetUseActiveTransformations; active transformation enabled. USE ONLY IN DEVELOPMENT.");
    }
  }

  public boolean getUseActiveTransformations(){
    return this.bActive;
  }

  // Node configurator methods

  public void cfg(Node n, String configId){
    this.cfg(n, this.configs.findById(configId, true));
  }

  public void cfg(Node n, Model mod){
    this.apply(mod, (ModelBase m) -> {
      m.with("name", (String val) -> n.setName(val));
      m.withBool("interactive", (Boolean val) -> n.setInteractive(val));
      m.withFloat("plane", (Float val) -> n.setPlane(val));
      m.withBool("clipping", (Boolean val) -> n.setClipContent(val));
      m.withBool("visible", (Boolean val) -> n.setVisible(val));
      if(m.has("pos")) n.setPosition(getPVector(m, "pos"));
      if(m.has("position")) n.setPosition(getPVector(m, "position"));
      if(m.has("scale")) n.setScale(getPVector(m, "scale"));

      m.with("x", (String val) -> {
        if(val.endsWith("%")){
          float f = Float.parseFloat(val.replace("%",""))/100.0f;
          ParentRelativeTransformer.enableFor(n).setPosFactorX(f);
        } else {
          n.setX(m.getFloat("x"));
        }
      });

      m.with("y", (String val) -> {
        if(val.endsWith("%")){
          float f = Float.parseFloat(val.replace("%",""))/100.0f;
          ParentRelativeTransformer.enableFor(n).setPosFactorY(f);
        } else {
          n.setY(m.getFloat("y"));
        }
      });

      m.with("width", (String val) -> {
        if(val.endsWith("%")){
          float f = Float.parseFloat(val.replace("%",""))/100.0f;
          ParentRelativeTransformer.enableFor(n).setSizeFactorX(f);
        } else {
          n.setWidth(m.getFloat("width"));
        }
      });

      m.with("height", (String val) -> {
        if(val.endsWith("%")){
          float f = Float.parseFloat(val.replace("%",""))/100.0f;
          ParentRelativeTransformer.enableFor(n).setSizeFactorY(f);
        } else {
          n.setHeight(m.getFloat("height"));
        }
      });


      if(m.has("size")) n.setSize(getPVector(m, "size"));

      if(m.has("sizeVar")){ // a variation of 10.0f means the size attribute will can go be modified from -10.0 to 10.0
        // variation vector
        PVector var = getPVector(m, "sizeVar");
        // current size
        PVector size = n.getSize();
        // apply random variation to size
        Random r = new Random();
        size.x += (r.nextFloat()-0.5f) * 2.0f * var.x;
        size.y += (r.nextFloat()-0.5f) * 2.0f * var.y;
        size.z += (r.nextFloat()-0.5f) * 2.0f * var.y;
        // update node
        n.setSize(size);
      }

      if(m.has("rot")){
        PVector rot = getPVector(m, "rot");
        rot.mult((float)Math.PI / 180.0f);
        n.setRotation(rot);
      }

      if(m.has("rotation")){
        PVector rot = getPVector(m, "rotation");
        rot.mult((float)Math.PI / 180.0f);
        n.setRotation(rot);
      }

      m.withFloat("rotateZ", (Float val) -> {
        PVector rot = n.getRotation();
        rot.z = val / 180.0f * (float)Math.PI;
        n.setRotation(rot);
      });
    });
  }

  public void cfg(TextNode n, String configId){
    this.cfg(n, this.configs.findById(configId, true));
  }

  public void cfg(TextNode n, Model mod){
    // first apply Node-specific configurations
    this.cfg((Node)n, mod);

    // then apply TextNode-specific configurations
    this.apply(mod, (ModelBase m) -> {
      m.with("text", (String val) -> n.setText(val));
      m.withFloat("textSize", (Float val) -> n.setTextSize(val));
      m.with("color", (String v) -> n.setTextColor(getColor(m, "color")));
      m.with("offset", (String v) -> n.setTextOffset(getPVector(m, "offset")));

      m.with("framePadding", (String v) -> n.setFramePadding(getPVector(m, "framePadding")));
      m.with("frameColor", (String v) -> n.setFrameColor(getColor(m, "frameColor")));

      m.with("alignX", (String v) -> {
        if(v.toUpperCase().equals("LEFT")) n.setAlignX(PApplet.LEFT);
        if(v.toUpperCase().equals("RIGHT")) n.setAlignX(PApplet.RIGHT);
        if(v.toUpperCase().equals("CENTER")) n.setAlignX(PApplet.CENTER);
      });

      m.with("alignY", (String v) -> {
        if(v.toUpperCase().equals("TOP")) n.setAlignY(PApplet.TOP);
        if(v.toUpperCase().equals("BOTTOM")) n.setAlignY(PApplet.BOTTOM);
        if(v.toUpperCase().equals("CENTER")) n.setAlignY(PApplet.CENTER);
        if(v.toUpperCase().equals("BASELINE")) n.setAlignY(PApplet.BASELINE);
      });

      // m.with("font", (String v) -> {
      // });
    });
  }

  public void cfg(RectNode n, String configId){
    this.cfg(n, this.configs.findById(configId, true));
  }

  public void cfg(RectNode n, Model mod){
    this.cfg((Node)n, mod);

    this.apply(mod, (ModelBase m) -> {
      m.withFloat("fillAlpha", (Float val) -> n.setFillAlpha(val));
      m.withFloat("strokeWeight", (Float val) -> n.setStrokeWeight(val));
      m.with("color", (String val) -> n.setRectColor(getColor(m, "color")));
      m.with("fillColor", (String val) -> n.setRectColor(getColor(m, "fillColor")));
      m.with("strokeColor", (String val) -> n.setRectStrokeColor(getColor(m, "strokeColor")));
    });
  }

  public void cfg(LineNode n, String configId){
    this.cfg(n, this.configs.findById(configId, true));
  }

  public void cfg(LineNode n, Model mod){
    this.cfg((Node)n, mod);

    this.apply(mod, (ModelBase m) -> {
      m.withFloat("strokeWeight", (Float val) -> n.setStrokeWeight(val));
      m.with("color", (String val) -> n.setLineColor(getColor(m, "color")));
      m.with("lineColor", (String val) -> n.setLineColor(getColor(m, "lineColor")));
      m.with("from", (String val) -> n.setFrom(getPVector(m, "from")));
      m.with("to", (String val) -> n.setTo(getPVector(m, "to")));
    });
  }

  public void cfg(ImageNode n, String configId){
    this.cfg(n, this.configs.findById(configId, true));
  }

  public void cfg(ImageNode n, Model mod){
    this.cfg((Node)n, mod);

    this.apply(mod, (ModelBase m) -> {
      m.with("mode", (String mode) -> {
        mode = mode.toUpperCase();
        if(mode.equals("CENTER")) n.setMode(ImageNode.Mode.CENTER);
        if(mode.equals("NORMAL")) n.setMode(ImageNode.Mode.NORMAL);
        if(mode.equals("FIT")) n.setMode(ImageNode.Mode.FIT);
        if(mode.equals("FIT_CENTERED")) n.setMode(ImageNode.Mode.FIT_CENTERED);
        if(mode.equals("FILL")) n.setMode(ImageNode.Mode.FILL);
      });

      m.withBool("autoResize", (Boolean v) -> n.setAutoResizeToImage(v));
      m.with("tint", (String v) -> n.setTint(getColor(m, "tint")));

      // m.with("image", (String v) -> {
      //   // load image?
      // });
    });
  }


  // ExtensionBase configurator methods

  public void cfg(ExtensionBase e, String configId){
    this.cfg(e, this.configs.findById(configId, true));
  }

  public void cfg(ExtensionBase e, Model mod){
    this.apply(mod, (ModelBase m) -> {
      m.withBool("enabled", (Boolean v) -> { if(v) e.enable(); else e.disable(); });
    });
  }

  public void cfg(TransformerExtension e, String configId){
    this.cfg(e, this.configs.findById(configId, true));
  }

  public void cfg(TransformerExtension e, Model mod){
    this.cfg((ExtensionBase)e, mod);

    this.apply(mod, (ModelBase m) -> {
      m.withFloat("minPosX", (Float v) -> e.setMinPosX(v));
      m.withFloat("minPosY", (Float v) -> e.setMinPosY(v));
      m.withFloat("maxPosX", (Float v) -> e.setMaxPosX(v));
      m.withFloat("maxPosY", (Float v) -> e.setMaxPosY(v));
      m.withFloat("minScale", (Float v) -> e.setMinScale(v));
      m.withFloat("maxScale", (Float v) -> e.setMaxScale(v));
      m.withFloat("smootValue", (Float v) -> e.setSmoothValue(v));
      m.withBool("stopOnTouch", (Boolean v) -> e.setStopOnTouch(v));
      m.withFloat("maxTransformationTime", (Float v) -> e.setMaxTransformationTime(v));
      m.withFloat("doneScaleDeltaMag", (Float v) -> e.setDoneScaleDeltaMag(v));
      m.withFloat("donePositionDeltaMag", (Float v) -> e.setDonePositionDeltaMag(v));
      m.withFloat("doneSizeDeltaMag", (Float v) -> e.setDoneSizeDeltaMag(v));
      m.withFloat("doneRotationDeltaMag", (Float v) -> e.setDoneRotationDeltaMag(v));
    });
  }

  public void cfg(Draggable e, String configId){
    this.cfg(e, this.configs.findById(configId, true));
  }

  public void cfg(Draggable e, Model mod){
    this.cfg((TransformerExtension)e, mod);

    this.apply(mod, (ModelBase m) -> {
      m.withBool("abortOnSecondTouch", (Boolean v) -> e.setAbortOnSecondTouch(v));
    });
  }

  public void cfg(Constrain e, String configId){
    this.cfg(e, this.configs.findById(configId, true));
  }

  public void cfg(Constrain e, Model mod){
    this.cfg((TransformerExtension)e, mod);

    this.apply(mod, (ModelBase m) -> {
      m.withBool("centerWhenFitting", (Boolean v) -> e.setCenterWhenFitting(v));
      m.withBool("fillParent", (Boolean v) -> e.setFillParent(v));
    });
  }

  public void cfg(PinchZoom e, String configId){
    this.cfg(e, this.configs.findById(configId, true));
  }

  public void cfg(PinchZoom e, Model mod){
    this.cfg((TransformerExtension)e, mod);

    // no custom configs
    // this.apply(mod, (ModelBase m) -> {
    //
    // });
  }

  public void cfg(DoubleClickZoom e, String configId){
    this.cfg(e, this.configs.findById(configId, true));
  }

  public void cfg(DoubleClickZoom e, Model mod){
    this.cfg((TransformerExtension)e, mod);

    this.apply(mod, (ModelBase m) -> {
      m.withLong("doubleClickMaxInterval", (Long v) -> e.setDoubleClickMaxInterval(v));
      if(m.has("scaleFactor")) e.setScaleFactor(getPVector(m, "scaleFactor"));
    });
  }

  public void cfg(Swiper e, String configId){
    this.cfg(e, this.configs.findById(configId, true));
  }

  public void cfg(Swiper e, Model mod){
    this.cfg((TransformerExtension)e, mod);

    this.apply(mod, (ModelBase m) -> {
      m.withBool("snapEnabled", (Boolean v) -> e.setSnapEnabled(v));
      m.withFloat("snapThrowFactor", (Float v) -> e.setSnapThrowFactor(v));
      m.withFloat("snapVelocity", (Float v) -> e.setSnapVelocity(v));
      m.withFloat("snapThrowFactor", (Float v) -> e.setSnapThrowFactor(v));
      m.withFloat("velocityReductionFactor", (Float v) -> e.setVelocityReductionFactor(v));
      m.withFloat("dampingFactor", (Float v) -> e.setDampingFactor(v));
      if(m.has("snapInterval")) e.setSnapInterval(getPVector(m, "snapInterval", new PVector(200,0,0)));
      if(m.has("minOffset")) e.setMinOffset(getPVector(m, "minOffset", new PVector(0,0,0)));
      if(m.has("maxOffset")) e.setMaxOffset(getPVector(m, "maxOffset", new PVector(0,0,0)));
    });
  }

  /// fetch model and apply using lambda
  protected void apply(String configId, Consumer<ModelBase> func){
    this.apply(this.configs.findById(configId, true), func);
  }

  /// apply lambda using data from model
  protected void apply(Model m, Consumer<ModelBase> func){
    this.apply(m, this, func);
  }

  protected void apply(Model m, Object owner, Consumer<ModelBase> func){
    // if(this.bActive){
    //   m.stopTransform(owner);
    // }

    m.transform(func, owner, this.bActive);
  }

  // static helper methods // // // // //

  public static Integer getColor(ModelBase m, String prefix){
    return getColor(m, prefix, null);
  }

  public static Integer getColor(ModelBase m, String prefix, Integer defaultValue){
    if(m.has(prefix+"_r") || m.has(prefix+"_g") || m.has(prefix+"_b") || m.has(prefix+"_a")){
      pg.colorMode(PGraphics.RGB, 255);
      return pg.color(m.getInt(prefix+"_r", 255), m.getInt(prefix+"_g", 255), m.getInt(prefix+"_b", 255), m.getInt(prefix+"_a", 255));
    }

    if(m.has(prefix)){
      pg.colorMode(PGraphics.RGB, 255);

      if(defaultValue == null)
          defaultValue = pg.color(255);

      float[] _default = {pg.red(defaultValue), pg.green(defaultValue), pg.blue(defaultValue), pg.alpha(defaultValue)};
      float[] numbers = m.getVecX(prefix, 4, _default);

      return pg.color(numbers[0], numbers[1], numbers[2], numbers[3]);
    }

    return defaultValue;
  }

  public static PVector getPVector(ModelBase m, String attr){
    float[] floats = m.getVec3(attr);
    return new PVector(floats[0], floats[1], floats[2]);
  }

  public static PVector getPVector(ModelBase m, String attr, PVector defaultValue){
    float[] defaults = {defaultValue.x,defaultValue.y,defaultValue.z};
    float[] floats = m.getVec3(attr, defaults);
    return new PVector(floats[0], floats[1], floats[2]);
  }
}
