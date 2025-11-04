// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.support.annotation.VisibleForTesting;
import java.util.List;
import android.view.ViewGroup;
import android.view.ViewGroup$LayoutParams;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.MotionEvent;
import android.support.annotation.ColorInt;
import android.view.View;
import android.support.v4.view.ViewCompat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View$MeasureSpec;
import android.content.res.Resources;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.widget.ImageView;
import android.support.design.R;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageHelper;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;

@CoordinatorLayout.DefaultBehavior(Behavior.class)
public class FloatingActionButton extends VisibilityAwareImageButton
{
    private static final int AUTO_MINI_LARGEST_SCREEN_WIDTH = 470;
    private static final String LOG_TAG = "FloatingActionButton";
    public static final int SIZE_AUTO = -1;
    public static final int SIZE_MINI = 1;
    public static final int SIZE_NORMAL = 0;
    private ColorStateList mBackgroundTint;
    private PorterDuff$Mode mBackgroundTintMode;
    private int mBorderWidth;
    boolean mCompatPadding;
    private AppCompatImageHelper mImageHelper;
    int mImagePadding;
    private FloatingActionButtonImpl mImpl;
    private int mMaxImageSize;
    private int mRippleColor;
    final Rect mShadowPadding;
    private int mSize;
    private final Rect mTouchArea;
    
    public FloatingActionButton(final Context context) {
        this(context, null);
    }
    
    public FloatingActionButton(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public FloatingActionButton(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mShadowPadding = new Rect();
        this.mTouchArea = new Rect();
        ThemeUtils.checkAppCompatTheme(context);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.FloatingActionButton, n, R.style.Widget_Design_FloatingActionButton);
        this.mBackgroundTint = obtainStyledAttributes.getColorStateList(R.styleable.FloatingActionButton_backgroundTint);
        this.mBackgroundTintMode = ViewUtils.parseTintMode(obtainStyledAttributes.getInt(R.styleable.FloatingActionButton_backgroundTintMode, -1), null);
        this.mRippleColor = obtainStyledAttributes.getColor(R.styleable.FloatingActionButton_rippleColor, 0);
        this.mSize = obtainStyledAttributes.getInt(R.styleable.FloatingActionButton_fabSize, -1);
        this.mBorderWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.FloatingActionButton_borderWidth, 0);
        final float dimension = obtainStyledAttributes.getDimension(R.styleable.FloatingActionButton_elevation, 0.0f);
        final float dimension2 = obtainStyledAttributes.getDimension(R.styleable.FloatingActionButton_pressedTranslationZ, 0.0f);
        this.mCompatPadding = obtainStyledAttributes.getBoolean(R.styleable.FloatingActionButton_useCompatPadding, false);
        obtainStyledAttributes.recycle();
        (this.mImageHelper = new AppCompatImageHelper((ImageView)this)).loadFromAttributes(set, n);
        this.mMaxImageSize = (int)this.getResources().getDimension(R.dimen.design_fab_image_size);
        this.getImpl().setBackgroundDrawable(this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
        this.getImpl().setElevation(dimension);
        this.getImpl().setPressedTranslationZ(dimension2);
    }
    
    static /* synthetic */ void access$001(final FloatingActionButton floatingActionButton, final Drawable backgroundDrawable) {
        floatingActionButton.setBackgroundDrawable(backgroundDrawable);
    }
    
    private FloatingActionButtonImpl createImpl() {
        if (Build$VERSION.SDK_INT < 21) {
            return new FloatingActionButtonImpl(this, new ShadowDelegateImpl());
        }
        return new FloatingActionButtonLollipop(this, new ShadowDelegateImpl());
    }
    
    private FloatingActionButtonImpl getImpl() {
        if (this.mImpl == null) {
            this.mImpl = this.createImpl();
        }
        return this.mImpl;
    }
    
    private int getSizeDimension(int n) {
        final Resources resources = this.getResources();
        switch (n) {
            default: {
                return resources.getDimensionPixelSize(R.dimen.design_fab_size_normal);
            }
            case -1: {
                if (Math.max(resources.getConfiguration().screenWidthDp, resources.getConfiguration().screenHeightDp) >= 470) {
                    n = this.getSizeDimension(0);
                }
                else {
                    n = this.getSizeDimension(1);
                }
                return n;
            }
            case 1: {
                return resources.getDimensionPixelSize(R.dimen.design_fab_size_mini);
            }
        }
    }
    
