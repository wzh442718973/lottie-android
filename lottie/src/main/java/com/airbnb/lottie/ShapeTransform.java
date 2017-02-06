package com.airbnb.lottie;

import android.graphics.Rect;
import android.util.JsonReader;

import java.io.IOException;

class ShapeTransform implements Transform {

  private LottieComposition composition;
  private IAnimatablePathValue position;
  private IAnimatablePathValue anchor;
  private AnimatableScaleValue scale;
  private AnimatableFloatValue rotation;
  private AnimatableIntegerValue opacity;

  ShapeTransform(LottieComposition composition) {
    this.composition = composition;
    this.position = new AnimatablePathValue(composition);
    this.anchor = new AnimatablePathValue(composition);
    this.scale = new AnimatableScaleValue(composition);
    this.rotation = new AnimatableFloatValue(composition, 0f);
    this.opacity = new AnimatableIntegerValue(composition, 255);
  }

  ShapeTransform(JsonReader reader, LottieComposition composition) throws IOException {
    this.composition = composition;

    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "p":
          position =
              AnimatablePathValue.createAnimatablePathOrSplitDimensionPath(reader, composition);
          break;
        case "a":
          anchor =
              AnimatablePathValue.createAnimatablePathOrSplitDimensionPath(reader, composition);
          break;
        case "s":
          scale = new AnimatableScaleValue(reader, composition, false);
          break;
        case "r":
        case "rz":
          rotation = new AnimatableFloatValue(reader, composition, false);
          break;
        case "o":
          opacity = new AnimatableIntegerValue(reader, composition, false, true);

      }
    }
    reader.endObject();
  }

  @Override public Rect getBounds() {
    return composition.getBounds();
  }

  @Override public IAnimatablePathValue getPosition() {
    return position;
  }

  @Override public IAnimatablePathValue getAnchor() {
    return anchor;
  }

  @Override public AnimatableScaleValue getScale() {
    return scale;
  }

  @Override public AnimatableFloatValue getRotation() {
    return rotation;
  }

  @Override public AnimatableIntegerValue getOpacity() {
    return opacity;
  }

  @Override public String toString() {
    return "ShapeTransform{" + "anchor=" + anchor.toString() +
        ", position=" + position.toString() +
        ", scale=" + scale.toString() +
        ", rotation=" + rotation.getInitialValue() +
        ", opacity=" + opacity.getInitialValue() +
        '}';
  }
}
