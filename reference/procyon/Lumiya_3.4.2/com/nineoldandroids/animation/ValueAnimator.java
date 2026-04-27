// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import android.os.Message;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.view.animation.AnimationUtils;
import java.util.Iterator;
import android.util.AndroidRuntimeException;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.util.HashMap;
import android.view.animation.Interpolator;
import java.util.ArrayList;

public class ValueAnimator extends Animator
{
    static final int ANIMATION_FRAME = 1;
    static final int ANIMATION_START = 0;
    private static final long DEFAULT_FRAME_DELAY = 10L;
    public static final int INFINITE = -1;
    public static final int RESTART = 1;
    public static final int REVERSE = 2;
    static final int RUNNING = 1;
    static final int SEEKED = 2;
    static final int STOPPED = 0;
    private static ThreadLocal<AnimationHandler> sAnimationHandler;
    private static final ThreadLocal<ArrayList<ValueAnimator>> sAnimations;
    private static final Interpolator sDefaultInterpolator;
    private static final ThreadLocal<ArrayList<ValueAnimator>> sDelayedAnims;
    private static final ThreadLocal<ArrayList<ValueAnimator>> sEndingAnims;
    private static final TypeEvaluator sFloatEvaluator;
    private static long sFrameDelay;
    private static final TypeEvaluator sIntEvaluator;
    private static final ThreadLocal<ArrayList<ValueAnimator>> sPendingAnimations;
    private static final ThreadLocal<ArrayList<ValueAnimator>> sReadyAnims;
    private float mCurrentFraction;
    private int mCurrentIteration;
    private long mDelayStartTime;
    private long mDuration;
    boolean mInitialized;
    private Interpolator mInterpolator;
    private boolean mPlayingBackwards;
    int mPlayingState;
    private int mRepeatCount;
    private int mRepeatMode;
    private boolean mRunning;
    long mSeekTime;
    private long mStartDelay;
    long mStartTime;
    private boolean mStarted;
    private boolean mStartedDelay;
    private ArrayList<AnimatorUpdateListener> mUpdateListeners;
    PropertyValuesHolder[] mValues;
    HashMap<String, PropertyValuesHolder> mValuesMap;
    
    static {
        ValueAnimator.sAnimationHandler = new ThreadLocal<AnimationHandler>();
        sAnimations = new ThreadLocal<ArrayList<ValueAnimator>>() {
            @Override
            protected ArrayList<ValueAnimator> initialValue() {
                return new ArrayList<ValueAnimator>();
            }
        };
        sPendingAnimations = new ThreadLocal<ArrayList<ValueAnimator>>() {
            @Override
            protected ArrayList<ValueAnimator> initialValue() {
                return new ArrayList<ValueAnimator>();
            }
        };
        sDelayedAnims = new ThreadLocal<ArrayList<ValueAnimator>>() {
            @Override
            protected ArrayList<ValueAnimator> initialValue() {
                return new ArrayList<ValueAnimator>();
            }
        };
        sEndingAnims = new ThreadLocal<ArrayList<ValueAnimator>>() {
            @Override
            protected ArrayList<ValueAnimator> initialValue() {
                return new ArrayList<ValueAnimator>();
            }
        };
        sReadyAnims = new ThreadLocal<ArrayList<ValueAnimator>>() {
            @Override
            protected ArrayList<ValueAnimator> initialValue() {
                return new ArrayList<ValueAnimator>();
            }
        };
        sDefaultInterpolator = (Interpolator)new AccelerateDecelerateInterpolator();
        sIntEvaluator = new IntEvaluator();
        sFloatEvaluator = new FloatEvaluator();
        ValueAnimator.sFrameDelay = 10L;
    }
    
    public ValueAnimator() {
        this.mSeekTime = -1L;
        this.mPlayingBackwards = false;
        this.mCurrentIteration = 0;
        this.mCurrentFraction = 0.0f;
        this.mStartedDelay = false;
        this.mPlayingState = 0;
        this.mRunning = false;
        this.mStarted = false;
        this.mInitialized = false;
        this.mDuration = 300L;
        this.mStartDelay = 0L;
        this.mRepeatCount = 0;
        this.mRepeatMode = 1;
        this.mInterpolator = ValueAnimator.sDefaultInterpolator;
        this.mUpdateListeners = null;
    }
    
    public static void clearAllAnimations() {
        ValueAnimator.sAnimations.get().clear();
        ValueAnimator.sPendingAnimations.get().clear();
        ValueAnimator.sDelayedAnims.get().clear();
    }
    