    private static int resolveAdjustedSize(final int a, int min) {
        final int mode = View$MeasureSpec.getMode(min);
        final int size = View$MeasureSpec.getSize(min);
        min = a;
        switch (mode) {
            default: {
                min = a;
                return min;
            }
            case 1073741824: {
                min = size;
                return min;
            }
            case Integer.MIN_VALUE: {
                min = Math.min(a, size);
            }
            case 0: {
                return min;
            }
        }
    }
    
    @Nullable
    private FloatingActionButtonImpl.InternalVisibilityChangedListener wrapOnVisibilityChangedListener(@Nullable final OnVisibilityChangedListener onVisibilityChangedListener) {
        if (onVisibilityChangedListener != null) {
            return new FloatingActionButtonImpl.InternalVisibilityChangedListener() {
                @Override
                public void onHidden() {
                    onVisibilityChangedListener.onHidden(FloatingActionButton.this);
                }
                
                @Override
                public void onShown() {
                    onVisibilityChangedListener.onShown(FloatingActionButton.this);
                }
            };
        }
        return null;
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.getImpl().onDrawableStateChanged(this.getDrawableState());
    }
    
    @Nullable
    public ColorStateList getBackgroundTintList() {
        return this.mBackgroundTint;
    }
    
    @Nullable
    public PorterDuff$Mode getBackgroundTintMode() {
        return this.mBackgroundTintMode;
    }
    
    public float getCompatElevation() {
        return this.getImpl().getElevation();
    }
    
    @NonNull
    public Drawable getContentBackground() {
        return this.getImpl().getContentBackground();
    }
    
    public boolean getContentRect(@NonNull final Rect rect) {
        if (!ViewCompat.isLaidOut((View)this)) {
            return false;
        }
        rect.set(0, 0, this.getWidth(), this.getHeight());
        rect.left += this.mShadowPadding.left;
        rect.top += this.mShadowPadding.top;
        rect.right -= this.mShadowPadding.right;
        rect.bottom -= this.mShadowPadding.bottom;
        return true;
    }
    
    @ColorInt
    public int getRippleColor() {
        return this.mRippleColor;
    }
    
    public int getSize() {
        return this.mSize;
    }
    
    int getSizeDimension() {
        return this.getSizeDimension(this.mSize);
    }
    
    public boolean getUseCompatPadding() {
        return this.mCompatPadding;
    }
    
    public void hide() {
        this.hide(null);
    }
    
    public void hide(@Nullable final OnVisibilityChangedListener onVisibilityChangedListener) {
        this.hide(onVisibilityChangedListener, true);
    }
    
