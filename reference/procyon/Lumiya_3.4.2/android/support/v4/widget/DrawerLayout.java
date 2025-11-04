// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.widget;

import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.v4.view.AbsSavedState;
import android.content.res.TypedArray;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.annotation.DrawableRes;
import android.support.annotation.RestrictTo;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import android.view.KeyEvent;
import android.view.ViewGroup$MarginLayoutParams;
import android.support.v4.view.GravityCompat;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.view.ViewGroup$LayoutParams;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.annotation.TargetApi;
import android.view.WindowInsets;
import android.view.View$OnApplyWindowInsetsListener;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.graphics.drawable.Drawable;
import android.graphics.Paint;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

public class DrawerLayout extends ViewGroup
{
    private static final boolean ALLOW_EDGE_LOCK = false;
    static final boolean CAN_HIDE_DESCENDANTS;
    private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
    private static final int DEFAULT_SCRIM_COLOR = -1728053248;
    private static final int DRAWER_ELEVATION = 10;
    static final int[] LAYOUT_ATTRS;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN = 2;
    public static final int LOCK_MODE_UNDEFINED = 3;
    public static final int LOCK_MODE_UNLOCKED = 0;
    private static final int MIN_DRAWER_MARGIN = 64;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final int PEEK_DELAY = 160;
    private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "DrawerLayout";
    private static final int[] THEME_ATTRS;
    private static final float TOUCH_SLOP_SENSITIVITY = 1.0f;
    private final ChildAccessibilityDelegate mChildAccessibilityDelegate;
    private boolean mChildrenCanceledTouch;
    private boolean mDisallowInterceptRequested;
    private boolean mDrawStatusBarBackground;
    private float mDrawerElevation;
    private int mDrawerState;
    private boolean mFirstLayout;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private Object mLastInsets;
    private final ViewDragCallback mLeftCallback;
    private final ViewDragHelper mLeftDragger;
    @Nullable
    private DrawerListener mListener;
    private List<DrawerListener> mListeners;
    private int mLockModeEnd;
    private int mLockModeLeft;
    private int mLockModeRight;
    private int mLockModeStart;
    private int mMinDrawerMargin;
    private final ArrayList<View> mNonDrawerViews;
    private final ViewDragCallback mRightCallback;
    private final ViewDragHelper mRightDragger;
    private int mScrimColor;
    private float mScrimOpacity;
    private Paint mScrimPaint;
    private Drawable mShadowEnd;
    private Drawable mShadowLeft;
    private Drawable mShadowLeftResolved;
    private Drawable mShadowRight;
    private Drawable mShadowRightResolved;
    private Drawable mShadowStart;
    private Drawable mStatusBarBackground;
    private CharSequence mTitleLeft;
    private CharSequence mTitleRight;
    
    static {
        final boolean b = false;
        THEME_ATTRS = new int[] { 16843828 };
        LAYOUT_ATTRS = new int[] { 16842931 };
        CAN_HIDE_DESCENDANTS = (Build$VERSION.SDK_INT >= 19);
        SET_DRAWER_SHADOW_FROM_ELEVATION = (Build$VERSION.SDK_INT >= 21 || b);
    }
    
    public DrawerLayout(final Context context) {
        this(context, null);
    }
    
    public DrawerLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public DrawerLayout(Context obtainStyledAttributes, final AttributeSet set, final int n) {
        super(obtainStyledAttributes, set, n);
        this.mChildAccessibilityDelegate = new ChildAccessibilityDelegate();
        this.mScrimColor = -1728053248;
        this.mScrimPaint = new Paint();
        this.mFirstLayout = true;
        this.mLockModeLeft = 3;
        this.mLockModeRight = 3;
        this.mLockModeStart = 3;
        this.mLockModeEnd = 3;
        this.mShadowStart = null;
        this.mShadowEnd = null;
        this.mShadowLeft = null;
        this.mShadowRight = null;
        this.setDescendantFocusability(262144);
        final float density = this.getResources().getDisplayMetrics().density;
        this.mMinDrawerMargin = (int)(64.0f * density + 0.5f);
        final float n2 = 400.0f * density;
        this.mLeftCallback = new ViewDragCallback(3);
        this.mRightCallback = new ViewDragCallback(5);
        (this.mLeftDragger = ViewDragHelper.create(this, 1.0f, (ViewDragHelper.Callback)this.mLeftCallback)).setEdgeTrackingEnabled(1);
        this.mLeftDragger.setMinVelocity(n2);
        this.mLeftCallback.setDragger(this.mLeftDragger);
        (this.mRightDragger = ViewDragHelper.create(this, 1.0f, (ViewDragHelper.Callback)this.mRightCallback)).setEdgeTrackingEnabled(2);
        this.mRightDragger.setMinVelocity(n2);
        this.mRightCallback.setDragger(this.mRightDragger);
        this.setFocusableInTouchMode(true);
        ViewCompat.setImportantForAccessibility((View)this, 1);
        ViewCompat.setAccessibilityDelegate((View)this, new AccessibilityDelegate());
        ViewGroupCompat.setMotionEventSplittingEnabled(this, false);
        if (ViewCompat.getFitsSystemWindows((View)this)) {
            if (Build$VERSION.SDK_INT < 21) {
                this.mStatusBarBackground = null;
            }
            else {
                this.setOnApplyWindowInsetsListener((View$OnApplyWindowInsetsListener)new View$OnApplyWindowInsetsListener() {
                    @TargetApi(21)
                    public WindowInsets onApplyWindowInsets(final View view, final WindowInsets windowInsets) {
                        boolean b = false;
                        final DrawerLayout drawerLayout = (DrawerLayout)view;
                        if (windowInsets.getSystemWindowInsetTop() > 0) {
                            b = true;
                        }
                        drawerLayout.setChildInsets(windowInsets, b);
                        return windowInsets.consumeSystemWindowInsets();
                    }
                });
                this.setSystemUiVisibility(1280);
                obtainStyledAttributes = (Context)obtainStyledAttributes.obtainStyledAttributes(DrawerLayout.THEME_ATTRS);
                try {
                    this.mStatusBarBackground = ((TypedArray)obtainStyledAttributes).getDrawable(0);
                }
                finally {
                    ((TypedArray)obtainStyledAttributes).recycle();
                }
            }
        }
        this.mDrawerElevation = density * 10.0f;
        this.mNonDrawerViews = new ArrayList<View>();
    }
    
    static String gravityToString(final int i) {
        if ((i & 0x3) == 0x3) {
            return "LEFT";
        }
        if ((i & 0x5) != 0x5) {
            return Integer.toHexString(i);
        }
        return "RIGHT";
    }
    
    private static boolean hasOpaqueBackground(final View view) {
        boolean b = false;
        final Drawable background = view.getBackground();
        if (background == null) {
            return false;
        }
        if (background.getOpacity() == -1) {
            b = true;
        }
        return b;
    }
    
    private boolean hasPeekingDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            if (((LayoutParams)this.getChildAt(i).getLayoutParams()).isPeeking) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasVisibleDrawer() {
        return this.findVisibleDrawer() != null;
    }
    
