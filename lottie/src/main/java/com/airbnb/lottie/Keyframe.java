package com.airbnb.lottie;

import android.graphics.PointF;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.JsonReader;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Keyframe<T> {
  private static Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

  static <T> List<Keyframe<T>> parseKeyframes(JsonReader reader, float scale,
      AnimatableValueDeserializer<T> valueDeserializer) throws IOException {
    List<Keyframe<T>> keyframes = new ArrayList<>();
    reader.beginArray();
    while (reader.hasNext()) {
      keyframes.add(new Keyframe<>(reader, scale, valueDeserializer));
    }
    reader.endArray();
    if (keyframes.isEmpty()) {
      return keyframes;
    } else if (keyframes.size() == 1) {
      throw new IllegalArgumentException("There must be at least 2 keyframes. One value keyframe " +
          "and one end keyframe.");
    }

    Keyframe<T> lastKeyframe = keyframes.get(keyframes.size() - 1);
    if (lastKeyframe.startValue != null || lastKeyframe.endValue != null) {
      throw new IllegalArgumentException("Last keyframe contains values. It should only contain " +
          "the ending frame");
    }

    // Set the end value for keyframes with missing end values to the start of the next keyframe.
    // Also set the end frame to the start frame of the next keyframe.
    for (int i = 0; i < keyframes.size() - 1; i++) {
      Keyframe<T> keyframe = keyframes.get(i);
      if (keyframe.endValue == null) {
        keyframe.endValue = keyframes.get(i + 1).startValue;
      }
      if (keyframe != lastKeyframe) {
        keyframe.endFrame = keyframes.get(i + 1).startFrame;
      }
    }
    keyframes.remove(lastKeyframe);

    return keyframes;
  }

  T startValue;
  T endValue;
  long startFrame;
  long endFrame;
  Interpolator timingFunction;

  Keyframe(JsonReader reader, float scale, AnimatableValueDeserializer<T> valueDeserializer)
      throws IOException {
    PointF cp1 = null;
    PointF cp2 = null;
    boolean hold = false;
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "t":
          startFrame = reader.nextLong();
          break;
        case "s":
          startValue = valueDeserializer.valueFromObject(reader, scale);
          break;
        case "e":
          endValue = valueDeserializer.valueFromObject(reader, scale);
          break;
        case "o":
          cp1 = JsonUtils.pointValueFromJsonObject(reader);
          break;
        case "i":
          cp2 = JsonUtils.pointValueFromJsonObject(reader);
          break;
        case "h":
          if (reader.nextInt() == 1) {
            hold = true;
          }
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();

    if (hold) {
      endValue = startValue;
      // TODO: create a HoldInterpolator so progress changes don't invalidate.
      timingFunction = LINEAR_INTERPOLATOR;
    } else if (cp1 == null || cp2 == null) {
      timingFunction = LINEAR_INTERPOLATOR;
    } else {
      timingFunction = PathInterpolatorCompat.create(cp1.x, cp1.y, cp2.x, cp2.y);
    }
  }

  @Override public String toString() {
    return "Keyframe{" + "startValue=" + startValue +
        ", endValue=" + endValue +
        ", startFrame=" + startFrame +
        ", timingFunction=" + timingFunction +
        '}';
  }
}