    void hide(@Nullable final OnVisibilityChangedListener onVisibilityChangedListener, final boolean b) {
        this.getImpl().hide(this.wrapOnVisibilityChangedListener(onVisibilityChangedListener), b);
    }
    
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.getImpl().jumpDrawableToCurrentState();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.getImpl().onAttachedToWindow();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.getImpl().onDetachedFromWindow();
    }
    
    protected void onMeasure(int min, final int n) {
        final int sizeDimension = this.getSizeDimension();
        this.mImagePadding = (sizeDimension - this.mMaxImageSize) / 2;
        this.getImpl().updatePadding();
        min = Math.min(resolveAdjustedSize(sizeDimension, min), resolveAdjustedSize(sizeDimension, n));
        this.setMeasuredDimension(this.mShadowPadding.left + min + this.mShadowPadding.right, min + this.mShadowPadding.top + this.mShadowPadding.bottom);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0: {
                if (this.getContentRect(this.mTouchArea) && !this.mTouchArea.contains((int)motionEvent.getX(), (int)motionEvent.getY())) {
                    return false;
                }
                break;
            }
        }
        return super.onTouchEvent(motionEvent);
    }
    
    public void setBackgroundColor(final int n) {
        Log.i("FloatingActionButton", "Setting a custom background is not supported.");
    }
    
    public void setBackgroundDrawable(final Drawable drawable) {
        Log.i("FloatingActionButton", "Setting a custom background is not supported.");
    }
    
    public void setBackgroundResource(final int n) {
        Log.i("FloatingActionButton", "Setting a custom background is not supported.");
    }
    
    public void setBackgroundTintList(@Nullable final ColorStateList list) {
        if (this.mBackgroundTint != list) {
            this.mBackgroundTint = list;
            this.getImpl().setBackgroundTintList(list);
        }
    }
    
    public void setBackgroundTintMode(@Nullable final PorterDuff$Mode porterDuff$Mode) {
        if (this.mBackgroundTintMode != porterDuff$Mode) {
            this.mBackgroundTintMode = porterDuff$Mode;
            this.getImpl().setBackgroundTintMode(porterDuff$Mode);
        }
    }
    
    public void setCompatElevation(final float elevation) {
        this.getImpl().setElevation(elevation);
    }
    
    public void setImageResource(@DrawableRes final int imageResource) {
        this.mImageHelper.setImageResource(imageResource);
    }
    
    public void setRippleColor(@ColorInt final int n) {
        if (this.mRippleColor != n) {
            this.mRippleColor = n;
            this.getImpl().setRippleColor(n);
        }
    }
    
    public void setSize(final int mSize) {
        if (mSize != this.mSize) {
            this.mSize = mSize;
            this.requestLayout();
        }
    }
    
    public void setUseCompatPadding(final boolean mCompatPadding) {
        if (this.mCompatPadding != mCompatPadding) {
            this.mCompatPadding = mCompatPadding;
            this.getImpl().onCompatShadowChanged();
        }
    }
    
    public void show() {
        this.show(null);
    }
    
    public void show(@Nullable final OnVisibilityChangedListener onVisibilityChangedListener) {
        this.show(onVisibilityChangedListener, true);
    }
    
    void show(final OnVisibilityChangedListener onVisibilityChangedListener, final boolean b) {
        this.getImpl().show(this.wrapOnVisibilityChangedListener(onVisibilityChangedListener), b);
    }
    
    public static class Behavior extends CoordinatorLayout.Behavior<FloatingActionButton>
    {
        private static final boolean AUTO_HIDE_DEFAULT = true;
        private boolean mAutoHideEnabled;
        private OnVisibilityChangedListener mInternalAutoHideListener;
        private Rect mTmpRect;
        
        public Behavior() {
            this.mAutoHideEnabled = true;
        }
        
        public Behavior(final Context context, final AttributeSet set) {
            super(context, set);
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.FloatingActionButton_Behavior_Layout);
            this.mAutoHideEnabled = obtainStyledAttributes.getBoolean(R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide, true);
            obtainStyledAttributes.recycle();
        }
        
        private static boolean isBottomSheet(@NonNull final View view) {
            final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
            return layoutParams instanceof LayoutParams && ((LayoutParams)layoutParams).getBehavior() instanceof BottomSheetBehavior;
        }
        
        private void offsetIfNeeded(final CoordinatorLayout coordinatorLayout, final FloatingActionButton floatingActionButton) {
            int bottom = 0;
            final Rect mShadowPadding = floatingActionButton.mShadowPadding;
            if (mShadowPadding != null && mShadowPadding.centerX() > 0 && mShadowPadding.centerY() > 0) {
                final LayoutParams layoutParams = (LayoutParams)floatingActionButton.getLayoutParams();
                int right;
                if (floatingActionButton.getRight() < coordinatorLayout.getWidth() - layoutParams.rightMargin) {
                    if (floatingActionButton.getLeft() > layoutParams.leftMargin) {
                        right = 0;
                    }
                    else {
                        right = -mShadowPadding.left;
                    }
                }
                else {
                    right = mShadowPadding.right;
                }
                if (floatingActionButton.getBottom() < coordinatorLayout.getHeight() - layoutParams.bottomMargin) {
                    if (floatingActionButton.getTop() <= layoutParams.topMargin) {
                        bottom = -mShadowPadding.top;
                    }
                }
                else {
                    bottom = mShadowPadding.bottom;
                }
                if (bottom != 0) {
                    ViewCompat.offsetTopAndBottom((View)floatingActionButton, bottom);
                }
                if (right != 0) {
                    ViewCompat.offsetLeftAndRight((View)floatingActionButton, right);
                }
            }
        }
        
        private boolean shouldUpdateVisibility(final View view, final FloatingActionButton floatingActionButton) {
            final LayoutParams layoutParams = (LayoutParams)floatingActionButton.getLayoutParams();
            return this.mAutoHideEnabled && layoutParams.getAnchorId() == view.getId() && floatingActionButton.getUserSetVisibility() == 0;
        }
        
        private boolean updateFabVisibilityForAppBarLayout(final CoordinatorLayout coordinatorLayout, final AppBarLayout appBarLayout, final FloatingActionButton floatingActionButton) {
            if (this.shouldUpdateVisibility((View)appBarLayout, floatingActionButton)) {
                if (this.mTmpRect == null) {
                    this.mTmpRect = new Rect();
                }
                final Rect mTmpRect = this.mTmpRect;
                ViewGroupUtils.getDescendantRect(coordinatorLayout, (View)appBarLayout, mTmpRect);
                if (mTmpRect.bottom > appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
                    floatingActionButton.show(this.mInternalAutoHideListener, false);
                }
                else {
                    floatingActionButton.hide(this.mInternalAutoHideListener, false);
                }
                return true;
            }
            return false;
        }
        
        private boolean updateFabVisibilityForBottomSheet(final View view, final FloatingActionButton floatingActionButton) {
            if (this.shouldUpdateVisibility(view, floatingActionButton)) {
                if (view.getTop() >= ((LayoutParams)floatingActionButton.getLayoutParams()).topMargin + floatingActionButton.getHeight() / 2) {
                    floatingActionButton.show(this.mInternalAutoHideListener, false);
                }
                else {
                    floatingActionButton.hide(this.mInternalAutoHideListener, false);
                }
                return true;
            }
            return false;
        }
        
        public boolean getInsetDodgeRect(@NonNull final CoordinatorLayout coordinatorLayout, @NonNull final FloatingActionButton floatingActionButton, @NonNull final Rect rect) {
            final Rect mShadowPadding = floatingActionButton.mShadowPadding;
            rect.set(floatingActionButton.getLeft() + mShadowPadding.left, floatingActionButton.getTop() + mShadowPadding.top, floatingActionButton.getRight() - mShadowPadding.right, floatingActionButton.getBottom() - mShadowPadding.bottom);
            return true;
        }
        
        public boolean isAutoHideEnabled() {
            return this.mAutoHideEnabled;
        }
        
        @Override
        public void onAttachedToLayoutParams(@NonNull final LayoutParams layoutParams) {
            if (layoutParams.dodgeInsetEdges == 0) {
                layoutParams.dodgeInsetEdges = 80;
            }
        }
        
        public boolean onDependentViewChanged(final CoordinatorLayout coordinatorLayout, final FloatingActionButton floatingActionButton, final View view) {
            if (!(view instanceof AppBarLayout)) {
                if (isBottomSheet(view)) {
                    this.updateFabVisibilityForBottomSheet(view, floatingActionButton);
                }
            }
            else {
                this.updateFabVisibilityForAppBarLayout(coordinatorLayout, (AppBarLayout)view, floatingActionButton);
            }
            return false;
        }
        
        public boolean onLayoutChild(final CoordinatorLayout coordinatorLayout, final FloatingActionButton floatingActionButton, final int n) {
            final List<View> dependencies = coordinatorLayout.getDependencies((View)floatingActionButton);
            for (int size = dependencies.size(), i = 0; i < size; ++i) {
                final View view = dependencies.get(i);
                if (!(view instanceof AppBarLayout)) {
                    if (isBottomSheet(view)) {
                        if (this.updateFabVisibilityForBottomSheet(view, floatingActionButton)) {
                            break;
                        }
                    }
                }
                else if (this.updateFabVisibilityForAppBarLayout(coordinatorLayout, (AppBarLayout)view, floatingActionButton)) {
                    break;
                }
            }
            coordinatorLayout.onLayoutChild((View)floatingActionButton, n);
            this.offsetIfNeeded(coordinatorLayout, floatingActionButton);
            return true;
        }
        
        public void setAutoHideEnabled(final boolean mAutoHideEnabled) {
            this.mAutoHideEnabled = mAutoHideEnabled;
        }
        
        @VisibleForTesting
        void setInternalAutoHideListener(final OnVisibilityChangedListener mInternalAutoHideListener) {
            this.mInternalAutoHideListener = mInternalAutoHideListener;
        }
    }
    
    public abstract static class OnVisibilityChangedListener
    {
        public void onHidden(final FloatingActionButton floatingActionButton) {
        }
        
        public void onShown(final FloatingActionButton floatingActionButton) {
        }
    }
    
    private class ShadowDelegateImpl implements ShadowViewDelegate
    {
        ShadowDelegateImpl() {
        }
        
        @Override
        public float getRadius() {
            return FloatingActionButton.this.getSizeDimension() / 2.0f;
        }
        
        @Override
        public boolean isCompatPaddingEnabled() {
            return FloatingActionButton.this.mCompatPadding;
        }
        
        @Override
        public void setBackgroundDrawable(final Drawable drawable) {
            FloatingActionButton.access$001(FloatingActionButton.this, drawable);
        }
        
        @Override
        public void setShadowPadding(final int n, final int n2, final int n3, final int n4) {
            FloatingActionButton.this.mShadowPadding.set(n, n2, n3, n4);
            FloatingActionButton.this.setPadding(FloatingActionButton.this.mImagePadding + n, FloatingActionButton.this.mImagePadding + n2, FloatingActionButton.this.mImagePadding + n3, FloatingActionButton.this.mImagePadding + n4);
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface Size {
    }
}
