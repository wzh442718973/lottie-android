package com.airbnb.lottie;

import android.util.JsonReader;

import org.json.JSONException;

import java.io.IOException;

class Mask {
  private enum MaskMode {
    MaskModeAdd,
    MaskModeSubtract,
    MaskModeIntersect,
    MaskModeUnknown;

    private static MaskMode forJsonKey(String key) {
      switch (key) {
        case "a":
          return MaskMode.MaskModeAdd;
        case "s":
          return MaskMode.MaskModeSubtract;
        case "i":
          return MaskMode.MaskModeIntersect;
        default:
          return MaskMode.MaskModeUnknown;
      }
    }
  }

  private MaskMode maskMode;
  private AnimatableShapeValue maskPath;

  Mask(JsonReader reader, LottieComposition composition) throws IOException {
    boolean closed = false;
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "cl":
          closed = true;
          break;
        case "mode":
          maskMode = MaskMode.forJsonKey(reader.nextString());
          break;
        case"pt":
          maskPath = new AnimatableShapeValue(reader, composition, closed);
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();
  }


  MaskMode getMaskMode() {
    return maskMode;
  }

  AnimatableShapeValue getMaskPath() {
    return maskPath;
  }
}
