package com.fuse.ui.builder;

import java.util.Random;
import java.util.function.Consumer;

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

  public void cfg(Node n, String configId){
    this.apply(configId, (ModelBase m) -> {
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

      if(m.has("sizeVar")){
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

      m.withFloat("rotateZ", (Float val) -> {
        PVector rot = n.getRotation();
        rot.z = val / 180.0f * (float)Math.PI;
        n.setRotation(rot);
      });

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
    });
  }

  public void cfg(TextNode n, String configId){
    // first apply Node-specific configurations
    this.cfg((Node)n, configId);

    // then apply TextNode-specific configurations
    this.apply(configId, (ModelBase m) -> {
      m.with("text", (String val) -> n.setText(val));
    });
  }

  private void apply(String configId, Consumer<ModelBase> func){
    this.configs.findById(configId, true).transform(func, this.bActive);
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
