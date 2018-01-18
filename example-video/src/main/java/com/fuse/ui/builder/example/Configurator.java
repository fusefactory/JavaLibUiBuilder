package com.fuse.ui.builder.example;

import processing.video.Movie;
import com.fuse.cms.*;
import com.fuse.ui.*;
import com.fuse.ui.extensions.*;

public class Configurator extends com.fuse.ui.builder.Configurator{

  private AsyncFacade<String, Movie> movieAsyncFacade = null;

  public Configurator(ModelCollection configCollection, AsyncFacade<String, Movie> movieAsyncFacade){
    this.movieAsyncFacade = movieAsyncFacade;
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

  public void cfg(MovieNode n, String id){
    this.cfg(n, this.getDataCollection().findById(id, true));
  }

  public void cfg(MovieNode node, Model model){
    this.cfg((Node)node, model);

    this.apply(model, (ModelBase m) -> {
      m.withBool("autoResize", (Boolean v) -> node.setAutoResizeToMovie(v));
      m.withBool("autoStart", (Boolean v) -> node.setAutoStart(v));
      withColor(m, "tint", (Integer v) -> node.setTint(v));

      m.with("mode", (String v) -> {
        String mode = v.toUpperCase();
        if(mode.equals("CENTER")) node.setMode(MovieNode.Mode.CENTER);
        if(mode.equals("NORMAL")) node.setMode(MovieNode.Mode.NORMAL);
        if(mode.equals("FIT")) node.setMode(MovieNode.Mode.FIT);
        if(mode.equals("FIT_CENTERED")) node.setMode(MovieNode.Mode.FIT_CENTERED);
      });

      m.with("movie", (String v) -> {
        if(movieAsyncFacade == null){
          System.err.println("movieAsyncFacade == null; can't load movie for Node: "+node.getName());
          return;
        }

        AsyncOperation<Movie> op = movieAsyncFacade.getAsync(v);

        if(op == null){
          System.err.println("got null movie async operation");
          return;
        }

        op.withSingleResult((Movie loadedItem) -> {
          node.setMovie(loadedItem);
        });
      });
    });
  }
}
