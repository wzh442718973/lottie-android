package com.airbnb.lottie;

import android.graphics.PointF;

import java.util.Collections;

class SplitDimensionPathKeyframeAnimation extends KeyframeAnimation<PointF> {
  private final PointF point = new PointF();
  private final KeyframeAnimation<Float> xAnimation;
  private final KeyframeAnimation<Float> yAnimation;

  SplitDimensionPathKeyframeAnimation(KeyframeAnimation<Float> xAnimation,
      KeyframeAnimation<Float> yAnimation) {
    super(0, null, Collections.<Keyframe<PointF>>emptyList());

    this.xAnimation = xAnimation;
    this.yAnimation = yAnimation;
  }

  @Override void setProgress(float progress) {
    xAnimation.setProgress(progress);
    yAnimation.setProgress(progress);
    // The keyframe and progress will be pulled directly from the x and y animations.
    point.set(xAnimation.getValue(null, 0), yAnimation.getValue(null, 0));
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).onValueChanged(point);
    }
  }

  @Override PointF getValue(Keyframe<PointF> keyframe, float keyframeProgress) {
    int xKeyframeIndex = xAnimation.getCurrentKeyframeIndex();
    Keyframe<Float> xKeyframe = xAnimation.keyframes.get(xKeyframeIndex);
    float xKeyframeProgress = yAnimation.getCurrentKeyframeProgress();
    int yKeyframeIndex = yAnimation.getCurrentKeyframeIndex();
    Keyframe<Float> yKeyframe = yAnimation.keyframes.get(yKeyframeIndex);
    float yKeyframeProgress = yAnimation.getCurrentKeyframeProgress();

    point.set(xAnimation.getValue(xKeyframe, xKeyframeProgress),
        yAnimation.getValue(yKeyframe, yKeyframeProgress));
    return point;
  }
}
