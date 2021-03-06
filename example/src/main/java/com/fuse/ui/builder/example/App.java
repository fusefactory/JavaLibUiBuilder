package com.fuse.ui.builder.example;

import java.util.logging.*;
import processing.core.*;
import com.fuse.ui.builder.*;
import com.fuse.ui.*;


public class App extends PApplet {

  private static final String UI_DATA_FILE = "data/ui.json";
  private PApplet papplet;
  private PGraphics pg;
  private Logger logger;
  private int tLastFrame;
  private Builder builder;
  private Node sceneNode;
  private TouchManager touchManager;
  private boolean bDrawDebug = false;

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

    pg = papplet.createGraphics(papplet.width, papplet.height, P3D);
    Node.setPGraphics(pg);

    builder = new Builder();
    builder.getLayoutCollection().loadJsonFromFile(UI_DATA_FILE);
    builder.setUseActiveTransformations(true); // allow dynamic reload by reloading data
    builder.setDefaultNodesToNotInteractive(true);

    this.sceneNode = new Node();
    this.sceneNode.setSize(papplet.width, papplet.height);
    this.populateScene();

    touchManager = new TouchManager();
    touchManager.setNode(sceneNode);
    touchManager.setDispatchOnUpdate(true);
  }

  private void populateScene(){
    this.sceneNode.removeAllChildren();
    Node page = builder.createNode("Page1");
    this.sceneNode.addChild(page);
  }

  public void update(float dt){
    touchManager.update(dt);
    this.sceneNode.setSize(papplet.width, papplet.height);
    this.sceneNode.updateSubtree(dt);
  }

  public void draw(){
    int t = papplet.millis();
    float dt = (float)(t - this.tLastFrame) / 1000.0f;
    this.update(dt);
    this.tLastFrame = t;

    papplet.background(0);
    papplet.clear();

    // UI (2D)
    pg.beginDraw();
    {
      pg.clear();
      sceneNode.render();

      if(bDrawDebug){
        sceneNode.renderDebug();
        touchManager.drawActiveTouches();
      }
    }
    pg.endDraw();

    papplet.image(pg, 0f,0f);
  }

  public void mousePressed(){
    touchManager.touchDown(0, new PVector(mouseX, mouseY, 0f));
  }

  public void mouseDragged(){
    touchManager.touchMove(0, new PVector(mouseX, mouseY, 0f));
  }

  public void mouseReleased(){
    touchManager.touchUp(0, new PVector(mouseX, mouseY, 0f));
  }

  public void keyPressed(){
    switch(key){
      case 'd': {
        bDrawDebug = !bDrawDebug;
        return;
      }

      case 'l': {
        // reload
        builder.getLayoutCollection().loadJsonFromFile(UI_DATA_FILE);
        System.out.println("reload!");
        return;
      }

      case '0': {
        this.populateScene();
        return;
      }
    }
  }
}