    static boolean includeChildForAccessibility(final View view) {
        return ViewCompat.getImportantForAccessibility(view) != 4 && ViewCompat.getImportantForAccessibility(view) != 2;
    }
    
    private boolean mirror(final Drawable drawable, final int n) {
        if (drawable != null && DrawableCompat.isAutoMirrored(drawable)) {
            DrawableCompat.setLayoutDirection(drawable, n);
            return true;
        }
        return false;
    }
    
    private Drawable resolveLeftShadow() {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        if (layoutDirection != 0) {
            if (this.mShadowEnd != null) {
                this.mirror(this.mShadowEnd, layoutDirection);
                return this.mShadowEnd;
            }
        }
        else if (this.mShadowStart != null) {
            this.mirror(this.mShadowStart, layoutDirection);
            return this.mShadowStart;
        }
        return this.mShadowLeft;
    }
    
    private Drawable resolveRightShadow() {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        if (layoutDirection != 0) {
            if (this.mShadowStart != null) {
                this.mirror(this.mShadowStart, layoutDirection);
                return this.mShadowStart;
            }
        }
        else if (this.mShadowEnd != null) {
            this.mirror(this.mShadowEnd, layoutDirection);
            return this.mShadowEnd;
        }
        return this.mShadowRight;
    }
    
    private void resolveShadowDrawables() {
        if (!DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
            this.mShadowLeftResolved = this.resolveLeftShadow();
            this.mShadowRightResolved = this.resolveRightShadow();
        }
    }
    
