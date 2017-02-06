package com.airbnb.lottie;

import android.graphics.PathMeasure;
import android.graphics.PointF;

import java.util.List;

class PathKeyframeAnimation extends KeyframeAnimation<PointF> {
  private final PointF point = new PointF();
  private final float[] pos = new float[2];
  private PathKeyframe pathMeasureKeyframe;
  private PathMeasure pathMeasure;

  PathKeyframeAnimation(long duration, LottieComposition composition,
      List<Keyframe<PointF>> keyframes) {
    super(duration, composition, keyframes);
  }

  @Override public PointF getValue(Keyframe<PointF> keyframe, float keyframeProgress) {
    PathKeyframe pathKeyframe = (PathKeyframe) keyframe;
    if (pathMeasureKeyframe != pathKeyframe) {
      pathMeasure = new PathMeasure(pathKeyframe.getPath(), false);
      pathMeasureKeyframe = pathKeyframe;
    }

    pathMeasure.getPosTan(keyframeProgress * pathMeasure.getLength(), pos, null);
    point.set(pos[0], pos[1]);
    return point;
  }
}
