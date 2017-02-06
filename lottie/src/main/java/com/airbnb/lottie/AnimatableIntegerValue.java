package com.airbnb.lottie;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;

class AnimatableIntegerValue extends BaseAnimatableValue<Integer, Integer> {
  AnimatableIntegerValue(LottieComposition composition, Integer initialValue) {
    super(composition);
    this.initialValue = initialValue;
  }

  AnimatableIntegerValue(JsonReader reader, LottieComposition composition,
      boolean isDp, boolean remap100To255) throws IOException {
    super(reader, composition, isDp);
    if (remap100To255) {
      initialValue = initialValue * 255 / 100;
      for (int i = 0; i < keyframes.size(); i++) {
        Keyframe<Integer> keyframe = keyframes.get(i);
        keyframe.startValue = keyframe.startValue * 255 / 100;
        keyframe.endValue = keyframe.endValue * 255 / 100;
      }
    }
  }

  @Override public Integer valueFromObject(JsonReader reader, float scale) throws IOException {
    JsonToken token = reader.peek();

    if (token == JsonToken.BEGIN_ARRAY) {
      reader.beginArray();
      Integer value = null;
      while (reader.hasNext()) {
        if (value == null) {
          value = Math.round((float) (reader.nextDouble() * scale));
        } else {
          reader.skipValue();
        }
      }
      reader.endArray();
    } else if (token == JsonToken.NUMBER){
      return Math.round((float) (reader.nextDouble() * scale));
    }

    throw new IllegalArgumentException("Can't parse " + token + " into an integer.");
  }

  @Override public KeyframeAnimation<Integer> createAnimation() {
    if (!hasAnimation()) {
      return new StaticKeyframeAnimation<>(initialValue);
    }

    KeyframeAnimation<Integer> animation =
        new NumberKeyframeAnimation<>(getDuration(), composition, keyframes, Integer.class);
    animation.setStartDelay(getDelay());
    return animation;
  }

  public Integer getInitialValue() {
    return initialValue;
  }
}