    private boolean delayedAnimationFrame(final long mDelayStartTime) {
        if (this.mStartedDelay) {
            final long n = mDelayStartTime - this.mDelayStartTime;
            int n2;
            if (n <= this.mStartDelay) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                this.mStartTime = mDelayStartTime - (n - this.mStartDelay);
                this.mPlayingState = 1;
                return true;
            }
        }
        else {
            this.mStartedDelay = true;
            this.mDelayStartTime = mDelayStartTime;
        }
        return false;
    }
    
    private void endAnimation() {
        ValueAnimator.sAnimations.get().remove(this);
        ValueAnimator.sPendingAnimations.get().remove(this);
        ValueAnimator.sDelayedAnims.get().remove(this);
        this.mPlayingState = 0;
        if (this.mRunning && this.mListeners != null) {
            final ArrayList list = (ArrayList)this.mListeners.clone();
            for (int size = list.size(), i = 0; i < size; ++i) {
                ((AnimatorListener)list.get(i)).onAnimationEnd(this);
            }
        }
        this.mRunning = false;
        this.mStarted = false;
    }
    
    public static int getCurrentAnimationsCount() {
        return ValueAnimator.sAnimations.get().size();
    }
    
    public static long getFrameDelay() {
        return ValueAnimator.sFrameDelay;
    }
    
    public static ValueAnimator ofFloat(final float... floatValues) {
        final ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(floatValues);
        return valueAnimator;
    }
    
    public static ValueAnimator ofInt(final int... intValues) {
        final ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(intValues);
        return valueAnimator;
    }
    
    public static ValueAnimator ofObject(final TypeEvaluator evaluator, final Object... objectValues) {
        final ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setObjectValues(objectValues);
        valueAnimator.setEvaluator(evaluator);
        return valueAnimator;
    }
    
    public static ValueAnimator ofPropertyValuesHolder(final PropertyValuesHolder... values) {
        final ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setValues(values);
        return valueAnimator;
    }
    
    public static void setFrameDelay(final long sFrameDelay) {
        ValueAnimator.sFrameDelay = sFrameDelay;
    }
    
    private void start(final boolean mPlayingBackwards) {
        if (Looper.myLooper() != null) {
            this.mPlayingBackwards = mPlayingBackwards;
            this.mCurrentIteration = 0;
            this.mPlayingState = 0;
            this.mStarted = true;
            this.mStartedDelay = false;
            ValueAnimator.sPendingAnimations.get().add(this);
            if (this.mStartDelay == 0L) {
                this.setCurrentPlayTime(this.getCurrentPlayTime());
                this.mPlayingState = 0;
                this.mRunning = true;
                if (this.mListeners != null) {
                    final ArrayList list = (ArrayList)this.mListeners.clone();
                    for (int size = list.size(), i = 0; i < size; ++i) {
                        ((AnimatorListener)list.get(i)).onAnimationStart(this);
                    }
                }
            }
            AnimationHandler value = ValueAnimator.sAnimationHandler.get();
            if (value == null) {
                value = new AnimationHandler();
                ValueAnimator.sAnimationHandler.set(value);
            }
            value.sendEmptyMessage(0);
            return;
        }
        throw new AndroidRuntimeException("Animators may only be run on Looper threads");
    }
    
    private void startAnimation() {
        this.initAnimation();
        ValueAnimator.sAnimations.get().add(this);
        boolean b;
        if (this.mStartDelay <= 0L) {
            b = true;
        }
        else {
            b = false;
        }
        if (!b && this.mListeners != null) {
            final ArrayList list = (ArrayList)this.mListeners.clone();
            for (int size = list.size(), i = 0; i < size; ++i) {
                ((AnimatorListener)list.get(i)).onAnimationStart(this);
            }
        }
    }
    
    public void addUpdateListener(final AnimatorUpdateListener e) {
        if (this.mUpdateListeners == null) {
            this.mUpdateListeners = new ArrayList<AnimatorUpdateListener>();
        }
        this.mUpdateListeners.add(e);
    }
    
    void animateValue(float interpolation) {
        interpolation = this.mInterpolator.getInterpolation(interpolation);
        this.mCurrentFraction = interpolation;
        for (int length = this.mValues.length, i = 0; i < length; ++i) {
            this.mValues[i].calculateValue(interpolation);
        }
        if (this.mUpdateListeners != null) {
            for (int size = this.mUpdateListeners.size(), j = 0; j < size; ++j) {
                this.mUpdateListeners.get(j).onAnimationUpdate(this);
            }
        }
    }
    
    boolean animationFrame(final long mStartTime) {
        final boolean b = false;
        boolean b2 = false;
        if (this.mPlayingState == 0) {
            this.mPlayingState = 1;
            int n;
            if (this.mSeekTime >= 0L) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                this.mStartTime = mStartTime;
            }
            else {
                this.mStartTime = mStartTime - this.mSeekTime;
                this.mSeekTime = -1L;
            }
        }
        switch (this.mPlayingState) {
            case 1:
            case 2: {
                int n2;
                if (this.mDuration <= 0L) {
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                float a;
                if (n2 == 0) {
                    a = (mStartTime - this.mStartTime) / (float)this.mDuration;
                }
                else {
                    a = 1.0f;
                }
                float min = a;
                b2 = b;
                if (a >= 1.0f) {
                    if (this.mCurrentIteration >= this.mRepeatCount && this.mRepeatCount != -1) {
                        min = Math.min(a, 1.0f);
                        b2 = true;
                    }
                    else {
                        if (this.mListeners != null) {
                            for (int size = this.mListeners.size(), i = 0; i < size; ++i) {
                                ((AnimatorListener)this.mListeners.get(i)).onAnimationRepeat(this);
                            }
                        }
                        if (this.mRepeatMode == 2) {
                            this.mPlayingBackwards = !this.mPlayingBackwards;
                        }
                        this.mCurrentIteration += (int)a;
                        min = a % 1.0f;
                        this.mStartTime += this.mDuration;
                        b2 = b;
                    }
                }
                if (this.mPlayingBackwards) {
                    min = 1.0f - min;
                }
                this.animateValue(min);
                break;
            }
        }
        return b2;
    }
    
    @Override
    public void cancel() {
        if (this.mPlayingState != 0 || ValueAnimator.sPendingAnimations.get().contains(this) || ValueAnimator.sDelayedAnims.get().contains(this)) {
            if (this.mRunning && this.mListeners != null) {
                final Iterator iterator = ((ArrayList)this.mListeners.clone()).iterator();
                while (iterator.hasNext()) {
                    ((AnimatorListener)iterator.next()).onAnimationCancel(this);
                }
            }
            this.endAnimation();
        }
    }
    
    @Override
    public ValueAnimator clone() {
        final int n = 0;
        final ValueAnimator valueAnimator = (ValueAnimator)super.clone();
        if (this.mUpdateListeners != null) {
            final ArrayList<AnimatorUpdateListener> mUpdateListeners = this.mUpdateListeners;
            valueAnimator.mUpdateListeners = new ArrayList<AnimatorUpdateListener>();
            for (int size = mUpdateListeners.size(), i = 0; i < size; ++i) {
                valueAnimator.mUpdateListeners.add((AnimatorUpdateListener)mUpdateListeners.get(i));
            }
        }
        valueAnimator.mSeekTime = -1L;
        valueAnimator.mPlayingBackwards = false;
        valueAnimator.mCurrentIteration = 0;
        valueAnimator.mInitialized = false;
        valueAnimator.mPlayingState = 0;
        valueAnimator.mStartedDelay = false;
        final PropertyValuesHolder[] mValues = this.mValues;
        if (mValues != null) {
            final int length = mValues.length;
            valueAnimator.mValues = new PropertyValuesHolder[length];
            valueAnimator.mValuesMap = new HashMap<String, PropertyValuesHolder>(length);
            for (int j = n; j < length; ++j) {
                final PropertyValuesHolder clone = mValues[j].clone();
                valueAnimator.mValues[j] = clone;
                valueAnimator.mValuesMap.put(clone.getPropertyName(), clone);
            }
        }
        return valueAnimator;
    }
    
    @Override
    public void end() {
        if (!ValueAnimator.sAnimations.get().contains(this) && !ValueAnimator.sPendingAnimations.get().contains(this)) {
            this.mStartedDelay = false;
            this.startAnimation();
        }
        else if (!this.mInitialized) {
            this.initAnimation();
        }
        if (this.mRepeatCount > 0 && (this.mRepeatCount & 0x1) == 0x1) {
            this.animateValue(0.0f);
        }
        else {
            this.animateValue(1.0f);
        }
        this.endAnimation();
    }
    
    public float getAnimatedFraction() {
        return this.mCurrentFraction;
    }
    
    public Object getAnimatedValue() {
        if (this.mValues != null && this.mValues.length > 0) {
            return this.mValues[0].getAnimatedValue();
        }
        return null;
    }
    
    public Object getAnimatedValue(final String key) {
        final PropertyValuesHolder propertyValuesHolder = this.mValuesMap.get(key);
        if (propertyValuesHolder == null) {
            return null;
        }
        return propertyValuesHolder.getAnimatedValue();
    }
    
    public long getCurrentPlayTime() {
        if (this.mInitialized && this.mPlayingState != 0) {
            return AnimationUtils.currentAnimationTimeMillis() - this.mStartTime;
        }
        return 0L;
    }
    
    @Override
    public long getDuration() {
        return this.mDuration;
    }
    
    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }
    
    public int getRepeatCount() {
        return this.mRepeatCount;
    }
    
    public int getRepeatMode() {
        return this.mRepeatMode;
    }
    
    @Override
    public long getStartDelay() {
        return this.mStartDelay;
    }
    
    public PropertyValuesHolder[] getValues() {
        return this.mValues;
    }
    
    void initAnimation() {
        int i = 0;
        if (!this.mInitialized) {
            while (i < this.mValues.length) {
                this.mValues[i].init();
                ++i;
            }
            this.mInitialized = true;
        }
    }
    
    @Override
    public boolean isRunning() {
        boolean b = false;
        if (this.mPlayingState == 1 || this.mRunning) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean isStarted() {
        return this.mStarted;
    }
    
    public void removeAllUpdateListeners() {
        if (this.mUpdateListeners != null) {
            this.mUpdateListeners.clear();
            this.mUpdateListeners = null;
        }
    }
    
    public void removeUpdateListener(final AnimatorUpdateListener o) {
        if (this.mUpdateListeners != null) {
            this.mUpdateListeners.remove(o);
            if (this.mUpdateListeners.size() == 0) {
                this.mUpdateListeners = null;
            }
        }
    }
    
    public void reverse() {
        boolean mPlayingBackwards = false;
        if (!this.mPlayingBackwards) {
            mPlayingBackwards = true;
        }
        this.mPlayingBackwards = mPlayingBackwards;
        if (this.mPlayingState != 1) {
            this.start(true);
        }
        else {
            final long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            this.mStartTime = currentAnimationTimeMillis - (this.mDuration - (currentAnimationTimeMillis - this.mStartTime));
        }
    }
    
    public void setCurrentPlayTime(final long mSeekTime) {
        this.initAnimation();
        final long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        if (this.mPlayingState != 1) {
            this.mSeekTime = mSeekTime;
            this.mPlayingState = 2;
        }
        this.mStartTime = currentAnimationTimeMillis - mSeekTime;
        this.animationFrame(currentAnimationTimeMillis);
    }
    
    @Override
    public ValueAnimator setDuration(final long n) {
        int n2;
        if (n >= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            throw new IllegalArgumentException("Animators cannot have negative duration: " + n);
        }
        this.mDuration = n;
        return this;
    }
    
    public void setEvaluator(final TypeEvaluator evaluator) {
        if (evaluator != null && this.mValues != null && this.mValues.length > 0) {
            this.mValues[0].setEvaluator(evaluator);
        }
    }
    
    public void setFloatValues(final float... floatValues) {
        if (floatValues != null && floatValues.length != 0) {
            if (this.mValues != null && this.mValues.length != 0) {
                this.mValues[0].setFloatValues(floatValues);
            }
            else {
                this.setValues(PropertyValuesHolder.ofFloat("", floatValues));
            }
            this.mInitialized = false;
        }
    }
    
    public void setIntValues(final int... intValues) {
        if (intValues != null && intValues.length != 0) {
            if (this.mValues != null && this.mValues.length != 0) {
                this.mValues[0].setIntValues(intValues);
            }
            else {
                this.setValues(PropertyValuesHolder.ofInt("", intValues));
            }
            this.mInitialized = false;
        }
    }
    
    @Override
    public void setInterpolator(final Interpolator mInterpolator) {
        if (mInterpolator == null) {
            this.mInterpolator = (Interpolator)new LinearInterpolator();
        }
        else {
            this.mInterpolator = mInterpolator;
        }
    }
    
    public void setObjectValues(final Object... objectValues) {
        if (objectValues != null && objectValues.length != 0) {
            if (this.mValues != null && this.mValues.length != 0) {
                this.mValues[0].setObjectValues(objectValues);
            }
            else {
                this.setValues(PropertyValuesHolder.ofObject("", null, objectValues));
            }
            this.mInitialized = false;
        }
    }
    
    public void setRepeatCount(final int mRepeatCount) {
        this.mRepeatCount = mRepeatCount;
    }
    
    public void setRepeatMode(final int mRepeatMode) {
        this.mRepeatMode = mRepeatMode;
    }
    
    @Override
    public void setStartDelay(final long mStartDelay) {
        this.mStartDelay = mStartDelay;
    }
    
    public void setValues(final PropertyValuesHolder... mValues) {
        final int length = mValues.length;
        this.mValues = mValues;
        this.mValuesMap = new HashMap<String, PropertyValuesHolder>(length);
        for (final PropertyValuesHolder value : mValues) {
            this.mValuesMap.put(value.getPropertyName(), value);
        }
        this.mInitialized = false;
    }
    
    @Override
    public void start() {
        this.start(false);
    }
    
    @Override
    public String toString() {
        String str = "ValueAnimator@" + Integer.toHexString(this.hashCode());
        String s;
        if (this.mValues == null) {
            s = str;
        }
        else {
            int n = 0;
            while (true) {
                s = str;
                if (n >= this.mValues.length) {
                    break;
                }
                str = str + "\n    " + this.mValues[n].toString();
                ++n;
            }
        }
        return s;
    }
    
    private static class AnimationHandler extends Handler
    {
        public void handleMessage(final Message message) {
            final ArrayList list = ValueAnimator.sAnimations.get();
            final ArrayList list2 = ValueAnimator.sDelayedAnims.get();
            int n = 0;
            Label_0052: {
                switch (message.what) {
                    default: {
                        return;
                    }
                    case 1: {
                        n = 1;
                        break;
                    }
                    case 0: {
                        final ArrayList list3 = ValueAnimator.sPendingAnimations.get();
                        int n2;
                        if (list.size() <= 0 && list2.size() <= 0) {
                            n2 = 1;
                        }
                        else {
                            n2 = 0;
                        }
                        while (true) {
                            n = n2;
                            if (list3.size() <= 0) {
                                break Label_0052;
                            }
                            final ArrayList list4 = (ArrayList)list3.clone();
                            list3.clear();
                            for (int size = list4.size(), i = 0; i < size; ++i) {
                                final ValueAnimator e = list4.get(i);
                                if (e.mStartDelay == 0L) {
                                    e.startAnimation();
                                }
                                else {
                                    list2.add(e);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            final long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            final ArrayList list5 = ValueAnimator.sReadyAnims.get();
            final ArrayList list6 = ValueAnimator.sEndingAnims.get();
            for (int size2 = list2.size(), j = 0; j < size2; ++j) {
                final ValueAnimator e2 = (ValueAnimator)list2.get(j);
                if (e2.delayedAnimationFrame(currentAnimationTimeMillis)) {
                    list5.add(e2);
                }
            }
            final int size3 = list5.size();
            if (size3 > 0) {
                for (int k = 0; k < size3; ++k) {
                    final ValueAnimator o = (ValueAnimator)list5.get(k);
                    o.startAnimation();
                    o.mRunning = true;
                    list2.remove(o);
                }
                list5.clear();
            }
            int n5;
            int n7;
            for (int size4 = list.size(), l = 0; l < size4; l = n5, size4 = n7) {
                final ValueAnimator valueAnimator = (ValueAnimator)list.get(l);
                if (valueAnimator.animationFrame(currentAnimationTimeMillis)) {
                    list6.add(valueAnimator);
                }
                int n4;
                if (list.size() != size4) {
                    list6.remove(valueAnimator);
                    final int n3 = l;
                    n4 = size4 - 1;
                    n5 = n3;
                }
                else {
                    final int n6 = l + 1;
                    n4 = size4;
                    n5 = n6;
                }
                n7 = n4;
            }
            if (list6.size() > 0) {
                for (int index = 0; index < list6.size(); ++index) {
                    ((ValueAnimator)list6.get(index)).endAnimation();
                }
                list6.clear();
            }
            if (n == 0) {
                return;
            }
            if (list.isEmpty() && list2.isEmpty()) {
                return;
            }
            this.sendEmptyMessageDelayed(1, Math.max(0L, ValueAnimator.sFrameDelay - (AnimationUtils.currentAnimationTimeMillis() - currentAnimationTimeMillis)));
        }
    }
    
    public interface AnimatorUpdateListener
    {
        void onAnimationUpdate(final ValueAnimator p0);
    }
}
