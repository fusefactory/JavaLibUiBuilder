package com.fuse.ui.builder.example;

import processing.core.PApplet;
import processing.core.PVector;
import processing.video.Movie;
// import ddf.minim.AudioPlayer; // for separate audio tracks
import com.fuse.ui.Node;
import com.fuse.utils.Event;

class AudioPlayer {
  public void pause(){}
  public void play(){}
  public void close(){}
  public double length(){ return 0.0; }
  public void cue(int foo){}
}

public class MovieNode extends Node {

  public enum Mode {
    NORMAL, // image rendered at original size at Node's origin (0,0) position
    CENTER, // image rendered at original size centered inside the node
    FIT, // image stretched into the dimensions of the Node
    FIT_CENTERED // image stretched within original aspect ratio and centered
  }

  private Movie movie = null;
  private AudioPlayer audioPlayer = null;

  private Mode mode = Mode.NORMAL;
  private boolean autoResizeToMovie = false;
  private Integer tintColor = null;
  private boolean bAutoStart = false;
  private boolean bAutoStarted = false;
  private boolean bPaused = false;

  public Event<MovieNode> autoStartEvent;

  /** Default constructor; intialized with default values: movie=null and mode=NORMAL */
  public MovieNode(){
    super();
    autoStartEvent = new Event<>();
  }

  public MovieNode(String nodeName){
    super(nodeName);
    autoStartEvent = new Event<>();
  }

  @Override
  public void destroy(){
    super.destroy();
    autoStartEvent.destroy();

    if(this.movie!=null){
      if(this.movie.playbin != null) {
      // if(this.movie.isLoaded()) {
        this.movie.stop();
        this.movie.dispose();
      }
      this.movie = null;
    }

    if(this.audioPlayer!=null){
      this.audioPlayer.pause();
      this.audioPlayer.close();
      this.audioPlayer = null;
    }
  }

  @Override public void update(float dt){
    super.update(dt);
    // this actually causes _some_ videos to stay black...
    // if(movie != null) && movie.available())
    //    movie.read();
    if(bAutoStart && (!bAutoStarted) ) {
      this.autoStart();
    }

    if(bPaused) {
    		if(this.movie != null) this.movie.pause(); // no way to read paused state from movie, we'll just keep setting it to paused?
    		if(this.audioPlayer!=null) this.audioPlayer.pause();
    }
  }

  /** Draw this node's movie at this Node's position */
  @Override public void draw(){
    if(movie == null)
    return;

    if(tintColor != null)
    pg.tint(tintColor);

    if(mode == Mode.NORMAL){
      pg.image(movie, 0.0f, 0.0f);
    } else if(mode == Mode.CENTER){
      PVector pos = PVector.mult(getSize(), 0.5f);
      pg.imageMode(PApplet.CENTER);
      pg.image(movie, pos.x, pos.y);
      pg.imageMode(PApplet.CORNERS); // restore default
    } else if(mode == Mode.FIT){
      pg.imageMode(PApplet.CORNERS);
      pg.image(movie, 0.0f, 0.0f, getSize().x, getSize().y);
    } else if(mode == Mode.FIT_CENTERED){
      //if(fitCenteredSize == null)
      PVector fitCenteredSize = calculateFitCenteredSize();
      PVector pos = PVector.mult(getSize(), 0.5f);
      pg.imageMode(PApplet.CENTER);
      pg.image(movie, pos.x, pos.y, fitCenteredSize.x, fitCenteredSize.y);
      pg.imageMode(PApplet.CORNERS); // restore default
    }

    if(tintColor != null)
    pg.noTint();
  }

  /**
  * Set/change the movie of this node.
  * @param newMovie The movie that should from now on be rendered by this node
  */
  public void setMovie(Movie newMovie){
    this.setMovie(newMovie, null);
  }

  public void setMovie(Movie newMovie, AudioPlayer audioPlayer){
    this.movie = newMovie;
    this.audioPlayer = audioPlayer;

    if(this.movie == null)
    return; // done

    if(this.audioPlayer != null) {
      this.movie.volume(0.0f);
    }

    if(autoResizeToMovie)
    setSize(movie.width, movie.height);

    if(bAutoStart) {
      this.autoStart();
    }
  }

  private void autoStart() {
    if(bAutoStarted || movie == null || movie.playbin == null || movie.playbin.isPlaying())
    return;

    bAutoStarted = true; // don't keep starting; user might want to pause it at some point
    movie.play();
    if(this.audioPlayer != null) {
      this.audioPlayer.play();
    }

    autoStartEvent.trigger(this);
  }

  /** @return PImage The movie that this node is rendering */
  public Movie getMovie(){ return movie; }

  public Mode getMode(){ return mode; }
  public void setMode(Mode newMode){ mode = newMode; }

  public boolean getAutoResizeToMovie(){ return autoResizeToMovie; }
  public void setAutoResizeToMovie(boolean enable){
    autoResizeToMovie = enable;
    if(autoResizeToMovie && movie != null){
      setSize(movie.width, movie.height);
    }
  }

  public void setTint(Integer clr){ tintColor = clr; }
  public Integer getTint(){ return tintColor; }

  public boolean getAutoStart(){ return bAutoStart; }
  public void setAutoStart(boolean enable){
    bAutoStart = enable;
    bAutoStarted = false;

    if(bAutoStart) {
      this.autoStart();
    }
  }

  private PVector calculateFitCenteredSize(){
    if(movie == null) return new PVector(0.0f,0.0f,0.0f);

    float w = getSize().x / movie.width;
    float h = getSize().y / movie.height;
    if(w > h){
      w = h * movie.width;
      h = h * movie.height;
    } else {
      h = w * movie.height;
      w = w * movie.width;
    }

    return new PVector(w,h,0.0f);
  }

  public void setPaused(boolean paused) {
    if(this.movie == null){
      return;
    }

    this.bPaused = paused;

    if(paused) {
      this.movie.pause();
      if(this.audioPlayer != null) this.audioPlayer.pause();
    	} else {
      try {
        //if(this.movie.available() || this.moviei) {
        this.movie.play();
        if(this.audioPlayer != null) this.audioPlayer.play();
        // }
      } catch(java.lang.NullPointerException exc) { // in case movie isn't fully initialized yet
    	  	System.err.println("movie exc: "+exc.toString());
    	  	exc.printStackTrace();
      }
  }
}

public void jumpTo(float percentage) {
  this.jumpTo(percentage, true);
}

public void jumpTo(float percentage, boolean andPlay) {
  if(this.audioPlayer != null){
    this.audioPlayer.cue((int)(((float)this.audioPlayer.length())*percentage));
    this.audioPlayer.pause();
  }

  try{
    movie.jump(percentage * movie.duration());

    if(andPlay) {
      if(this.audioPlayer != null){
        this.audioPlayer.play();
      }
    } else {
      movie.pause();
    }
  } catch(java.lang.NullPointerException exc) { // in case movie isn't fully initialized yet
  System.err.println("movie exc: "+exc.toString());
  exc.printStackTrace();
}
}
}
