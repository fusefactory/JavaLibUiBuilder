package com.fuse.ui.builder;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Ignore;

import processing.core.PVector;
import com.fuse.cms.Model;
import com.fuse.ui.*;

public class InstantiatorTest {
  @Test public void createNode_defaultSupportedTypes(){
    Instantiator inst = new Instantiator();

    Model m = new Model();
    assertEquals(Node.class.isInstance(inst.createNode(m)), true);

    m.set("type", "TextNode");
    assertEquals(TextNode.class.isInstance(inst.createNode(m)), true);
    m.set("type", "ImageNode");
    assertEquals(ImageNode.class.isInstance(inst.createNode(m)), true);
    m.set("type", "RectNode");
    assertEquals(RectNode.class.isInstance(inst.createNode(m)), true);
    m.set("type", "EventNode");
    assertEquals(EventNode.class.isInstance(inst.createNode(m)), true);
    m.set("type", "LambdaNode");
    assertEquals(LambdaNode.class.isInstance(inst.createNode(m)), true);

    m.set("type", "FooBarUbaUba");
    assertEquals(Node.class.isInstance(inst.createNode(m)), true);
  }

  @Test public void ceateNode_defaultAttributeConfigurations(){
    Instantiator inst = new Instantiator();

    Model m = new Model();
    assertEquals(inst.createNode(m).getPosition(), new PVector(0.0f, 0.0f, 0.0f));
    m.set("pos", "11");
    assertEquals(inst.createNode(m).getPosition(), new PVector(11.0f, 0.0f, 0.0f));
    m.set("pos", "11.1,22.2");
    assertEquals(inst.createNode(m).getPosition(), new PVector(11.1f, 22.2f, 0.0f));
    m.set("pos", "111.1,222.2,333.3");
    assertEquals(inst.createNode(m).getPosition(), new PVector(111.1f, 222.2f, 333.3f));

    m.set("size", "4,3,2");
    assertEquals(inst.createNode(m).getSize(), new PVector(4.0f,3.0f,2.0f));
    m.set("scale", "4,3,2");
    assertEquals(inst.createNode(m).getScale(), new PVector(4.0f,3.0f,2.0f));
    m.set("rot", "180,90,45");
      PVector v = inst.createNode(m).getRotation();
      v.mult(180.0f / (float)Math.PI);
      assertEquals(v, new PVector(180,90,45));
    m.set("rotateZ", "270");
      v = inst.createNode(m).getRotation();
      v.mult(180.0f / (float)Math.PI);
    assertEquals(v, new PVector(180.0f,90.0f,270.0f));

    m.set("plane", 101);
    assertEquals(inst.createNode(m).getPlane(), 101.0f, 0.000001f);

    m.set("name", "foo_bar");
    assertEquals(inst.createNode(m).getName(), "foo_bar");

    m.set("clipping", "true");
    assertEquals(inst.createNode(m).isClippingContent(), true);
    m.set("clipping", "false");
    assertEquals(inst.createNode(m).isClippingContent(), false);

    m.set("visible", "true");
    assertEquals(inst.createNode(m).isVisible(), true);
    m.set("visible", "false");
    assertEquals(inst.createNode(m).isVisible(), false);

    m.set("interactive", "true");
    assertEquals(inst.createNode(m).isInteractive(), true);
    m.set("interactive", "false");
    assertEquals(inst.createNode(m).isInteractive(), false);

    m.set("size", "100,10,0");
    m.set("sizeVar", "20,0");  // todo; applies some random variation to size

    // we'll run 100 times, because the variation is generated random
    float minX = 1000.0f;
    float maxX = -1000.0f;
    for(int i=0; i<100; i++){
      float x = inst.createNode(m).getSize().x;
      minX = Math.min(x, minX);
      maxX = Math.max(x, maxX);
    }

    assertEquals(minX, 82.0f, 2.0f); // from 80 (100.0 - 20.0)...
    assertEquals(maxX, 118.0f, 2.0f); // ... to 120.0 (100.0+20.0)
  }

  @Ignore @Test public void createNode_defaultExtensions(){
    // todo...
  }
}
