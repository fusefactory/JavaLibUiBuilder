package com.fuse.ui.builder;

import com.fuse.ui.Node;
import com.fuse.ui.extensions.ExtensionBase;
import com.fuse.ui.extensions.TransformerExtension;

public class ParentRelativeTransformer extends TransformerExtension {

  private Float sizeFactorX,sizeFactorY,sizeFactorZ;

  public ParentRelativeTransformer(){
    this.setSmoothValue(0.0f); // disable smoothing by default
  }

  public ParentRelativeTransformer setSizeFactorX(Float factor){
    this.sizeFactorX = factor; return this;
  }

  public ParentRelativeTransformer setSizeFactorY(Float factor){
    this.sizeFactorY = factor; return this;
  }

  public ParentRelativeTransformer setSizeFactorZ(Float factor){
    this.sizeFactorZ = factor; return this;
  }

  // static factory methods // // // // //

  public static ParentRelativeTransformer enableFor(Node n){
    ParentRelativeTransformer d = getFor(n);

    if(d == null){
      d = new ParentRelativeTransformer();
      n.use(d);
    }
    return d;
  }

  public static ParentRelativeTransformer getFor(Node n){
    for(ExtensionBase ext : n.getExtensions())
      if(ParentRelativeTransformer.class.isInstance(ext))
        return (ParentRelativeTransformer)ext;
    return null;
  }

  public static void disableFor(Node n){
    for(ExtensionBase ext : n.getExtensions()) {
      if(ParentRelativeTransformer.class.isInstance(ext))
          n.stopUsing(ext);
    }
  }
}
