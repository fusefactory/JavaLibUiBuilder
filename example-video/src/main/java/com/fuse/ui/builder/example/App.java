package com.fuse.ui.builder.example;

import java.util.logging.*;
import processing.core.*;
import com.fuse.cms.*;
import com.fuse.ui.builder.*;
import com.fuse.ui.*;
import com.fuse.resources.MovieSource;

class Recorder {
  public String filename = "output-%04d.png";
  public int counter = 0;
  public boolean enabled=false;

  public void recordFrame(PGraphics pg){
    if (!this.enabled) return; // skip
    pg.save(String.format(this.filename, this.counter));
    counter += 1;
  }
}

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

  private Configurator cfg;
  private ModelCollection configs = new ModelCollection();
  private MovieSource movieSource;
  private Recorder recorder = new Recorder();

  public static void main( String[] args ){
    PApplet.main("com.fuse.ui.builder.example.App");
  }

  public App(){
    super();
    logger = Logger.getLogger(App.class.getName());
    logger.setLevel(Level.ALL);
    papplet = this;

    // load json data file
    this.configs.loadJsonFromFile(UI_DATA_FILE);

    // create movie source and configurator
    this.movieSource = new MovieSource(this.papplet);
    this.cfg = new Configurator(configs, this.movieSource.getAsyncFacade());
  }

  public void settings(){
    this.cfg.apply("App", (ModelBase m) -> {
      papplet.size(m.getInt("width", 800), m.getInt("height", 600), P3D);
    });
  }

  public void setup(){
    System.setProperty("jna.library.path", "lib/archiviodigitale-binaries/macosx64");
    System.setProperty("gstreamer.library.path", "lib/macosx64");
    System.setProperty("gstreamer.plugin.path", "lib/macosx64/plugins");

    papplet.frameRate(30.0f);

    pg = papplet.createGraphics(papplet.width, papplet.height, P3D);
    Node.setPGraphics(pg);

    builder = new Builder(configs, cfg);
    builder.setUseActiveTransformations(true); // allow dynamic reload by reloading data
    builder.setDefaultNodesToNotInteractive(true);

    this.sceneNode = new Node();
    this.sceneNode.setSize(papplet.width, papplet.height);
    this.populateScene();

    touchManager = new TouchManager();
    touchManager.setNode(sceneNode);
    touchManager.setDispatchOnUpdate(true);

    this.cfg.apply("Recorder", (ModelBase mod) -> {
      mod.with("filename", (String v) -> this.recorder.filename = v);
      mod.withBool("enabled", (Boolean v) -> {
        this.recorder.enabled = v;
      });
    });
  }

  private void populateScene(){
    this.sceneNode.removeAllChildren();
    Node page = builder.createNode("Page");
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
    this.recorder.recordFrame(this.pg);
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
