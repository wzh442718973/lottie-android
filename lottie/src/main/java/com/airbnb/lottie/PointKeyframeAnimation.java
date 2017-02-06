package com.airbnb.lottie;

import android.graphics.PointF;

import java.util.List;

class PointKeyframeAnimation extends KeyframeAnimation<PointF> {
  private final PointF point = new PointF();

  PointKeyframeAnimation(long duration, LottieComposition composition,
      List<Keyframe<PointF>> keyframes) {
    super(duration, composition, keyframes);
  }

  @Override public PointF getValue(Keyframe<PointF> keyframe, float keyframeProgress) {
    PointF startPoint = keyframe.startValue;
    PointF endPoint = keyframe.endValue;

    point.set(startPoint.x + keyframeProgress * (endPoint.x - startPoint.x),
        startPoint.y + keyframeProgress * (endPoint.y - startPoint.y));
    return point;
  }
}
