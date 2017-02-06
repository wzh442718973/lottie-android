package com.airbnb.lottie;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.JsonReader;

import java.io.IOException;

class PathKeyframe extends Keyframe<PointF> {

  private final Path path;

  PathKeyframe(JsonReader reader, PathValueDeserializer valueDeserializer)
      throws IOException {
    super(reader, 1f, valueDeserializer);
    path = valueDeserializer.createPath(startValue, endValue);
  }

  Path getPath() {
    return path;
  }
}