    private void updateChildrenImportantForAccessibility(final View view, final boolean b) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if ((b || this.isDrawerView(child)) && (!b || child != view)) {
                ViewCompat.setImportantForAccessibility(child, 4);
            }
            else {
                ViewCompat.setImportantForAccessibility(child, 1);
            }
        }
    }
    
    public void addDrawerListener(@NonNull final DrawerListener drawerListener) {
        if (drawerListener != null) {
            if (this.mListeners == null) {
                this.mListeners = new ArrayList<DrawerListener>();
            }
            this.mListeners.add(drawerListener);
        }
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        final int n3 = 0;
        if (this.getDescendantFocusability() != 393216) {
            final int childCount = this.getChildCount();
            int i = 0;
            int n4 = 0;
            while (i < childCount) {
                final View child = this.getChildAt(i);
                if (!this.isDrawerView(child)) {
                    this.mNonDrawerViews.add(child);
                }
                else if (this.isDrawerOpen(child)) {
                    n4 = 1;
                    child.addFocusables((ArrayList)list, n, n2);
                }
                ++i;
            }
            if (n4 == 0) {
                for (int size = this.mNonDrawerViews.size(), j = n3; j < size; ++j) {
                    final View view = this.mNonDrawerViews.get(j);
                    if (view.getVisibility() == 0) {
                        view.addFocusables((ArrayList)list, n, n2);
                    }
                }
            }
            this.mNonDrawerViews.clear();
        }
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        super.addView(view, n, viewGroup$LayoutParams);
        if (this.findOpenDrawer() == null && !this.isDrawerView(view)) {
            ViewCompat.setImportantForAccessibility(view, 1);
        }
        else {
            ViewCompat.setImportantForAccessibility(view, 4);
        }
        if (!DrawerLayout.CAN_HIDE_DESCENDANTS) {
            ViewCompat.setAccessibilityDelegate(view, this.mChildAccessibilityDelegate);
        }
    }
    
    void cancelChildViewTouch() {
        int i = 0;
        if (!this.mChildrenCanceledTouch) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            while (i < this.getChildCount()) {
                this.getChildAt(i).dispatchTouchEvent(obtain);
                ++i;
            }
            obtain.recycle();
            this.mChildrenCanceledTouch = true;
        }
    }
    
    boolean checkDrawerViewAbsoluteGravity(final View view, final int n) {
        return (this.getDrawerViewAbsoluteGravity(view) & n) == n;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        boolean b = false;
        if (viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams)) {
            b = true;
        }
        return b;
    }
    
    public void closeDrawer(final int n) {
        this.closeDrawer(n, true);
    }
    
    public void closeDrawer(final int n, final boolean b) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        if (drawerWithGravity != null) {
            this.closeDrawer(drawerWithGravity, b);
            return;
        }
        throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(n));
    }
    
    public void closeDrawer(final View view) {
        this.closeDrawer(view, true);
    }
    
    public void closeDrawer(final View obj, final boolean b) {
        if (this.isDrawerView(obj)) {
            final LayoutParams layoutParams = (LayoutParams)obj.getLayoutParams();
            if (!this.mFirstLayout) {
                if (!b) {
                    this.moveDrawerToOffset(obj, 0.0f);
                    this.updateDrawerState(layoutParams.gravity, 0, obj);
                    obj.setVisibility(4);
                }
                else {
                    layoutParams.openState |= 0x4;
                    if (!this.checkDrawerViewAbsoluteGravity(obj, 3)) {
                        this.mRightDragger.smoothSlideViewTo(obj, this.getWidth(), obj.getTop());
                    }
                    else {
                        this.mLeftDragger.smoothSlideViewTo(obj, -obj.getWidth(), obj.getTop());
                    }
                }
            }
            else {
                layoutParams.onScreen = 0.0f;
                layoutParams.openState = 0;
            }
            this.invalidate();
            return;
        }
        throw new IllegalArgumentException("View " + obj + " is not a sliding drawer");
    }
    
    public void closeDrawers() {
        this.closeDrawers(false);
    }
    
    void closeDrawers(final boolean b) {
        final int childCount = this.getChildCount();
        int i = 0;
        int n = 0;
        while (i < childCount) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            int n2 = 0;
            Label_0065: {
                if (!this.isDrawerView(child)) {
                    n2 = n;
                }
                else {
                    if (b) {
                        n2 = n;
                        if (!layoutParams.isPeeking) {
                            break Label_0065;
                        }
                    }
                    final int width = child.getWidth();
                    boolean b2;
                    if (!this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        b2 = ((n | (this.mRightDragger.smoothSlideViewTo(child, this.getWidth(), child.getTop()) ? 1 : 0)) != 0x0);
                    }
                    else {
                        b2 = ((n | (this.mLeftDragger.smoothSlideViewTo(child, -width, child.getTop()) ? 1 : 0)) != 0x0);
                    }
                    layoutParams.isPeeking = false;
                    n2 = (b2 ? 1 : 0);
                }
            }
            ++i;
            n = n2;
        }
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (n != 0) {
            this.invalidate();
        }
    }
    
    public void computeScroll() {
        final int childCount = this.getChildCount();
        float max = 0.0f;
        for (int i = 0; i < childCount; ++i) {
            max = Math.max(max, ((LayoutParams)this.getChildAt(i).getLayoutParams()).onScreen);
        }
        this.mScrimOpacity = max;
        final boolean continueSettling = this.mLeftDragger.continueSettling(true);
        final boolean continueSettling2 = this.mRightDragger.continueSettling(true);
        if (continueSettling || continueSettling2) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    void dispatchOnDrawerClosed(View rootView) {
        final LayoutParams layoutParams = (LayoutParams)rootView.getLayoutParams();
        if ((layoutParams.openState & 0x1) == 0x1) {
            layoutParams.openState = 0;
            if (this.mListeners != null) {
                for (int i = this.mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerClosed(rootView);
                }
            }
            this.updateChildrenImportantForAccessibility(rootView, false);
            if (this.hasWindowFocus()) {
                rootView = this.getRootView();
                if (rootView != null) {
                    rootView.sendAccessibilityEvent(32);
                }
            }
        }
    }
    
    void dispatchOnDrawerOpened(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if ((layoutParams.openState & 0x1) == 0x0) {
            layoutParams.openState = 1;
            if (this.mListeners != null) {
                for (int i = this.mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerOpened(view);
                }
            }
            this.updateChildrenImportantForAccessibility(view, true);
            if (this.hasWindowFocus()) {
                this.sendAccessibilityEvent(32);
            }
        }
    }
    
    void dispatchOnDrawerSlide(final View view, final float n) {
        if (this.mListeners != null) {
            for (int i = this.mListeners.size() - 1; i >= 0; --i) {
                this.mListeners.get(i).onDrawerSlide(view, n);
            }
        }
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final int height = this.getHeight();
        final boolean contentView = this.isContentView(view);
        int n2 = 0;
        final int n3 = 0;
        int width = this.getWidth();
        final int save = canvas.save();
        if (!contentView) {
            n2 = n3;
        }
        else {
            int n11;
            for (int childCount = this.getChildCount(), i = 0; i < childCount; i = n11) {
                final View child = this.getChildAt(i);
                int n5;
                int n6;
                if (child != view && child.getVisibility() == 0) {
                    if (hasOpaqueBackground(child) && this.isDrawerView(child)) {
                        if (child.getHeight() >= height) {
                            if (!this.checkDrawerViewAbsoluteGravity(child, 3)) {
                                final int left = child.getLeft();
                                if (left >= width) {
                                    final int n4 = n2;
                                    n5 = width;
                                    n6 = n4;
                                }
                                else {
                                    n6 = n2;
                                    n5 = left;
                                }
                            }
                            else {
                                final int right = child.getRight();
                                if (right > n2) {
                                    n2 = right;
                                }
                                final int n7 = n2;
                                n5 = width;
                                n6 = n7;
                            }
                        }
                        else {
                            final int n8 = n2;
                            n5 = width;
                            n6 = n8;
                        }
                    }
                    else {
                        final int n9 = n2;
                        n5 = width;
                        n6 = n9;
                    }
                }
                else {
                    final int n10 = n2;
                    n5 = width;
                    n6 = n10;
                }
                n11 = i + 1;
                final int n12 = n5;
                n2 = n6;
                width = n12;
            }
            canvas.clipRect(n2, 0, width, this.getHeight());
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restoreToCount(save);
        if (this.mScrimOpacity <= 0.0f || !contentView) {
            if (this.mShadowLeftResolved != null && this.checkDrawerViewAbsoluteGravity(view, 3)) {
                final int intrinsicWidth = this.mShadowLeftResolved.getIntrinsicWidth();
                final int right2 = view.getRight();
                final float max = Math.max(0.0f, Math.min(right2 / (float)this.mLeftDragger.getEdgeSize(), 1.0f));
                this.mShadowLeftResolved.setBounds(right2, view.getTop(), intrinsicWidth + right2, view.getBottom());
                this.mShadowLeftResolved.setAlpha((int)(255.0f * max));
                this.mShadowLeftResolved.draw(canvas);
            }
            else if (this.mShadowRightResolved != null && this.checkDrawerViewAbsoluteGravity(view, 5)) {
                final int intrinsicWidth2 = this.mShadowRightResolved.getIntrinsicWidth();
                final int left2 = view.getLeft();
                final float max2 = Math.max(0.0f, Math.min((this.getWidth() - left2) / (float)this.mRightDragger.getEdgeSize(), 1.0f));
                this.mShadowRightResolved.setBounds(left2 - intrinsicWidth2, view.getTop(), left2, view.getBottom());
                this.mShadowRightResolved.setAlpha((int)(255.0f * max2));
                this.mShadowRightResolved.draw(canvas);
            }
        }
        else {
            this.mScrimPaint.setColor((int)(((this.mScrimColor & 0xFF000000) >>> 24) * this.mScrimOpacity) << 24 | (this.mScrimColor & 0xFFFFFF));
            canvas.drawRect((float)n2, 0.0f, (float)width, (float)this.getHeight(), this.mScrimPaint);
        }
        return drawChild;
    }
    
    View findDrawerWithGravity(int i) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection((View)this));
        int childCount;
        View child;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if ((this.getDrawerViewAbsoluteGravity(child) & 0x7) == (absoluteGravity & 0x7)) {
                return child;
            }
        }
        return null;
    }
    
    View findOpenDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if ((((LayoutParams)child.getLayoutParams()).openState & 0x1) == 0x1) {
                return child;
            }
        }
        return null;
    }
    
    View findVisibleDrawer() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child) && this.isDrawerVisible(child)) {
                return child;
            }
        }
        return null;
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new LayoutParams(-1, -1);
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        LayoutParams layoutParams;
        if (!(viewGroup$LayoutParams instanceof LayoutParams)) {
            if (!(viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams)) {
                layoutParams = new LayoutParams(viewGroup$LayoutParams);
            }
            else {
                layoutParams = new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
            }
        }
        else {
            layoutParams = new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        return (ViewGroup$LayoutParams)layoutParams;
    }
    
    public float getDrawerElevation() {
        if (!DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
            return 0.0f;
        }
        return this.mDrawerElevation;
    }
    
    public int getDrawerLockMode(int n) {
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        switch (n) {
            case 3: {
                if (this.mLockModeLeft != 3) {
                    return this.mLockModeLeft;
                }
                if (layoutDirection != 0) {
                    n = this.mLockModeEnd;
                }
                else {
                    n = this.mLockModeStart;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
            case 5: {
                if (this.mLockModeRight != 3) {
                    return this.mLockModeRight;
                }
                if (layoutDirection != 0) {
                    n = this.mLockModeStart;
                }
                else {
                    n = this.mLockModeEnd;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
            case 8388611: {
                if (this.mLockModeStart != 3) {
                    return this.mLockModeStart;
                }
                if (layoutDirection != 0) {
                    n = this.mLockModeRight;
                }
                else {
                    n = this.mLockModeLeft;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
            case 8388613: {
                if (this.mLockModeEnd != 3) {
                    return this.mLockModeEnd;
                }
                if (layoutDirection != 0) {
                    n = this.mLockModeLeft;
                }
                else {
                    n = this.mLockModeRight;
                }
                if (n != 3) {
                    return n;
                }
                break;
            }
        }
        return 0;
    }
    
    public int getDrawerLockMode(final View obj) {
        if (this.isDrawerView(obj)) {
            return this.getDrawerLockMode(((LayoutParams)obj.getLayoutParams()).gravity);
        }
        throw new IllegalArgumentException("View " + obj + " is not a drawer");
    }
    
    @Nullable
    public CharSequence getDrawerTitle(int absoluteGravity) {
        absoluteGravity = GravityCompat.getAbsoluteGravity(absoluteGravity, ViewCompat.getLayoutDirection((View)this));
        if (absoluteGravity == 3) {
            return this.mTitleLeft;
        }
        if (absoluteGravity != 5) {
            return null;
        }
        return this.mTitleRight;
    }
    
    int getDrawerViewAbsoluteGravity(final View view) {
        return GravityCompat.getAbsoluteGravity(((LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection((View)this));
    }
    
    float getDrawerViewOffset(final View view) {
        return ((LayoutParams)view.getLayoutParams()).onScreen;
    }
    
    public Drawable getStatusBarBackgroundDrawable() {
        return this.mStatusBarBackground;
    }
    
    boolean isContentView(final View view) {
        return ((LayoutParams)view.getLayoutParams()).gravity == 0;
    }
    
    public boolean isDrawerOpen(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        return drawerWithGravity != null && this.isDrawerOpen(drawerWithGravity);
    }
    
    public boolean isDrawerOpen(final View obj) {
        if (this.isDrawerView(obj)) {
            return (((LayoutParams)obj.getLayoutParams()).openState & 0x1) == 0x1;
        }
        throw new IllegalArgumentException("View " + obj + " is not a drawer");
    }
    
    boolean isDrawerView(final View view) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(((LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(view));
        return (absoluteGravity & 0x3) != 0x0 || (absoluteGravity & 0x5) != 0x0;
    }
    
    public boolean isDrawerVisible(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        return drawerWithGravity != null && this.isDrawerVisible(drawerWithGravity);
    }
    
    public boolean isDrawerVisible(final View obj) {
        if (this.isDrawerView(obj)) {
            return ((LayoutParams)obj.getLayoutParams()).onScreen > 0.0f;
        }
        throw new IllegalArgumentException("View " + obj + " is not a drawer");
    }
    
    void moveDrawerToOffset(final View view, final float n) {
        final float drawerViewOffset = this.getDrawerViewOffset(view);
        final int width = view.getWidth();
        int n2 = (int)(width * n) - (int)(drawerViewOffset * width);
        if (!this.checkDrawerViewAbsoluteGravity(view, 3)) {
            n2 = -n2;
        }
        view.offsetLeftAndRight(n2);
        this.setDrawerViewOffset(view, n);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            int systemWindowInsetTop;
            if (Build$VERSION.SDK_INT < 21) {
                systemWindowInsetTop = 0;
            }
            else if (this.mLastInsets == null) {
                systemWindowInsetTop = 0;
            }
            else {
                systemWindowInsetTop = ((WindowInsets)this.mLastInsets).getSystemWindowInsetTop();
            }
            if (systemWindowInsetTop > 0) {
                this.mStatusBarBackground.setBounds(0, 0, this.getWidth(), systemWindowInsetTop);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        boolean b = false;
        final int actionMasked = motionEvent.getActionMasked();
        final boolean shouldInterceptTouchEvent = this.mLeftDragger.shouldInterceptTouchEvent(motionEvent);
        final boolean shouldInterceptTouchEvent2 = this.mRightDragger.shouldInterceptTouchEvent(motionEvent);
        boolean b2 = false;
        switch (actionMasked) {
            default: {
                b2 = false;
                break;
            }
            case 0: {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                if (this.mScrimOpacity > 0.0f) {
                    final View topChildUnder = this.mLeftDragger.findTopChildUnder((int)x, (int)y);
                    b2 = (topChildUnder != null && this.isContentView(topChildUnder));
                }
                else {
                    b2 = false;
                }
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                break;
            }
            case 2: {
                if (!this.mLeftDragger.checkTouchSlop(3)) {
                    b2 = false;
                    break;
                }
                this.mLeftCallback.removeCallbacks();
                this.mRightCallback.removeCallbacks();
                b2 = false;
                break;
            }
            case 1:
            case 3: {
                this.closeDrawers(true);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                b2 = false;
                break;
            }
        }
        if ((shouldInterceptTouchEvent | shouldInterceptTouchEvent2) || b2 || this.hasPeekingDrawer() || this.mChildrenCanceledTouch) {
            b = true;
        }
        return b;
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n == 4 && this.hasVisibleDrawer()) {
            keyEvent.startTracking();
            return true;
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        boolean b = false;
        if (n != 4) {
            return super.onKeyUp(n, keyEvent);
        }
        final View visibleDrawer = this.findVisibleDrawer();
        if (visibleDrawer != null && this.getDrawerLockMode(visibleDrawer) == 0) {
            this.closeDrawers();
        }
        if (visibleDrawer != null) {
            b = true;
        }
        return b;
    }
    
    protected void onLayout(final boolean b, int topMargin, final int n, int i, final int n2) {
        this.mInLayout = true;
        final int n3 = i - topMargin;
        int childCount;
        View child;
        LayoutParams layoutParams;
        int measuredWidth;
        int measuredHeight;
        int n4;
        float n5;
        int n6;
        int n7;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                layoutParams = (LayoutParams)child.getLayoutParams();
                if (!this.isContentView(child)) {
                    measuredWidth = child.getMeasuredWidth();
                    measuredHeight = child.getMeasuredHeight();
                    if (!this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        n4 = n3 - (int)(measuredWidth * layoutParams.onScreen);
                        n5 = (n3 - n4) / (float)measuredWidth;
                    }
                    else {
                        topMargin = -measuredWidth;
                        n4 = (int)(measuredWidth * layoutParams.onScreen) + topMargin;
                        n5 = (measuredWidth + n4) / (float)measuredWidth;
                    }
                    if (n5 != layoutParams.onScreen) {
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    switch (layoutParams.gravity & 0x70) {
                        default: {
                            child.layout(n4, layoutParams.topMargin, measuredWidth + n4, measuredHeight + layoutParams.topMargin);
                            break;
                        }
                        case 80: {
                            topMargin = n2 - n;
                            child.layout(n4, topMargin - layoutParams.bottomMargin - child.getMeasuredHeight(), measuredWidth + n4, topMargin - layoutParams.bottomMargin);
                            break;
                        }
                        case 16: {
                            n7 = n2 - n;
                            topMargin = (n7 - measuredHeight) / 2;
                            if (topMargin >= layoutParams.topMargin) {
                                if (topMargin + measuredHeight > n7 - layoutParams.bottomMargin) {
                                    topMargin = n7 - layoutParams.bottomMargin - measuredHeight;
                                }
                            }
                            else {
                                topMargin = layoutParams.topMargin;
                            }
                            child.layout(n4, topMargin, measuredWidth + n4, measuredHeight + topMargin);
                            break;
                        }
                    }
                    if (n6 != 0) {
                        this.setDrawerViewOffset(child, n5);
                    }
                    if (layoutParams.onScreen > 0.0f) {
                        topMargin = 0;
                    }
                    else {
                        topMargin = 4;
                    }
                    if (child.getVisibility() != topMargin) {
                        child.setVisibility(topMargin);
                    }
                }
                else {
                    child.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + child.getMeasuredWidth(), layoutParams.topMargin + child.getMeasuredHeight());
                }
            }
        }
        this.mInLayout = false;
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        final int size = View$MeasureSpec.getSize(n);
        final int size2 = View$MeasureSpec.getSize(n2);
        int n3;
        int n4;
        if (mode == 1073741824 && mode2 == 1073741824) {
            n3 = size2;
            n4 = size;
        }
        else {
            if (!this.isInEditMode()) {
                throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
            }
            int n5 = size;
            if (mode != Integer.MIN_VALUE) {
                if (mode != 0) {
                    n5 = size;
                }
                else {
                    n5 = 300;
                }
            }
            if (mode2 != Integer.MIN_VALUE) {
                if (mode2 != 0) {
                    final int n6 = size2;
                    n4 = n5;
                    n3 = n6;
                }
                else {
                    final int n7 = 300;
                    n4 = n5;
                    n3 = n7;
                }
            }
            else {
                final int n8 = n5;
                n3 = size2;
                n4 = n8;
            }
        }
        this.setMeasuredDimension(n4, n3);
        final boolean b = this.mLastInsets != null && ViewCompat.getFitsSystemWindows((View)this);
        final int layoutDirection = ViewCompat.getLayoutDirection((View)this);
        int n9 = 0;
        int n10 = 0;
        int n11;
        int n12;
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i, n10 = n11, n9 = n12) {
            final View child = this.getChildAt(i);
            n11 = n10;
            n12 = n9;
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (b) {
                    final int absoluteGravity = GravityCompat.getAbsoluteGravity(layoutParams.gravity, layoutDirection);
                    if (!ViewCompat.getFitsSystemWindows(child)) {
                        if (Build$VERSION.SDK_INT >= 21) {
                            WindowInsets windowInsets = (WindowInsets)this.mLastInsets;
                            if (absoluteGravity != 3) {
                                if (absoluteGravity == 5) {
                                    windowInsets = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
                                }
                            }
                            else {
                                windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
                            }
                            layoutParams.leftMargin = windowInsets.getSystemWindowInsetLeft();
                            layoutParams.topMargin = windowInsets.getSystemWindowInsetTop();
                            layoutParams.rightMargin = windowInsets.getSystemWindowInsetRight();
                            layoutParams.bottomMargin = windowInsets.getSystemWindowInsetBottom();
                        }
                    }
                    else if (Build$VERSION.SDK_INT >= 21) {
                        WindowInsets windowInsets2 = (WindowInsets)this.mLastInsets;
                        if (absoluteGravity != 3) {
                            if (absoluteGravity == 5) {
                                windowInsets2 = windowInsets2.replaceSystemWindowInsets(0, windowInsets2.getSystemWindowInsetTop(), windowInsets2.getSystemWindowInsetRight(), windowInsets2.getSystemWindowInsetBottom());
                            }
                        }
                        else {
                            windowInsets2 = windowInsets2.replaceSystemWindowInsets(windowInsets2.getSystemWindowInsetLeft(), windowInsets2.getSystemWindowInsetTop(), 0, windowInsets2.getSystemWindowInsetBottom());
                        }
                        child.dispatchApplyWindowInsets(windowInsets2);
                    }
                }
                if (!this.isContentView(child)) {
                    if (!this.isDrawerView(child)) {
                        throw new IllegalStateException("Child " + child + " at index " + i + " does not have a valid layout_gravity - must be Gravity.LEFT, " + "Gravity.RIGHT or Gravity.NO_GRAVITY");
                    }
                    if (DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION && ViewCompat.getElevation(child) != this.mDrawerElevation) {
                        ViewCompat.setElevation(child, this.mDrawerElevation);
                    }
                    final int n13 = this.getDrawerViewAbsoluteGravity(child) & 0x7;
                    boolean b2;
                    if (n13 != 3) {
                        b2 = false;
                    }
                    else {
                        b2 = true;
                    }
                    if ((b2 && n9 != 0) || (!b2 && n10 != 0)) {
                        throw new IllegalStateException("Child drawer has absolute gravity " + gravityToString(n13) + " but this " + "DrawerLayout" + " already has a " + "drawer view along that edge");
                    }
                    int n15;
                    int n16;
                    if (!b2) {
                        final int n14 = 1;
                        n15 = n9;
                        n16 = n14;
                    }
                    else {
                        final int n17 = 1;
                        n16 = n10;
                        n15 = n17;
                    }
                    child.measure(getChildMeasureSpec(n, this.mMinDrawerMargin + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), getChildMeasureSpec(n2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                    n11 = n16;
                    n12 = n15;
                }
                else {
                    child.measure(View$MeasureSpec.makeMeasureSpec(n4 - layoutParams.leftMargin - layoutParams.rightMargin, 1073741824), View$MeasureSpec.makeMeasureSpec(n3 - layoutParams.topMargin - layoutParams.bottomMargin, 1073741824));
                    n12 = n9;
                    n11 = n10;
                }
            }
        }
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            if (savedState.openDrawerGravity != 0) {
                final View drawerWithGravity = this.findDrawerWithGravity(savedState.openDrawerGravity);
                if (drawerWithGravity != null) {
                    this.openDrawer(drawerWithGravity);
                }
            }
            if (savedState.lockModeLeft != 3) {
                this.setDrawerLockMode(savedState.lockModeLeft, 3);
            }
            if (savedState.lockModeRight != 3) {
                this.setDrawerLockMode(savedState.lockModeRight, 5);
            }
            if (savedState.lockModeStart != 3) {
                this.setDrawerLockMode(savedState.lockModeStart, 8388611);
            }
            if (savedState.lockModeEnd != 3) {
                this.setDrawerLockMode(savedState.lockModeEnd, 8388613);
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    public void onRtlPropertiesChanged(final int n) {
        this.resolveShadowDrawables();
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final LayoutParams layoutParams = (LayoutParams)this.getChildAt(i).getLayoutParams();
            boolean b;
            if (layoutParams.openState != 1) {
                b = false;
            }
            else {
                b = true;
            }
            final boolean b2 = layoutParams.openState == 2;
            if (b || b2) {
                savedState.openDrawerGravity = layoutParams.gravity;
                break;
            }
        }
        savedState.lockModeLeft = this.mLockModeLeft;
        savedState.lockModeRight = this.mLockModeRight;
        savedState.lockModeStart = this.mLockModeStart;
        savedState.lockModeEnd = this.mLockModeEnd;
        return (Parcelable)savedState;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        this.mLeftDragger.processTouchEvent(motionEvent);
        this.mRightDragger.processTouchEvent(motionEvent);
        switch (motionEvent.getAction() & 0xFF) {
            case 0: {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                break;
            }
            case 1: {
                final float x2 = motionEvent.getX();
                final float y2 = motionEvent.getY();
                final View topChildUnder = this.mLeftDragger.findTopChildUnder((int)x2, (int)y2);
                boolean b;
                if (topChildUnder != null && this.isContentView(topChildUnder)) {
                    final float n = x2 - this.mInitialMotionX;
                    final float n2 = y2 - this.mInitialMotionY;
                    final int touchSlop = this.mLeftDragger.getTouchSlop();
                    if (n * n + n2 * n2 < touchSlop * touchSlop) {
                        final View openDrawer = this.findOpenDrawer();
                        b = (openDrawer == null || this.getDrawerLockMode(openDrawer) == 2);
                    }
                    else {
                        b = true;
                    }
                }
                else {
                    b = true;
                }
                this.closeDrawers(b);
                this.mDisallowInterceptRequested = false;
                break;
            }
            case 3: {
                this.closeDrawers(true);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                break;
            }
        }
        return true;
    }
    
    public void openDrawer(final int n) {
        this.openDrawer(n, true);
    }
    
    public void openDrawer(final int n, final boolean b) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        if (drawerWithGravity != null) {
            this.openDrawer(drawerWithGravity, b);
            return;
        }
        throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(n));
    }
    
    public void openDrawer(final View view) {
        this.openDrawer(view, true);
    }
    
    public void openDrawer(final View obj, final boolean b) {
        if (this.isDrawerView(obj)) {
            final LayoutParams layoutParams = (LayoutParams)obj.getLayoutParams();
            if (!this.mFirstLayout) {
                if (!b) {
                    this.moveDrawerToOffset(obj, 1.0f);
                    this.updateDrawerState(layoutParams.gravity, 0, obj);
                    obj.setVisibility(0);
                }
                else {
                    layoutParams.openState |= 0x2;
                    if (!this.checkDrawerViewAbsoluteGravity(obj, 3)) {
                        this.mRightDragger.smoothSlideViewTo(obj, this.getWidth() - obj.getWidth(), obj.getTop());
                    }
                    else {
                        this.mLeftDragger.smoothSlideViewTo(obj, 0, obj.getTop());
                    }
                }
            }
            else {
                layoutParams.onScreen = 1.0f;
                layoutParams.openState = 1;
                this.updateChildrenImportantForAccessibility(obj, true);
            }
            this.invalidate();
            return;
        }
        throw new IllegalArgumentException("View " + obj + " is not a sliding drawer");
    }
    
    public void removeDrawerListener(@NonNull final DrawerListener drawerListener) {
        if (drawerListener == null) {
            return;
        }
        if (this.mListeners != null) {
            this.mListeners.remove(drawerListener);
        }
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean mDisallowInterceptRequested) {
        super.requestDisallowInterceptTouchEvent(mDisallowInterceptRequested);
        if (this.mDisallowInterceptRequested = mDisallowInterceptRequested) {
            this.closeDrawers(true);
        }
    }
    
    public void requestLayout() {
        if (!this.mInLayout) {
            super.requestLayout();
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setChildInsets(final Object mLastInsets, final boolean mDrawStatusBarBackground) {
        final boolean b = false;
        this.mLastInsets = mLastInsets;
        this.mDrawStatusBarBackground = mDrawStatusBarBackground;
        boolean willNotDraw;
        if (mDrawStatusBarBackground) {
            willNotDraw = b;
        }
        else {
            willNotDraw = b;
            if (this.getBackground() == null) {
                willNotDraw = true;
            }
        }
        this.setWillNotDraw(willNotDraw);
        this.requestLayout();
    }
    
    public void setDrawerElevation(final float mDrawerElevation) {
        int i = 0;
        this.mDrawerElevation = mDrawerElevation;
        while (i < this.getChildCount()) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child)) {
                ViewCompat.setElevation(child, this.mDrawerElevation);
            }
            ++i;
        }
    }
    
    @Deprecated
    public void setDrawerListener(final DrawerListener mListener) {
        if (this.mListener != null) {
            this.removeDrawerListener(this.mListener);
        }
        if (mListener != null) {
            this.addDrawerListener(mListener);
        }
        this.mListener = mListener;
    }
    
    public void setDrawerLockMode(final int n) {
        this.setDrawerLockMode(n, 3);
        this.setDrawerLockMode(n, 5);
    }
    
    public void setDrawerLockMode(final int n, final int n2) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(n2, ViewCompat.getLayoutDirection((View)this));
        switch (n2) {
            case 3: {
                this.mLockModeLeft = n;
                break;
            }
            case 5: {
                this.mLockModeRight = n;
                break;
            }
            case 8388611: {
                this.mLockModeStart = n;
                break;
            }
            case 8388613: {
                this.mLockModeEnd = n;
                break;
            }
        }
        if (n != 0) {
            ViewDragHelper viewDragHelper;
            if (absoluteGravity != 3) {
                viewDragHelper = this.mRightDragger;
            }
            else {
                viewDragHelper = this.mLeftDragger;
            }
            viewDragHelper.cancel();
        }
        switch (n) {
            case 2: {
                final View drawerWithGravity = this.findDrawerWithGravity(absoluteGravity);
                if (drawerWithGravity != null) {
                    this.openDrawer(drawerWithGravity);
                    break;
                }
                break;
            }
            case 1: {
                final View drawerWithGravity2 = this.findDrawerWithGravity(absoluteGravity);
                if (drawerWithGravity2 != null) {
                    this.closeDrawer(drawerWithGravity2);
                    break;
                }
                break;
            }
        }
    }
    
    public void setDrawerLockMode(final int n, final View obj) {
        if (this.isDrawerView(obj)) {
            this.setDrawerLockMode(n, ((LayoutParams)obj.getLayoutParams()).gravity);
            return;
        }
        throw new IllegalArgumentException("View " + obj + " is not a " + "drawer with appropriate layout_gravity");
    }
    
    public void setDrawerShadow(@DrawableRes final int n, final int n2) {
        this.setDrawerShadow(ContextCompat.getDrawable(this.getContext(), n), n2);
    }
    
    public void setDrawerShadow(final Drawable drawable, final int n) {
        if (!DrawerLayout.SET_DRAWER_SHADOW_FROM_ELEVATION) {
            if ((n & 0x800003) != 0x800003) {
                if ((n & 0x800005) != 0x800005) {
                    if ((n & 0x3) != 0x3) {
                        if ((n & 0x5) != 0x5) {
                            return;
                        }
                        this.mShadowRight = drawable;
                    }
                    else {
                        this.mShadowLeft = drawable;
                    }
                }
                else {
                    this.mShadowEnd = drawable;
                }
            }
            else {
                this.mShadowStart = drawable;
            }
            this.resolveShadowDrawables();
            this.invalidate();
        }
    }
    
    public void setDrawerTitle(int absoluteGravity, final CharSequence charSequence) {
        absoluteGravity = GravityCompat.getAbsoluteGravity(absoluteGravity, ViewCompat.getLayoutDirection((View)this));
        if (absoluteGravity != 3) {
            if (absoluteGravity == 5) {
                this.mTitleRight = charSequence;
            }
        }
        else {
            this.mTitleLeft = charSequence;
        }
    }
    
    void setDrawerViewOffset(final View view, final float onScreen) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (onScreen == layoutParams.onScreen) {
            return;
        }
        this.dispatchOnDrawerSlide(view, layoutParams.onScreen = onScreen);
    }
    
    public void setScrimColor(@ColorInt final int mScrimColor) {
        this.mScrimColor = mScrimColor;
        this.invalidate();
    }
    
    public void setStatusBarBackground(final int n) {
        Drawable drawable;
        if (n == 0) {
            drawable = null;
        }
        else {
            drawable = ContextCompat.getDrawable(this.getContext(), n);
        }
        this.mStatusBarBackground = drawable;
        this.invalidate();
    }
    
    public void setStatusBarBackground(final Drawable mStatusBarBackground) {
        this.mStatusBarBackground = mStatusBarBackground;
        this.invalidate();
    }
    
    public void setStatusBarBackgroundColor(@ColorInt final int n) {
        this.mStatusBarBackground = (Drawable)new ColorDrawable(n);
        this.invalidate();
    }
    
    void updateDrawerState(int mDrawerState, int i, final View view) {
        mDrawerState = 1;
        final int viewDragState = this.mLeftDragger.getViewDragState();
        final int viewDragState2 = this.mRightDragger.getViewDragState();
        if (viewDragState != 1 && viewDragState2 != 1) {
            if (viewDragState != 2 && viewDragState2 != 2) {
                mDrawerState = 0;
            }
            else {
                mDrawerState = 2;
            }
        }
        if (view != null && i == 0) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            if (layoutParams.onScreen == 0.0f) {
                this.dispatchOnDrawerClosed(view);
            }
            else if (layoutParams.onScreen == 1.0f) {
                this.dispatchOnDrawerOpened(view);
            }
        }
        if (mDrawerState != this.mDrawerState) {
            this.mDrawerState = mDrawerState;
            if (this.mListeners != null) {
                for (i = this.mListeners.size() - 1; i >= 0; --i) {
                    this.mListeners.get(i).onDrawerStateChanged(mDrawerState);
                }
            }
        }
    }
    
    class AccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private final Rect mTmpRect;
        
        AccessibilityDelegate() {
            this.mTmpRect = new Rect();
        }
        
        private void addChildrenForAccessibility(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                final View child = viewGroup.getChildAt(i);
                if (DrawerLayout.includeChildForAccessibility(child)) {
                    accessibilityNodeInfoCompat.addChild(child);
                }
            }
        }
        
        private void copyNodeInfoNoChildren(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat2) {
            final Rect mTmpRect = this.mTmpRect;
            accessibilityNodeInfoCompat2.getBoundsInParent(mTmpRect);
            accessibilityNodeInfoCompat.setBoundsInParent(mTmpRect);
            accessibilityNodeInfoCompat2.getBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setVisibleToUser(accessibilityNodeInfoCompat2.isVisibleToUser());
            accessibilityNodeInfoCompat.setPackageName(accessibilityNodeInfoCompat2.getPackageName());
            accessibilityNodeInfoCompat.setClassName(accessibilityNodeInfoCompat2.getClassName());
            accessibilityNodeInfoCompat.setContentDescription(accessibilityNodeInfoCompat2.getContentDescription());
            accessibilityNodeInfoCompat.setEnabled(accessibilityNodeInfoCompat2.isEnabled());
            accessibilityNodeInfoCompat.setClickable(accessibilityNodeInfoCompat2.isClickable());
            accessibilityNodeInfoCompat.setFocusable(accessibilityNodeInfoCompat2.isFocusable());
            accessibilityNodeInfoCompat.setFocused(accessibilityNodeInfoCompat2.isFocused());
            accessibilityNodeInfoCompat.setAccessibilityFocused(accessibilityNodeInfoCompat2.isAccessibilityFocused());
            accessibilityNodeInfoCompat.setSelected(accessibilityNodeInfoCompat2.isSelected());
            accessibilityNodeInfoCompat.setLongClickable(accessibilityNodeInfoCompat2.isLongClickable());
            accessibilityNodeInfoCompat.addAction(accessibilityNodeInfoCompat2.getActions());
        }
        
        @Override
        public boolean dispatchPopulateAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            if (accessibilityEvent.getEventType() != 32) {
                return super.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
            }
            final List text = accessibilityEvent.getText();
            final View visibleDrawer = DrawerLayout.this.findVisibleDrawer();
            if (visibleDrawer != null) {
                final CharSequence drawerTitle = DrawerLayout.this.getDrawerTitle(DrawerLayout.this.getDrawerViewAbsoluteGravity(visibleDrawer));
                if (drawerTitle != null) {
                    text.add(drawerTitle);
                }
            }
            return true;
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)DrawerLayout.class.getName());
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final View source, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (!DrawerLayout.CAN_HIDE_DESCENDANTS) {
                final AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(accessibilityNodeInfoCompat);
                super.onInitializeAccessibilityNodeInfo(source, obtain);
                accessibilityNodeInfoCompat.setSource(source);
                final ViewParent parentForAccessibility = ViewCompat.getParentForAccessibility(source);
                if (parentForAccessibility instanceof View) {
                    accessibilityNodeInfoCompat.setParent((View)parentForAccessibility);
                }
                this.copyNodeInfoNoChildren(accessibilityNodeInfoCompat, obtain);
                obtain.recycle();
                this.addChildrenForAccessibility(accessibilityNodeInfoCompat, (ViewGroup)source);
            }
            else {
                super.onInitializeAccessibilityNodeInfo(source, accessibilityNodeInfoCompat);
            }
            accessibilityNodeInfoCompat.setClassName(DrawerLayout.class.getName());
            accessibilityNodeInfoCompat.setFocusable(false);
            accessibilityNodeInfoCompat.setFocused(false);
            accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_FOCUS);
            accessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLEAR_FOCUS);
        }
        
        @Override
        public boolean onRequestSendAccessibilityEvent(final ViewGroup viewGroup, final View view, final AccessibilityEvent accessibilityEvent) {
            return (DrawerLayout.CAN_HIDE_DESCENDANTS || DrawerLayout.includeChildForAccessibility(view)) && super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }
    
    static final class ChildAccessibilityDelegate extends AccessibilityDelegateCompat
    {
        @Override
        public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            if (!DrawerLayout.includeChildForAccessibility(view)) {
                accessibilityNodeInfoCompat.setParent(null);
            }
        }
    }
    
    public interface DrawerListener
    {
        void onDrawerClosed(final View p0);
        
        void onDrawerOpened(final View p0);
        
        void onDrawerSlide(final View p0, final float p1);
        
        void onDrawerStateChanged(final int p0);
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private @interface EdgeGravity {
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        private static final int FLAG_IS_CLOSING = 4;
        private static final int FLAG_IS_OPENED = 1;
        private static final int FLAG_IS_OPENING = 2;
        public int gravity;
        boolean isPeeking;
        float onScreen;
        int openState;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.gravity = 0;
        }
        
        public LayoutParams(final int n, final int n2, final int gravity) {
            this(n, n2);
            this.gravity = gravity;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.gravity = 0;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, DrawerLayout.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInt(0, 0);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.gravity = 0;
            this.gravity = layoutParams.gravity;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.gravity = 0;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.gravity = 0;
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private @interface LockMode {
    }
    
    protected static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int lockModeEnd;
        int lockModeLeft;
        int lockModeRight;
        int lockModeStart;
        int openDrawerGravity;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel, null);
                }
                
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        public SavedState(final Parcel parcel, final ClassLoader classLoader) {
            super(parcel, classLoader);
            this.openDrawerGravity = 0;
            this.openDrawerGravity = parcel.readInt();
            this.lockModeLeft = parcel.readInt();
            this.lockModeRight = parcel.readInt();
            this.lockModeStart = parcel.readInt();
            this.lockModeEnd = parcel.readInt();
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
            this.openDrawerGravity = 0;
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.openDrawerGravity);
            parcel.writeInt(this.lockModeLeft);
            parcel.writeInt(this.lockModeRight);
            parcel.writeInt(this.lockModeStart);
            parcel.writeInt(this.lockModeEnd);
        }
    }
    
    public abstract static class SimpleDrawerListener implements DrawerListener
    {
        @Override
        public void onDrawerClosed(final View view) {
        }
        
        @Override
        public void onDrawerOpened(final View view) {
        }
        
        @Override
        public void onDrawerSlide(final View view, final float n) {
        }
        
        @Override
        public void onDrawerStateChanged(final int n) {
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }
    
    private class ViewDragCallback extends Callback
    {
        private final int mAbsGravity;
        private ViewDragHelper mDragger;
        private final Runnable mPeekRunnable;
        
        ViewDragCallback(final int mAbsGravity) {
            this.mPeekRunnable = new Runnable() {
                @Override
                public void run() {
                    ViewDragCallback.this.peekDrawer();
                }
            };
            this.mAbsGravity = mAbsGravity;
        }
        
        private void closeOtherDrawer() {
            int n = 3;
            if (this.mAbsGravity == 3) {
                n = 5;
            }
            final View drawerWithGravity = DrawerLayout.this.findDrawerWithGravity(n);
            if (drawerWithGravity != null) {
                DrawerLayout.this.closeDrawer(drawerWithGravity);
            }
        }
        
        @Override
        public int clampViewPositionHorizontal(final View view, final int n, int width) {
            if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                width = DrawerLayout.this.getWidth();
                return Math.max(width - view.getWidth(), Math.min(n, width));
            }
            return Math.max(-view.getWidth(), Math.min(n, 0));
        }
        
        @Override
        public int clampViewPositionVertical(final View view, final int n, final int n2) {
            return view.getTop();
        }
        
        @Override
        public int getViewHorizontalDragRange(final View view) {
            int width = 0;
            if (DrawerLayout.this.isDrawerView(view)) {
                width = view.getWidth();
            }
            return width;
        }
        
        @Override
        public void onEdgeDragStarted(final int n, final int n2) {
            View view;
            if ((n & 0x1) != 0x1) {
                view = DrawerLayout.this.findDrawerWithGravity(5);
            }
            else {
                view = DrawerLayout.this.findDrawerWithGravity(3);
            }
            if (view != null && DrawerLayout.this.getDrawerLockMode(view) == 0) {
                this.mDragger.captureChildView(view, n2);
            }
        }
        
        @Override
        public boolean onEdgeLock(final int n) {
            return false;
        }
        
        @Override
        public void onEdgeTouched(final int n, final int n2) {
            DrawerLayout.this.postDelayed(this.mPeekRunnable, 160L);
        }
        
        @Override
        public void onViewCaptured(final View view, final int n) {
            ((LayoutParams)view.getLayoutParams()).isPeeking = false;
            this.closeOtherDrawer();
        }
        
        @Override
        public void onViewDragStateChanged(final int n) {
            DrawerLayout.this.updateDrawerState(this.mAbsGravity, n, this.mDragger.getCapturedView());
        }
        
        @Override
        public void onViewPositionChanged(final View view, int visibility, int width, final int n, final int n2) {
            width = view.getWidth();
            float n3;
            if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                n3 = (DrawerLayout.this.getWidth() - visibility) / (float)width;
            }
            else {
                n3 = (width + visibility) / (float)width;
            }
            DrawerLayout.this.setDrawerViewOffset(view, n3);
            if (n3 == 0.0f) {
                visibility = 4;
            }
            else {
                visibility = 0;
            }
            view.setVisibility(visibility);
            DrawerLayout.this.invalidate();
        }
        
        @Override
        public void onViewReleased(final View view, final float n, float drawerViewOffset) {
            int n2 = 1;
            final int n3 = 0;
            boolean b = false;
            drawerViewOffset = DrawerLayout.this.getDrawerViewOffset(view);
            final int width = view.getWidth();
            int n4 = 0;
            Label_0079: {
                if (!DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                    final int width2 = DrawerLayout.this.getWidth();
                    if (n < 0.0f) {
                        b = true;
                    }
                    if (b || (n == 0.0f && drawerViewOffset > 0.5f)) {
                        n4 = width2 - width;
                    }
                    else {
                        n4 = width2;
                    }
                }
                else {
                    if (n <= 0.0f) {
                        n2 = 0;
                    }
                    n4 = n3;
                    if (n2 == 0) {
                        if (n == 0.0f) {
                            n4 = n3;
                            if (drawerViewOffset > 0.5f) {
                                break Label_0079;
                            }
                        }
                        n4 = -width;
                    }
                }
            }
            this.mDragger.settleCapturedViewAt(n4, view.getTop());
            DrawerLayout.this.invalidate();
        }
        
        void peekDrawer() {
            int n = 0;
            final int edgeSize = this.mDragger.getEdgeSize();
            boolean b;
            if (this.mAbsGravity != 3) {
                b = false;
            }
            else {
                b = true;
            }
            View view;
            int n2;
            if (!b) {
                view = DrawerLayout.this.findDrawerWithGravity(5);
                n2 = DrawerLayout.this.getWidth() - edgeSize;
            }
            else {
                view = DrawerLayout.this.findDrawerWithGravity(3);
                if (view != null) {
                    n = -view.getWidth();
                }
                n2 = n + edgeSize;
            }
            if (view != null) {
                if (!b || view.getLeft() >= n2) {
                    if (b || view.getLeft() <= n2) {
                        return;
                    }
                }
                if (DrawerLayout.this.getDrawerLockMode(view) == 0) {
                    final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
                    this.mDragger.smoothSlideViewTo(view, n2, view.getTop());
                    layoutParams.isPeeking = true;
                    DrawerLayout.this.invalidate();
                    this.closeOtherDrawer();
                    DrawerLayout.this.cancelChildViewTouch();
                }
            }
        }
        
        public void removeCallbacks() {
            DrawerLayout.this.removeCallbacks(this.mPeekRunnable);
        }
        
        public void setDragger(final ViewDragHelper mDragger) {
            this.mDragger = mDragger;
        }
        
        @Override
        public boolean tryCaptureView(final View view, final int n) {
            final boolean b = false;
            boolean b2;
            if (!DrawerLayout.this.isDrawerView(view)) {
                b2 = b;
            }
            else {
                b2 = b;
                if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, this.mAbsGravity)) {
                    b2 = b;
                    if (DrawerLayout.this.getDrawerLockMode(view) == 0) {
                        b2 = true;
                    }
                }
            }
            return b2;
        }
    }
}
