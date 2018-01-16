package com.fuse.ui.builder.example;

import com.fuse.cms.Model;
import com.fuse.cms.ModelCollection;
import com.fuse.ui.*;
import com.fuse.ui.builder.*;

public class Builder extends com.fuse.ui.builder.Builder {

  private Configurator configurator;

  public Builder(ModelCollection configs, Configurator configurator){
    super(configs, configurator);
    this.configurator = configurator;
    this.setDefaultNodesToNotInteractive(true);
    // register logic for every type we can instantiate/extend
    this.setup();
  }

  private void setup(){
    // this.setTypeInstantiator("MoverNode", (Model model) -> {
    //   MoverNode n = new MoverNode();
    //   this.configurator.cfg(n, model.getId());
    //   return n;
    // });

    this.setTypeExtender("MoverExt", (Node node, Model model) -> {
      MoverExt ext = MoverExt.createFor(node);
      this.configurator.cfg(ext, model.getId());
    });
  }
}
