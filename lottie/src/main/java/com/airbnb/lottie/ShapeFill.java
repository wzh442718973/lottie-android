package com.airbnb.lottie;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

class ShapeFill {
  private static final String TAG = ShapeFill.class.getSimpleName();
  private boolean fillEnabled;
  private AnimatableColorValue color;
  private AnimatableIntegerValue opacity;

  ShapeFill(JsonReader reader, LottieComposition composition) throws IOException {
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "c":
          color = new AnimatableColorValue(reader, composition);
          break;
        case "o":
          opacity = new AnimatableIntegerValue(reader, composition, false, true);
          break;
        case "fillEnabled":
          fillEnabled = reader.nextBoolean();
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();
  }

  public AnimatableColorValue getColor() {
    return color;
  }

  public AnimatableIntegerValue getOpacity() {
    return opacity;
  }

  @Override
  public String toString() {
    return "ShapeFill{" + "color=" + Integer.toHexString(color.getInitialValue()) +
        ", fillEnabled=" + fillEnabled +
        ", opacity=" + opacity.getInitialValue() +
        '}';
  }
}
