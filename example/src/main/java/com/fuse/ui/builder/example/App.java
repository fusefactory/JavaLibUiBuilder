package com.fuse.ui.builder.example;

import java.util.logging.*;
import processing.core.*;
import com.fuse.ui.builder.*;

public class App extends PApplet {

  private static final String UI_DATA_FILE = "data/ui.json";
  private PApplet papplet;
  private Logger logger;
  private int tLastFrame;
  private Builder builder;

  public static void main( String[] args ){
    PApplet.main("com.fuse.ui.builder.example.App");
  }

  public App(){
    super();
    logger = Logger.getLogger(App.class.getName());
    logger.setLevel(Level.ALL);
    papplet = this;
  }

  public void settings(){
    papplet.size(800, 600, P3D);
  }

  public void setup(){
    papplet.frameRate(30.0f);

    builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile(UI_DATA_FILE);
  }

  public void update(float dt){

  }

  public void draw(){
    int t = papplet.millis();
    float dt = (float)(t - this.tLastFrame) / 1000.0f;
    this.update(dt);
    this.tLastFrame = t;
  }
}
