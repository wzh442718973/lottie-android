package com.airbnb.lottie;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;

public class PathValueDeserializer implements AnimatableValueDeserializer<PointF> {

  private boolean firstPoint = true;
  private final PointF initialPoint = new PointF();
  private PointF cp1 = null;
  private PointF cp2 = null;

  @Override public PointF valueFromObject(JsonReader reader, float scale) throws IOException {
    JsonToken token = reader.peek();

    if (token == JsonToken.BEGIN_ARRAY) {
      return JsonUtils.pointFromJsonArray(reader);
    } else if (token == JsonToken.BEGIN_OBJECT) {
      PointF endPoint = null;
      reader.beginObject();
      while (reader.hasNext()) {
        switch (reader.nextName()) {
          case "to":
            cp1 = JsonUtils.pointFromJsonArray(reader, scale);
            break;
          case "ti":
            cp2 = JsonUtils.pointFromJsonArray(reader, scale);
            break;
          case "e":
            endPoint = JsonUtils.pointFromJsonArray(reader, scale);
            break;
          default:
            reader.skipValue();
        }
      }
      reader.endObject();
      return endPoint;
    } else {
      throw new IllegalArgumentException("Unknown token for path keyframe value " + token);
    }
  }

  Path createPath(PointF startPoint, PointF endPoint) {
    Path path = new Path();
    if (firstPoint) {
      path.moveTo(initialPoint.x, initialPoint.y);
      firstPoint = false;
    } else {
      path.lineTo(initialPoint.x, initialPoint.y);
    }

    if (cp1 != null && cp1.length() != 0 && cp2 != null && cp2.length() != 0) {
      path.cubicTo(
          startPoint.x + cp1.x, startPoint.y + cp1.y,
          endPoint.x + cp2.x, endPoint.y + cp2.y,
          endPoint.x, endPoint.y);
    } else {
      path.lineTo(endPoint.x, endPoint.y);
    }
    return path;
  }

  void reset(PointF initialPoint) {
    this.initialPoint.set(initialPoint.x, initialPoint.y);
    cp1 = null;
    cp2 = null;
  }


}
