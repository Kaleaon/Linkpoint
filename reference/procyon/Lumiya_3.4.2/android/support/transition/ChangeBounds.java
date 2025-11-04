// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.util.Map;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Bitmap$Config;
import android.animation.Animator;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.content.res.TypedArray;
import org.xmlpull.v1.XmlPullParser;
import android.support.v4.content.res.TypedArrayUtils;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.PointF;
import android.view.View;
import android.util.Property;

public class ChangeBounds extends Transition
{
    private static final Property<View, PointF> BOTTOM_RIGHT_ONLY_PROPERTY;
    private static final Property<ViewBounds, PointF> BOTTOM_RIGHT_PROPERTY;
    private static final Property<Drawable, PointF> DRAWABLE_ORIGIN_PROPERTY;
    private static final Property<View, PointF> POSITION_PROPERTY;
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_CLIP = "android:changeBounds:clip";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static final Property<View, PointF> TOP_LEFT_ONLY_PROPERTY;
    private static final Property<ViewBounds, PointF> TOP_LEFT_PROPERTY;
    private static RectEvaluator sRectEvaluator;
    private static final String[] sTransitionProperties;
    private boolean mReparent;
    private boolean mResizeClip;
    private int[] mTempLocation;
    
    static {
        sTransitionProperties = new String[] { "android:changeBounds:bounds", "android:changeBounds:clip", "android:changeBounds:parent", "android:changeBounds:windowX", "android:changeBounds:windowY" };
        DRAWABLE_ORIGIN_PROPERTY = new Property<Drawable, PointF>(PointF.class, "boundsOrigin") {
            private Rect mBounds = new Rect();
            
            public PointF get(final Drawable drawable) {
                drawable.copyBounds(this.mBounds);
                return new PointF((float)this.mBounds.left, (float)this.mBounds.top);
            }
            
            public void set(final Drawable drawable, final PointF pointF) {
                drawable.copyBounds(this.mBounds);
                this.mBounds.offsetTo(Math.round(pointF.x), Math.round(pointF.y));
                drawable.setBounds(this.mBounds);
            }
        };
        TOP_LEFT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "topLeft") {
            public PointF get(final ViewBounds viewBounds) {
                return null;
            }
            
            public void set(final ViewBounds viewBounds, final PointF topLeft) {
                viewBounds.setTopLeft(topLeft);
            }
        };
        BOTTOM_RIGHT_PROPERTY = new Property<ViewBounds, PointF>(PointF.class, "bottomRight") {
            public PointF get(final ViewBounds viewBounds) {
                return null;
            }
            
            public void set(final ViewBounds viewBounds, final PointF bottomRight) {
                viewBounds.setBottomRight(bottomRight);
            }
        };
        BOTTOM_RIGHT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "bottomRight") {
            public PointF get(final View view) {
                return null;
            }
            
            public void set(final View view, final PointF pointF) {
                ViewUtils.setLeftTopRightBottom(view, view.getLeft(), view.getTop(), Math.round(pointF.x), Math.round(pointF.y));
            }
        };
        TOP_LEFT_ONLY_PROPERTY = new Property<View, PointF>(PointF.class, "topLeft") {
            public PointF get(final View view) {
                return null;
            }
            
            public void set(final View view, final PointF pointF) {
                ViewUtils.setLeftTopRightBottom(view, Math.round(pointF.x), Math.round(pointF.y), view.getRight(), view.getBottom());
            }
        };
        POSITION_PROPERTY = new Property<View, PointF>(PointF.class, "position") {
            public PointF get(final View view) {
                return null;
            }
            
            public void set(final View view, final PointF pointF) {
                final int round = Math.round(pointF.x);
                final int round2 = Math.round(pointF.y);
                ViewUtils.setLeftTopRightBottom(view, round, round2, view.getWidth() + round, view.getHeight() + round2);
            }
        };
        ChangeBounds.sRectEvaluator = new RectEvaluator();
    }
    
    public ChangeBounds() {
        this.mTempLocation = new int[2];
        this.mResizeClip = false;
        this.mReparent = false;
    }
    
    public ChangeBounds(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTempLocation = new int[2];
        this.mResizeClip = false;
        this.mReparent = false;
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, Styleable.CHANGE_BOUNDS);
        final boolean namedBoolean = TypedArrayUtils.getNamedBoolean(obtainStyledAttributes, (XmlPullParser)set, "resizeClip", 0, false);
        obtainStyledAttributes.recycle();
        this.setResizeClip(namedBoolean);
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (ViewCompat.isLaidOut(view) || view.getWidth() != 0 || view.getHeight() != 0) {
            transitionValues.values.put("android:changeBounds:bounds", new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
            transitionValues.values.put("android:changeBounds:parent", transitionValues.view.getParent());
            if (this.mReparent) {
                transitionValues.view.getLocationInWindow(this.mTempLocation);
                transitionValues.values.put("android:changeBounds:windowX", this.mTempLocation[0]);
                transitionValues.values.put("android:changeBounds:windowY", this.mTempLocation[1]);
            }
            if (this.mResizeClip) {
                transitionValues.values.put("android:changeBounds:clip", ViewCompat.getClipBounds(view));
            }
        }
    }
    
    private boolean parentMatches(final View view, final View view2) {
        boolean b = false;
        if (!this.mReparent) {
            b = true;
        }
        else {
            final TransitionValues matchedTransitionValues = this.getMatchedTransitionValues(view, true);
            if (matchedTransitionValues != null) {
                if (view2 == matchedTransitionValues.view) {
                    b = true;
                }
            }
            else if (view == view2) {
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public void captureEndValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Override
    public void captureStartValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Nullable
    @Override
    public Animator createAnimator(@NonNull final ViewGroup viewGroup, @Nullable final TransitionValues transitionValues, @Nullable final TransitionValues transitionValues2) {
        if (transitionValues == null || transitionValues2 == null) {
            return null;
        }
        final Map<String, Object> values = transitionValues.values;
        final Map<String, Object> values2 = transitionValues2.values;
        final ViewGroup viewGroup2 = values.get("android:changeBounds:parent");
        final ViewGroup viewGroup3 = values2.get("android:changeBounds:parent");
        if (viewGroup2 != null && viewGroup3 != null) {
            final View view = transitionValues2.view;
            if (!this.parentMatches((View)viewGroup2, (View)viewGroup3)) {
                final int intValue = transitionValues.values.get("android:changeBounds:windowX");
                final int intValue2 = transitionValues.values.get("android:changeBounds:windowY");
                final int intValue3 = transitionValues2.values.get("android:changeBounds:windowX");
                final int intValue4 = transitionValues2.values.get("android:changeBounds:windowY");
                if (intValue != intValue3 || intValue2 != intValue4) {
                    viewGroup.getLocationInWindow(this.mTempLocation);
                    final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap$Config.ARGB_8888);
                    view.draw(new Canvas(bitmap));
                    final BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    final float transitionAlpha = ViewUtils.getTransitionAlpha(view);
                    ViewUtils.setTransitionAlpha(view, 0.0f);
                    ViewUtils.getOverlay((View)viewGroup).add((Drawable)bitmapDrawable);
                    final ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder((Object)bitmapDrawable, new PropertyValuesHolder[] { PropertyValuesHolderUtils.ofPointF(ChangeBounds.DRAWABLE_ORIGIN_PROPERTY, this.getPathMotion().getPath((float)(intValue - this.mTempLocation[0]), (float)(intValue2 - this.mTempLocation[1]), (float)(intValue3 - this.mTempLocation[0]), (float)(intValue4 - this.mTempLocation[1]))) });
                    ofPropertyValuesHolder.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                        public void onAnimationEnd(final Animator animator) {
                            ViewUtils.getOverlay((View)viewGroup).remove((Drawable)bitmapDrawable);
                            ViewUtils.setTransitionAlpha(view, transitionAlpha);
                        }
                    });
                    return (Animator)ofPropertyValuesHolder;
                }
            }
            else {
                final Rect rect = transitionValues.values.get("android:changeBounds:bounds");
                final Rect rect2 = transitionValues2.values.get("android:changeBounds:bounds");
                final int left = rect.left;
                final int left2 = rect2.left;
                final int top = rect.top;
                final int top2 = rect2.top;
                final int right = rect.right;
                final int right2 = rect2.right;
                final int bottom = rect.bottom;
                final int bottom2 = rect2.bottom;
                final int a = right - left;
                final int a2 = bottom - top;
                final int b = right2 - left2;
                final int b2 = bottom2 - top2;
                Rect rect3 = transitionValues.values.get("android:changeBounds:clip");
                final Rect rect4 = transitionValues2.values.get("android:changeBounds:clip");
                final int n = 0;
                int n2 = 0;
                while (true) {
                    Label_0497: {
                        if (a != 0 && a2 != 0) {
                            break Label_0497;
                        }
                        if (b != 0) {
                            if (b2 != 0) {
                                break Label_0497;
                            }
                        }
                        while (true) {
                            Label_0561: {
                                if (rect3 != null && !rect3.equals((Object)rect4)) {
                                    break Label_0561;
                                }
                                if (rect3 == null) {
                                    if (rect4 != null) {
                                        break Label_0561;
                                    }
                                }
                                if (n2 <= 0) {
                                    return null;
                                }
                                Object o;
                                if (this.mResizeClip) {
                                    ViewUtils.setLeftTopRightBottom(view, left, top, Math.max(a, b) + left, Math.max(a2, b2) + top);
                                    Object ofPointF;
                                    if (left == left2 && top == top2) {
                                        ofPointF = null;
                                    }
                                    else {
                                        ofPointF = ObjectAnimatorUtils.ofPointF(view, ChangeBounds.POSITION_PROPERTY, this.getPathMotion().getPath((float)left, (float)top, (float)left2, (float)top2));
                                    }
                                    if (rect3 == null) {
                                        rect3 = new Rect(0, 0, a, a2);
                                    }
                                    Rect rect5;
                                    if (rect4 != null) {
                                        rect5 = rect4;
                                    }
                                    else {
                                        rect5 = new Rect(0, 0, b, b2);
                                    }
                                    Object ofObject;
                                    if (rect3.equals((Object)rect5)) {
                                        ofObject = null;
                                    }
                                    else {
                                        ViewCompat.setClipBounds(view, rect3);
                                        ofObject = ObjectAnimator.ofObject((Object)view, "clipBounds", (TypeEvaluator)ChangeBounds.sRectEvaluator, new Object[] { rect3, rect5 });
                                        ((ObjectAnimator)ofObject).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                                            private boolean mIsCanceled;
                                            
                                            public void onAnimationCancel(final Animator animator) {
                                                this.mIsCanceled = true;
                                            }
                                            
                                            public void onAnimationEnd(final Animator animator) {
                                                if (!this.mIsCanceled) {
                                                    ViewCompat.setClipBounds(view, rect4);
                                                    ViewUtils.setLeftTopRightBottom(view, left2, top2, right2, bottom2);
                                                }
                                            }
                                        });
                                    }
                                    o = TransitionUtils.mergeAnimators((Animator)ofPointF, (Animator)ofObject);
                                }
                                else {
                                    ViewUtils.setLeftTopRightBottom(view, left, top, right, bottom);
                                    if (n2 != 2) {
                                        if (left == left2 && top == top2) {
                                            o = ObjectAnimatorUtils.ofPointF(view, ChangeBounds.BOTTOM_RIGHT_ONLY_PROPERTY, this.getPathMotion().getPath((float)right, (float)bottom, (float)right2, (float)bottom2));
                                        }
                                        else {
                                            o = ObjectAnimatorUtils.ofPointF(view, ChangeBounds.TOP_LEFT_ONLY_PROPERTY, this.getPathMotion().getPath((float)left, (float)top, (float)left2, (float)top2));
                                        }
                                    }
                                    else if (a == b && a2 == b2) {
                                        o = ObjectAnimatorUtils.ofPointF(view, ChangeBounds.POSITION_PROPERTY, this.getPathMotion().getPath((float)left, (float)top, (float)left2, (float)top2));
                                    }
                                    else {
                                        final ViewBounds viewBounds = new ViewBounds(view);
                                        final ObjectAnimator ofPointF2 = ObjectAnimatorUtils.ofPointF(viewBounds, ChangeBounds.TOP_LEFT_PROPERTY, this.getPathMotion().getPath((float)left, (float)top, (float)left2, (float)top2));
                                        final ObjectAnimator ofPointF3 = ObjectAnimatorUtils.ofPointF(viewBounds, ChangeBounds.BOTTOM_RIGHT_PROPERTY, this.getPathMotion().getPath((float)right, (float)bottom, (float)right2, (float)bottom2));
                                        o = new AnimatorSet();
                                        ((AnimatorSet)o).playTogether(new Animator[] { (Animator)ofPointF2, (Animator)ofPointF3 });
                                        ((AnimatorSet)o).addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                                            private ViewBounds mViewBounds = viewBounds;
                                        });
                                    }
                                }
                                if (view.getParent() instanceof ViewGroup) {
                                    final ViewGroup viewGroup4 = (ViewGroup)view.getParent();
                                    ViewGroupUtils.suppressLayout(viewGroup4, true);
                                    this.addListener((TransitionListener)new TransitionListenerAdapter() {
                                        boolean mCanceled = false;
                                        
                                        @Override
                                        public void onTransitionCancel(@NonNull final Transition transition) {
                                            ViewGroupUtils.suppressLayout(viewGroup4, false);
                                            this.mCanceled = true;
                                        }
                                        
                                        @Override
                                        public void onTransitionEnd(@NonNull final Transition transition) {
                                            if (!this.mCanceled) {
                                                ViewGroupUtils.suppressLayout(viewGroup4, false);
                                            }
                                            transition.removeListener((TransitionListener)this);
                                        }
                                        
                                        @Override
                                        public void onTransitionPause(@NonNull final Transition transition) {
                                            ViewGroupUtils.suppressLayout(viewGroup4, false);
                                        }
                                        
                                        @Override
                                        public void onTransitionResume(@NonNull final Transition transition) {
                                            ViewGroupUtils.suppressLayout(viewGroup4, true);
                                        }
                                    });
                                }
                                return (Animator)o;
                            }
                            ++n2;
                            continue;
                        }
                    }
                    if (left == left2 && top == top2) {
                        n2 = n;
                    }
                    else {
                        n2 = 1;
                    }
                    if (right == right2 && bottom == bottom2) {
                        continue;
                    }
                    ++n2;
                    continue;
                }
            }
            return null;
        }
        return null;
    }
    
    public boolean getResizeClip() {
        return this.mResizeClip;
    }
    
    @Nullable
    @Override
    public String[] getTransitionProperties() {
        return ChangeBounds.sTransitionProperties;
    }
    
    public void setResizeClip(final boolean mResizeClip) {
        this.mResizeClip = mResizeClip;
    }
    
    private static class ViewBounds
    {
        private int mBottom;
        private int mBottomRightCalls;
        private int mLeft;
        private int mRight;
        private int mTop;
        private int mTopLeftCalls;
        private View mView;
        
        ViewBounds(final View mView) {
            this.mView = mView;
        }
        
        private void setLeftTopRightBottom() {
            ViewUtils.setLeftTopRightBottom(this.mView, this.mLeft, this.mTop, this.mRight, this.mBottom);
            this.mTopLeftCalls = 0;
            this.mBottomRightCalls = 0;
        }
        
        void setBottomRight(final PointF pointF) {
            this.mRight = Math.round(pointF.x);
            this.mBottom = Math.round(pointF.y);
            ++this.mBottomRightCalls;
            if (this.mTopLeftCalls == this.mBottomRightCalls) {
                this.setLeftTopRightBottom();
            }
        }
        
        void setTopLeft(final PointF pointF) {
            this.mLeft = Math.round(pointF.x);
            this.mTop = Math.round(pointF.y);
            ++this.mTopLeftCalls;
            if (this.mTopLeftCalls == this.mBottomRightCalls) {
                this.setLeftTopRightBottom();
            }
        }
    }
}
