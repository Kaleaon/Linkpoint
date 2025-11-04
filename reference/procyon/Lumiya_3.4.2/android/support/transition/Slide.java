// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.animation.Animator;
import android.support.annotation.NonNull;
import android.content.res.TypedArray;
import android.support.v4.content.res.TypedArrayUtils;
import org.xmlpull.v1.XmlPullParser;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.animation.TimeInterpolator;

public class Slide extends Visibility
{
    private static final String PROPNAME_SCREEN_POSITION = "android:slide:screenPosition";
    private static final TimeInterpolator sAccelerate;
    private static final CalculateSlide sCalculateBottom;
    private static final CalculateSlide sCalculateEnd;
    private static final CalculateSlide sCalculateLeft;
    private static final CalculateSlide sCalculateRight;
    private static final CalculateSlide sCalculateStart;
    private static final CalculateSlide sCalculateTop;
    private static final TimeInterpolator sDecelerate;
    private CalculateSlide mSlideCalculator;
    private int mSlideEdge;
    
    static {
        sDecelerate = (TimeInterpolator)new DecelerateInterpolator();
        sAccelerate = (TimeInterpolator)new AccelerateInterpolator();
        sCalculateLeft = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGoneX(final ViewGroup viewGroup, final View view) {
                return view.getTranslationX() - viewGroup.getWidth();
            }
        };
        sCalculateStart = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGoneX(final ViewGroup viewGroup, final View view) {
                int n = 0;
                if (ViewCompat.getLayoutDirection((View)viewGroup) == 1) {
                    n = 1;
                }
                float n2;
                if (n == 0) {
                    n2 = view.getTranslationX() - viewGroup.getWidth();
                }
                else {
                    n2 = view.getTranslationX() + viewGroup.getWidth();
                }
                return n2;
            }
        };
        sCalculateTop = (CalculateSlide)new CalculateSlideVertical() {
            @Override
            public float getGoneY(final ViewGroup viewGroup, final View view) {
                return view.getTranslationY() - viewGroup.getHeight();
            }
        };
        sCalculateRight = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGoneX(final ViewGroup viewGroup, final View view) {
                return view.getTranslationX() + viewGroup.getWidth();
            }
        };
        sCalculateEnd = (CalculateSlide)new CalculateSlideHorizontal() {
            @Override
            public float getGoneX(final ViewGroup viewGroup, final View view) {
                int n = 0;
                if (ViewCompat.getLayoutDirection((View)viewGroup) == 1) {
                    n = 1;
                }
                float n2;
                if (n == 0) {
                    n2 = view.getTranslationX() + viewGroup.getWidth();
                }
                else {
                    n2 = view.getTranslationX() - viewGroup.getWidth();
                }
                return n2;
            }
        };
        sCalculateBottom = (CalculateSlide)new CalculateSlideVertical() {
            @Override
            public float getGoneY(final ViewGroup viewGroup, final View view) {
                return view.getTranslationY() + viewGroup.getHeight();
            }
        };
    }
    
    public Slide() {
        this.mSlideCalculator = Slide.sCalculateBottom;
        this.setSlideEdge(this.mSlideEdge = 80);
    }
    
    public Slide(final int slideEdge) {
        this.mSlideCalculator = Slide.sCalculateBottom;
        this.mSlideEdge = 80;
        this.setSlideEdge(slideEdge);
    }
    
    public Slide(final Context context, final AttributeSet set) {
        super(context, set);
        this.mSlideCalculator = Slide.sCalculateBottom;
        this.mSlideEdge = 80;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, Styleable.SLIDE);
        final int namedInt = TypedArrayUtils.getNamedInt(obtainStyledAttributes, (XmlPullParser)set, "slideEdge", 0, 80);
        obtainStyledAttributes.recycle();
        this.setSlideEdge(namedInt);
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        final View view = transitionValues.view;
        final int[] array = new int[2];
        view.getLocationOnScreen(array);
        transitionValues.values.put("android:slide:screenPosition", array);
    }
    
    @Override
    public void captureEndValues(@NonNull final TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        this.captureValues(transitionValues);
    }
    
    @Override
    public void captureStartValues(@NonNull final TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        this.captureValues(transitionValues);
    }
    
    public int getSlideEdge() {
        return this.mSlideEdge;
    }
    
    @Override
    public Animator onAppear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues2 != null) {
            final int[] array = transitionValues2.values.get("android:slide:screenPosition");
            return TranslationAnimationCreator.createAnimation(view, transitionValues2, array[0], array[1], this.mSlideCalculator.getGoneX(viewGroup, view), this.mSlideCalculator.getGoneY(viewGroup, view), view.getTranslationX(), view.getTranslationY(), Slide.sDecelerate);
        }
        return null;
    }
    
    @Override
    public Animator onDisappear(final ViewGroup viewGroup, final View view, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        if (transitionValues != null) {
            final int[] array = transitionValues.values.get("android:slide:screenPosition");
            return TranslationAnimationCreator.createAnimation(view, transitionValues, array[0], array[1], view.getTranslationX(), view.getTranslationY(), this.mSlideCalculator.getGoneX(viewGroup, view), this.mSlideCalculator.getGoneY(viewGroup, view), Slide.sAccelerate);
        }
        return null;
    }
    
    public void setSlideEdge(final int n) {
        switch (n) {
            default: {
                throw new IllegalArgumentException("Invalid slide direction");
            }
            case 3: {
                this.mSlideCalculator = Slide.sCalculateLeft;
                break;
            }
            case 48: {
                this.mSlideCalculator = Slide.sCalculateTop;
                break;
            }
            case 5: {
                this.mSlideCalculator = Slide.sCalculateRight;
                break;
            }
            case 80: {
                this.mSlideCalculator = Slide.sCalculateBottom;
                break;
            }
            case 8388611: {
                this.mSlideCalculator = Slide.sCalculateStart;
                break;
            }
            case 8388613: {
                this.mSlideCalculator = Slide.sCalculateEnd;
                break;
            }
        }
        this.mSlideEdge = n;
        final SidePropagation propagation = new SidePropagation();
        propagation.setSide(n);
        this.setPropagation(propagation);
    }
    
    private interface CalculateSlide
    {
        float getGoneX(final ViewGroup p0, final View p1);
        
        float getGoneY(final ViewGroup p0, final View p1);
    }
    
    private abstract static class CalculateSlideHorizontal implements CalculateSlide
    {
        @Override
        public float getGoneY(final ViewGroup viewGroup, final View view) {
            return view.getTranslationY();
        }
    }
    
    private abstract static class CalculateSlideVertical implements CalculateSlide
    {
        @Override
        public float getGoneX(final ViewGroup viewGroup, final View view) {
            return view.getTranslationX();
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface GravityFlag {
    }
}
