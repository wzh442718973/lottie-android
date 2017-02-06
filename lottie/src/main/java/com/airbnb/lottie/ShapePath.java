package com.airbnb.lottie;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

class ShapePath {
  private static final String TAG = ShapePath.class.getSimpleName();

  private String name;
  private int index;
  private AnimatableShapeValue shapePath;

  ShapePath(JsonReader reader, LottieComposition composition) throws IOException {
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "ind":
          index = reader.nextInt();
          break;
        case "nm":
          name = reader.nextString();
          break;
        case "closed":
          // TODO (jsonreader)

      }
    }
    reader.endObject();

    boolean closed = false;
    try {
      closed = json.getBoolean("closed");
    } catch (JSONException e) {
      // Do nothing. Bodymovin 4.4 moved "closed" to be "c" inside of the shape json itself.
    }

    JSONObject shape;
    try {
      shape = json.getJSONObject("ks");
      shapePath = new AnimatableShapeValue(shape, composition, closed);
    } catch (JSONException e) {
      // Ignore
    }

    if (L.DBG) {
      Log.d(TAG, "Parsed new shape path " + toString());
    }
  }

  AnimatableShapeValue getShapePath() {
    return shapePath;
  }

  @Override public String toString() {
    return "ShapePath{" + "name=" + name +
        ", index=" + index +
        ", hasAnimation=" + shapePath.hasAnimation() +
        '}';
  }
}
