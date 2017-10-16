package com.fuse.ui.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.function.Consumer;
import org.junit.Test;
import org.junit.Ignore;

import processing.core.PVector;
import com.fuse.cms.Model;
import com.fuse.cms.ModelBase;
import com.fuse.ui.*;


public class ConfiguratorTest {

  @Test public void Node(){
    Configurator c = new Configurator();
    c.getDataCollection().loadJsonFromFile("testdata/ConfiguratorTest.json");

    Node n = new Node();
    assertEquals(n.getPosition(), new PVector(0.0f, 0.0f, 0.0f));
    c.cfg(n, "Node.1");
    assertEquals(n.getPosition(), new PVector(10.0f, 20.0f, 0.0f));
  }

  @Test public void TextNode(){
    Configurator c = new Configurator();
    c.getDataCollection().loadJsonFromFile("testdata/ConfiguratorTest.json");

    TextNode n = new TextNode();
    assertEquals(n.getText(), "");
    c.cfg(n, "TextNode.1");
    assertEquals(n.getText(), "foo_bar");
  }

  @Test public void Node_percentageSize(){
    Configurator c = new Configurator();
    c.getDataCollection().loadJsonFromFile("testdata/ConfiguratorTest.json");

    Node parent = new Node();
    parent.setSize(800,600);
    Node n = new Node();
    parent.addChild(n);

    Model m = new Model();
    m.set("width", "80%");
    m.set("height", "50%");
    c.cfg(n, m);
    assertEquals(n.getSize(), new PVector(640.0f, 300.0f, 0.0f));
    parent.setSize(1920,1080);
    assertEquals(n.getSize(), new PVector(1536.0f, 540.0f, 0.0f));

    Node newParent = new Node();
    newParent.setSize(1000,700);
    newParent.addChild(n);
    assertEquals(n.getSize(), new PVector(800.0f, 350.0f, 0.0f));
  }

  @Ignore @Test public void activeTransformations_testLambdaRescycling(){
    class CustomConfigurator extends Configurator {
      public int counter1 = 0;
      public int counter2 = 0;

      @Override
      public void cfg(Node n, Model mod){
        this.apply(mod, (ModelBase m) -> {
          // System.out.println("apply1");
          this.counter1 += 1;
        });

        this.apply(mod, (ModelBase m) -> {
          // System.out.println("apply2");
          this.counter2 += 1;
        });
      }
    }

    CustomConfigurator c = new CustomConfigurator();
    c.getDataCollection().loadJsonFromFile("testdata/ConfiguratorTest.json");
    c.setUseActiveTransformations(true);

    assertEquals(c.counter1, 0);
    assertEquals(c.counter2, 0);
    assertEquals(c.getDataCollection().size(), 2);

    Node n = new Node();
    c.cfg(n, "FooBar");
    assertEquals(c.getDataCollection().size(), 3);

    assertEquals(c.counter2, 1);
    assertEquals(c.counter1, 1);

    c.getDataCollection().findById("FooBar").set("attr1", "value1");

    assertEquals(c.counter2, 2);
    assertEquals(c.counter1, 2);

    c.cfg(n, "FooBar");

    assertEquals(c.counter2, 3);
    assertEquals(c.counter1, 3);

    c.getDataCollection().findById("FooBar").set("attr1", "value2");

    assertEquals(c.counter2, 4); // => 5 because when active, every time a new tranformer is regisrered ON TOP of existing.
    assertEquals(c.counter1, 4);
  }

  // private Consumer<Integer> getget(Model m){
  //   return ((Integer i) -> System.out.println(Integer.toString(i*m.getInt("mult"))));
  // }
  //
  // @Test public void test_Test(){
  //   Model m = new Model();
  //   Consumer<Integer> c1 = getget(m);
  //   Consumer<Integer> c2 = getget(m);
  //   assertEquals(c1,c2);
  // }

}
