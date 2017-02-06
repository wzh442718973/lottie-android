package com.airbnb.lottie;

import android.support.annotation.Nullable;
import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ShapeGroup {
  @Nullable
  static Object parseShapeItem(JsonReader reader, LottieComposition composition)
      throws IOException{
    String type = null;
    reader.beginObject();
    while (reader.hasNext()) {
      if (reader.nextName().equals("ty")) {
        type = reader.nextString();
      } else {
        reader.skipValue();;
      }
    }
    reader.endObject();
    if (type == null) {
      throw new IllegalStateException("Shape has no type.");
    }

    switch (type) {
      case "gr":
        return new ShapeGroup(reader, composition);
      case "st":
        return new ShapeStroke(reader, composition);
      case "fl":
        return new ShapeFill(reader, composition);
      case "tr":
        return new ShapeTransform(reader, composition);
      case "sh":
        return new ShapePath(reader);
      case "el":
        // TODO (jsonreader)
        return new CircleShape(reader, composition);
      case "rc":
        return new RectangleShape(reader, composition);
      case "tm":
        return new ShapeTrimPath(reader, composition);
    }
    return null;
  }

  private String name;
  private final List<Object> items = new ArrayList<>();

  private ShapeGroup(JsonReader reader, LottieComposition composition) throws IOException {
    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.nextName()) {
        case "it":
          reader.beginArray();
          while (reader.hasNext()) {
            items.add(parseShapeItem(reader, composition));
          }
          reader.endArray();
          break;
        case "nm":
          name = reader.nextString();
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();
  }

  List<Object> getItems() {
    return items;
  }

  @Override
  public String toString() {
    return "ShapeGroup{" + "name='" + name + "\' Shapes: " + Arrays.toString(items.toArray()) + '}';
  }
}
