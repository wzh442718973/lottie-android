package com.airbnb.lottie;

import android.graphics.PointF;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class AnimatablePathValue implements IAnimatablePathValue {

  static IAnimatablePathValue createAnimatablePathOrSplitDimensionPath(
      JsonReader reader, LottieComposition composition) throws IOException {
    IAnimatablePathValue animatablePathValue = null;
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "k":
          animatablePathValue = new AnimatablePathValue(reader, composition);
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();

    if (animatablePathValue == null) {
      animatablePathValue = new AnimatableSplitDimensionPathValue(reader, composition);
    }

    return animatablePathValue;
  }

  private final List<PathKeyframe> keyframes = new ArrayList<>();
  private PointF initialPoint;
  private final LottieComposition composition;

  /**
   * Create a default static animatable path.
   */
  AnimatablePathValue(LottieComposition composition) {
    this.composition = composition;
    this.initialPoint = new PointF(0, 0);
  }

  private AnimatablePathValue(JsonReader reader, LottieComposition composition)
      throws IOException {
    this.composition = composition;

    boolean foundValue = false;
    reader.beginObject();
    while (reader.hasNext()) {
      if (reader.nextName().equals("k") && !foundValue) {
        foundValue = true;
        setupAnimationForValue(reader);
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();

    if (!foundValue) {
      throw new IllegalArgumentException("Point values have no keyframes.");
    }
  }

  private void setupAnimationForValue(JsonReader reader) throws IOException {
    JsonToken token = reader.peek();

    if (token == JsonToken.BEGIN_ARRAY) {
      buildAnimationForKeyframes(reader);
    } else {
      initialPoint = JsonUtils.pointFromJsonArray(reader, composition.getScale());
    }
  }

  @SuppressWarnings("Duplicates")
  private void buildAnimationForKeyframes(JsonReader reader) throws IOException {
    PointF currentPoint = new PointF();
    PathValueDeserializer deserializer = new PathValueDeserializer();
    reader.beginArray();
    while (reader.hasNext()) {
      deserializer.reset(currentPoint);
      PathKeyframe keyframe = new PathKeyframe(reader, deserializer);
      keyframes.add(keyframe);
      deserializer.createPath(keyframe.startValue, keyframe.endValue);
    }
    reader.endArray();
  }

  private long getDurationFrames() {
    if (keyframes.isEmpty()) {
      throw new IllegalStateException("There are no keyframes.");
    }
    return keyframes.get(keyframes.size() - 1).startFrame - keyframes.get(0).startFrame;
  }

  long getDuration() {
    return (long) (getDurationFrames() / (float) composition.getFrameRate() * 1000);
  }

  long getDelay() {
    if (keyframes.isEmpty()) {
      return 0;
    }
    return (long) (keyframes.get(0).startFrame / (float) composition.getFrameRate() * 1000);
  }

  @Override
  public KeyframeAnimation<PointF> createAnimation() {
    if (!hasAnimation()) {
      return new StaticKeyframeAnimation<>(initialPoint);
    }

    KeyframeAnimation<PointF> animation =
        new PathKeyframeAnimation(getDuration(), composition, keyTimes, animationPath, interpolators);
    animation.setStartDelay(getDelay());
    return animation;
  }

  @Override
  public boolean hasAnimation() {
    return !keyframes.isEmpty();
  }

  @Override
  public PointF getInitialPoint() {
    return initialPoint;
  }

  @Override
  public String toString() {
    return "initialPoint=" + initialPoint;
  }
}
