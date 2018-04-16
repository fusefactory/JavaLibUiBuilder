package com.fuse.ui.builder.example;

import com.fuse.cms.*;
import com.fuse.ui.extensions.*;

public class Configurator extends com.fuse.ui.builder.Configurator{
  public Configurator(ModelCollection configCollection){
    this.setDataCollection(configCollection);
  }

  public void cfg(MoverExt ext, String id){
    this.cfg(ext, this.getDataCollection().findById(id, true));
  }

  public void cfg(MoverExt ext, Model model){
    this.cfg((ExtensionBase)ext, model);

    this.apply(model, (ModelBase m) -> {
      m.withFloat("speed", (Float v) -> ext.setSpeed(v));
      m.withFloat("amplitude", (Float v) -> ext.setAmplitude(v));
    });
  }
}
