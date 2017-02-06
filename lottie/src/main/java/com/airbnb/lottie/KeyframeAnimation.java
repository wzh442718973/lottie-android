package com.airbnb.lottie;

import android.support.annotation.FloatRange;

import java.util.ArrayList;
import java.util.List;

abstract class KeyframeAnimation<T> {
  interface AnimationListener<T> {
    void onValueChanged(T progress);
  }

  final List<AnimationListener<T>> listeners = new ArrayList<>();
  private final long duration;
  private final LottieComposition composition;
  private long startDelay;
  final List<Keyframe<T>> keyframes;
  boolean isDiscrete = false;

  float progress;

  private int cachedKeyframeIndex = -1;
  private float cachedKeyframeIndexStart;
  private float cachedKeyframeIndexEnd;
  private float cachedDurationEndProgress = Float.MIN_VALUE;

  KeyframeAnimation(long duration, LottieComposition composition, List<Keyframe<T>> keyframes) {
    this.duration = duration;
    this.composition = composition;
    this.keyframes = keyframes;
  }

  void setStartDelay(long startDelay) {
    this.startDelay = startDelay;
    cachedDurationEndProgress = Float.MIN_VALUE;
  }

  void setIsDiscrete() {
    isDiscrete = true;
  }

  void addUpdateListener(AnimationListener<T> listener) {
    listeners.add(listener);
  }

  void removeUpdateListener(AnimationListener<T> listener) {
    listeners.remove(listener);
  }

  void setProgress(@FloatRange(from = 0f, to = 1f) float progress) {
    if (progress < getStartDelayProgress()) {
      progress = 0f;
    } else if (progress > getDurationEndProgress()) {
      progress = 1f;
    } else {
      progress = (progress - getStartDelayProgress()) / getDurationRangeProgress();
    }
    if (progress == this.progress) {
      return;
    }
    this.progress = progress;

    T value = getValue(keyframes.get(getCurrentKeyframeIndex()), getCurrentKeyframeProgress());
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).onValueChanged(value);
    }
  }

  int getCurrentKeyframeIndex() {
    if (keyframes.isEmpty()) {
      return 0;
    } else if (progress <= 0f) {
      return 0;
    } else if (progress >= 1f) {
      return keyframes.size() - 1;
    }

    int keyframeIndex = 0;
    if (cachedKeyframeIndex != -1 && progress >= cachedKeyframeIndexStart &&
        progress <= cachedKeyframeIndexEnd) {
      keyframeIndex = cachedKeyframeIndex;
    } else {
      float startProgress = getProgressForKeyframeStart(keyframes.get(0));
      while (startProgress < progress && keyframeIndex < keyframes.size()) {
        keyframeIndex++;
        startProgress = getProgressForKeyframeStart(keyframes.get(keyframeIndex));
      }
      cachedKeyframeIndex = keyframeIndex;
      cachedKeyframeIndexStart = startProgress;
      cachedKeyframeIndexEnd = getProgressForKeyframeEnd(keyframes.get(keyframeIndex));
    }

    return keyframeIndex - 1;
  }

  /**
   * This wil be [0, 1] unless the interpolator has overshoot in which case getValue() should be
   * able to handle values outside of that range.
   */
  float getCurrentKeyframeProgress() {
    if (progress <= 0f) {
      return 0f;
    } else if (progress >= 1f) {
      return 1f;
    }

    Keyframe<T> keyframe = keyframes.get(getCurrentKeyframeIndex());
    long durationFrames = keyframes.get(keyframes.size() - 1).startFrame -
        keyframes.get(0).startFrame;
    long progressFrame = (long) (keyframes.get(0).startFrame + progress * (float) durationFrames);

    float percentageIntoFrame = 0;
    if (!isDiscrete) {
      percentageIntoFrame = (progressFrame - keyframe.startFrame) /
          (keyframe.endFrame - keyframe.startFrame);
      percentageIntoFrame = keyframe.timingFunction.getInterpolation(percentageIntoFrame);
    }

    return percentageIntoFrame;
  }

  @FloatRange(from = 0f, to = 1f)
  private float getProgressForKeyframeStart(Keyframe<?> keyframe) {
    if (keyframes.isEmpty()) {
      return 0f;
    }
    return getProgressForFrame(keyframe.startFrame);
  }

  @FloatRange(from = 0f, to = 1f)
  private float getProgressForKeyframeEnd(Keyframe<?> keyframe) {
    if (keyframes.isEmpty()) {
      return 0f;
    }
    return getProgressForFrame(keyframe.endFrame);
  }

  private float getProgressForFrame(long frame) {
    float firstFrame = keyframes.get(0).startFrame;
    float lastFrame = keyframes.get(keyframes.size() - 1).startFrame;

    return (frame - firstFrame) / (lastFrame - firstFrame);
  }

  @FloatRange(from = 0f, to = 1f)
  private float getStartDelayProgress() {
    return (float) startDelay / (float) (composition.getDuration());
  }

  @FloatRange(from = 0f, to = 1f)
  private float getDurationEndProgress() {
    if (cachedDurationEndProgress == Float.MIN_VALUE) {
      // This was taking a surprisingly long time according to systrace. Cache it!
      cachedDurationEndProgress = getStartDelayProgress() + getDurationRangeProgress();
    }
    return cachedDurationEndProgress;
  }

  @FloatRange(from = 0f, to = 1f)
  private float getDurationRangeProgress() {
    return (float) duration / (float) composition.getDuration();
  }

  /**
   * keyframeProgress will be [0, 1] unless the interpolator has overshoot in which case, this
   * should be able to handle values outside of that range.
   */
  abstract T getValue(Keyframe<T> keyframe, float keyframeProgress);
}
