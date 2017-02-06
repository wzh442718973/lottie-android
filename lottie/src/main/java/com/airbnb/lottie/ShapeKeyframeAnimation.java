package com.airbnb.lottie;

import java.util.List;

class ShapeKeyframeAnimation extends KeyframeAnimation<ShapeData> {
  private final ShapeData tempShapeData = new ShapeData();

  ShapeKeyframeAnimation(long duration, LottieComposition composition,
      List<Keyframe<ShapeData>> keyframes) {
    super(duration, composition, keyframes);
  }

  @Override public ShapeData getValue(Keyframe<ShapeData> keyframe, float keyframeProgress) {
    ShapeData startShapeData = keyframe.startValue;
    ShapeData endShapeData = keyframe.endValue;

    tempShapeData.interpolateBetween(startShapeData, endShapeData, keyframeProgress);
    return tempShapeData;
  }
}
