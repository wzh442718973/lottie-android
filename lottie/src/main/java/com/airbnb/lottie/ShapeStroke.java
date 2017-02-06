package com.airbnb.lottie;

import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ShapeStroke {
  enum LineCapType {
    Butt,
    Round,
    Unknown
  }

  enum LineJoinType {
    Miter,
    Round,
    Bevel
  }

  private AnimatableFloatValue offset;
  private final List<AnimatableFloatValue> lineDashPattern = new ArrayList<>();

  private AnimatableColorValue color;
  private AnimatableIntegerValue opacity;
  private AnimatableFloatValue width;
  private LineCapType capType;
  private LineJoinType joinType;

  ShapeStroke(JsonReader reader, LottieComposition composition) throws IOException {
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "c":
          color = new AnimatableColorValue(reader, composition);
          break;
        case "w":
          width = new AnimatableFloatValue(reader, composition);
          break;
        case "o":
          opacity = new AnimatableIntegerValue(reader, composition, false, true);
          break;
        case "lc":
          capType = LineCapType.values()[reader.nextInt() - 1];
          break;
        case "lj":
          joinType = LineJoinType.values()[reader.nextInt() - 1];
          break;
        case "d":
          parseDashPattern(reader, composition);
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();
  }

  private void parseDashPattern(JsonReader reader, LottieComposition composition) throws IOException {
    reader.beginArray();
    while (reader.hasNext()) {
      String n = null;
      AnimatableFloatValue value = null;
      reader.beginObject();
      while (reader.hasNext()) {
        switch (reader.nextName()) {
          case "n":
            n = reader.nextString();
            break;
          case "v":
            value = new AnimatableFloatValue(reader, composition);
            break;
          default:
            reader.skipValue();
        }
      }
      reader.endObject();

      if (n == null) {
        throw new IllegalArgumentException("Unknown dash pattern type.");
      }
      if (value == null) {
        throw new IllegalStateException("Unknown dash pattern value.");
      }

      if (n.equals("o")) {
        offset = value;
      } else if (n.equals("d") || n.equals("g")) {
        lineDashPattern.add(value);
      }
    }
    reader.endArray();

    if (lineDashPattern.size() == 1) {
      // If there is only 1 value then it is assumed to be equal parts on and off.
      lineDashPattern.add(lineDashPattern.get(0));
    }
  }

  AnimatableColorValue getColor() {
    return color;
  }

  AnimatableIntegerValue getOpacity() {
    return opacity;
  }

  AnimatableFloatValue getWidth() {
    return width;
  }

  List<AnimatableFloatValue> getLineDashPattern() {
    return lineDashPattern;
  }

  AnimatableFloatValue getDashOffset() {
    return offset;
  }

  LineCapType getCapType() {
    return capType;
  }

  LineJoinType getJoinType() {
    return joinType;
  }
}
