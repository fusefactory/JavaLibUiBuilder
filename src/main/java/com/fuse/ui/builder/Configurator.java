package com.fuse.ui.builder;

import java.util.Random;
import java.util.function.Consumer;

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PGraphics;

import com.fuse.cms.*;
import com.fuse.ui.*;

public class Configurator {

  private ModelCollection configs = new ModelCollection();
  private static PGraphics pg = new PGraphics(); // only needed for color stuff
  private boolean bActive = false;
  private boolean bDefaultNodesToNotInteractive = false;

  public ModelCollection getDataCollection(){
    return this.configs;
  }

  public void setUseActiveTransformations(boolean active){
    this.bActive = active;
  }

  public boolean getUseActiveTransformations(){
    return this.bActive;
  }


  public void cfg(Node n, String configId){
    this.cfg(n, this.configs.findById(configId, true));
  }

  public void cfg(Node n, Model mod){
    this.apply(mod, (ModelBase m) -> {
      m.with("name", (String val) -> n.setName(val));
      m.withFloat("width", (Float val) -> n.setWidth(val));
      m.withFloat("height", (Float val) -> n.setHeight(val));
      m.withFloat("x", (Float val) -> n.setX(val));
      m.withFloat("y", (Float val) -> n.setY(val));
      m.withFloat("plane", (Float val) -> n.setPlane(val));
      m.withBool("clipping", (Boolean val) -> n.setClipContent(val));
      m.withBool("visible", (Boolean val) -> n.setVisible(val));

      if(m.has("pos")) n.setPosition(getPVector(m, "pos"));
      if(m.has("position")) n.setPosition(getPVector(m, "position"));
      if(m.has("size")) n.setSize(getPVector(m, "size"));
      if(m.has("scale")) n.setScale(getPVector(m, "scale"));

      if(bDefaultNodesToNotInteractive)
          n.setInteractive(m.getBool("interactive", false));
      else
        m.withBool("interactive", (Boolean val) -> n.setInteractive(val));

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



  /// fetch model and apply using lambda
  protected void apply(String configId, Consumer<ModelBase> func){
    this.apply(this.configs.findById(configId, true), func);
  }

  /// apply lambda using data from model
  protected void apply(Model m, Consumer<ModelBase> func){
    m.transform(func, this.bActive);
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
