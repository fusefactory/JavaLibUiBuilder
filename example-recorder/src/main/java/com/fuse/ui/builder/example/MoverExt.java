package com.fuse.ui.builder.example;

import processing.core.PVector;
import com.fuse.ui.extensions.ExtensionBase;
import com.fuse.ui.Node;
// import com.fuse.anim.AnimatableBase;

public class MoverExt extends ExtensionBase {
  private PVector origin = null;
  private float time = 0.0f;
  private float speed = 1.0f;
  private float amplitude = 100.0f;

  @Override
  protected void setup(){
    System.out.println("MoverExt setup: "+node.getPosition().toString());
  }

  @Override
  protected void teardown(){

  }

  @Override
  public void update(float dt){
    if(this.origin == null)
      this.origin = this.node.getPosition();

    this.time += dt;
    PVector pos = new PVector((float)Math.sin(this.time * this.speed) * this.amplitude, 0.0f, 0.0f);
    pos.add(this.origin);
    this.node.setPosition(pos);
  }

  public void setSpeed(float speed) { this.speed = speed; }
  public void setAmplitude(float amp) { this.amplitude = amp; }
  // static factory methods // // // // //

  public static MoverExt createFor(Node n){
    MoverExt ext = new MoverExt();
    n.use(ext);
    return ext;
  }
}
