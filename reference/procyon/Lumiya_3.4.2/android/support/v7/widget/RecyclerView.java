// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.annotation.RestrictTo;
import android.support.v4.view.AbsSavedState;
import java.lang.ref.WeakReference;
import java.util.Collections;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.annotation.CallSuper;
import android.graphics.Matrix;
import android.view.ViewGroup$MarginLayoutParams;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.database.Observable;
import android.os.SystemClock;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.View$MeasureSpec;
import android.view.Display;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.FocusFinder;
import android.widget.OverScroller;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.SparseArray;
import android.support.v4.os.TraceCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.view.accessibility.AccessibilityEvent;
import android.view.ViewParent;
import android.view.ViewGroup$LayoutParams;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.recyclerview.R;
import android.view.View;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.ViewConfiguration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.content.Context;
import android.os.Build$VERSION;
import android.view.VelocityTracker;
import android.graphics.RectF;
import android.graphics.Rect;
import android.support.v4.view.NestedScrollingChildHelper;
import java.util.List;
import java.util.ArrayList;
import android.support.annotation.VisibleForTesting;
import android.widget.EdgeEffect;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.ScrollingView;
import android.view.ViewGroup;

public class RecyclerView extends ViewGroup implements ScrollingView, NestedScrollingChild2
{
    static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
    private static final boolean ALLOW_THREAD_GAP_WORK;
    private static final int[] CLIP_TO_PADDING_ATTR;
    static final boolean DEBUG = false;
    static final boolean DISPATCH_TEMP_DETACH = false;
    private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION;
    static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
    static final long FOREVER_NS = Long.MAX_VALUE;
    public static final int HORIZONTAL = 0;
    private static final boolean IGNORE_DETACHED_FOCUSED_CHILD;
    private static final int INVALID_POINTER = -1;
    public static final int INVALID_TYPE = -1;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
    static final int MAX_SCROLL_DURATION = 2000;
    private static final int[] NESTED_SCROLLING_ATTRS;
    public static final long NO_ID = -1L;
    public static final int NO_POSITION = -1;
    static final boolean POST_UPDATES_ON_ANIMATION;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    static final String TAG = "RecyclerView";
    public static final int TOUCH_SLOP_DEFAULT = 0;
    public static final int TOUCH_SLOP_PAGING = 1;
    static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
    static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
    private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
    static final String TRACE_NESTED_PREFETCH_TAG = "RV Nested Prefetch";
    private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
    private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
    static final String TRACE_PREFETCH_TAG = "RV Prefetch";
    static final String TRACE_SCROLL_TAG = "RV Scroll";
    static final boolean VERBOSE_TRACING = false;
    public static final int VERTICAL = 1;
    static final Interpolator sQuinticInterpolator;
    RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
    private final AccessibilityManager mAccessibilityManager;
    private OnItemTouchListener mActiveOnItemTouchListener;
    Adapter mAdapter;
    AdapterHelper mAdapterHelper;
    boolean mAdapterUpdateDuringMeasure;
    private EdgeEffect mBottomGlow;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    ChildHelper mChildHelper;
    boolean mClipToPadding;
    boolean mDataSetHasChangedAfterLayout;
    private int mDispatchScrollCounter;
    private int mEatRequestLayout;
    private int mEatenAccessibilityChangeFlags;
    boolean mEnableFastScroller;
    @VisibleForTesting
    boolean mFirstLayoutComplete;
    GapWorker mGapWorker;
    boolean mHasFixedSize;
    private boolean mIgnoreMotionEventTillDown;
    private int mInitialTouchX;
    private int mInitialTouchY;
    boolean mIsAttached;
    ItemAnimator mItemAnimator;
    private ItemAnimatorListener mItemAnimatorListener;
    private Runnable mItemAnimatorRunner;
    final ArrayList<ItemDecoration> mItemDecorations;
    boolean mItemsAddedOrRemoved;
    boolean mItemsChanged;
    private int mLastTouchX;
    private int mLastTouchY;
    @VisibleForTesting
    LayoutManager mLayout;
    boolean mLayoutFrozen;
    private int mLayoutOrScrollCounter;
    boolean mLayoutRequestEaten;
    private EdgeEffect mLeftGlow;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private final int[] mMinMaxLayoutPositions;
    private final int[] mNestedOffsets;
    private final RecyclerViewDataObserver mObserver;
    private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
    private OnFlingListener mOnFlingListener;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners;
    @VisibleForTesting
    final List<ViewHolder> mPendingAccessibilityImportanceChange;
    private SavedState mPendingSavedState;
    boolean mPostedAnimatorRunner;
    GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
    private boolean mPreserveFocusAfterLayout;
    final Recycler mRecycler;
    RecyclerListener mRecyclerListener;
    private EdgeEffect mRightGlow;
    private float mScaledHorizontalScrollFactor;
    private float mScaledVerticalScrollFactor;
    private final int[] mScrollConsumed;
    private OnScrollListener mScrollListener;
    private List<OnScrollListener> mScrollListeners;
    private final int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    private NestedScrollingChildHelper mScrollingChildHelper;
    final State mState;
    final Rect mTempRect;
    private final Rect mTempRect2;
    final RectF mTempRectF;
    private EdgeEffect mTopGlow;
    private int mTouchSlop;
    final Runnable mUpdateChildViewsRunnable;
    private VelocityTracker mVelocityTracker;
    final ViewFlinger mViewFlinger;
    private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
    final ViewInfoStore mViewInfoStore;
    
    static {
        NESTED_SCROLLING_ATTRS = new int[] { 16843830 };
        CLIP_TO_PADDING_ATTR = new int[] { 16842987 };
        FORCE_INVALIDATE_DISPLAY_LIST = (Build$VERSION.SDK_INT == 18 || Build$VERSION.SDK_INT == 19 || Build$VERSION.SDK_INT == 20);
        ALLOW_SIZE_IN_UNSPECIFIED_SPEC = (Build$VERSION.SDK_INT >= 23);
        POST_UPDATES_ON_ANIMATION = (Build$VERSION.SDK_INT >= 16);
        ALLOW_THREAD_GAP_WORK = (Build$VERSION.SDK_INT >= 21);
        FORCE_ABS_FOCUS_SEARCH_DIRECTION = (Build$VERSION.SDK_INT <= 15);
        IGNORE_DETACHED_FOCUSED_CHILD = (Build$VERSION.SDK_INT <= 15);
        LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE };
        sQuinticInterpolator = (Interpolator)new Interpolator() {
            public float getInterpolation(float n) {
                --n;
                return n * (n * n * n * n) + 1.0f;
            }
        };
    }
    
    public RecyclerView(final Context context) {
        this(context, null);
    }
    
    public RecyclerView(final Context context, @Nullable final AttributeSet set) {
        this(context, set, 0);
    }
    
    public RecyclerView(final Context context, @Nullable final AttributeSet set, final int n) {
        GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry = null;
        final boolean b = true;
        super(context, set, n);
        this.mObserver = new RecyclerViewDataObserver();
        this.mRecycler = new Recycler();
        this.mViewInfoStore = new ViewInfoStore();
        this.mUpdateChildViewsRunnable = new Runnable() {
            @Override
            public void run() {
                if (!RecyclerView.this.mFirstLayoutComplete || RecyclerView.this.isLayoutRequested()) {
                    return;
                }
                if (!RecyclerView.this.mIsAttached) {
                    RecyclerView.this.requestLayout();
                    return;
                }
                if (!RecyclerView.this.mLayoutFrozen) {
                    RecyclerView.this.consumePendingUpdateOperations();
                    return;
                }
                RecyclerView.this.mLayoutRequestEaten = true;
            }
        };
        this.mTempRect = new Rect();
        this.mTempRect2 = new Rect();
        this.mTempRectF = new RectF();
        this.mItemDecorations = new ArrayList<ItemDecoration>();
        this.mOnItemTouchListeners = new ArrayList<OnItemTouchListener>();
        this.mEatRequestLayout = 0;
        this.mDataSetHasChangedAfterLayout = false;
        this.mLayoutOrScrollCounter = 0;
        this.mDispatchScrollCounter = 0;
        this.mItemAnimator = (ItemAnimator)new DefaultItemAnimator();
        this.mScrollState = 0;
        this.mScrollPointerId = -1;
        this.mScaledHorizontalScrollFactor = Float.MIN_VALUE;
        this.mScaledVerticalScrollFactor = Float.MIN_VALUE;
        this.mPreserveFocusAfterLayout = true;
        this.mViewFlinger = new ViewFlinger();
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            mPrefetchRegistry = new GapWorker.LayoutPrefetchRegistryImpl();
        }
        this.mPrefetchRegistry = mPrefetchRegistry;
        this.mState = new State();
        this.mItemsAddedOrRemoved = false;
        this.mItemsChanged = false;
        this.mItemAnimatorListener = (ItemAnimatorListener)new ItemAnimatorRestoreListener();
        this.mPostedAnimatorRunner = false;
        this.mMinMaxLayoutPositions = new int[2];
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.mNestedOffsets = new int[2];
        this.mPendingAccessibilityImportanceChange = new ArrayList<ViewHolder>();
        this.mItemAnimatorRunner = new Runnable() {
            @Override
            public void run() {
                if (RecyclerView.this.mItemAnimator != null) {
                    RecyclerView.this.mItemAnimator.runPendingAnimations();
                }
                RecyclerView.this.mPostedAnimatorRunner = false;
            }
        };
        this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback() {
            @Override
            public void processAppeared(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo, final ItemHolderInfo itemHolderInfo2) {
                RecyclerView.this.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2);
            }
            
            @Override
            public void processDisappeared(final ViewHolder viewHolder, @NonNull final ItemHolderInfo itemHolderInfo, @Nullable final ItemHolderInfo itemHolderInfo2) {
                RecyclerView.this.mRecycler.unscrapView(viewHolder);
                RecyclerView.this.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2);
            }
            
            @Override
            public void processPersistent(final ViewHolder viewHolder, @NonNull final ItemHolderInfo itemHolderInfo, @NonNull final ItemHolderInfo itemHolderInfo2) {
                viewHolder.setIsRecyclable(false);
                if (!RecyclerView.this.mDataSetHasChangedAfterLayout) {
                    if (RecyclerView.this.mItemAnimator.animatePersistence(viewHolder, itemHolderInfo, itemHolderInfo2)) {
                        RecyclerView.this.postAnimationRunner();
                    }
                }
                else if (RecyclerView.this.mItemAnimator.animateChange(viewHolder, viewHolder, itemHolderInfo, itemHolderInfo2)) {
                    RecyclerView.this.postAnimationRunner();
                }
            }
            
            @Override
            public void unused(final ViewHolder viewHolder) {
                RecyclerView.this.mLayout.removeAndRecycleView(viewHolder.itemView, RecyclerView.this.mRecycler);
            }
        };
        if (set == null) {
            this.mClipToPadding = true;
        }
        else {
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, RecyclerView.CLIP_TO_PADDING_ATTR, n, 0);
            this.mClipToPadding = obtainStyledAttributes.getBoolean(0, true);
            obtainStyledAttributes.recycle();
        }
        this.setScrollContainer(true);
        this.setFocusableInTouchMode(true);
        final ViewConfiguration value = ViewConfiguration.get(context);
        this.mTouchSlop = value.getScaledTouchSlop();
        this.mScaledHorizontalScrollFactor = ViewConfigurationCompat.getScaledHorizontalScrollFactor(value, context);
        this.mScaledVerticalScrollFactor = ViewConfigurationCompat.getScaledVerticalScrollFactor(value, context);
        this.mMinFlingVelocity = value.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = value.getScaledMaximumFlingVelocity();
        this.setWillNotDraw(this.getOverScrollMode() == 2);
        this.mItemAnimator.setListener(this.mItemAnimatorListener);
        this.initAdapterManager();
        this.initChildrenHelper();
        if (ViewCompat.getImportantForAccessibility((View)this) == 0) {
            ViewCompat.setImportantForAccessibility((View)this, 1);
        }
        this.mAccessibilityManager = (AccessibilityManager)this.getContext().getSystemService("accessibility");
        this.setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
        boolean boolean1;
        if (set == null) {
            this.setDescendantFocusability(262144);
            boolean1 = b;
        }
        else {
            final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, R.styleable.RecyclerView, n, 0);
            final String string = obtainStyledAttributes2.getString(R.styleable.RecyclerView_layoutManager);
            if (obtainStyledAttributes2.getInt(R.styleable.RecyclerView_android_descendantFocusability, -1) == -1) {
                this.setDescendantFocusability(262144);
            }
            if (this.mEnableFastScroller = obtainStyledAttributes2.getBoolean(R.styleable.RecyclerView_fastScrollEnabled, false)) {
                this.initFastScroller((StateListDrawable)obtainStyledAttributes2.getDrawable(R.styleable.RecyclerView_fastScrollVerticalThumbDrawable), obtainStyledAttributes2.getDrawable(R.styleable.RecyclerView_fastScrollVerticalTrackDrawable), (StateListDrawable)obtainStyledAttributes2.getDrawable(R.styleable.RecyclerView_fastScrollHorizontalThumbDrawable), obtainStyledAttributes2.getDrawable(R.styleable.RecyclerView_fastScrollHorizontalTrackDrawable));
            }
            obtainStyledAttributes2.recycle();
            this.createLayoutManager(context, string, set, n, 0);
            boolean1 = b;
            if (Build$VERSION.SDK_INT >= 21) {
                final TypedArray obtainStyledAttributes3 = context.obtainStyledAttributes(set, RecyclerView.NESTED_SCROLLING_ATTRS, n, 0);
                boolean1 = obtainStyledAttributes3.getBoolean(0, true);
                obtainStyledAttributes3.recycle();
            }
        }
        this.setNestedScrollingEnabled(boolean1);
    }
    
    static /* synthetic */ void access$000(final RecyclerView recyclerView, final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        recyclerView.attachViewToParent(view, n, viewGroup$LayoutParams);
    }
    
    static /* synthetic */ void access$100(final RecyclerView recyclerView, final int n) {
        recyclerView.detachViewFromParent(n);
    }
    
    static /* synthetic */ void access$1200(final RecyclerView recyclerView, final int n, final int n2) {
        recyclerView.setMeasuredDimension(n, n2);
    }
    
    static /* synthetic */ boolean access$700(final RecyclerView recyclerView) {
        return recyclerView.awakenScrollBars();
    }
    
    private void addAnimatingView(final ViewHolder viewHolder) {
        int n = 0;
        final View itemView = viewHolder.itemView;
        if (itemView.getParent() == this) {
            n = 1;
        }
        this.mRecycler.unscrapView(this.getChildViewHolder(itemView));
        if (!viewHolder.isTmpDetached()) {
            if (n != 0) {
                this.mChildHelper.hide(itemView);
            }
            else {
                this.mChildHelper.addView(itemView, true);
            }
        }
        else {
            this.mChildHelper.attachViewToParent(itemView, -1, itemView.getLayoutParams(), true);
        }
    }
    
    private void animateChange(@NonNull final ViewHolder mShadowingHolder, @NonNull final ViewHolder mShadowedHolder, @NonNull final ItemHolderInfo itemHolderInfo, @NonNull final ItemHolderInfo itemHolderInfo2, final boolean b, final boolean b2) {
        mShadowingHolder.setIsRecyclable(false);
        if (b) {
            this.addAnimatingView(mShadowingHolder);
        }
        if (mShadowingHolder != mShadowedHolder) {
            if (b2) {
                this.addAnimatingView(mShadowedHolder);
            }
            mShadowingHolder.mShadowedHolder = mShadowedHolder;
            this.addAnimatingView(mShadowingHolder);
            this.mRecycler.unscrapView(mShadowingHolder);
            mShadowedHolder.setIsRecyclable(false);
            mShadowedHolder.mShadowingHolder = mShadowingHolder;
        }
        if (this.mItemAnimator.animateChange(mShadowingHolder, mShadowedHolder, itemHolderInfo, itemHolderInfo2)) {
            this.postAnimationRunner();
        }
    }
    
    private void cancelTouch() {
        this.resetTouch();
        this.setScrollState(0);
    }
    
    static void clearNestedRecyclerViewIfNotNested(@NonNull final ViewHolder viewHolder) {
        if (viewHolder.mNestedRecyclerView != null) {
            View view = viewHolder.mNestedRecyclerView.get();
            while (view != null) {
                if (view == viewHolder.itemView) {
                    return;
                }
                final ViewParent parent = view.getParent();
                if (!(parent instanceof View)) {
                    view = null;
                }
                else {
                    view = (View)parent;
                }
            }
            viewHolder.mNestedRecyclerView = null;
        }
    }
    
    private void createLayoutManager(final Context p0, final String p1, final AttributeSet p2, final int p3, final int p4) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnonnull       5
        //     4: return         
        //     5: aload_2        
        //     6: invokevirtual   java/lang/String.trim:()Ljava/lang/String;
        //     9: astore_2       
        //    10: aload_2        
        //    11: invokevirtual   java/lang/String.isEmpty:()Z
        //    14: ifne            4
        //    17: aload_0        
        //    18: aload_1        
        //    19: aload_2        
        //    20: invokespecial   android/support/v7/widget/RecyclerView.getFullClassName:(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;
        //    23: astore          6
        //    25: aload_0        
        //    26: invokevirtual   android/support/v7/widget/RecyclerView.isInEditMode:()Z
        //    29: ifne            156
        //    32: aload_1        
        //    33: invokevirtual   android/content/Context.getClassLoader:()Ljava/lang/ClassLoader;
        //    36: astore_2       
        //    37: aload_2        
        //    38: aload           6
        //    40: invokevirtual   java/lang/ClassLoader.loadClass:(Ljava/lang/String;)Ljava/lang/Class;
        //    43: ldc             Landroid/support/v7/widget/RecyclerView$LayoutManager;.class
        //    45: invokevirtual   java/lang/Class.asSubclass:(Ljava/lang/Class;)Ljava/lang/Class;
        //    48: astore          7
        //    50: aload           7
        //    52: getstatic       android/support/v7/widget/RecyclerView.LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;
        //    55: invokevirtual   java/lang/Class.getConstructor:([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
        //    58: astore_2       
        //    59: iconst_4       
        //    60: anewarray       Ljava/lang/Object;
        //    63: astore          8
        //    65: aload           8
        //    67: iconst_0       
        //    68: aload_1        
        //    69: aastore        
        //    70: aload           8
        //    72: iconst_1       
        //    73: aload_3        
        //    74: aastore        
        //    75: aload           8
        //    77: iconst_2       
        //    78: iload           4
        //    80: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    83: aastore        
        //    84: aload           8
        //    86: iconst_3       
        //    87: iload           5
        //    89: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    92: aastore        
        //    93: aload           8
        //    95: astore_1       
        //    96: aload_2        
        //    97: iconst_1       
        //    98: invokevirtual   java/lang/reflect/Constructor.setAccessible:(Z)V
        //   101: aload_0        
        //   102: aload_2        
        //   103: aload_1        
        //   104: invokevirtual   java/lang/reflect/Constructor.newInstance:([Ljava/lang/Object;)Ljava/lang/Object;
        //   107: checkcast       Landroid/support/v7/widget/RecyclerView$LayoutManager;
        //   110: invokevirtual   android/support/v7/widget/RecyclerView.setLayoutManager:(Landroid/support/v7/widget/RecyclerView$LayoutManager;)V
        //   113: goto            4
        //   116: astore_1       
        //   117: new             Ljava/lang/IllegalStateException;
        //   120: dup            
        //   121: new             Ljava/lang/StringBuilder;
        //   124: dup            
        //   125: invokespecial   java/lang/StringBuilder.<init>:()V
        //   128: aload_3        
        //   129: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   134: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   137: ldc_w           ": Unable to find LayoutManager "
        //   140: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   143: aload           6
        //   145: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   148: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   151: aload_1        
        //   152: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   155: athrow         
        //   156: aload_0        
        //   157: invokevirtual   java/lang/Object.getClass:()Ljava/lang/Class;
        //   160: invokevirtual   java/lang/Class.getClassLoader:()Ljava/lang/ClassLoader;
        //   163: astore_2       
        //   164: goto            37
        //   167: astore          8
        //   169: aload           7
        //   171: iconst_0       
        //   172: anewarray       Ljava/lang/Class;
        //   175: invokevirtual   java/lang/Class.getConstructor:([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
        //   178: astore_2       
        //   179: aconst_null    
        //   180: astore_1       
        //   181: goto            96
        //   184: astore_1       
        //   185: aload_1        
        //   186: aload           8
        //   188: invokevirtual   java/lang/NoSuchMethodException.initCause:(Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //   191: pop            
        //   192: new             Ljava/lang/IllegalStateException;
        //   195: astore_2       
        //   196: new             Ljava/lang/StringBuilder;
        //   199: astore          8
        //   201: aload           8
        //   203: invokespecial   java/lang/StringBuilder.<init>:()V
        //   206: aload_2        
        //   207: aload           8
        //   209: aload_3        
        //   210: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   215: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   218: ldc_w           ": Error creating LayoutManager "
        //   221: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   224: aload           6
        //   226: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   229: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   232: aload_1        
        //   233: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   236: aload_2        
        //   237: athrow         
        //   238: astore_1       
        //   239: new             Ljava/lang/IllegalStateException;
        //   242: dup            
        //   243: new             Ljava/lang/StringBuilder;
        //   246: dup            
        //   247: invokespecial   java/lang/StringBuilder.<init>:()V
        //   250: aload_3        
        //   251: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   256: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   259: ldc_w           ": Could not instantiate the LayoutManager: "
        //   262: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   265: aload           6
        //   267: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   270: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   273: aload_1        
        //   274: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   277: athrow         
        //   278: astore_1       
        //   279: new             Ljava/lang/IllegalStateException;
        //   282: dup            
        //   283: new             Ljava/lang/StringBuilder;
        //   286: dup            
        //   287: invokespecial   java/lang/StringBuilder.<init>:()V
        //   290: aload_3        
        //   291: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   296: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   299: ldc_w           ": Could not instantiate the LayoutManager: "
        //   302: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   305: aload           6
        //   307: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   310: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   313: aload_1        
        //   314: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   317: athrow         
        //   318: astore_1       
        //   319: new             Ljava/lang/IllegalStateException;
        //   322: dup            
        //   323: new             Ljava/lang/StringBuilder;
        //   326: dup            
        //   327: invokespecial   java/lang/StringBuilder.<init>:()V
        //   330: aload_3        
        //   331: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   336: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   339: ldc_w           ": Cannot access non-public constructor "
        //   342: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   345: aload           6
        //   347: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   350: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   353: aload_1        
        //   354: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   357: athrow         
        //   358: astore_1       
        //   359: new             Ljava/lang/IllegalStateException;
        //   362: dup            
        //   363: new             Ljava/lang/StringBuilder;
        //   366: dup            
        //   367: invokespecial   java/lang/StringBuilder.<init>:()V
        //   370: aload_3        
        //   371: invokeinterface android/util/AttributeSet.getPositionDescription:()Ljava/lang/String;
        //   376: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   379: ldc_w           ": Class is not a LayoutManager "
        //   382: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   385: aload           6
        //   387: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   390: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   393: aload_1        
        //   394: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   397: athrow         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                         
        //  -----  -----  -----  -----  ---------------------------------------------
        //  25     37     116    156    Ljava/lang/ClassNotFoundException;
        //  25     37     238    278    Ljava/lang/reflect/InvocationTargetException;
        //  25     37     278    318    Ljava/lang/InstantiationException;
        //  25     37     318    358    Ljava/lang/IllegalAccessException;
        //  25     37     358    398    Ljava/lang/ClassCastException;
        //  37     50     116    156    Ljava/lang/ClassNotFoundException;
        //  37     50     238    278    Ljava/lang/reflect/InvocationTargetException;
        //  37     50     278    318    Ljava/lang/InstantiationException;
        //  37     50     318    358    Ljava/lang/IllegalAccessException;
        //  37     50     358    398    Ljava/lang/ClassCastException;
        //  50     65     167    238    Ljava/lang/NoSuchMethodException;
        //  50     65     116    156    Ljava/lang/ClassNotFoundException;
        //  50     65     238    278    Ljava/lang/reflect/InvocationTargetException;
        //  50     65     278    318    Ljava/lang/InstantiationException;
        //  50     65     318    358    Ljava/lang/IllegalAccessException;
        //  50     65     358    398    Ljava/lang/ClassCastException;
        //  75     93     167    238    Ljava/lang/NoSuchMethodException;
        //  75     93     116    156    Ljava/lang/ClassNotFoundException;
        //  75     93     238    278    Ljava/lang/reflect/InvocationTargetException;
        //  75     93     278    318    Ljava/lang/InstantiationException;
        //  75     93     318    358    Ljava/lang/IllegalAccessException;
        //  75     93     358    398    Ljava/lang/ClassCastException;
        //  96     113    116    156    Ljava/lang/ClassNotFoundException;
        //  96     113    238    278    Ljava/lang/reflect/InvocationTargetException;
        //  96     113    278    318    Ljava/lang/InstantiationException;
        //  96     113    318    358    Ljava/lang/IllegalAccessException;
        //  96     113    358    398    Ljava/lang/ClassCastException;
        //  156    164    116    156    Ljava/lang/ClassNotFoundException;
        //  156    164    238    278    Ljava/lang/reflect/InvocationTargetException;
        //  156    164    278    318    Ljava/lang/InstantiationException;
        //  156    164    318    358    Ljava/lang/IllegalAccessException;
        //  156    164    358    398    Ljava/lang/ClassCastException;
        //  169    179    184    238    Ljava/lang/NoSuchMethodException;
        //  169    179    116    156    Ljava/lang/ClassNotFoundException;
        //  169    179    238    278    Ljava/lang/reflect/InvocationTargetException;
        //  169    179    278    318    Ljava/lang/InstantiationException;
        //  169    179    318    358    Ljava/lang/IllegalAccessException;
        //  169    179    358    398    Ljava/lang/ClassCastException;
        //  185    238    116    156    Ljava/lang/ClassNotFoundException;
        //  185    238    238    278    Ljava/lang/reflect/InvocationTargetException;
        //  185    238    278    318    Ljava/lang/InstantiationException;
        //  185    238    318    358    Ljava/lang/IllegalAccessException;
        //  185    238    358    398    Ljava/lang/ClassCastException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 187 out of bounds for length 187
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3611)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private boolean didChildRangeChange(final int n, final int n2) {
        boolean b = false;
        this.findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mMinMaxLayoutPositions[0] != n || this.mMinMaxLayoutPositions[1] != n2) {
            b = true;
        }
        return b;
    }
    
    private void dispatchContentChangedIfNecessary() {
        final int mEatenAccessibilityChangeFlags = this.mEatenAccessibilityChangeFlags;
        this.mEatenAccessibilityChangeFlags = 0;
        if (mEatenAccessibilityChangeFlags != 0 && this.isAccessibilityEnabled()) {
            final AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(2048);
            AccessibilityEventCompat.setContentChangeTypes(obtain, mEatenAccessibilityChangeFlags);
            this.sendAccessibilityEventUnchecked(obtain);
        }
    }
    
    private void dispatchLayoutStep1() {
        boolean mTrackOldChangeHolders = true;
        this.mState.assertLayoutStep(1);
        this.fillRemainingScrollValues(this.mState);
        this.mState.mIsMeasuring = false;
        this.eatRequestLayout();
        this.mViewInfoStore.clear();
        this.onEnterLayoutOrScroll();
        this.processAdapterUpdatesAndSetAnimationFlags();
        this.saveFocusInfo();
        final State mState = this.mState;
        if (!this.mState.mRunSimpleAnimations || !this.mItemsChanged) {
            mTrackOldChangeHolders = false;
        }
        mState.mTrackOldChangeHolders = mTrackOldChangeHolders;
        this.mItemsChanged = false;
        this.mItemsAddedOrRemoved = false;
        this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
        this.mState.mItemCount = this.mAdapter.getItemCount();
        this.findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mState.mRunSimpleAnimations) {
            for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
                final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore() && (!childViewHolderInt.isInvalid() || this.mAdapter.hasStableIds())) {
                    this.mViewInfoStore.addToPreLayout(childViewHolderInt, this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt, ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt), childViewHolderInt.getUnmodifiedPayloads()));
                    if (this.mState.mTrackOldChangeHolders && childViewHolderInt.isUpdated() && !childViewHolderInt.isRemoved() && !childViewHolderInt.shouldIgnore() && !childViewHolderInt.isInvalid()) {
                        this.mViewInfoStore.addToOldChangeHolders(this.getChangedHolderKey(childViewHolderInt), childViewHolderInt);
                    }
                }
            }
        }
        if (!this.mState.mRunPredictiveAnimations) {
            this.clearOldPositions();
        }
        else {
            this.saveOldPositions();
            final boolean mStructureChanged = this.mState.mStructureChanged;
            this.mState.mStructureChanged = false;
            this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
            this.mState.mStructureChanged = mStructureChanged;
            for (int j = 0; j < this.mChildHelper.getChildCount(); ++j) {
                final ViewHolder childViewHolderInt2 = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
                if (!childViewHolderInt2.shouldIgnore() && !this.mViewInfoStore.isInPreLayout(childViewHolderInt2)) {
                    int buildAdapterChangeFlagsForAnimations = ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt2);
                    final boolean hasAnyOfTheFlags = childViewHolderInt2.hasAnyOfTheFlags(8192);
                    if (!hasAnyOfTheFlags) {
                        buildAdapterChangeFlagsForAnimations |= 0x1000;
                    }
                    final ItemHolderInfo recordPreLayoutInformation = this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt2, buildAdapterChangeFlagsForAnimations, childViewHolderInt2.getUnmodifiedPayloads());
                    if (!hasAnyOfTheFlags) {
                        this.mViewInfoStore.addToAppearedInPreLayoutHolders(childViewHolderInt2, recordPreLayoutInformation);
                    }
                    else {
                        this.recordAnimationInfoIfBouncedHiddenView(childViewHolderInt2, recordPreLayoutInformation);
                    }
                }
            }
            this.clearOldPositions();
        }
        this.onExitLayoutOrScroll();
        this.resumeRequestLayout(false);
        this.mState.mLayoutStep = 2;
    }
    
    private void dispatchLayoutStep2() {
        this.eatRequestLayout();
        this.onEnterLayoutOrScroll();
        this.mState.assertLayoutStep(6);
        this.mAdapterHelper.consumeUpdatesInOnePass();
        this.mState.mItemCount = this.mAdapter.getItemCount();
        this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
        this.mState.mInPreLayout = false;
        this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
        this.mState.mStructureChanged = false;
        this.mPendingSavedState = null;
        this.mState.mRunSimpleAnimations = (this.mState.mRunSimpleAnimations && this.mItemAnimator != null);
        this.mState.mLayoutStep = 4;
        this.onExitLayoutOrScroll();
        this.resumeRequestLayout(false);
    }
    
    private void dispatchLayoutStep3() {
        this.mState.assertLayoutStep(4);
        this.eatRequestLayout();
        this.onEnterLayoutOrScroll();
        this.mState.mLayoutStep = 1;
        if (this.mState.mRunSimpleAnimations) {
            for (int i = this.mChildHelper.getChildCount() - 1; i >= 0; --i) {
                final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore()) {
                    final long changedHolderKey = this.getChangedHolderKey(childViewHolderInt);
                    final ItemHolderInfo recordPostLayoutInformation = this.mItemAnimator.recordPostLayoutInformation(this.mState, childViewHolderInt);
                    final ViewHolder fromOldChangeHolders = this.mViewInfoStore.getFromOldChangeHolders(changedHolderKey);
                    if (fromOldChangeHolders != null && !fromOldChangeHolders.shouldIgnore()) {
                        final boolean disappearing = this.mViewInfoStore.isDisappearing(fromOldChangeHolders);
                        final boolean disappearing2 = this.mViewInfoStore.isDisappearing(childViewHolderInt);
                        if (disappearing && fromOldChangeHolders == childViewHolderInt) {
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                        }
                        else {
                            final ItemHolderInfo popFromPreLayout = this.mViewInfoStore.popFromPreLayout(fromOldChangeHolders);
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                            final ItemHolderInfo popFromPostLayout = this.mViewInfoStore.popFromPostLayout(childViewHolderInt);
                            if (popFromPreLayout != null) {
                                this.animateChange(fromOldChangeHolders, childViewHolderInt, popFromPreLayout, popFromPostLayout, disappearing, disappearing2);
                            }
                            else {
                                this.handleMissingPreInfoForChangeError(changedHolderKey, childViewHolderInt, fromOldChangeHolders);
                            }
                        }
                    }
                    else {
                        this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                    }
                }
            }
            this.mViewInfoStore.process(this.mViewInfoProcessCallback);
        }
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
        this.mDataSetHasChangedAfterLayout = false;
        this.mState.mRunSimpleAnimations = false;
        this.mState.mRunPredictiveAnimations = false;
        this.mLayout.mRequestedSimpleAnimations = false;
        if (this.mRecycler.mChangedScrap != null) {
            this.mRecycler.mChangedScrap.clear();
        }
        if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch) {
            this.mLayout.mPrefetchMaxCountObserved = 0;
            this.mLayout.mPrefetchMaxObservedInInitialPrefetch = false;
            this.mRecycler.updateViewCacheSize();
        }
        this.mLayout.onLayoutCompleted(this.mState);
        this.onExitLayoutOrScroll();
        this.resumeRequestLayout(false);
        this.mViewInfoStore.clear();
        if (this.didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1])) {
            this.dispatchOnScrolled(0, 0);
        }
        this.recoverFocusFromState();
        this.resetFocusInfo();
    }
    
    private boolean dispatchOnItemTouch(final MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        if (this.mActiveOnItemTouchListener != null) {
            if (action != 0) {
                this.mActiveOnItemTouchListener.onTouchEvent(this, motionEvent);
                if (action == 3 || action == 1) {
                    this.mActiveOnItemTouchListener = null;
                }
                return true;
            }
            this.mActiveOnItemTouchListener = null;
        }
        if (action != 0) {
            for (int size = this.mOnItemTouchListeners.size(), i = 0; i < size; ++i) {
                final OnItemTouchListener mActiveOnItemTouchListener = this.mOnItemTouchListeners.get(i);
                if (mActiveOnItemTouchListener.onInterceptTouchEvent(this, motionEvent)) {
                    this.mActiveOnItemTouchListener = mActiveOnItemTouchListener;
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean dispatchOnItemTouchIntercept(final MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        if (action == 3 || action == 0) {
            this.mActiveOnItemTouchListener = null;
        }
        for (int size = this.mOnItemTouchListeners.size(), i = 0; i < size; ++i) {
            final OnItemTouchListener mActiveOnItemTouchListener = this.mOnItemTouchListeners.get(i);
            if (mActiveOnItemTouchListener.onInterceptTouchEvent(this, motionEvent) && action != 3) {
                this.mActiveOnItemTouchListener = mActiveOnItemTouchListener;
                return true;
            }
        }
        return false;
    }
    
    private void findMinMaxChildLayoutPositions(final int[] array) {
        final int childCount = this.mChildHelper.getChildCount();
        if (childCount != 0) {
            int n = Integer.MAX_VALUE;
            int n2 = Integer.MIN_VALUE;
            for (int i = 0; i < childCount; ++i) {
                final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore()) {
                    final int layoutPosition = childViewHolderInt.getLayoutPosition();
                    if (layoutPosition < n) {
                        n = layoutPosition;
                    }
                    if (layoutPosition > n2) {
                        n2 = layoutPosition;
                    }
                }
            }
            array[0] = n;
            array[1] = n2;
            return;
        }
        array[1] = (array[0] = -1);
    }
    
    @Nullable
    static RecyclerView findNestedRecyclerView(@NonNull final View view) {
        int i = 0;
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        if (!(view instanceof RecyclerView)) {
            for (ViewGroup viewGroup = (ViewGroup)view; i < viewGroup.getChildCount(); ++i) {
                final RecyclerView nestedRecyclerView = findNestedRecyclerView(viewGroup.getChildAt(i));
                if (nestedRecyclerView != null) {
                    return nestedRecyclerView;
                }
            }
            return null;
        }
        return (RecyclerView)view;
    }
    
    @Nullable
    private View findNextViewToFocus() {
        int mFocusedItemPosition = 0;
        if (this.mState.mFocusedItemPosition != -1) {
            mFocusedItemPosition = this.mState.mFocusedItemPosition;
        }
        final int itemCount = this.mState.getItemCount();
        for (int i = mFocusedItemPosition; i < itemCount; ++i) {
            final ViewHolder viewHolderForAdapterPosition = this.findViewHolderForAdapterPosition(i);
            if (viewHolderForAdapterPosition == null) {
                break;
            }
            if (viewHolderForAdapterPosition.itemView.hasFocusable()) {
                return viewHolderForAdapterPosition.itemView;
            }
        }
        int min = Math.min(itemCount, mFocusedItemPosition);
        while (--min >= 0) {
            final ViewHolder viewHolderForAdapterPosition2 = this.findViewHolderForAdapterPosition(min);
            if (viewHolderForAdapterPosition2 == null) {
                return null;
            }
            if (viewHolderForAdapterPosition2.itemView.hasFocusable()) {
                return viewHolderForAdapterPosition2.itemView;
            }
        }
        return null;
    }
    
    static ViewHolder getChildViewHolderInt(final View view) {
        if (view != null) {
            return ((LayoutParams)view.getLayoutParams()).mViewHolder;
        }
        return null;
    }
    
    static void getDecoratedBoundsWithMarginsInt(final View view, final Rect rect) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final Rect mDecorInsets = layoutParams.mDecorInsets;
        rect.set(view.getLeft() - mDecorInsets.left - layoutParams.leftMargin, view.getTop() - mDecorInsets.top - layoutParams.topMargin, view.getRight() + mDecorInsets.right + layoutParams.rightMargin, layoutParams.bottomMargin + (mDecorInsets.bottom + view.getBottom()));
    }
    
    private int getDeepestFocusedViewWithId(View focusedChild) {
        int n = focusedChild.getId();
        while (!focusedChild.isFocused() && focusedChild instanceof ViewGroup && focusedChild.hasFocus()) {
            focusedChild = ((ViewGroup)focusedChild).getFocusedChild();
            if (focusedChild.getId() == -1) {
                continue;
            }
            n = focusedChild.getId();
        }
        return n;
    }
    
    private String getFullClassName(final Context context, final String s) {
        if (s.charAt(0) == '.') {
            return context.getPackageName() + s;
        }
        if (!s.contains(".")) {
            return RecyclerView.class.getPackage().getName() + '.' + s;
        }
        return s;
    }
    
    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (this.mScrollingChildHelper == null) {
            this.mScrollingChildHelper = new NestedScrollingChildHelper((View)this);
        }
        return this.mScrollingChildHelper;
    }
    
    private void handleMissingPreInfoForChangeError(final long n, final ViewHolder obj, final ViewHolder obj2) {
        int i = 0;
        while (i < this.mChildHelper.getChildCount()) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != obj && this.getChangedHolderKey(childViewHolderInt) == n) {
                if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
                    throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + childViewHolderInt + " \n View Holder 2:" + obj + this.exceptionLabel());
                }
                throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + childViewHolderInt + " \n View Holder 2:" + obj + this.exceptionLabel());
            }
            else {
                ++i;
            }
        }
        Log.e("RecyclerView", "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + obj2 + " cannot be found but it is necessary for " + obj + this.exceptionLabel());
    }
    
    private boolean hasUpdatedView() {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.isUpdated()) {
                return true;
            }
        }
        return false;
    }
    
    private void initChildrenHelper() {
        this.mChildHelper = new ChildHelper((ChildHelper.Callback)new ChildHelper.Callback() {
            @Override
            public void addView(final View view, final int n) {
                RecyclerView.this.addView(view, n);
                RecyclerView.this.dispatchChildAttached(view);
            }
            
            @Override
            public void attachViewToParent(final View view, final int n, final ViewGroup$LayoutParams viewGroup$LayoutParams) {
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    if (!childViewHolderInt.isTmpDetached() && !childViewHolderInt.shouldIgnore()) {
                        throw new IllegalArgumentException("Called attach on a child which is not detached: " + childViewHolderInt + RecyclerView.this.exceptionLabel());
                    }
                    childViewHolderInt.clearTmpDetachFlag();
                }
                RecyclerView.access$000(RecyclerView.this, view, n, viewGroup$LayoutParams);
            }
            
            @Override
            public void detachViewFromParent(final int n) {
                final View child = this.getChildAt(n);
                if (child != null) {
                    final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(child);
                    if (childViewHolderInt != null) {
                        if (childViewHolderInt.isTmpDetached() && !childViewHolderInt.shouldIgnore()) {
                            throw new IllegalArgumentException("called detach on an already detached child " + childViewHolderInt + RecyclerView.this.exceptionLabel());
                        }
                        childViewHolderInt.addFlags(256);
                    }
                }
                RecyclerView.access$100(RecyclerView.this, n);
            }
            
            @Override
            public View getChildAt(final int n) {
                return RecyclerView.this.getChildAt(n);
            }
            
            @Override
            public int getChildCount() {
                return RecyclerView.this.getChildCount();
            }
            
            @Override
            public ViewHolder getChildViewHolder(final View view) {
                return RecyclerView.getChildViewHolderInt(view);
            }
            
            @Override
            public int indexOfChild(final View view) {
                return RecyclerView.this.indexOfChild(view);
            }
            
            @Override
            public void onEnteredHiddenState(final View view) {
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    childViewHolderInt.onEnteredHiddenState(RecyclerView.this);
                }
            }
            
            @Override
            public void onLeftHiddenState(final View view) {
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                if (childViewHolderInt != null) {
                    childViewHolderInt.onLeftHiddenState(RecyclerView.this);
                }
            }
            
            @Override
            public void removeAllViews() {
                for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                    final View child = this.getChildAt(i);
                    RecyclerView.this.dispatchChildDetached(child);
                    child.clearAnimation();
                }
                RecyclerView.this.removeAllViews();
            }
            
            @Override
            public void removeViewAt(final int n) {
                final View child = RecyclerView.this.getChildAt(n);
                if (child != null) {
                    RecyclerView.this.dispatchChildDetached(child);
                    child.clearAnimation();
                }
                RecyclerView.this.removeViewAt(n);
            }
        });
    }
    
    private boolean isPreferredNextFocus(final View view, final View view2, final int n) {
        boolean b = false;
        if (view2 == null || view2 == this) {
            return false;
        }
        if (view == null) {
            return true;
        }
        if (n != 2 && n != 1) {
            return this.isPreferredNextFocusAbsolute(view, view2, n);
        }
        boolean b2;
        if (this.mLayout.getLayoutDirection() != 1) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        if (n == 2) {
            b = true;
        }
        int n2;
        if (!(b2 ^ b)) {
            n2 = 17;
        }
        else {
            n2 = 66;
        }
        if (this.isPreferredNextFocusAbsolute(view, view2, n2)) {
            return true;
        }
        if (n != 2) {
            return this.isPreferredNextFocusAbsolute(view, view2, 33);
        }
        return this.isPreferredNextFocusAbsolute(view, view2, 130);
    }
    
    private boolean isPreferredNextFocusAbsolute(final View view, final View view2, final int i) {
        final boolean b = true;
        final boolean b2 = true;
        final boolean b3 = true;
        boolean b4 = true;
        this.mTempRect.set(0, 0, view.getWidth(), view.getHeight());
        this.mTempRect2.set(0, 0, view2.getWidth(), view2.getHeight());
        this.offsetDescendantRectToMyCoords(view, this.mTempRect);
        this.offsetDescendantRectToMyCoords(view2, this.mTempRect2);
        switch (i) {
            default: {
                throw new IllegalArgumentException("direction must be absolute. received:" + i + this.exceptionLabel());
            }
            case 17: {
                if (this.mTempRect.right > this.mTempRect2.right || this.mTempRect.left >= this.mTempRect2.right) {
                    if (this.mTempRect.left > this.mTempRect2.left) {
                        return b4;
                    }
                }
                b4 = false;
                return b4;
            }
            case 66: {
                if (this.mTempRect.left < this.mTempRect2.left || this.mTempRect.right <= this.mTempRect2.left) {
                    final boolean b5 = b;
                    if (this.mTempRect.right < this.mTempRect2.right) {
                        return b5;
                    }
                }
                return false;
            }
            case 33: {
                if (this.mTempRect.bottom > this.mTempRect2.bottom || this.mTempRect.top >= this.mTempRect2.bottom) {
                    final boolean b6 = b2;
                    if (this.mTempRect.top > this.mTempRect2.top) {
                        return b6;
                    }
                }
                return false;
            }
            case 130: {
                if (this.mTempRect.top < this.mTempRect2.top || this.mTempRect.bottom <= this.mTempRect2.top) {
                    final boolean b7 = b3;
                    if (this.mTempRect.bottom < this.mTempRect2.bottom) {
                        return b7;
                    }
                }
                return false;
            }
        }
    }
    
    private void onPointerUp(final MotionEvent motionEvent) {
        int n = 0;
        final int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
            if (actionIndex == 0) {
                n = 1;
            }
            this.mScrollPointerId = motionEvent.getPointerId(n);
            final int n2 = (int)(motionEvent.getX(n) + 0.5f);
            this.mLastTouchX = n2;
            this.mInitialTouchX = n2;
            final int n3 = (int)(motionEvent.getY(n) + 0.5f);
            this.mLastTouchY = n3;
            this.mInitialTouchY = n3;
        }
    }
    
    private boolean predictiveItemAnimationsEnabled() {
        boolean b = false;
        if (this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations()) {
            b = true;
        }
        return b;
    }
    
    private void processAdapterUpdatesAndSetAnimationFlags() {
        final boolean b = true;
        if (this.mDataSetHasChangedAfterLayout) {
            this.mAdapterHelper.reset();
            this.mLayout.onItemsChanged(this);
        }
        if (!this.predictiveItemAnimationsEnabled()) {
            this.mAdapterHelper.consumeUpdatesInOnePass();
        }
        else {
            this.mAdapterHelper.preProcess();
        }
        final boolean b2 = this.mItemsAddedOrRemoved || this.mItemsChanged;
        final State mState = this.mState;
        boolean mRunSimpleAnimations = false;
        Label_0047: {
            if (this.mFirstLayoutComplete && this.mItemAnimator != null) {
                if (this.mDataSetHasChangedAfterLayout || b2 || this.mLayout.mRequestedSimpleAnimations) {
                    if (!this.mDataSetHasChangedAfterLayout || this.mAdapter.hasStableIds()) {
                        mRunSimpleAnimations = true;
                        break Label_0047;
                    }
                }
            }
            mRunSimpleAnimations = false;
        }
        mState.mRunSimpleAnimations = mRunSimpleAnimations;
        final State mState2 = this.mState;
        boolean mRunPredictiveAnimations = false;
        Label_0071: {
            if (this.mState.mRunSimpleAnimations && b2 && !this.mDataSetHasChangedAfterLayout) {
                mRunPredictiveAnimations = b;
                if (this.predictiveItemAnimationsEnabled()) {
                    break Label_0071;
                }
            }
            mRunPredictiveAnimations = false;
        }
        mState2.mRunPredictiveAnimations = mRunPredictiveAnimations;
    }
    
    private void pullGlows(final float n, final float n2, final float n3, final float n4) {
        int n5 = 0;
        final int n6 = 1;
        if (n2 < 0.0f) {
            this.ensureLeftGlow();
            EdgeEffectCompat.onPull(this.mLeftGlow, -n2 / this.getWidth(), 1.0f - n3 / this.getHeight());
            n5 = 1;
        }
        else if (n2 > 0.0f) {
            this.ensureRightGlow();
            EdgeEffectCompat.onPull(this.mRightGlow, n2 / this.getWidth(), n3 / this.getHeight());
            n5 = 1;
        }
        if (n4 < 0.0f) {
            this.ensureTopGlow();
            EdgeEffectCompat.onPull(this.mTopGlow, -n4 / this.getHeight(), n / this.getWidth());
            n5 = n6;
        }
        else if (n4 > 0.0f) {
            this.ensureBottomGlow();
            EdgeEffectCompat.onPull(this.mBottomGlow, n4 / this.getHeight(), 1.0f - n / this.getWidth());
            n5 = n6;
        }
        if (n5 != 0 || n2 != 0.0f || n4 != 0.0f) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    private void recoverFocusFromState() {
        if (this.mPreserveFocusAfterLayout && this.mAdapter != null && this.hasFocus() && this.getDescendantFocusability() != 393216 && (this.getDescendantFocusability() != 131072 || !this.isFocused())) {
            if (!this.isFocused()) {
                final View focusedChild = this.getFocusedChild();
                if (RecyclerView.IGNORE_DETACHED_FOCUSED_CHILD && (focusedChild.getParent() == null || !focusedChild.hasFocus())) {
                    if (this.mChildHelper.getChildCount() == 0) {
                        this.requestFocus();
                        return;
                    }
                }
                else if (!this.mChildHelper.isHidden(focusedChild)) {
                    return;
                }
            }
            ViewHolder viewHolderForItemId;
            if (this.mState.mFocusedItemId == -1L || !this.mAdapter.hasStableIds()) {
                viewHolderForItemId = null;
            }
            else {
                viewHolderForItemId = this.findViewHolderForItemId(this.mState.mFocusedItemId);
            }
            View view;
            if (viewHolderForItemId != null && !this.mChildHelper.isHidden(viewHolderForItemId.itemView) && viewHolderForItemId.itemView.hasFocusable()) {
                view = viewHolderForItemId.itemView;
            }
            else if (this.mChildHelper.getChildCount() <= 0) {
                view = null;
            }
            else {
                view = this.findNextViewToFocus();
            }
            if (view != null) {
                View view2 = view;
                if (this.mState.mFocusedSubChildId != -1L) {
                    final View viewById = view.findViewById(this.mState.mFocusedSubChildId);
                    if (viewById == null) {
                        view2 = view;
                    }
                    else {
                        view2 = view;
                        if (viewById.isFocusable()) {
                            view2 = viewById;
                        }
                    }
                }
                view2.requestFocus();
            }
        }
    }
    
    private void releaseGlows() {
        boolean finished = false;
        if (this.mLeftGlow != null) {
            this.mLeftGlow.onRelease();
            finished = this.mLeftGlow.isFinished();
        }
        if (this.mTopGlow != null) {
            this.mTopGlow.onRelease();
            finished |= this.mTopGlow.isFinished();
        }
        if (this.mRightGlow != null) {
            this.mRightGlow.onRelease();
            finished |= this.mRightGlow.isFinished();
        }
        if (this.mBottomGlow != null) {
            this.mBottomGlow.onRelease();
            finished |= this.mBottomGlow.isFinished();
        }
        if (finished) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    private void requestChildOnScreen(@NonNull final View view, @Nullable final View view2) {
        boolean b = false;
        View view3;
        if (view2 == null) {
            view3 = view;
        }
        else {
            view3 = view2;
        }
        this.mTempRect.set(0, 0, view3.getWidth(), view3.getHeight());
        final ViewGroup$LayoutParams layoutParams = view3.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            final LayoutParams layoutParams2 = (LayoutParams)layoutParams;
            if (!layoutParams2.mInsetsDirty) {
                final Rect mDecorInsets = layoutParams2.mDecorInsets;
                final Rect mTempRect = this.mTempRect;
                mTempRect.left -= mDecorInsets.left;
                final Rect mTempRect2 = this.mTempRect;
                mTempRect2.right += mDecorInsets.right;
                final Rect mTempRect3 = this.mTempRect;
                mTempRect3.top -= mDecorInsets.top;
                final Rect mTempRect4 = this.mTempRect;
                mTempRect4.bottom += mDecorInsets.bottom;
            }
        }
        if (view2 != null) {
            this.offsetDescendantRectToMyCoords(view2, this.mTempRect);
            this.offsetRectIntoDescendantCoords(view, this.mTempRect);
        }
        final LayoutManager mLayout = this.mLayout;
        final Rect mTempRect5 = this.mTempRect;
        final boolean b2 = !this.mFirstLayoutComplete;
        if (view2 == null) {
            b = true;
        }
        mLayout.requestChildRectangleOnScreen(this, view, mTempRect5, b2, b);
    }
    
    private void resetFocusInfo() {
        this.mState.mFocusedItemId = -1L;
        this.mState.mFocusedItemPosition = -1;
        this.mState.mFocusedSubChildId = -1;
    }
    
    private void resetTouch() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.clear();
        }
        this.stopNestedScroll(0);
        this.releaseGlows();
    }
    
    private void saveFocusInfo() {
        ViewHolder containingViewHolder = null;
        View focusedChild;
        if (this.mPreserveFocusAfterLayout && this.hasFocus() && this.mAdapter != null) {
            focusedChild = this.getFocusedChild();
        }
        else {
            focusedChild = null;
        }
        if (focusedChild != null) {
            containingViewHolder = this.findContainingViewHolder(focusedChild);
        }
        if (containingViewHolder != null) {
            final State mState = this.mState;
            long itemId;
            if (!this.mAdapter.hasStableIds()) {
                itemId = -1L;
            }
            else {
                itemId = containingViewHolder.getItemId();
            }
            mState.mFocusedItemId = itemId;
            final State mState2 = this.mState;
            int mFocusedItemPosition;
            if (!this.mDataSetHasChangedAfterLayout) {
                if (!containingViewHolder.isRemoved()) {
                    mFocusedItemPosition = containingViewHolder.getAdapterPosition();
                }
                else {
                    mFocusedItemPosition = containingViewHolder.mOldPosition;
                }
            }
            else {
                mFocusedItemPosition = -1;
            }
            mState2.mFocusedItemPosition = mFocusedItemPosition;
            this.mState.mFocusedSubChildId = this.getDeepestFocusedViewWithId(containingViewHolder.itemView);
        }
        else {
            this.resetFocusInfo();
        }
    }
    
    private void setAdapterInternal(final Adapter mAdapter, final boolean b, final boolean b2) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromRecyclerView(this);
        }
        if (!b || b2) {
            this.removeAndRecycleViews();
        }
        this.mAdapterHelper.reset();
        final Adapter mAdapter2 = this.mAdapter;
        this.mAdapter = mAdapter;
        if (mAdapter != null) {
            mAdapter.registerAdapterDataObserver(this.mObserver);
            mAdapter.onAttachedToRecyclerView(this);
        }
        if (this.mLayout != null) {
            this.mLayout.onAdapterChanged(mAdapter2, this.mAdapter);
        }
        this.mRecycler.onAdapterChanged(mAdapter2, this.mAdapter, b);
        this.mState.mStructureChanged = true;
        this.setDataSetChangedAfterLayout();
    }
    
    private void stopScrollersInternal() {
        this.mViewFlinger.stop();
        if (this.mLayout != null) {
            this.mLayout.stopSmoothScroller();
        }
    }
    
    void absorbGlows(final int n, final int n2) {
        if (n >= 0) {
            if (n > 0) {
                this.ensureRightGlow();
                this.mRightGlow.onAbsorb(n);
            }
        }
        else {
            this.ensureLeftGlow();
            this.mLeftGlow.onAbsorb(-n);
        }
        if (n2 >= 0) {
            if (n2 > 0) {
                this.ensureBottomGlow();
                this.mBottomGlow.onAbsorb(n2);
            }
        }
        else {
            this.ensureTopGlow();
            this.mTopGlow.onAbsorb(-n2);
        }
        if (n != 0 || n2 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        if (this.mLayout == null || !this.mLayout.onAddFocusables(this, list, n, n2)) {
            super.addFocusables((ArrayList)list, n, n2);
        }
    }
    
    public void addItemDecoration(final ItemDecoration itemDecoration) {
        this.addItemDecoration(itemDecoration, -1);
    }
    
    public void addItemDecoration(final ItemDecoration itemDecoration, final int index) {
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
        }
        if (this.mItemDecorations.isEmpty()) {
            this.setWillNotDraw(false);
        }
        if (index >= 0) {
            this.mItemDecorations.add(index, itemDecoration);
        }
        else {
            this.mItemDecorations.add(itemDecoration);
        }
        this.markItemDecorInsetsDirty();
        this.requestLayout();
    }
    
    public void addOnChildAttachStateChangeListener(final OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners == null) {
            this.mOnChildAttachStateListeners = new ArrayList<OnChildAttachStateChangeListener>();
        }
        this.mOnChildAttachStateListeners.add(onChildAttachStateChangeListener);
    }
    
    public void addOnItemTouchListener(final OnItemTouchListener e) {
        this.mOnItemTouchListeners.add(e);
    }
    
    public void addOnScrollListener(final OnScrollListener onScrollListener) {
        if (this.mScrollListeners == null) {
            this.mScrollListeners = new ArrayList<OnScrollListener>();
        }
        this.mScrollListeners.add(onScrollListener);
    }
    
    void animateAppearance(@NonNull final ViewHolder viewHolder, @Nullable final ItemHolderInfo itemHolderInfo, @NonNull final ItemHolderInfo itemHolderInfo2) {
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            this.postAnimationRunner();
        }
    }
    
    void animateDisappearance(@NonNull final ViewHolder viewHolder, @NonNull final ItemHolderInfo itemHolderInfo, @Nullable final ItemHolderInfo itemHolderInfo2) {
        this.addAnimatingView(viewHolder);
        viewHolder.setIsRecyclable(false);
        if (this.mItemAnimator.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            this.postAnimationRunner();
        }
    }
    
    void assertInLayoutOrScroll(final String str) {
        if (this.isComputingLayout()) {
            return;
        }
        if (str != null) {
            throw new IllegalStateException(str + this.exceptionLabel());
        }
        throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling" + this.exceptionLabel());
    }
    
    void assertNotInLayoutOrScroll(final String s) {
        if (!this.isComputingLayout()) {
            if (this.mDispatchScrollCounter > 0) {
                Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", (Throwable)new IllegalStateException("" + this.exceptionLabel()));
            }
            return;
        }
        if (s != null) {
            throw new IllegalStateException(s);
        }
        throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling" + this.exceptionLabel());
    }
    
    boolean canReuseUpdatedViewHolder(final ViewHolder viewHolder) {
        boolean b = false;
        if (this.mItemAnimator == null || this.mItemAnimator.canReuseUpdatedViewHolder(viewHolder, viewHolder.getUnmodifiedPayloads())) {
            b = true;
        }
        return b;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        boolean b = false;
        if (viewGroup$LayoutParams instanceof LayoutParams && this.mLayout.checkLayoutParams((LayoutParams)viewGroup$LayoutParams)) {
            b = true;
        }
        return b;
    }
    
    void clearOldPositions() {
        for (int i = 0; i < this.mChildHelper.getUnfilteredChildCount(); ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.clearOldPosition();
            }
        }
        this.mRecycler.clearOldPositions();
    }
    
    public void clearOnChildAttachStateChangeListeners() {
        if (this.mOnChildAttachStateListeners != null) {
            this.mOnChildAttachStateListeners.clear();
        }
    }
    
    public void clearOnScrollListeners() {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.clear();
        }
    }
    
    public int computeHorizontalScrollExtent() {
        int computeHorizontalScrollExtent = 0;
        if (this.mLayout != null) {
            if (this.mLayout.canScrollHorizontally()) {
                computeHorizontalScrollExtent = this.mLayout.computeHorizontalScrollExtent(this.mState);
            }
            return computeHorizontalScrollExtent;
        }
        return 0;
    }
    
    public int computeHorizontalScrollOffset() {
        int computeHorizontalScrollOffset = 0;
        if (this.mLayout != null) {
            if (this.mLayout.canScrollHorizontally()) {
                computeHorizontalScrollOffset = this.mLayout.computeHorizontalScrollOffset(this.mState);
            }
            return computeHorizontalScrollOffset;
        }
        return 0;
    }
    
    public int computeHorizontalScrollRange() {
        int computeHorizontalScrollRange = 0;
        if (this.mLayout != null) {
            if (this.mLayout.canScrollHorizontally()) {
                computeHorizontalScrollRange = this.mLayout.computeHorizontalScrollRange(this.mState);
            }
            return computeHorizontalScrollRange;
        }
        return 0;
    }
    
    public int computeVerticalScrollExtent() {
        int computeVerticalScrollExtent = 0;
        if (this.mLayout != null) {
            if (this.mLayout.canScrollVertically()) {
                computeVerticalScrollExtent = this.mLayout.computeVerticalScrollExtent(this.mState);
            }
            return computeVerticalScrollExtent;
        }
        return 0;
    }
    
    public int computeVerticalScrollOffset() {
        int computeVerticalScrollOffset = 0;
        if (this.mLayout != null) {
            if (this.mLayout.canScrollVertically()) {
                computeVerticalScrollOffset = this.mLayout.computeVerticalScrollOffset(this.mState);
            }
            return computeVerticalScrollOffset;
        }
        return 0;
    }
    
    public int computeVerticalScrollRange() {
        int computeVerticalScrollRange = 0;
        if (this.mLayout != null) {
            if (this.mLayout.canScrollVertically()) {
                computeVerticalScrollRange = this.mLayout.computeVerticalScrollRange(this.mState);
            }
            return computeVerticalScrollRange;
        }
        return 0;
    }
    
    void considerReleasingGlowsOnScroll(final int n, final int n2) {
        final boolean b = false;
        boolean finished;
        if (this.mLeftGlow == null) {
            finished = b;
        }
        else {
            finished = b;
            if (!this.mLeftGlow.isFinished()) {
                finished = b;
                if (n > 0) {
                    this.mLeftGlow.onRelease();
                    finished = this.mLeftGlow.isFinished();
                }
            }
        }
        boolean b2;
        if (this.mRightGlow == null) {
            b2 = finished;
        }
        else {
            b2 = finished;
            if (!this.mRightGlow.isFinished()) {
                b2 = finished;
                if (n < 0) {
                    this.mRightGlow.onRelease();
                    b2 = (finished | this.mRightGlow.isFinished());
                }
            }
        }
        boolean b3;
        if (this.mTopGlow == null) {
            b3 = b2;
        }
        else {
            b3 = b2;
            if (!this.mTopGlow.isFinished()) {
                b3 = b2;
                if (n2 > 0) {
                    this.mTopGlow.onRelease();
                    b3 = (b2 | this.mTopGlow.isFinished());
                }
            }
        }
        int n3;
        if (this.mBottomGlow == null) {
            n3 = (b3 ? 1 : 0);
        }
        else {
            n3 = (b3 ? 1 : 0);
            if (!this.mBottomGlow.isFinished()) {
                n3 = (b3 ? 1 : 0);
                if (n2 < 0) {
                    this.mBottomGlow.onRelease();
                    n3 = ((b3 | this.mBottomGlow.isFinished()) ? 1 : 0);
                }
            }
        }
        if (n3 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    void consumePendingUpdateOperations() {
        if (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout) {
            TraceCompat.beginSection("RV FullInvalidate");
            this.dispatchLayout();
            TraceCompat.endSection();
            return;
        }
        if (this.mAdapterHelper.hasPendingUpdates()) {
            if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
                TraceCompat.beginSection("RV PartialInvalidate");
                this.eatRequestLayout();
                this.onEnterLayoutOrScroll();
                this.mAdapterHelper.preProcess();
                if (!this.mLayoutRequestEaten) {
                    if (!this.hasUpdatedView()) {
                        this.mAdapterHelper.consumePostponedUpdates();
                    }
                    else {
                        this.dispatchLayout();
                    }
                }
                this.resumeRequestLayout(true);
                this.onExitLayoutOrScroll();
                TraceCompat.endSection();
            }
            else if (this.mAdapterHelper.hasPendingUpdates()) {
                TraceCompat.beginSection("RV FullInvalidate");
                this.dispatchLayout();
                TraceCompat.endSection();
            }
        }
    }
    
    void defaultOnMeasure(final int n, final int n2) {
        this.setMeasuredDimension(LayoutManager.chooseSize(n, this.getPaddingLeft() + this.getPaddingRight(), ViewCompat.getMinimumWidth((View)this)), LayoutManager.chooseSize(n2, this.getPaddingTop() + this.getPaddingBottom(), ViewCompat.getMinimumHeight((View)this)));
    }
    
    void dispatchChildAttached(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        this.onChildAttachedToWindow(view);
        if (this.mAdapter != null && childViewHolderInt != null) {
            this.mAdapter.onViewAttachedToWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int i = this.mOnChildAttachStateListeners.size() - 1; i >= 0; --i) {
                this.mOnChildAttachStateListeners.get(i).onChildViewAttachedToWindow(view);
            }
        }
    }
    
    void dispatchChildDetached(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        this.onChildDetachedFromWindow(view);
        if (this.mAdapter != null && childViewHolderInt != null) {
            this.mAdapter.onViewDetachedFromWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int i = this.mOnChildAttachStateListeners.size() - 1; i >= 0; --i) {
                this.mOnChildAttachStateListeners.get(i).onChildViewDetachedFromWindow(view);
            }
        }
    }
    
    void dispatchLayout() {
        if (this.mAdapter == null) {
            Log.e("RecyclerView", "No adapter attached; skipping layout");
            return;
        }
        if (this.mLayout != null) {
            this.mState.mIsMeasuring = false;
            if (this.mState.mLayoutStep != 1) {
                if (!this.mAdapterHelper.hasUpdates() && this.mLayout.getWidth() == this.getWidth() && this.mLayout.getHeight() == this.getHeight()) {
                    this.mLayout.setExactMeasureSpecsFrom(this);
                }
                else {
                    this.mLayout.setExactMeasureSpecsFrom(this);
                    this.dispatchLayoutStep2();
                }
            }
            else {
                this.dispatchLayoutStep1();
                this.mLayout.setExactMeasureSpecsFrom(this);
                this.dispatchLayoutStep2();
            }
            this.dispatchLayoutStep3();
            return;
        }
        Log.e("RecyclerView", "No layout manager attached; skipping layout");
    }
    
    public boolean dispatchNestedFling(final float n, final float n2, final boolean b) {
        return this.getScrollingChildHelper().dispatchNestedFling(n, n2, b);
    }
    
    public boolean dispatchNestedPreFling(final float n, final float n2) {
        return this.getScrollingChildHelper().dispatchNestedPreFling(n, n2);
    }
    
    public boolean dispatchNestedPreScroll(final int n, final int n2, final int[] array, final int[] array2) {
        return this.getScrollingChildHelper().dispatchNestedPreScroll(n, n2, array, array2);
    }
    
    public boolean dispatchNestedPreScroll(final int n, final int n2, final int[] array, final int[] array2, final int n3) {
        return this.getScrollingChildHelper().dispatchNestedPreScroll(n, n2, array, array2, n3);
    }
    
    public boolean dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, final int[] array) {
        return this.getScrollingChildHelper().dispatchNestedScroll(n, n2, n3, n4, array);
    }
    
    public boolean dispatchNestedScroll(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5) {
        return this.getScrollingChildHelper().dispatchNestedScroll(n, n2, n3, n4, array, n5);
    }
    
    void dispatchOnScrollStateChanged(final int n) {
        if (this.mLayout != null) {
            this.mLayout.onScrollStateChanged(n);
        }
        this.onScrollStateChanged(n);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrollStateChanged(this, n);
        }
        if (this.mScrollListeners != null) {
            for (int i = this.mScrollListeners.size() - 1; i >= 0; --i) {
                this.mScrollListeners.get(i).onScrollStateChanged(this, n);
            }
        }
    }
    
    void dispatchOnScrolled(final int n, final int n2) {
        ++this.mDispatchScrollCounter;
        final int scrollX = this.getScrollX();
        final int scrollY = this.getScrollY();
        this.onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        this.onScrolled(n, n2);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrolled(this, n, n2);
        }
        if (this.mScrollListeners != null) {
            for (int i = this.mScrollListeners.size() - 1; i >= 0; --i) {
                this.mScrollListeners.get(i).onScrolled(this, n, n2);
            }
        }
        --this.mDispatchScrollCounter;
    }
    
    void dispatchPendingImportantForAccessibilityChanges() {
        for (int i = this.mPendingAccessibilityImportanceChange.size() - 1; i >= 0; --i) {
            final ViewHolder viewHolder = this.mPendingAccessibilityImportanceChange.get(i);
            if (viewHolder.itemView.getParent() == this && !viewHolder.shouldIgnore()) {
                final int mPendingAccessibilityState = viewHolder.mPendingAccessibilityState;
                if (mPendingAccessibilityState != -1) {
                    ViewCompat.setImportantForAccessibility(viewHolder.itemView, mPendingAccessibilityState);
                    viewHolder.mPendingAccessibilityState = -1;
                }
            }
        }
        this.mPendingAccessibilityImportanceChange.clear();
    }
    
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> sparseArray) {
        this.dispatchThawSelfOnly((SparseArray)sparseArray);
    }
    
    protected void dispatchSaveInstanceState(final SparseArray<Parcelable> sparseArray) {
        this.dispatchFreezeSelfOnly((SparseArray)sparseArray);
    }
    
    public void draw(final Canvas canvas) {
        final int n = 1;
        final boolean b = false;
        super.draw(canvas);
        for (int size = this.mItemDecorations.size(), i = 0; i < size; ++i) {
            this.mItemDecorations.get(i).onDrawOver(canvas, this, this.mState);
        }
        boolean b2;
        if (this.mLeftGlow != null && !this.mLeftGlow.isFinished()) {
            final int save = canvas.save();
            int paddingBottom;
            if (!this.mClipToPadding) {
                paddingBottom = 0;
            }
            else {
                paddingBottom = this.getPaddingBottom();
            }
            canvas.rotate(270.0f);
            canvas.translate((float)(paddingBottom + -this.getHeight()), 0.0f);
            if (this.mLeftGlow != null && this.mLeftGlow.draw(canvas)) {
                b2 = true;
            }
            else {
                b2 = false;
            }
            canvas.restoreToCount(save);
        }
        else {
            b2 = false;
        }
        boolean b3;
        if (this.mTopGlow == null) {
            b3 = b2;
        }
        else {
            b3 = b2;
            if (!this.mTopGlow.isFinished()) {
                final int save2 = canvas.save();
                if (this.mClipToPadding) {
                    canvas.translate((float)this.getPaddingLeft(), (float)this.getPaddingTop());
                }
                boolean b4;
                if (this.mTopGlow != null && this.mTopGlow.draw(canvas)) {
                    b4 = true;
                }
                else {
                    b4 = false;
                }
                b3 = (b2 | b4);
                canvas.restoreToCount(save2);
            }
        }
        boolean b5;
        if (this.mRightGlow == null) {
            b5 = b3;
        }
        else {
            b5 = b3;
            if (!this.mRightGlow.isFinished()) {
                final int save3 = canvas.save();
                final int width = this.getWidth();
                int paddingTop;
                if (!this.mClipToPadding) {
                    paddingTop = 0;
                }
                else {
                    paddingTop = this.getPaddingTop();
                }
                canvas.rotate(90.0f);
                canvas.translate((float)(-paddingTop), (float)(-width));
                boolean b6;
                if (this.mRightGlow != null && this.mRightGlow.draw(canvas)) {
                    b6 = true;
                }
                else {
                    b6 = false;
                }
                b5 = (b3 | b6);
                canvas.restoreToCount(save3);
            }
        }
        boolean b7;
        if (this.mBottomGlow == null) {
            b7 = b5;
        }
        else {
            b7 = b5;
            if (!this.mBottomGlow.isFinished()) {
                final int save4 = canvas.save();
                canvas.rotate(180.0f);
                if (!this.mClipToPadding) {
                    canvas.translate((float)(-this.getWidth()), (float)(-this.getHeight()));
                }
                else {
                    canvas.translate((float)(-this.getWidth() + this.getPaddingRight()), (float)(-this.getHeight() + this.getPaddingBottom()));
                }
                int n2;
                if (this.mBottomGlow == null) {
                    n2 = (b ? 1 : 0);
                }
                else {
                    n2 = (b ? 1 : 0);
                    if (this.mBottomGlow.draw(canvas)) {
                        n2 = 1;
                    }
                }
                b7 = (((b5 ? 1 : 0) | n2) != 0x0);
                canvas.restoreToCount(save4);
            }
        }
        int n3;
        if (!b7 && this.mItemAnimator != null && this.mItemDecorations.size() > 0) {
            n3 = n;
            if (!this.mItemAnimator.isRunning()) {
                n3 = (b7 ? 1 : 0);
            }
        }
        else {
            n3 = (b7 ? 1 : 0);
        }
        if (n3 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    public boolean drawChild(final Canvas canvas, final View view, final long n) {
        return super.drawChild(canvas, view, n);
    }
    
    void eatRequestLayout() {
        ++this.mEatRequestLayout;
        if (this.mEatRequestLayout == 1 && !this.mLayoutFrozen) {
            this.mLayoutRequestEaten = false;
        }
    }
    
    void ensureBottomGlow() {
        if (this.mBottomGlow == null) {
            this.mBottomGlow = new EdgeEffect(this.getContext());
            if (!this.mClipToPadding) {
                this.mBottomGlow.setSize(this.getMeasuredWidth(), this.getMeasuredHeight());
            }
            else {
                this.mBottomGlow.setSize(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom());
            }
        }
    }
    
    void ensureLeftGlow() {
        if (this.mLeftGlow == null) {
            this.mLeftGlow = new EdgeEffect(this.getContext());
            if (!this.mClipToPadding) {
                this.mLeftGlow.setSize(this.getMeasuredHeight(), this.getMeasuredWidth());
            }
            else {
                this.mLeftGlow.setSize(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight());
            }
        }
    }
    
    void ensureRightGlow() {
        if (this.mRightGlow == null) {
            this.mRightGlow = new EdgeEffect(this.getContext());
            if (!this.mClipToPadding) {
                this.mRightGlow.setSize(this.getMeasuredHeight(), this.getMeasuredWidth());
            }
            else {
                this.mRightGlow.setSize(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight());
            }
        }
    }
    
    void ensureTopGlow() {
        if (this.mTopGlow == null) {
            this.mTopGlow = new EdgeEffect(this.getContext());
            if (!this.mClipToPadding) {
                this.mTopGlow.setSize(this.getMeasuredWidth(), this.getMeasuredHeight());
            }
            else {
                this.mTopGlow.setSize(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom());
            }
        }
    }
    
    String exceptionLabel() {
        return " " + super.toString() + ", adapter:" + this.mAdapter + ", layout:" + this.mLayout + ", context:" + this.getContext();
    }
    
    final void fillRemainingScrollValues(final State state) {
        if (this.getScrollState() != 2) {
            state.mRemainingScrollHorizontal = 0;
            state.mRemainingScrollVertical = 0;
        }
        else {
            final OverScroller access$400 = this.mViewFlinger.mScroller;
            state.mRemainingScrollHorizontal = access$400.getFinalX() - access$400.getCurrX();
            state.mRemainingScrollVertical = access$400.getFinalY() - access$400.getCurrY();
        }
    }
    
    public View findChildViewUnder(final float n, final float n2) {
        int childCount = this.mChildHelper.getChildCount();
        while (true) {
            final int n3 = childCount - 1;
            if (n3 < 0) {
                return null;
            }
            final View child = this.mChildHelper.getChildAt(n3);
            final float translationX = child.getTranslationX();
            final float translationY = child.getTranslationY();
            childCount = n3;
            if (n < child.getLeft() + translationX) {
                continue;
            }
            childCount = n3;
            if (n > translationX + child.getRight()) {
                continue;
            }
            childCount = n3;
            if (n2 < child.getTop() + translationY) {
                continue;
            }
            childCount = n3;
            if (n2 <= child.getBottom() + translationY) {
                return child;
            }
        }
    }
    
    @Nullable
    public View findContainingItemView(View view) {
        ViewParent viewParent;
        for (viewParent = view.getParent(); viewParent != null && viewParent != this && viewParent instanceof View; viewParent = view.getParent()) {
            view = (View)viewParent;
        }
        if (viewParent != this) {
            view = null;
        }
        return view;
    }
    
    @Nullable
    public ViewHolder findContainingViewHolder(final View view) {
        final ViewHolder viewHolder = null;
        final View containingItemView = this.findContainingItemView(view);
        ViewHolder childViewHolder = viewHolder;
        if (containingItemView != null) {
            childViewHolder = this.getChildViewHolder(containingItemView);
        }
        return childViewHolder;
    }
    
    public ViewHolder findViewHolderForAdapterPosition(final int n) {
        if (!this.mDataSetHasChangedAfterLayout) {
            final int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
            int i = 0;
            ViewHolder viewHolder = null;
            while (i < unfilteredChildCount) {
                final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
                ViewHolder viewHolder2;
                if (childViewHolderInt == null) {
                    viewHolder2 = viewHolder;
                }
                else {
                    viewHolder2 = viewHolder;
                    if (!childViewHolderInt.isRemoved()) {
                        viewHolder2 = viewHolder;
                        if (this.getAdapterPositionFor(childViewHolderInt) == n) {
                            if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                                return childViewHolderInt;
                            }
                            viewHolder2 = childViewHolderInt;
                        }
                    }
                }
                ++i;
                viewHolder = viewHolder2;
            }
            return viewHolder;
        }
        return null;
    }
    
    public ViewHolder findViewHolderForItemId(final long n) {
        if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
            final int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
            int i = 0;
            ViewHolder viewHolder = null;
            while (i < unfilteredChildCount) {
                final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
                ViewHolder viewHolder2;
                if (childViewHolderInt == null) {
                    viewHolder2 = viewHolder;
                }
                else {
                    viewHolder2 = viewHolder;
                    if (!childViewHolderInt.isRemoved()) {
                        viewHolder2 = viewHolder;
                        if (childViewHolderInt.getItemId() == n) {
                            if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                                return childViewHolderInt;
                            }
                            viewHolder2 = childViewHolderInt;
                        }
                    }
                }
                ++i;
                viewHolder = viewHolder2;
            }
            return viewHolder;
        }
        return null;
    }
    
    public ViewHolder findViewHolderForLayoutPosition(final int n) {
        return this.findViewHolderForPosition(n, false);
    }
    
    @Deprecated
    public ViewHolder findViewHolderForPosition(final int n) {
        return this.findViewHolderForPosition(n, false);
    }
    
    ViewHolder findViewHolderForPosition(final int n, final boolean b) {
        final int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int i = 0;
        ViewHolder viewHolder = null;
        while (i < unfilteredChildCount) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            ViewHolder viewHolder2 = null;
            Label_0046: {
                if (childViewHolderInt == null) {
                    viewHolder2 = viewHolder;
                }
                else {
                    viewHolder2 = viewHolder;
                    if (!childViewHolderInt.isRemoved()) {
                        if (!b) {
                            viewHolder2 = viewHolder;
                            if (childViewHolderInt.getLayoutPosition() != n) {
                                break Label_0046;
                            }
                        }
                        else {
                            viewHolder2 = viewHolder;
                            if (childViewHolderInt.mPosition != n) {
                                break Label_0046;
                            }
                        }
                        if (!this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                            return childViewHolderInt;
                        }
                        viewHolder2 = childViewHolderInt;
                    }
                }
            }
            ++i;
            viewHolder = viewHolder2;
        }
        return viewHolder;
    }
    
    public boolean fling(int max, int max2) {
        if (this.mLayout == null) {
            Log.e("RecyclerView", "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return false;
        }
        if (this.mLayoutFrozen) {
            return false;
        }
        final boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
        final boolean canScrollVertically = this.mLayout.canScrollVertically();
        int a;
        if (canScrollHorizontally && Math.abs(max) >= this.mMinFlingVelocity) {
            a = max;
        }
        else {
            a = 0;
        }
        if (!canScrollVertically || Math.abs(max2) < this.mMinFlingVelocity) {
            max2 = 0;
        }
        if (a == 0 && max2 == 0) {
            return false;
        }
        if (!this.dispatchNestedPreFling((float)a, (float)max2)) {
            final boolean b = canScrollHorizontally || canScrollVertically;
            this.dispatchNestedFling((float)a, (float)max2, b);
            if (this.mOnFlingListener != null && this.mOnFlingListener.onFling(a, max2)) {
                return true;
            }
            if (b) {
                if (!canScrollHorizontally) {
                    max = 0;
                }
                else {
                    max = 1;
                }
                if (canScrollVertically) {
                    max |= 0x2;
                }
                this.startNestedScroll(max, 1);
                max = Math.max(-this.mMaxFlingVelocity, Math.min(a, this.mMaxFlingVelocity));
                max2 = Math.max(-this.mMaxFlingVelocity, Math.min(max2, this.mMaxFlingVelocity));
                this.mViewFlinger.fling(max, max2);
                return true;
            }
        }
        return false;
    }
    
    public View focusSearch(final View view, int n) {
        final int n2 = 1;
        final View onInterceptFocusSearch = this.mLayout.onInterceptFocusSearch(view, n);
        if (onInterceptFocusSearch != null) {
            return onInterceptFocusSearch;
        }
        boolean b;
        if (this.mAdapter != null && this.mLayout != null && !this.isComputingLayout() && !this.mLayoutFrozen) {
            b = true;
        }
        else {
            b = false;
        }
        final FocusFinder instance = FocusFinder.getInstance();
        View view2;
        if (b && (n == 2 || n == 1)) {
            boolean b2;
            if (!this.mLayout.canScrollVertically()) {
                b2 = false;
            }
            else {
                int n3;
                if (n != 2) {
                    n3 = 33;
                }
                else {
                    n3 = 130;
                }
                if (instance.findNextFocus((ViewGroup)this, view, n3) != null) {
                    b2 = false;
                }
                else {
                    b2 = true;
                }
                if (RecyclerView.FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    n = n3;
                }
            }
            int n6;
            if ((b2 ? 1 : 0) == 0 && this.mLayout.canScrollHorizontally()) {
                boolean b3;
                if (this.mLayout.getLayoutDirection() != 1) {
                    b3 = false;
                }
                else {
                    b3 = true;
                }
                boolean b4;
                if (n != 2) {
                    b4 = false;
                }
                else {
                    b4 = true;
                }
                int n4;
                if (!(b3 ^ b4)) {
                    n4 = 17;
                }
                else {
                    n4 = 66;
                }
                int n5 = n2;
                if (instance.findNextFocus((ViewGroup)this, view, n4) != null) {
                    n5 = 0;
                }
                n6 = n5;
                if (RecyclerView.FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
                    n = n4;
                    n6 = n5;
                }
            }
            else {
                n6 = (b2 ? 1 : 0);
            }
            if (n6 != 0) {
                this.consumePendingUpdateOperations();
                if (this.findContainingItemView(view) == null) {
                    return null;
                }
                this.eatRequestLayout();
                this.mLayout.onFocusSearchFailed(view, n, this.mRecycler, this.mState);
                this.resumeRequestLayout(false);
            }
            view2 = instance.findNextFocus((ViewGroup)this, view, n);
        }
        else {
            view2 = instance.findNextFocus((ViewGroup)this, view, n);
            if (view2 == null && b) {
                this.consumePendingUpdateOperations();
                if (this.findContainingItemView(view) == null) {
                    return null;
                }
                this.eatRequestLayout();
                view2 = this.mLayout.onFocusSearchFailed(view, n, this.mRecycler, this.mState);
                this.resumeRequestLayout(false);
            }
        }
        if (view2 == null || view2.hasFocusable()) {
            View focusSearch = view2;
            if (!this.isPreferredNextFocus(view, view2, n)) {
                focusSearch = super.focusSearch(view, n);
            }
            return focusSearch;
        }
        if (this.getFocusedChild() != null) {
            this.requestChildOnScreen(view2, null);
            return view;
        }
        return super.focusSearch(view, n);
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        if (this.mLayout != null) {
            return (ViewGroup$LayoutParams)this.mLayout.generateDefaultLayoutParams();
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager" + this.exceptionLabel());
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        if (this.mLayout != null) {
            return (ViewGroup$LayoutParams)this.mLayout.generateLayoutParams(this.getContext(), set);
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager" + this.exceptionLabel());
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (this.mLayout != null) {
            return (ViewGroup$LayoutParams)this.mLayout.generateLayoutParams(viewGroup$LayoutParams);
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager" + this.exceptionLabel());
    }
    
    public Adapter getAdapter() {
        return this.mAdapter;
    }
    
    int getAdapterPositionFor(final ViewHolder viewHolder) {
        if (!viewHolder.hasAnyOfTheFlags(524) && viewHolder.isBound()) {
            return this.mAdapterHelper.applyPendingUpdatesToPosition(viewHolder.mPosition);
        }
        return -1;
    }
    
    public int getBaseline() {
        if (this.mLayout == null) {
            return super.getBaseline();
        }
        return this.mLayout.getBaseline();
    }
    
    long getChangedHolderKey(final ViewHolder viewHolder) {
        long itemId;
        if (!this.mAdapter.hasStableIds()) {
            itemId = viewHolder.mPosition;
        }
        else {
            itemId = viewHolder.getItemId();
        }
        return itemId;
    }
    
    public int getChildAdapterPosition(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        int adapterPosition;
        if (childViewHolderInt == null) {
            adapterPosition = -1;
        }
        else {
            adapterPosition = childViewHolderInt.getAdapterPosition();
        }
        return adapterPosition;
    }
    
    protected int getChildDrawingOrder(final int n, final int n2) {
        if (this.mChildDrawingOrderCallback != null) {
            return this.mChildDrawingOrderCallback.onGetChildDrawingOrder(n, n2);
        }
        return super.getChildDrawingOrder(n, n2);
    }
    
    public long getChildItemId(final View view) {
        long itemId = -1L;
        if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
            if (childViewHolderInt != null) {
                itemId = childViewHolderInt.getItemId();
            }
            return itemId;
        }
        return -1L;
    }
    
    public int getChildLayoutPosition(final View view) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        int layoutPosition;
        if (childViewHolderInt == null) {
            layoutPosition = -1;
        }
        else {
            layoutPosition = childViewHolderInt.getLayoutPosition();
        }
        return layoutPosition;
    }
    
    @Deprecated
    public int getChildPosition(final View view) {
        return this.getChildAdapterPosition(view);
    }
    
    public ViewHolder getChildViewHolder(final View obj) {
        final ViewParent parent = obj.getParent();
        if (parent != null && parent != this) {
            throw new IllegalArgumentException("View " + obj + " is not a direct child of " + this);
        }
        return getChildViewHolderInt(obj);
    }
    
    public boolean getClipToPadding() {
        return this.mClipToPadding;
    }
    
    public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }
    
    public void getDecoratedBoundsWithMargins(final View view, final Rect rect) {
        getDecoratedBoundsWithMarginsInt(view, rect);
    }
    
    public ItemAnimator getItemAnimator() {
        return this.mItemAnimator;
    }
    
    Rect getItemDecorInsetsForChild(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.mInsetsDirty) {
            return layoutParams.mDecorInsets;
        }
        if (this.mState.isPreLayout() && (layoutParams.isItemChanged() || layoutParams.isViewInvalid())) {
            return layoutParams.mDecorInsets;
        }
        final Rect mDecorInsets = layoutParams.mDecorInsets;
        mDecorInsets.set(0, 0, 0, 0);
        for (int size = this.mItemDecorations.size(), i = 0; i < size; ++i) {
            this.mTempRect.set(0, 0, 0, 0);
            this.mItemDecorations.get(i).getItemOffsets(this.mTempRect, view, this, this.mState);
            mDecorInsets.left += this.mTempRect.left;
            mDecorInsets.top += this.mTempRect.top;
            mDecorInsets.right += this.mTempRect.right;
            mDecorInsets.bottom += this.mTempRect.bottom;
        }
        layoutParams.mInsetsDirty = false;
        return mDecorInsets;
    }
    
    public ItemDecoration getItemDecorationAt(final int index) {
        if (index >= 0 && index < this.mItemDecorations.size()) {
            return this.mItemDecorations.get(index);
        }
        return null;
    }
    
    public LayoutManager getLayoutManager() {
        return this.mLayout;
    }
    
    public int getMaxFlingVelocity() {
        return this.mMaxFlingVelocity;
    }
    
    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }
    
    long getNanoTime() {
        if (!RecyclerView.ALLOW_THREAD_GAP_WORK) {
            return 0L;
        }
        return System.nanoTime();
    }
    
    @Nullable
    public OnFlingListener getOnFlingListener() {
        return this.mOnFlingListener;
    }
    
    public boolean getPreserveFocusAfterLayout() {
        return this.mPreserveFocusAfterLayout;
    }
    
    public RecycledViewPool getRecycledViewPool() {
        return this.mRecycler.getRecycledViewPool();
    }
    
    public int getScrollState() {
        return this.mScrollState;
    }
    
    public boolean hasFixedSize() {
        return this.mHasFixedSize;
    }
    
    public boolean hasNestedScrollingParent() {
        return this.getScrollingChildHelper().hasNestedScrollingParent();
    }
    
    public boolean hasNestedScrollingParent(final int n) {
        return this.getScrollingChildHelper().hasNestedScrollingParent(n);
    }
    
    public boolean hasPendingAdapterUpdates() {
        boolean b = false;
        if (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout || this.mAdapterHelper.hasPendingUpdates()) {
            b = true;
        }
        return b;
    }
    
    void initAdapterManager() {
        this.mAdapterHelper = new AdapterHelper((AdapterHelper.Callback)new AdapterHelper.Callback() {
            void dispatchUpdate(final UpdateOp updateOp) {
                switch (updateOp.cmd) {
                    case 1: {
                        RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                        break;
                    }
                    case 2: {
                        RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                        break;
                    }
                    case 4: {
                        RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                        break;
                    }
                    case 8: {
                        RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, 1);
                        break;
                    }
                }
            }
            
            @Override
            public ViewHolder findViewHolder(final int n) {
                final ViewHolder viewHolderForPosition = RecyclerView.this.findViewHolderForPosition(n, true);
                if (viewHolderForPosition == null) {
                    return null;
                }
                if (!RecyclerView.this.mChildHelper.isHidden(viewHolderForPosition.itemView)) {
                    return viewHolderForPosition;
                }
                return null;
            }
            
            @Override
            public void markViewHoldersUpdated(final int n, final int n2, final Object o) {
                RecyclerView.this.viewRangeUpdate(n, n2, o);
                RecyclerView.this.mItemsChanged = true;
            }
            
            @Override
            public void offsetPositionsForAdd(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForInsert(n, n2);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
            
            @Override
            public void offsetPositionsForMove(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForMove(n, n2);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
            
            @Override
            public void offsetPositionsForRemovingInvisible(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForRemove(n, n2, true);
                RecyclerView.this.mItemsAddedOrRemoved = true;
                final State mState = RecyclerView.this.mState;
                mState.mDeletedInvisibleItemCountSincePreviousLayout += n2;
            }
            
            @Override
            public void offsetPositionsForRemovingLaidOutOrNewView(final int n, final int n2) {
                RecyclerView.this.offsetPositionRecordsForRemove(n, n2, false);
                RecyclerView.this.mItemsAddedOrRemoved = true;
            }
            
            @Override
            public void onDispatchFirstPass(final UpdateOp updateOp) {
                this.dispatchUpdate(updateOp);
            }
            
            @Override
            public void onDispatchSecondPass(final UpdateOp updateOp) {
                this.dispatchUpdate(updateOp);
            }
        });
    }
    
    @VisibleForTesting
    void initFastScroller(final StateListDrawable stateListDrawable, final Drawable drawable, final StateListDrawable stateListDrawable2, final Drawable drawable2) {
        if (stateListDrawable != null && drawable != null && stateListDrawable2 != null && drawable2 != null) {
            final Resources resources = this.getContext().getResources();
            new FastScroller(this, stateListDrawable, drawable, stateListDrawable2, drawable2, resources.getDimensionPixelSize(R.dimen.fastscroll_default_thickness), resources.getDimensionPixelSize(R.dimen.fastscroll_minimum_range), resources.getDimensionPixelOffset(R.dimen.fastscroll_margin));
            return;
        }
        throw new IllegalArgumentException("Trying to set fast scroller without both required drawables." + this.exceptionLabel());
    }
    
    void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }
    
    public void invalidateItemDecorations() {
        if (this.mItemDecorations.size() != 0) {
            if (this.mLayout != null) {
                this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
            }
            this.markItemDecorInsetsDirty();
            this.requestLayout();
        }
    }
    
    boolean isAccessibilityEnabled() {
        boolean b = false;
        if (this.mAccessibilityManager != null && this.mAccessibilityManager.isEnabled()) {
            b = true;
        }
        return b;
    }
    
    public boolean isAnimating() {
        boolean b = false;
        if (this.mItemAnimator != null && this.mItemAnimator.isRunning()) {
            b = true;
        }
        return b;
    }
    
    public boolean isAttachedToWindow() {
        return this.mIsAttached;
    }
    
    public boolean isComputingLayout() {
        boolean b = false;
        if (this.mLayoutOrScrollCounter > 0) {
            b = true;
        }
        return b;
    }
    
    public boolean isLayoutFrozen() {
        return this.mLayoutFrozen;
    }
    
    public boolean isNestedScrollingEnabled() {
        return this.getScrollingChildHelper().isNestedScrollingEnabled();
    }
    
    void jumpToPositionForSmoothScroller(final int n) {
        if (this.mLayout != null) {
            this.mLayout.scrollToPosition(n);
            this.awakenScrollBars();
        }
    }
    
    void markItemDecorInsetsDirty() {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            ((LayoutParams)this.mChildHelper.getUnfilteredChildAt(i).getLayoutParams()).mInsetsDirty = true;
        }
        this.mRecycler.markItemDecorInsetsDirty();
    }
    
    void markKnownViewsInvalid() {
        for (int i = 0; i < this.mChildHelper.getUnfilteredChildCount(); ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.addFlags(6);
            }
        }
        this.markItemDecorInsetsDirty();
        this.mRecycler.markKnownViewsInvalid();
    }
    
    public void offsetChildrenHorizontal(final int n) {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            this.mChildHelper.getChildAt(i).offsetLeftAndRight(n);
        }
    }
    
    public void offsetChildrenVertical(final int n) {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            this.mChildHelper.getChildAt(i).offsetTopAndBottom(n);
        }
    }
    
    void offsetPositionRecordsForInsert(final int n, final int n2) {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.mPosition >= n) {
                childViewHolderInt.offsetPosition(n2, false);
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForInsert(n, n2);
        this.requestLayout();
    }
    
    void offsetPositionRecordsForMove(final int n, final int n2) {
        final int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int n3;
        int n4;
        int n5;
        if (n >= n2) {
            n3 = 1;
            n4 = n;
            n5 = n2;
        }
        else {
            n3 = -1;
            n4 = n2;
            n5 = n;
        }
        for (int i = 0; i < unfilteredChildCount; ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && childViewHolderInt.mPosition >= n5 && childViewHolderInt.mPosition <= n4) {
                if (childViewHolderInt.mPosition != n) {
                    childViewHolderInt.offsetPosition(n3, false);
                }
                else {
                    childViewHolderInt.offsetPosition(n2 - n, false);
                }
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForMove(n, n2);
        this.requestLayout();
    }
    
    void offsetPositionRecordsForRemove(final int n, final int n2, final boolean b) {
        for (int i = 0; i < this.mChildHelper.getUnfilteredChildCount(); ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore()) {
                if (childViewHolderInt.mPosition < n + n2) {
                    if (childViewHolderInt.mPosition >= n) {
                        childViewHolderInt.flagRemovedAndOffsetPosition(n - 1, -n2, b);
                        this.mState.mStructureChanged = true;
                    }
                }
                else {
                    childViewHolderInt.offsetPosition(-n2, b);
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForRemove(n, n2, b);
        this.requestLayout();
    }
    
    protected void onAttachedToWindow() {
        boolean mFirstLayoutComplete = true;
        super.onAttachedToWindow();
        this.mLayoutOrScrollCounter = 0;
        this.mIsAttached = true;
        if (!this.mFirstLayoutComplete || this.isLayoutRequested()) {
            mFirstLayoutComplete = false;
        }
        this.mFirstLayoutComplete = mFirstLayoutComplete;
        if (this.mLayout != null) {
            this.mLayout.dispatchAttachedToWindow(this);
        }
        this.mPostedAnimatorRunner = false;
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            this.mGapWorker = GapWorker.sGapWorker.get();
            if (this.mGapWorker == null) {
                this.mGapWorker = new GapWorker();
                final Display display = ViewCompat.getDisplay((View)this);
                final float n = 60.0f;
                float n2;
                if (this.isInEditMode()) {
                    n2 = n;
                }
                else {
                    n2 = n;
                    if (display != null) {
                        final float refreshRate = display.getRefreshRate();
                        n2 = n;
                        if (refreshRate >= 30.0f) {
                            n2 = refreshRate;
                        }
                    }
                }
                this.mGapWorker.mFrameIntervalNs = (long)(1.0E9f / n2);
                GapWorker.sGapWorker.set(this.mGapWorker);
            }
            this.mGapWorker.add(this);
        }
    }
    
    public void onChildAttachedToWindow(final View view) {
    }
    
    public void onChildDetachedFromWindow(final View view) {
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
        }
        this.stopScroll();
        this.mIsAttached = false;
        if (this.mLayout != null) {
            this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
        }
        this.mPendingAccessibilityImportanceChange.clear();
        this.removeCallbacks(this.mItemAnimatorRunner);
        this.mViewInfoStore.onDetach();
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
            this.mGapWorker.remove(this);
            this.mGapWorker = null;
        }
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        for (int size = this.mItemDecorations.size(), i = 0; i < size; ++i) {
            this.mItemDecorations.get(i).onDraw(canvas, this, this.mState);
        }
    }
    
    void onEnterLayoutOrScroll() {
        ++this.mLayoutOrScrollCounter;
    }
    
    void onExitLayoutOrScroll() {
        this.onExitLayoutOrScroll(true);
    }
    
    void onExitLayoutOrScroll(final boolean b) {
        --this.mLayoutOrScrollCounter;
        if (this.mLayoutOrScrollCounter < 1) {
            this.mLayoutOrScrollCounter = 0;
            if (b) {
                this.dispatchContentChangedIfNecessary();
                this.dispatchPendingImportantForAccessibilityChanges();
            }
        }
    }
    
    public boolean onGenericMotionEvent(final MotionEvent motionEvent) {
        if (this.mLayout == null) {
            return false;
        }
        if (!this.mLayoutFrozen) {
            if (motionEvent.getAction() == 8) {
                float axisValue;
                float n;
                if ((motionEvent.getSource() & 0x2) == 0x0) {
                    if ((motionEvent.getSource() & 0x400000) == 0x0) {
                        axisValue = 0.0f;
                        n = 0.0f;
                    }
                    else {
                        axisValue = motionEvent.getAxisValue(26);
                        if (!this.mLayout.canScrollVertically()) {
                            if (!this.mLayout.canScrollHorizontally()) {
                                axisValue = 0.0f;
                                n = 0.0f;
                            }
                            else {
                                n = 0.0f;
                            }
                        }
                        else {
                            n = -axisValue;
                            axisValue = 0.0f;
                        }
                    }
                }
                else {
                    float n2;
                    if (!this.mLayout.canScrollVertically()) {
                        n2 = 0.0f;
                    }
                    else {
                        n2 = -motionEvent.getAxisValue(9);
                    }
                    if (!this.mLayout.canScrollHorizontally()) {
                        n = n2;
                        axisValue = 0.0f;
                    }
                    else {
                        final float axisValue2 = motionEvent.getAxisValue(10);
                        n = n2;
                        axisValue = axisValue2;
                    }
                }
                if (n != 0.0f || axisValue != 0.0f) {
                    this.scrollByInternal((int)(axisValue * this.mScaledHorizontalScrollFactor), (int)(this.mScaledVerticalScrollFactor * n), motionEvent);
                }
            }
            return false;
        }
        return false;
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        if (this.mLayoutFrozen) {
            return false;
        }
        if (this.dispatchOnItemTouchIntercept(motionEvent)) {
            this.cancelTouch();
            return true;
        }
        if (this.mLayout != null) {
            final boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            final boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            final int actionMasked = motionEvent.getActionMasked();
            final int actionIndex = motionEvent.getActionIndex();
            switch (actionMasked) {
                case 0: {
                    if (this.mIgnoreMotionEventTillDown) {
                        this.mIgnoreMotionEventTillDown = false;
                    }
                    this.mScrollPointerId = motionEvent.getPointerId(0);
                    final int n = (int)(motionEvent.getX() + 0.5f);
                    this.mLastTouchX = n;
                    this.mInitialTouchX = n;
                    final int n2 = (int)(motionEvent.getY() + 0.5f);
                    this.mLastTouchY = n2;
                    this.mInitialTouchY = n2;
                    if (this.mScrollState == 2) {
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                        this.setScrollState(1);
                    }
                    this.mNestedOffsets[this.mNestedOffsets[1] = 0] = 0;
                    int n3;
                    if (!canScrollHorizontally) {
                        n3 = 0;
                    }
                    else {
                        n3 = 1;
                    }
                    if (canScrollVertically) {
                        n3 |= 0x2;
                    }
                    this.startNestedScroll(n3, 0);
                    break;
                }
                case 5: {
                    this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
                    final int n4 = (int)(motionEvent.getX(actionIndex) + 0.5f);
                    this.mLastTouchX = n4;
                    this.mInitialTouchX = n4;
                    final int n5 = (int)(motionEvent.getY(actionIndex) + 0.5f);
                    this.mLastTouchY = n5;
                    this.mInitialTouchY = n5;
                    break;
                }
                case 2: {
                    final int pointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
                    if (pointerIndex < 0) {
                        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                        return false;
                    }
                    final int mLastTouchX = (int)(motionEvent.getX(pointerIndex) + 0.5f);
                    final int mLastTouchY = (int)(motionEvent.getY(pointerIndex) + 0.5f);
                    if (this.mScrollState == 1) {
                        break;
                    }
                    final int mInitialTouchX = this.mInitialTouchX;
                    final int mInitialTouchY = this.mInitialTouchY;
                    int n6;
                    if (canScrollHorizontally && Math.abs(mLastTouchX - mInitialTouchX) > this.mTouchSlop) {
                        this.mLastTouchX = mLastTouchX;
                        n6 = 1;
                    }
                    else {
                        n6 = 0;
                    }
                    if (canScrollVertically && Math.abs(mLastTouchY - mInitialTouchY) > this.mTouchSlop) {
                        this.mLastTouchY = mLastTouchY;
                        n6 = 1;
                    }
                    if (n6 != 0) {
                        this.setScrollState(1);
                        break;
                    }
                    break;
                }
                case 6: {
                    this.onPointerUp(motionEvent);
                    break;
                }
                case 1: {
                    this.mVelocityTracker.clear();
                    this.stopNestedScroll(0);
                    break;
                }
                case 3: {
                    this.cancelTouch();
                    break;
                }
            }
            return this.mScrollState == 1;
        }
        return false;
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        TraceCompat.beginSection("RV OnLayout");
        this.dispatchLayout();
        TraceCompat.endSection();
        this.mFirstLayoutComplete = true;
    }
    
    protected void onMeasure(final int n, final int n2) {
        boolean b = false;
        if (this.mLayout != null) {
            if (!this.mLayout.mAutoMeasure) {
                if (this.mHasFixedSize) {
                    this.mLayout.onMeasure(this.mRecycler, this.mState, n, n2);
                    return;
                }
                if (!this.mAdapterUpdateDuringMeasure) {
                    if (this.mState.mRunPredictiveAnimations) {
                        this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredHeight());
                        return;
                    }
                }
                else {
                    this.eatRequestLayout();
                    this.onEnterLayoutOrScroll();
                    this.processAdapterUpdatesAndSetAnimationFlags();
                    this.onExitLayoutOrScroll();
                    if (!this.mState.mRunPredictiveAnimations) {
                        this.mAdapterHelper.consumeUpdatesInOnePass();
                        this.mState.mInPreLayout = false;
                    }
                    else {
                        this.mState.mInPreLayout = true;
                    }
                    this.resumeRequestLayout(this.mAdapterUpdateDuringMeasure = false);
                }
                if (this.mAdapter == null) {
                    this.mState.mItemCount = 0;
                }
                else {
                    this.mState.mItemCount = this.mAdapter.getItemCount();
                }
                this.eatRequestLayout();
                this.mLayout.onMeasure(this.mRecycler, this.mState, n, n2);
                this.resumeRequestLayout(false);
                this.mState.mInPreLayout = false;
            }
            else {
                final int mode = View$MeasureSpec.getMode(n);
                final int mode2 = View$MeasureSpec.getMode(n2);
                if (mode == 1073741824 && mode2 == 1073741824) {
                    b = true;
                }
                this.mLayout.onMeasure(this.mRecycler, this.mState, n, n2);
                if (b || this.mAdapter == null) {
                    return;
                }
                if (this.mState.mLayoutStep == 1) {
                    this.dispatchLayoutStep1();
                }
                this.mLayout.setMeasureSpecs(n, n2);
                this.mState.mIsMeasuring = true;
                this.dispatchLayoutStep2();
                this.mLayout.setMeasuredDimensionFromChildren(n, n2);
                if (this.mLayout.shouldMeasureTwice()) {
                    this.mLayout.setMeasureSpecs(View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 1073741824), View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), 1073741824));
                    this.mState.mIsMeasuring = true;
                    this.dispatchLayoutStep2();
                    this.mLayout.setMeasuredDimensionFromChildren(n, n2);
                }
            }
            return;
        }
        this.defaultOnMeasure(n, n2);
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        return !this.isComputingLayout() && super.onRequestFocusInDescendants(n, rect);
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.mPendingSavedState = (SavedState)parcelable;
            super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
            if (this.mLayout != null && this.mPendingSavedState.mLayoutState != null) {
                this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mPendingSavedState == null) {
            if (this.mLayout == null) {
                savedState.mLayoutState = null;
            }
            else {
                savedState.mLayoutState = this.mLayout.onSaveInstanceState();
            }
        }
        else {
            savedState.copyFrom(this.mPendingSavedState);
        }
        return (Parcelable)savedState;
    }
    
    public void onScrollStateChanged(final int n) {
    }
    
    public void onScrolled(final int n, final int n2) {
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (n != n3 || n2 != n4) {
            this.invalidateGlows();
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final int n = 0;
        if (this.mLayoutFrozen || this.mIgnoreMotionEventTillDown) {
            return false;
        }
        if (this.dispatchOnItemTouch(motionEvent)) {
            this.cancelTouch();
            return true;
        }
        if (this.mLayout == null) {
            return false;
        }
        final boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
        final boolean canScrollVertically = this.mLayout.canScrollVertically();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        final MotionEvent obtain = MotionEvent.obtain(motionEvent);
        final int actionMasked = motionEvent.getActionMasked();
        final int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            this.mNestedOffsets[this.mNestedOffsets[1] = 0] = 0;
        }
        obtain.offsetLocation((float)this.mNestedOffsets[0], (float)this.mNestedOffsets[1]);
        int n2 = n;
        while (true) {
            switch (actionMasked) {
                default: {
                    n2 = n;
                    break Label_0151;
                }
                case 3: {
                    this.cancelTouch();
                    n2 = n;
                    break Label_0151;
                }
                case 1: {
                    this.mVelocityTracker.addMovement(obtain);
                    this.mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaxFlingVelocity);
                    float n3;
                    if (!canScrollHorizontally) {
                        n3 = 0.0f;
                    }
                    else {
                        n3 = -this.mVelocityTracker.getXVelocity(this.mScrollPointerId);
                    }
                    float n4;
                    if (!canScrollVertically) {
                        n4 = 0.0f;
                    }
                    else {
                        n4 = -this.mVelocityTracker.getYVelocity(this.mScrollPointerId);
                    }
                    if ((n3 == 0.0f && n4 == 0.0f) || !this.fling((int)n3, (int)n4)) {
                        this.setScrollState(0);
                    }
                    this.resetTouch();
                    n2 = 1;
                    break Label_0151;
                }
                case 6: {
                    this.onPointerUp(motionEvent);
                    n2 = n;
                    break Label_0151;
                }
                case 5: {
                    this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
                    final int n5 = (int)(motionEvent.getX(actionIndex) + 0.5f);
                    this.mLastTouchX = n5;
                    this.mInitialTouchX = n5;
                    final int n6 = (int)(motionEvent.getY(actionIndex) + 0.5f);
                    this.mLastTouchY = n6;
                    this.mInitialTouchY = n6;
                    n2 = n;
                    break Label_0151;
                }
                case 0: {
                    this.mScrollPointerId = motionEvent.getPointerId(0);
                    final int n7 = (int)(motionEvent.getX() + 0.5f);
                    this.mLastTouchX = n7;
                    this.mInitialTouchX = n7;
                    final int n8 = (int)(motionEvent.getY() + 0.5f);
                    this.mLastTouchY = n8;
                    this.mInitialTouchY = n8;
                    int n9;
                    if (!canScrollHorizontally) {
                        n9 = 0;
                    }
                    else {
                        n9 = 1;
                    }
                    if (canScrollVertically) {
                        n9 |= 0x2;
                    }
                    this.startNestedScroll(n9, 0);
                    n2 = n;
                }
                case 4: {
                    if (n2 == 0) {
                        this.mVelocityTracker.addMovement(obtain);
                    }
                    obtain.recycle();
                    return true;
                }
                case 2: {
                    final int pointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
                    if (pointerIndex < 0) {
                        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                        return false;
                    }
                    final int n10 = (int)(motionEvent.getX(pointerIndex) + 0.5f);
                    final int n11 = (int)(motionEvent.getY(pointerIndex) + 0.5f);
                    int a = this.mLastTouchX - n10;
                    int a2 = this.mLastTouchY - n11;
                    if (this.dispatchNestedPreScroll(a, a2, this.mScrollConsumed, this.mScrollOffset, 0)) {
                        a -= this.mScrollConsumed[0];
                        a2 -= this.mScrollConsumed[1];
                        obtain.offsetLocation((float)this.mScrollOffset[0], (float)this.mScrollOffset[1]);
                        final int[] mNestedOffsets = this.mNestedOffsets;
                        mNestedOffsets[0] += this.mScrollOffset[0];
                        final int[] mNestedOffsets2 = this.mNestedOffsets;
                        mNestedOffsets2[1] += this.mScrollOffset[1];
                    }
                    int n12;
                    if (this.mScrollState == 1) {
                        n12 = a;
                    }
                    else {
                        boolean b;
                        if (canScrollHorizontally && Math.abs(a) > this.mTouchSlop) {
                            if (a <= 0) {
                                a += this.mTouchSlop;
                            }
                            else {
                                a -= this.mTouchSlop;
                            }
                            b = true;
                        }
                        else {
                            b = false;
                        }
                        int n13;
                        int n14;
                        if (!canScrollVertically) {
                            n13 = a2;
                            n14 = (b ? 1 : 0);
                        }
                        else {
                            n14 = (b ? 1 : 0);
                            n13 = a2;
                            if (Math.abs(a2) > this.mTouchSlop) {
                                int n15;
                                if (a2 <= 0) {
                                    n15 = this.mTouchSlop + a2;
                                }
                                else {
                                    n15 = a2 - this.mTouchSlop;
                                }
                                n14 = 1;
                                n13 = n15;
                            }
                        }
                        n12 = a;
                        a2 = n13;
                        if (n14 != 0) {
                            this.setScrollState(1);
                            n12 = a;
                            a2 = n13;
                        }
                    }
                    n2 = n;
                    if (this.mScrollState != 1) {
                        continue;
                    }
                    this.mLastTouchX = n10 - this.mScrollOffset[0];
                    this.mLastTouchY = n11 - this.mScrollOffset[1];
                    int n16;
                    if (!canScrollHorizontally) {
                        n16 = 0;
                    }
                    else {
                        n16 = n12;
                    }
                    int n17;
                    if (!canScrollVertically) {
                        n17 = 0;
                    }
                    else {
                        n17 = a2;
                    }
                    if (this.scrollByInternal(n16, n17, obtain)) {
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    n2 = n;
                    if (this.mGapWorker == null) {
                        continue;
                    }
                    if (n12 == 0 && a2 == 0) {
                        n2 = n;
                        continue;
                    }
                    this.mGapWorker.postFromTraversal(this, n12, a2);
                    n2 = n;
                    continue;
                }
            }
            break;
        }
    }
    
    void postAnimationRunner() {
        if (!this.mPostedAnimatorRunner && this.mIsAttached) {
            ViewCompat.postOnAnimation((View)this, this.mItemAnimatorRunner);
            this.mPostedAnimatorRunner = true;
        }
    }
    
    void recordAnimationInfoIfBouncedHiddenView(final ViewHolder viewHolder, final ItemHolderInfo itemHolderInfo) {
        viewHolder.setFlags(0, 8192);
        if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore()) {
            this.mViewInfoStore.addToOldChangeHolders(this.getChangedHolderKey(viewHolder), viewHolder);
        }
        this.mViewInfoStore.addToPreLayout(viewHolder, itemHolderInfo);
    }
    
    void removeAndRecycleViews() {
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
        }
        if (this.mLayout != null) {
            this.mLayout.removeAndRecycleAllViews(this.mRecycler);
            this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        }
        this.mRecycler.clear();
    }
    
    boolean removeAnimatingView(final View view) {
        boolean b = false;
        this.eatRequestLayout();
        final boolean removeViewIfHidden = this.mChildHelper.removeViewIfHidden(view);
        if (removeViewIfHidden) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
            this.mRecycler.unscrapView(childViewHolderInt);
            this.mRecycler.recycleViewHolderInternal(childViewHolderInt);
        }
        if (!removeViewIfHidden) {
            b = true;
        }
        this.resumeRequestLayout(b);
        return removeViewIfHidden;
    }
    
    protected void removeDetachedView(final View view, final boolean b) {
        final ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        if (childViewHolderInt != null) {
            if (!childViewHolderInt.isTmpDetached()) {
                if (!childViewHolderInt.shouldIgnore()) {
                    throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + childViewHolderInt + this.exceptionLabel());
                }
            }
            else {
                childViewHolderInt.clearTmpDetachFlag();
            }
        }
        view.clearAnimation();
        this.dispatchChildDetached(view);
        super.removeDetachedView(view, b);
    }
    
    public void removeItemDecoration(final ItemDecoration o) {
        boolean willNotDraw = false;
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
        }
        this.mItemDecorations.remove(o);
        if (this.mItemDecorations.isEmpty()) {
            if (this.getOverScrollMode() == 2) {
                willNotDraw = true;
            }
            this.setWillNotDraw(willNotDraw);
        }
        this.markItemDecorInsetsDirty();
        this.requestLayout();
    }
    
    public void removeOnChildAttachStateChangeListener(final OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners != null) {
            this.mOnChildAttachStateListeners.remove(onChildAttachStateChangeListener);
        }
    }
    
    public void removeOnItemTouchListener(final OnItemTouchListener o) {
        this.mOnItemTouchListeners.remove(o);
        if (this.mActiveOnItemTouchListener == o) {
            this.mActiveOnItemTouchListener = null;
        }
    }
    
    public void removeOnScrollListener(final OnScrollListener onScrollListener) {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.remove(onScrollListener);
        }
    }
    
    void repositionShadowingViews() {
        for (int childCount = this.mChildHelper.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.mChildHelper.getChildAt(i);
            final ViewHolder childViewHolder = this.getChildViewHolder(child);
            if (childViewHolder != null && childViewHolder.mShadowingHolder != null) {
                final View itemView = childViewHolder.mShadowingHolder.itemView;
                final int left = child.getLeft();
                final int top = child.getTop();
                if (left != itemView.getLeft() || top != itemView.getTop()) {
                    itemView.layout(left, top, itemView.getWidth() + left, itemView.getHeight() + top);
                }
            }
        }
    }
    
    public void requestChildFocus(final View view, final View view2) {
        if (!this.mLayout.onRequestChildFocus(this, this.mState, view, view2) && view2 != null) {
            this.requestChildOnScreen(view, view2);
        }
        super.requestChildFocus(view, view2);
    }
    
    public boolean requestChildRectangleOnScreen(final View view, final Rect rect, final boolean b) {
        return this.mLayout.requestChildRectangleOnScreen(this, view, rect, b);
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean b) {
        for (int size = this.mOnItemTouchListeners.size(), i = 0; i < size; ++i) {
            this.mOnItemTouchListeners.get(i).onRequestDisallowInterceptTouchEvent(b);
        }
        super.requestDisallowInterceptTouchEvent(b);
    }
    
    public void requestLayout() {
        if (this.mEatRequestLayout == 0 && !this.mLayoutFrozen) {
            super.requestLayout();
        }
        else {
            this.mLayoutRequestEaten = true;
        }
    }
    
    void resumeRequestLayout(final boolean b) {
        if (this.mEatRequestLayout < 1) {
            this.mEatRequestLayout = 1;
        }
        if (!b) {
            this.mLayoutRequestEaten = false;
        }
        if (this.mEatRequestLayout == 1) {
            if (b && this.mLayoutRequestEaten && !this.mLayoutFrozen && this.mLayout != null && this.mAdapter != null) {
                this.dispatchLayout();
            }
            if (!this.mLayoutFrozen) {
                this.mLayoutRequestEaten = false;
            }
        }
        --this.mEatRequestLayout;
    }
    
    void saveOldPositions() {
        for (int i = 0; i < this.mChildHelper.getUnfilteredChildCount(); ++i) {
            final ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.saveOldPosition();
            }
        }
    }
    
    public void scrollBy(int n, int n2) {
        if (this.mLayout == null) {
            Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        if (!this.mLayoutFrozen) {
            final boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            final boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (canScrollHorizontally || canScrollVertically) {
                if (!canScrollHorizontally) {
                    n = 0;
                }
                if (!canScrollVertically) {
                    n2 = 0;
                }
                this.scrollByInternal(n, n2, null);
            }
        }
    }
    
    boolean scrollByInternal(final int n, final int n2, final MotionEvent motionEvent) {
        boolean b = false;
        this.consumePendingUpdateOperations();
        int n3;
        int n4;
        int n5;
        int n6;
        if (this.mAdapter == null) {
            n3 = 0;
            n4 = 0;
            n5 = 0;
            n6 = 0;
        }
        else {
            this.eatRequestLayout();
            this.onEnterLayoutOrScroll();
            TraceCompat.beginSection("RV Scroll");
            this.fillRemainingScrollValues(this.mState);
            int scrollHorizontallyBy;
            if (n == 0) {
                scrollHorizontallyBy = 0;
                n6 = 0;
            }
            else {
                scrollHorizontallyBy = this.mLayout.scrollHorizontallyBy(n, this.mRecycler, this.mState);
                n6 = n - scrollHorizontallyBy;
            }
            int scrollVerticallyBy;
            if (n2 == 0) {
                scrollVerticallyBy = 0;
                n5 = 0;
            }
            else {
                scrollVerticallyBy = this.mLayout.scrollVerticallyBy(n2, this.mRecycler, this.mState);
                n5 = n2 - scrollVerticallyBy;
            }
            TraceCompat.endSection();
            this.repositionShadowingViews();
            this.onExitLayoutOrScroll();
            this.resumeRequestLayout(false);
            final int n7 = scrollHorizontallyBy;
            n3 = scrollVerticallyBy;
            n4 = n7;
        }
        if (!this.mItemDecorations.isEmpty()) {
            this.invalidate();
        }
        if (!this.dispatchNestedScroll(n4, n3, n6, n5, this.mScrollOffset, 0)) {
            if (this.getOverScrollMode() != 2) {
                if (motionEvent != null && !MotionEventCompat.isFromSource(motionEvent, 8194)) {
                    this.pullGlows(motionEvent.getX(), (float)n6, motionEvent.getY(), (float)n5);
                }
                this.considerReleasingGlowsOnScroll(n, n2);
            }
        }
        else {
            this.mLastTouchX -= this.mScrollOffset[0];
            this.mLastTouchY -= this.mScrollOffset[1];
            if (motionEvent != null) {
                motionEvent.offsetLocation((float)this.mScrollOffset[0], (float)this.mScrollOffset[1]);
            }
            final int[] mNestedOffsets = this.mNestedOffsets;
            mNestedOffsets[0] += this.mScrollOffset[0];
            final int[] mNestedOffsets2 = this.mNestedOffsets;
            mNestedOffsets2[1] += this.mScrollOffset[1];
        }
        if (n4 != 0 || n3 != 0) {
            this.dispatchOnScrolled(n4, n3);
        }
        if (!this.awakenScrollBars()) {
            this.invalidate();
        }
        if (n4 != 0 || n3 != 0) {
            b = true;
        }
        return b;
    }
    
    public void scrollTo(final int n, final int n2) {
        Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }
    
    public void scrollToPosition(final int n) {
        if (this.mLayoutFrozen) {
            return;
        }
        this.stopScroll();
        if (this.mLayout != null) {
            this.mLayout.scrollToPosition(n);
            this.awakenScrollBars();
            return;
        }
        Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
    }
    
    public void sendAccessibilityEventUnchecked(final AccessibilityEvent accessibilityEvent) {
        if (!this.shouldDeferAccessibilityEvent(accessibilityEvent)) {
            super.sendAccessibilityEventUnchecked(accessibilityEvent);
        }
    }
    
    public void setAccessibilityDelegateCompat(final RecyclerViewAccessibilityDelegate mAccessibilityDelegate) {
        ViewCompat.setAccessibilityDelegate((View)this, this.mAccessibilityDelegate = mAccessibilityDelegate);
    }
    
    public void setAdapter(final Adapter adapter) {
        this.setLayoutFrozen(false);
        this.setAdapterInternal(adapter, false, true);
        this.requestLayout();
    }
    
    public void setChildDrawingOrderCallback(final ChildDrawingOrderCallback mChildDrawingOrderCallback) {
        if (mChildDrawingOrderCallback != this.mChildDrawingOrderCallback) {
            this.mChildDrawingOrderCallback = mChildDrawingOrderCallback;
            this.setChildrenDrawingOrderEnabled(this.mChildDrawingOrderCallback != null);
        }
    }
    
    @VisibleForTesting
    boolean setChildImportantForAccessibilityInternal(final ViewHolder viewHolder, final int mPendingAccessibilityState) {
        if (!this.isComputingLayout()) {
            ViewCompat.setImportantForAccessibility(viewHolder.itemView, mPendingAccessibilityState);
            return true;
        }
        viewHolder.mPendingAccessibilityState = mPendingAccessibilityState;
        this.mPendingAccessibilityImportanceChange.add(viewHolder);
        return false;
    }
    
    public void setClipToPadding(final boolean mClipToPadding) {
        if (mClipToPadding != this.mClipToPadding) {
            this.invalidateGlows();
        }
        super.setClipToPadding(this.mClipToPadding = mClipToPadding);
        if (this.mFirstLayoutComplete) {
            this.requestLayout();
        }
    }
    
    void setDataSetChangedAfterLayout() {
        this.mDataSetHasChangedAfterLayout = true;
        this.markKnownViewsInvalid();
    }
    
    public void setHasFixedSize(final boolean mHasFixedSize) {
        this.mHasFixedSize = mHasFixedSize;
    }
    
    public void setItemAnimator(final ItemAnimator mItemAnimator) {
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
            this.mItemAnimator.setListener(null);
        }
        this.mItemAnimator = mItemAnimator;
        if (this.mItemAnimator != null) {
            this.mItemAnimator.setListener(this.mItemAnimatorListener);
        }
    }
    
    public void setItemViewCacheSize(final int viewCacheSize) {
        this.mRecycler.setViewCacheSize(viewCacheSize);
    }
    
    public void setLayoutFrozen(final boolean b) {
        if (b != this.mLayoutFrozen) {
            this.assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
            if (b) {
                final long uptimeMillis = SystemClock.uptimeMillis();
                this.onTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
                this.mLayoutFrozen = true;
                this.mIgnoreMotionEventTillDown = true;
                this.stopScroll();
            }
            else {
                this.mLayoutFrozen = false;
                if (this.mLayoutRequestEaten && this.mLayout != null && this.mAdapter != null) {
                    this.requestLayout();
                }
                this.mLayoutRequestEaten = false;
            }
        }
    }
    
    public void setLayoutManager(final LayoutManager layoutManager) {
        if (layoutManager != this.mLayout) {
            this.stopScroll();
            if (this.mLayout == null) {
                this.mRecycler.clear();
            }
            else {
                if (this.mItemAnimator != null) {
                    this.mItemAnimator.endAnimations();
                }
                this.mLayout.removeAndRecycleAllViews(this.mRecycler);
                this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
                this.mRecycler.clear();
                if (this.mIsAttached) {
                    this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
                }
                this.mLayout.setRecyclerView(null);
                this.mLayout = null;
            }
            this.mChildHelper.removeAllViewsUnfiltered();
            this.mLayout = layoutManager;
            if (layoutManager != null) {
                if (layoutManager.mRecyclerView != null) {
                    throw new IllegalArgumentException("LayoutManager " + layoutManager + " is already attached to a RecyclerView:" + layoutManager.mRecyclerView.exceptionLabel());
                }
                this.mLayout.setRecyclerView(this);
                if (this.mIsAttached) {
                    this.mLayout.dispatchAttachedToWindow(this);
                }
            }
            this.mRecycler.updateViewCacheSize();
            this.requestLayout();
        }
    }
    
    public void setNestedScrollingEnabled(final boolean nestedScrollingEnabled) {
        this.getScrollingChildHelper().setNestedScrollingEnabled(nestedScrollingEnabled);
    }
    
    public void setOnFlingListener(@Nullable final OnFlingListener mOnFlingListener) {
        this.mOnFlingListener = mOnFlingListener;
    }
    
    @Deprecated
    public void setOnScrollListener(final OnScrollListener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }
    
    public void setPreserveFocusAfterLayout(final boolean mPreserveFocusAfterLayout) {
        this.mPreserveFocusAfterLayout = mPreserveFocusAfterLayout;
    }
    
    public void setRecycledViewPool(final RecycledViewPool recycledViewPool) {
        this.mRecycler.setRecycledViewPool(recycledViewPool);
    }
    
    public void setRecyclerListener(final RecyclerListener mRecyclerListener) {
        this.mRecyclerListener = mRecyclerListener;
    }
    
    void setScrollState(final int mScrollState) {
        if (mScrollState != this.mScrollState) {
            if ((this.mScrollState = mScrollState) != 2) {
                this.stopScrollersInternal();
            }
            this.dispatchOnScrollStateChanged(mScrollState);
        }
    }
    
    public void setScrollingTouchSlop(final int i) {
        final ViewConfiguration value = ViewConfiguration.get(this.getContext());
        switch (i) {
            default: {
                Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + i + "; using default value");
            }
            case 0: {
                this.mTouchSlop = value.getScaledTouchSlop();
                break;
            }
            case 1: {
                this.mTouchSlop = value.getScaledPagingTouchSlop();
                break;
            }
        }
    }
    
    public void setViewCacheExtension(final ViewCacheExtension viewCacheExtension) {
        this.mRecycler.setViewCacheExtension(viewCacheExtension);
    }
    
    boolean shouldDeferAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        if (!this.isComputingLayout()) {
            return false;
        }
        int contentChangeTypes;
        if (accessibilityEvent == null) {
            contentChangeTypes = 0;
        }
        else {
            contentChangeTypes = AccessibilityEventCompat.getContentChangeTypes(accessibilityEvent);
        }
        if (contentChangeTypes == 0) {
            contentChangeTypes = 0;
        }
        this.mEatenAccessibilityChangeFlags |= contentChangeTypes;
        return true;
    }
    
    public void smoothScrollBy(final int n, final int n2) {
        this.smoothScrollBy(n, n2, null);
    }
    
    public void smoothScrollBy(int n, int n2, final Interpolator interpolator) {
        if (this.mLayout == null) {
            Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return;
        }
        if (!this.mLayoutFrozen) {
            if (!this.mLayout.canScrollHorizontally()) {
                n = 0;
            }
            if (!this.mLayout.canScrollVertically()) {
                n2 = 0;
            }
            if (n != 0 || n2 != 0) {
                this.mViewFlinger.smoothScrollBy(n, n2, interpolator);
            }
        }
    }
    
    public void smoothScrollToPosition(final int n) {
        if (this.mLayoutFrozen) {
            return;
        }
        if (this.mLayout != null) {
            this.mLayout.smoothScrollToPosition(this, this.mState, n);
            return;
        }
        Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
    }
    
    public boolean startNestedScroll(final int n) {
        return this.getScrollingChildHelper().startNestedScroll(n);
    }
    
    public boolean startNestedScroll(final int n, final int n2) {
        return this.getScrollingChildHelper().startNestedScroll(n, n2);
    }
    
    public void stopNestedScroll() {
        this.getScrollingChildHelper().stopNestedScroll();
    }
    
    public void stopNestedScroll(final int n) {
        this.getScrollingChildHelper().stopNestedScroll(n);
    }
    
    public void stopScroll() {
        this.setScrollState(0);
        this.stopScrollersInternal();
    }
    
    public void swapAdapter(final Adapter adapter, final boolean b) {
        this.setLayoutFrozen(false);
        this.setAdapterInternal(adapter, true, b);
        this.requestLayout();
    }
    
    void viewRangeUpdate(final int n, final int n2, final Object o) {
        for (int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount(), i = 0; i < unfilteredChildCount; ++i) {
            final View unfilteredChild = this.mChildHelper.getUnfilteredChildAt(i);
            final ViewHolder childViewHolderInt = getChildViewHolderInt(unfilteredChild);
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.mPosition >= n && childViewHolderInt.mPosition < n + n2) {
                childViewHolderInt.addFlags(2);
                childViewHolderInt.addChangePayload(o);
                ((LayoutParams)unfilteredChild.getLayoutParams()).mInsetsDirty = true;
            }
        }
        this.mRecycler.viewRangeUpdate(n, n2);
    }
    
    public abstract static class Adapter<VH extends ViewHolder>
    {
        private boolean mHasStableIds;
        private final AdapterDataObservable mObservable;
        
        public Adapter() {
            this.mObservable = new AdapterDataObservable();
            this.mHasStableIds = false;
        }
        
        public final void bindViewHolder(final VH vh, final int mPosition) {
            vh.mPosition = mPosition;
            if (this.hasStableIds()) {
                vh.mItemId = this.getItemId(mPosition);
            }
            vh.setFlags(1, 519);
            TraceCompat.beginSection("RV OnBindView");
            this.onBindViewHolder(vh, mPosition, vh.getUnmodifiedPayloads());
            vh.clearPayload();
            final ViewGroup$LayoutParams layoutParams = vh.itemView.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                ((LayoutParams)layoutParams).mInsetsDirty = true;
            }
            TraceCompat.endSection();
        }
        
        public final VH createViewHolder(final ViewGroup viewGroup, final int mItemViewType) {
            TraceCompat.beginSection("RV CreateView");
            final ViewHolder onCreateViewHolder = this.onCreateViewHolder(viewGroup, mItemViewType);
            onCreateViewHolder.mItemViewType = mItemViewType;
            TraceCompat.endSection();
            return (VH)onCreateViewHolder;
        }
        
        public abstract int getItemCount();
        
        public long getItemId(final int n) {
            return -1L;
        }
        
        public int getItemViewType(final int n) {
            return 0;
        }
        
        public final boolean hasObservers() {
            return this.mObservable.hasObservers();
        }
        
        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }
        
        public final void notifyDataSetChanged() {
            this.mObservable.notifyChanged();
        }
        
        public final void notifyItemChanged(final int n) {
            this.mObservable.notifyItemRangeChanged(n, 1);
        }
        
        public final void notifyItemChanged(final int n, final Object o) {
            this.mObservable.notifyItemRangeChanged(n, 1, o);
        }
        
        public final void notifyItemInserted(final int n) {
            this.mObservable.notifyItemRangeInserted(n, 1);
        }
        
        public final void notifyItemMoved(final int n, final int n2) {
            this.mObservable.notifyItemMoved(n, n2);
        }
        
        public final void notifyItemRangeChanged(final int n, final int n2) {
            this.mObservable.notifyItemRangeChanged(n, n2);
        }
        
        public final void notifyItemRangeChanged(final int n, final int n2, final Object o) {
            this.mObservable.notifyItemRangeChanged(n, n2, o);
        }
        
        public final void notifyItemRangeInserted(final int n, final int n2) {
            this.mObservable.notifyItemRangeInserted(n, n2);
        }
        
        public final void notifyItemRangeRemoved(final int n, final int n2) {
            this.mObservable.notifyItemRangeRemoved(n, n2);
        }
        
        public final void notifyItemRemoved(final int n) {
            this.mObservable.notifyItemRangeRemoved(n, 1);
        }
        
        public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        }
        
        public abstract void onBindViewHolder(final VH p0, final int p1);
        
        public void onBindViewHolder(final VH vh, final int n, final List<Object> list) {
            this.onBindViewHolder(vh, n);
        }
        
        public abstract VH onCreateViewHolder(final ViewGroup p0, final int p1);
        
        public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        }
        
        public boolean onFailedToRecycleView(final VH vh) {
            return false;
        }
        
        public void onViewAttachedToWindow(final VH vh) {
        }
        
        public void onViewDetachedFromWindow(final VH vh) {
        }
        
        public void onViewRecycled(final VH vh) {
        }
        
        public void registerAdapterDataObserver(final AdapterDataObserver adapterDataObserver) {
            this.mObservable.registerObserver((Object)adapterDataObserver);
        }
        
        public void setHasStableIds(final boolean mHasStableIds) {
            if (!this.hasObservers()) {
                this.mHasStableIds = mHasStableIds;
                return;
            }
            throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
        }
        
        public void unregisterAdapterDataObserver(final AdapterDataObserver adapterDataObserver) {
            this.mObservable.unregisterObserver((Object)adapterDataObserver);
        }
    }
    
    static class AdapterDataObservable extends Observable<AdapterDataObserver>
    {
        public boolean hasObservers() {
            boolean b = false;
            if (!this.mObservers.isEmpty()) {
                b = true;
            }
            return b;
        }
        
        public void notifyChanged() {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)this.mObservers.get(i)).onChanged();
            }
        }
        
        public void notifyItemMoved(final int n, final int n2) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)this.mObservers.get(i)).onItemRangeMoved(n, n2, 1);
            }
        }
        
        public void notifyItemRangeChanged(final int n, final int n2) {
            this.notifyItemRangeChanged(n, n2, null);
        }
        
        public void notifyItemRangeChanged(final int n, final int n2, final Object o) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(n, n2, o);
            }
        }
        
        public void notifyItemRangeInserted(final int n, final int n2) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)this.mObservers.get(i)).onItemRangeInserted(n, n2);
            }
        }
        
        public void notifyItemRangeRemoved(final int n, final int n2) {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                ((AdapterDataObserver)this.mObservers.get(i)).onItemRangeRemoved(n, n2);
            }
        }
    }
    
    public abstract static class AdapterDataObserver
    {
        public void onChanged() {
        }
        
        public void onItemRangeChanged(final int n, final int n2) {
        }
        
        public void onItemRangeChanged(final int n, final int n2, final Object o) {
            this.onItemRangeChanged(n, n2);
        }
        
        public void onItemRangeInserted(final int n, final int n2) {
        }
        
        public void onItemRangeMoved(final int n, final int n2, final int n3) {
        }
        
        public void onItemRangeRemoved(final int n, final int n2) {
        }
    }
    
    public interface ChildDrawingOrderCallback
    {
        int onGetChildDrawingOrder(final int p0, final int p1);
    }
    
    public abstract static class ItemAnimator
    {
        public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        public static final int FLAG_CHANGED = 2;
        public static final int FLAG_INVALIDATED = 4;
        public static final int FLAG_MOVED = 2048;
        public static final int FLAG_REMOVED = 8;
        private long mAddDuration;
        private long mChangeDuration;
        private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners;
        private ItemAnimatorListener mListener;
        private long mMoveDuration;
        private long mRemoveDuration;
        
        public ItemAnimator() {
            this.mListener = null;
            this.mFinishedListeners = new ArrayList<ItemAnimatorFinishedListener>();
            this.mAddDuration = 120L;
            this.mRemoveDuration = 120L;
            this.mMoveDuration = 250L;
            this.mChangeDuration = 250L;
        }
        
        static int buildAdapterChangeFlagsForAnimations(final ViewHolder viewHolder) {
            final int n = viewHolder.mFlags & 0xE;
            if (!viewHolder.isInvalid()) {
                int n2;
                if ((n & 0x4) != 0x0) {
                    n2 = n;
                }
                else {
                    final int oldPosition = viewHolder.getOldPosition();
                    final int adapterPosition = viewHolder.getAdapterPosition();
                    n2 = n;
                    if (oldPosition != -1) {
                        n2 = n;
                        if (adapterPosition != -1) {
                            n2 = n;
                            if (oldPosition != adapterPosition) {
                                n2 = (n | 0x800);
                            }
                        }
                    }
                }
                return n2;
            }
            return 4;
        }
        
        public abstract boolean animateAppearance(@NonNull final ViewHolder p0, @Nullable final ItemHolderInfo p1, @NonNull final ItemHolderInfo p2);
        
        public abstract boolean animateChange(@NonNull final ViewHolder p0, @NonNull final ViewHolder p1, @NonNull final ItemHolderInfo p2, @NonNull final ItemHolderInfo p3);
        
        public abstract boolean animateDisappearance(@NonNull final ViewHolder p0, @NonNull final ItemHolderInfo p1, @Nullable final ItemHolderInfo p2);
        
        public abstract boolean animatePersistence(@NonNull final ViewHolder p0, @NonNull final ItemHolderInfo p1, @NonNull final ItemHolderInfo p2);
        
        public boolean canReuseUpdatedViewHolder(@NonNull final ViewHolder viewHolder) {
            return true;
        }
        
        public boolean canReuseUpdatedViewHolder(@NonNull final ViewHolder viewHolder, @NonNull final List<Object> list) {
            return this.canReuseUpdatedViewHolder(viewHolder);
        }
        
        public final void dispatchAnimationFinished(final ViewHolder viewHolder) {
            this.onAnimationFinished(viewHolder);
            if (this.mListener != null) {
                this.mListener.onAnimationFinished(viewHolder);
            }
        }
        
        public final void dispatchAnimationStarted(final ViewHolder viewHolder) {
            this.onAnimationStarted(viewHolder);
        }
        
        public final void dispatchAnimationsFinished() {
            for (int size = this.mFinishedListeners.size(), i = 0; i < size; ++i) {
                this.mFinishedListeners.get(i).onAnimationsFinished();
            }
            this.mFinishedListeners.clear();
        }
        
        public abstract void endAnimation(final ViewHolder p0);
        
        public abstract void endAnimations();
        
        public long getAddDuration() {
            return this.mAddDuration;
        }
        
        public long getChangeDuration() {
            return this.mChangeDuration;
        }
        
        public long getMoveDuration() {
            return this.mMoveDuration;
        }
        
        public long getRemoveDuration() {
            return this.mRemoveDuration;
        }
        
        public abstract boolean isRunning();
        
        public final boolean isRunning(final ItemAnimatorFinishedListener e) {
            final boolean running = this.isRunning();
            if (e != null) {
                if (running) {
                    this.mFinishedListeners.add(e);
                }
                else {
                    e.onAnimationsFinished();
                }
            }
            return running;
        }
        
        public ItemHolderInfo obtainHolderInfo() {
            return new ItemHolderInfo();
        }
        
        public void onAnimationFinished(final ViewHolder viewHolder) {
        }
        
        public void onAnimationStarted(final ViewHolder viewHolder) {
        }
        
        @NonNull
        public ItemHolderInfo recordPostLayoutInformation(@NonNull final State state, @NonNull final ViewHolder from) {
            return this.obtainHolderInfo().setFrom(from);
        }
        
        @NonNull
        public ItemHolderInfo recordPreLayoutInformation(@NonNull final State state, @NonNull final ViewHolder from, final int n, @NonNull final List<Object> list) {
            return this.obtainHolderInfo().setFrom(from);
        }
        
        public abstract void runPendingAnimations();
        
        public void setAddDuration(final long mAddDuration) {
            this.mAddDuration = mAddDuration;
        }
        
        public void setChangeDuration(final long mChangeDuration) {
            this.mChangeDuration = mChangeDuration;
        }
        
        void setListener(final ItemAnimatorListener mListener) {
            this.mListener = mListener;
        }
        
        public void setMoveDuration(final long mMoveDuration) {
            this.mMoveDuration = mMoveDuration;
        }
        
        public void setRemoveDuration(final long mRemoveDuration) {
            this.mRemoveDuration = mRemoveDuration;
        }
        
        @Retention(RetentionPolicy.SOURCE)
        public @interface AdapterChanges {
        }
        
        public interface ItemAnimatorFinishedListener
        {
            void onAnimationsFinished();
        }
        
        interface ItemAnimatorListener
        {
            void onAnimationFinished(final ViewHolder p0);
        }
        
        public static class ItemHolderInfo
        {
            public int bottom;
            public int changeFlags;
            public int left;
            public int right;
            public int top;
            
            public ItemHolderInfo setFrom(final ViewHolder viewHolder) {
                return this.setFrom(viewHolder, 0);
            }
            
            public ItemHolderInfo setFrom(final ViewHolder viewHolder, final int n) {
                final View itemView = viewHolder.itemView;
                this.left = itemView.getLeft();
                this.top = itemView.getTop();
                this.right = itemView.getRight();
                this.bottom = itemView.getBottom();
                return this;
            }
        }
    }
    
    private class ItemAnimatorRestoreListener implements ItemAnimatorListener
    {
        ItemAnimatorRestoreListener() {
        }
        
        @Override
        public void onAnimationFinished(final ViewHolder viewHolder) {
            viewHolder.setIsRecyclable(true);
            if (viewHolder.mShadowedHolder != null && viewHolder.mShadowingHolder == null) {
                viewHolder.mShadowedHolder = null;
            }
            viewHolder.mShadowingHolder = null;
            if (!viewHolder.shouldBeKeptAsChild() && !RecyclerView.this.removeAnimatingView(viewHolder.itemView) && viewHolder.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
            }
        }
    }
    
    public abstract static class ItemDecoration
    {
        @Deprecated
        public void getItemOffsets(final Rect rect, final int n, final RecyclerView recyclerView) {
            rect.set(0, 0, 0, 0);
        }
        
        public void getItemOffsets(final Rect rect, final View view, final RecyclerView recyclerView, final State state) {
            this.getItemOffsets(rect, ((LayoutParams)view.getLayoutParams()).getViewLayoutPosition(), recyclerView);
        }
        
        @Deprecated
        public void onDraw(final Canvas canvas, final RecyclerView recyclerView) {
        }
        
        public void onDraw(final Canvas canvas, final RecyclerView recyclerView, final State state) {
            this.onDraw(canvas, recyclerView);
        }
        
        @Deprecated
        public void onDrawOver(final Canvas canvas, final RecyclerView recyclerView) {
        }
        
        public void onDrawOver(final Canvas canvas, final RecyclerView recyclerView, final State state) {
            this.onDrawOver(canvas, recyclerView);
        }
    }
    
    public abstract static class LayoutManager
    {
        boolean mAutoMeasure;
        ChildHelper mChildHelper;
        private int mHeight;
        private int mHeightMode;
        ViewBoundsCheck mHorizontalBoundCheck;
        private final ViewBoundsCheck.Callback mHorizontalBoundCheckCallback;
        boolean mIsAttachedToWindow;
        private boolean mItemPrefetchEnabled;
        private boolean mMeasurementCacheEnabled;
        int mPrefetchMaxCountObserved;
        boolean mPrefetchMaxObservedInInitialPrefetch;
        RecyclerView mRecyclerView;
        boolean mRequestedSimpleAnimations;
        @Nullable
        SmoothScroller mSmoothScroller;
        ViewBoundsCheck mVerticalBoundCheck;
        private final ViewBoundsCheck.Callback mVerticalBoundCheckCallback;
        private int mWidth;
        private int mWidthMode;
        
        public LayoutManager() {
            this.mHorizontalBoundCheckCallback = new ViewBoundsCheck.Callback() {
                @Override
                public View getChildAt(final int n) {
                    return LayoutManager.this.getChildAt(n);
                }
                
                @Override
                public int getChildCount() {
                    return LayoutManager.this.getChildCount();
                }
                
                @Override
                public int getChildEnd(final View view) {
                    return ((LayoutParams)view.getLayoutParams()).rightMargin + LayoutManager.this.getDecoratedRight(view);
                }
                
                @Override
                public int getChildStart(final View view) {
                    return LayoutManager.this.getDecoratedLeft(view) - ((LayoutParams)view.getLayoutParams()).leftMargin;
                }
                
                @Override
                public View getParent() {
                    return (View)LayoutManager.this.mRecyclerView;
                }
                
                @Override
                public int getParentEnd() {
                    return LayoutManager.this.getWidth() - LayoutManager.this.getPaddingRight();
                }
                
                @Override
                public int getParentStart() {
                    return LayoutManager.this.getPaddingLeft();
                }
            };
            this.mVerticalBoundCheckCallback = new ViewBoundsCheck.Callback() {
                @Override
                public View getChildAt(final int n) {
                    return LayoutManager.this.getChildAt(n);
                }
                
                @Override
                public int getChildCount() {
                    return LayoutManager.this.getChildCount();
                }
                
                @Override
                public int getChildEnd(final View view) {
                    return ((LayoutParams)view.getLayoutParams()).bottomMargin + LayoutManager.this.getDecoratedBottom(view);
                }
                
                @Override
                public int getChildStart(final View view) {
                    return LayoutManager.this.getDecoratedTop(view) - ((LayoutParams)view.getLayoutParams()).topMargin;
                }
                
                @Override
                public View getParent() {
                    return (View)LayoutManager.this.mRecyclerView;
                }
                
                @Override
                public int getParentEnd() {
                    return LayoutManager.this.getHeight() - LayoutManager.this.getPaddingBottom();
                }
                
                @Override
                public int getParentStart() {
                    return LayoutManager.this.getPaddingTop();
                }
            };
            this.mHorizontalBoundCheck = new ViewBoundsCheck(this.mHorizontalBoundCheckCallback);
            this.mVerticalBoundCheck = new ViewBoundsCheck(this.mVerticalBoundCheckCallback);
            this.mRequestedSimpleAnimations = false;
            this.mIsAttachedToWindow = false;
            this.mAutoMeasure = false;
            this.mMeasurementCacheEnabled = true;
            this.mItemPrefetchEnabled = true;
        }
        
        private void addViewInt(final View view, int childCount, final boolean b) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!b && !childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            else {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            }
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            if (!childViewHolderInt.wasReturnedFromScrap() && !childViewHolderInt.isScrap()) {
                if (view.getParent() != this.mRecyclerView) {
                    this.mChildHelper.addView(view, childCount, false);
                    layoutParams.mInsetsDirty = true;
                    if (this.mSmoothScroller != null && this.mSmoothScroller.isRunning()) {
                        this.mSmoothScroller.onChildAttachedToWindow(view);
                    }
                }
                else {
                    final int indexOfChild = this.mChildHelper.indexOfChild(view);
                    if (childCount == -1) {
                        childCount = this.mChildHelper.getChildCount();
                    }
                    if (indexOfChild == -1) {
                        throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(view) + this.mRecyclerView.exceptionLabel());
                    }
                    if (indexOfChild != childCount) {
                        this.mRecyclerView.mLayout.moveView(indexOfChild, childCount);
                    }
                }
            }
            else {
                if (!childViewHolderInt.isScrap()) {
                    childViewHolderInt.clearReturnedFromScrapFlag();
                }
                else {
                    childViewHolderInt.unScrap();
                }
                this.mChildHelper.attachViewToParent(view, childCount, view.getLayoutParams(), false);
            }
            if (layoutParams.mPendingInvalidate) {
                childViewHolderInt.itemView.invalidate();
                layoutParams.mPendingInvalidate = false;
            }
        }
        
        public static int chooseSize(int size, final int n, final int n2) {
            final int mode = View$MeasureSpec.getMode(size);
            size = View$MeasureSpec.getSize(size);
            switch (mode) {
                default: {
                    return Math.max(n, n2);
                }
                case 1073741824: {
                    return size;
                }
                case Integer.MIN_VALUE: {
                    return Math.min(size, Math.max(n, n2));
                }
            }
        }
        
        private void detachViewInternal(final int n, final View view) {
            this.mChildHelper.detachViewFromParent(n);
        }
        
        public static int getChildMeasureSpec(int max, int n, final int n2, final int n3, final boolean b) {
            final int n4 = 0;
            final int n5 = 0;
            max = Math.max(0, max - n2);
            if (!b) {
                if (n3 < 0) {
                    if (n3 != -1) {
                        if (n3 != -2) {
                            max = 0;
                            n = n5;
                        }
                        else if (n != Integer.MIN_VALUE && n != 1073741824) {
                            n = n5;
                        }
                        else {
                            n = Integer.MIN_VALUE;
                        }
                    }
                }
                else {
                    n = 1073741824;
                    max = n3;
                }
            }
            else if (n3 < 0) {
                if (n3 != -1) {
                    if (n3 != -2) {
                        max = 0;
                        n = n5;
                    }
                    else {
                        max = 0;
                        n = n5;
                    }
                }
                else {
                    switch (n) {
                        default: {
                            n = 0;
                            max = n4;
                            break;
                        }
                        case Integer.MIN_VALUE:
                        case 1073741824: {
                            break;
                        }
                        case 0: {
                            n = 0;
                            max = n4;
                            break;
                        }
                    }
                }
            }
            else {
                n = 1073741824;
                max = n3;
            }
            return View$MeasureSpec.makeMeasureSpec(max, n);
        }
        
        @Deprecated
        public static int getChildMeasureSpec(int max, int n, int n2, final boolean b) {
            final int n3 = 0;
            max = Math.max(0, max - n);
            if (!b) {
                if (n2 < 0) {
                    if (n2 != -1) {
                        if (n2 != -2) {
                            n2 = 0;
                            max = n3;
                        }
                        else {
                            n = Integer.MIN_VALUE;
                            n2 = max;
                            max = n;
                        }
                    }
                    else {
                        n = 1073741824;
                        n2 = max;
                        max = n;
                    }
                }
                else {
                    max = 1073741824;
                }
            }
            else if (n2 < 0) {
                n2 = 0;
                max = n3;
            }
            else {
                max = 1073741824;
            }
            return View$MeasureSpec.makeMeasureSpec(n2, max);
        }
        
        private int[] getChildRectangleOnScreenScrollAmount(final RecyclerView recyclerView, final View view, final Rect rect, final boolean b) {
            final int paddingLeft = this.getPaddingLeft();
            final int paddingTop = this.getPaddingTop();
            final int n = this.getWidth() - this.getPaddingRight();
            final int height = this.getHeight();
            final int paddingBottom = this.getPaddingBottom();
            final int n2 = view.getLeft() + rect.left - view.getScrollX();
            final int n3 = view.getTop() + rect.top - view.getScrollY();
            final int n4 = n2 + rect.width();
            final int height2 = rect.height();
            final int min = Math.min(0, n2 - paddingLeft);
            int n5 = Math.min(0, n3 - paddingTop);
            final int max = Math.max(0, n4 - n);
            final int max2 = Math.max(0, n3 + height2 - (height - paddingBottom));
            int n6;
            if (this.getLayoutDirection() != 1) {
                if (min == 0) {
                    n6 = Math.min(n2 - paddingLeft, max);
                }
                else {
                    n6 = min;
                }
            }
            else if ((n6 = max) == 0) {
                n6 = Math.max(min, n4 - n);
            }
            if (n5 == 0) {
                n5 = Math.min(n3 - paddingTop, max2);
            }
            return new int[] { n6, n5 };
        }
        
        public static Properties getProperties(final Context context, final AttributeSet set, final int n, final int n2) {
            final Properties properties = new Properties();
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.RecyclerView, n, n2);
            properties.orientation = obtainStyledAttributes.getInt(R.styleable.RecyclerView_android_orientation, 1);
            properties.spanCount = obtainStyledAttributes.getInt(R.styleable.RecyclerView_spanCount, 1);
            properties.reverseLayout = obtainStyledAttributes.getBoolean(R.styleable.RecyclerView_reverseLayout, false);
            properties.stackFromEnd = obtainStyledAttributes.getBoolean(R.styleable.RecyclerView_stackFromEnd, false);
            obtainStyledAttributes.recycle();
            return properties;
        }
        
        private boolean isFocusedChildVisibleAfterScrolling(final RecyclerView recyclerView, final int n, final int n2) {
            final View focusedChild = recyclerView.getFocusedChild();
            if (focusedChild != null) {
                final int paddingLeft = this.getPaddingLeft();
                final int paddingTop = this.getPaddingTop();
                final int width = this.getWidth();
                final int paddingRight = this.getPaddingRight();
                final int height = this.getHeight();
                final int paddingBottom = this.getPaddingBottom();
                final Rect mTempRect = this.mRecyclerView.mTempRect;
                this.getDecoratedBoundsWithMargins(focusedChild, mTempRect);
                return mTempRect.left - n < width - paddingRight && mTempRect.right - n > paddingLeft && mTempRect.top - n2 < height - paddingBottom && mTempRect.bottom - n2 > paddingTop;
            }
            return false;
        }
        
        private static boolean isMeasurementUpToDate(final int n, int size, final int n2) {
            final boolean b = false;
            boolean b2 = false;
            final int mode = View$MeasureSpec.getMode(size);
            size = View$MeasureSpec.getSize(size);
            if (n2 > 0 && n != n2) {
                return false;
            }
            switch (mode) {
                default: {
                    return false;
                }
                case 0: {
                    return true;
                }
                case Integer.MIN_VALUE: {
                    if (size >= n) {
                        b2 = true;
                    }
                    return b2;
                }
                case 1073741824: {
                    return size == n || b;
                }
            }
        }
        
        private void onSmoothScrollerStopped(final SmoothScroller smoothScroller) {
            if (this.mSmoothScroller == smoothScroller) {
                this.mSmoothScroller = null;
            }
        }
        
        private void scrapOrRecycleView(final Recycler recycler, final int n, final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!childViewHolderInt.shouldIgnore()) {
                if (childViewHolderInt.isInvalid() && !childViewHolderInt.isRemoved() && !this.mRecyclerView.mAdapter.hasStableIds()) {
                    this.removeViewAt(n);
                    recycler.recycleViewHolderInternal(childViewHolderInt);
                }
                else {
                    this.detachViewAt(n);
                    recycler.scrapView(view);
                    this.mRecyclerView.mViewInfoStore.onViewDetached(childViewHolderInt);
                }
            }
        }
        
        public void addDisappearingView(final View view) {
            this.addDisappearingView(view, -1);
        }
        
        public void addDisappearingView(final View view, final int n) {
            this.addViewInt(view, n, true);
        }
        
        public void addView(final View view) {
            this.addView(view, -1);
        }
        
        public void addView(final View view, final int n) {
            this.addViewInt(view, n, false);
        }
        
        public void assertInLayoutOrScroll(final String s) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertInLayoutOrScroll(s);
            }
        }
        
        public void assertNotInLayoutOrScroll(final String s) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertNotInLayoutOrScroll(s);
            }
        }
        
        public void attachView(final View view) {
            this.attachView(view, -1);
        }
        
        public void attachView(final View view, final int n) {
            this.attachView(view, n, (LayoutParams)view.getLayoutParams());
        }
        
        public void attachView(final View view, final int n, final LayoutParams layoutParams) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            else {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            }
            this.mChildHelper.attachViewToParent(view, n, (ViewGroup$LayoutParams)layoutParams, childViewHolderInt.isRemoved());
        }
        
        public void calculateItemDecorationsForChild(final View view, final Rect rect) {
            if (this.mRecyclerView != null) {
                rect.set(this.mRecyclerView.getItemDecorInsetsForChild(view));
                return;
            }
            rect.set(0, 0, 0, 0);
        }
        
        public boolean canScrollHorizontally() {
            return false;
        }
        
        public boolean canScrollVertically() {
            return false;
        }
        
        public boolean checkLayoutParams(final LayoutParams layoutParams) {
            return layoutParams != null;
        }
        
        public void collectAdjacentPrefetchPositions(final int n, final int n2, final State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }
        
        public void collectInitialPrefetchPositions(final int n, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        }
        
        public int computeHorizontalScrollExtent(final State state) {
            return 0;
        }
        
        public int computeHorizontalScrollOffset(final State state) {
            return 0;
        }
        
        public int computeHorizontalScrollRange(final State state) {
            return 0;
        }
        
        public int computeVerticalScrollExtent(final State state) {
            return 0;
        }
        
        public int computeVerticalScrollOffset(final State state) {
            return 0;
        }
        
        public int computeVerticalScrollRange(final State state) {
            return 0;
        }
        
        public void detachAndScrapAttachedViews(final Recycler recycler) {
            int childCount = this.getChildCount();
            while (--childCount >= 0) {
                this.scrapOrRecycleView(recycler, childCount, this.getChildAt(childCount));
            }
        }
        
        public void detachAndScrapView(final View view, final Recycler recycler) {
            this.scrapOrRecycleView(recycler, this.mChildHelper.indexOfChild(view), view);
        }
        
        public void detachAndScrapViewAt(final int n, final Recycler recycler) {
            this.scrapOrRecycleView(recycler, n, this.getChildAt(n));
        }
        
        public void detachView(final View view) {
            final int indexOfChild = this.mChildHelper.indexOfChild(view);
            if (indexOfChild >= 0) {
                this.detachViewInternal(indexOfChild, view);
            }
        }
        
        public void detachViewAt(final int n) {
            this.detachViewInternal(n, this.getChildAt(n));
        }
        
        void dispatchAttachedToWindow(final RecyclerView recyclerView) {
            this.mIsAttachedToWindow = true;
            this.onAttachedToWindow(recyclerView);
        }
        
        void dispatchDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
            this.mIsAttachedToWindow = false;
            this.onDetachedFromWindow(recyclerView, recycler);
        }
        
        public void endAnimation(final View view) {
            if (this.mRecyclerView.mItemAnimator != null) {
                this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(view));
            }
        }
        
        @Nullable
        public View findContainingItemView(View containingItemView) {
            if (this.mRecyclerView == null) {
                return null;
            }
            containingItemView = this.mRecyclerView.findContainingItemView(containingItemView);
            if (containingItemView == null) {
                return null;
            }
            if (!this.mChildHelper.isHidden(containingItemView)) {
                return containingItemView;
            }
            return null;
        }
        
        public View findViewByPosition(final int n) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                final View child = this.getChildAt(i);
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(child);
                if (childViewHolderInt != null && childViewHolderInt.getLayoutPosition() == n && !childViewHolderInt.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !childViewHolderInt.isRemoved())) {
                    return child;
                }
            }
            return null;
        }
        
        public abstract LayoutParams generateDefaultLayoutParams();
        
        public LayoutParams generateLayoutParams(final Context context, final AttributeSet set) {
            return new LayoutParams(context, set);
        }
        
        public LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            if (viewGroup$LayoutParams instanceof LayoutParams) {
                return new LayoutParams((LayoutParams)viewGroup$LayoutParams);
            }
            if (!(viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams)) {
                return new LayoutParams(viewGroup$LayoutParams);
            }
            return new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        
        public int getBaseline() {
            return -1;
        }
        
        public int getBottomDecorationHeight(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.bottom;
        }
        
        public View getChildAt(final int n) {
            View child = null;
            if (this.mChildHelper != null) {
                child = this.mChildHelper.getChildAt(n);
            }
            return child;
        }
        
        public int getChildCount() {
            int childCount;
            if (this.mChildHelper == null) {
                childCount = 0;
            }
            else {
                childCount = this.mChildHelper.getChildCount();
            }
            return childCount;
        }
        
        public boolean getClipToPadding() {
            boolean b = false;
            if (this.mRecyclerView != null && this.mRecyclerView.mClipToPadding) {
                b = true;
            }
            return b;
        }
        
        public int getColumnCountForAccessibility(final Recycler recycler, final State state) {
            int itemCount = 1;
            if (this.mRecyclerView != null && this.mRecyclerView.mAdapter != null) {
                if (this.canScrollHorizontally()) {
                    itemCount = this.mRecyclerView.mAdapter.getItemCount();
                }
                return itemCount;
            }
            return 1;
        }
        
        public int getDecoratedBottom(final View view) {
            return view.getBottom() + this.getBottomDecorationHeight(view);
        }
        
        public void getDecoratedBoundsWithMargins(final View view, final Rect rect) {
            RecyclerView.getDecoratedBoundsWithMarginsInt(view, rect);
        }
        
        public int getDecoratedLeft(final View view) {
            return view.getLeft() - this.getLeftDecorationWidth(view);
        }
        
        public int getDecoratedMeasuredHeight(final View view) {
            final Rect mDecorInsets = ((LayoutParams)view.getLayoutParams()).mDecorInsets;
            return mDecorInsets.bottom + (view.getMeasuredHeight() + mDecorInsets.top);
        }
        
        public int getDecoratedMeasuredWidth(final View view) {
            final Rect mDecorInsets = ((LayoutParams)view.getLayoutParams()).mDecorInsets;
            return mDecorInsets.right + (view.getMeasuredWidth() + mDecorInsets.left);
        }
        
        public int getDecoratedRight(final View view) {
            return view.getRight() + this.getRightDecorationWidth(view);
        }
        
        public int getDecoratedTop(final View view) {
            return view.getTop() - this.getTopDecorationHeight(view);
        }
        
        public View getFocusedChild() {
            if (this.mRecyclerView == null) {
                return null;
            }
            final View focusedChild = this.mRecyclerView.getFocusedChild();
            if (focusedChild != null && !this.mChildHelper.isHidden(focusedChild)) {
                return focusedChild;
            }
            return null;
        }
        
        public int getHeight() {
            return this.mHeight;
        }
        
        public int getHeightMode() {
            return this.mHeightMode;
        }
        
        public int getItemCount() {
            Adapter adapter = null;
            if (this.mRecyclerView != null) {
                adapter = this.mRecyclerView.getAdapter();
            }
            int itemCount;
            if (adapter == null) {
                itemCount = 0;
            }
            else {
                itemCount = adapter.getItemCount();
            }
            return itemCount;
        }
        
        public int getItemViewType(final View view) {
            return RecyclerView.getChildViewHolderInt(view).getItemViewType();
        }
        
        public int getLayoutDirection() {
            return ViewCompat.getLayoutDirection((View)this.mRecyclerView);
        }
        
        public int getLeftDecorationWidth(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.left;
        }
        
        public int getMinimumHeight() {
            return ViewCompat.getMinimumHeight((View)this.mRecyclerView);
        }
        
        public int getMinimumWidth() {
            return ViewCompat.getMinimumWidth((View)this.mRecyclerView);
        }
        
        public int getPaddingBottom() {
            int paddingBottom;
            if (this.mRecyclerView == null) {
                paddingBottom = 0;
            }
            else {
                paddingBottom = this.mRecyclerView.getPaddingBottom();
            }
            return paddingBottom;
        }
        
        public int getPaddingEnd() {
            int paddingEnd;
            if (this.mRecyclerView == null) {
                paddingEnd = 0;
            }
            else {
                paddingEnd = ViewCompat.getPaddingEnd((View)this.mRecyclerView);
            }
            return paddingEnd;
        }
        
        public int getPaddingLeft() {
            int paddingLeft;
            if (this.mRecyclerView == null) {
                paddingLeft = 0;
            }
            else {
                paddingLeft = this.mRecyclerView.getPaddingLeft();
            }
            return paddingLeft;
        }
        
        public int getPaddingRight() {
            int paddingRight;
            if (this.mRecyclerView == null) {
                paddingRight = 0;
            }
            else {
                paddingRight = this.mRecyclerView.getPaddingRight();
            }
            return paddingRight;
        }
        
        public int getPaddingStart() {
            int paddingStart;
            if (this.mRecyclerView == null) {
                paddingStart = 0;
            }
            else {
                paddingStart = ViewCompat.getPaddingStart((View)this.mRecyclerView);
            }
            return paddingStart;
        }
        
        public int getPaddingTop() {
            int paddingTop;
            if (this.mRecyclerView == null) {
                paddingTop = 0;
            }
            else {
                paddingTop = this.mRecyclerView.getPaddingTop();
            }
            return paddingTop;
        }
        
        public int getPosition(final View view) {
            return ((LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        }
        
        public int getRightDecorationWidth(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.right;
        }
        
        public int getRowCountForAccessibility(final Recycler recycler, final State state) {
            int itemCount = 1;
            if (this.mRecyclerView != null && this.mRecyclerView.mAdapter != null) {
                if (this.canScrollVertically()) {
                    itemCount = this.mRecyclerView.mAdapter.getItemCount();
                }
                return itemCount;
            }
            return 1;
        }
        
        public int getSelectionModeForAccessibility(final Recycler recycler, final State state) {
            return 0;
        }
        
        public int getTopDecorationHeight(final View view) {
            return ((LayoutParams)view.getLayoutParams()).mDecorInsets.top;
        }
        
        public void getTransformedBoundingBox(final View view, final boolean b, final Rect rect) {
            if (!b) {
                rect.set(0, 0, view.getWidth(), view.getHeight());
            }
            else {
                final Rect mDecorInsets = ((LayoutParams)view.getLayoutParams()).mDecorInsets;
                rect.set(-mDecorInsets.left, -mDecorInsets.top, view.getWidth() + mDecorInsets.right, mDecorInsets.bottom + view.getHeight());
            }
            if (this.mRecyclerView != null) {
                final Matrix matrix = view.getMatrix();
                if (matrix != null && !matrix.isIdentity()) {
                    final RectF mTempRectF = this.mRecyclerView.mTempRectF;
                    mTempRectF.set(rect);
                    matrix.mapRect(mTempRectF);
                    rect.set((int)Math.floor(mTempRectF.left), (int)Math.floor(mTempRectF.top), (int)Math.ceil(mTempRectF.right), (int)Math.ceil(mTempRectF.bottom));
                }
            }
            rect.offset(view.getLeft(), view.getTop());
        }
        
        public int getWidth() {
            return this.mWidth;
        }
        
        public int getWidthMode() {
            return this.mWidthMode;
        }
        
        boolean hasFlexibleChildInBothOrientations() {
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final ViewGroup$LayoutParams layoutParams = this.getChildAt(i).getLayoutParams();
                if (layoutParams.width < 0 && layoutParams.height < 0) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean hasFocus() {
            boolean b = false;
            if (this.mRecyclerView != null && this.mRecyclerView.hasFocus()) {
                b = true;
            }
            return b;
        }
        
        public void ignoreView(final View view) {
            if (view.getParent() == this.mRecyclerView && this.mRecyclerView.indexOfChild(view) != -1) {
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
                childViewHolderInt.addFlags(128);
                this.mRecyclerView.mViewInfoStore.removeViewHolder(childViewHolderInt);
                return;
            }
            throw new IllegalArgumentException("View should be fully attached to be ignored" + this.mRecyclerView.exceptionLabel());
        }
        
        public boolean isAttachedToWindow() {
            return this.mIsAttachedToWindow;
        }
        
        public boolean isAutoMeasureEnabled() {
            return this.mAutoMeasure;
        }
        
        public boolean isFocused() {
            boolean b = false;
            if (this.mRecyclerView != null && this.mRecyclerView.isFocused()) {
                b = true;
            }
            return b;
        }
        
        public final boolean isItemPrefetchEnabled() {
            return this.mItemPrefetchEnabled;
        }
        
        public boolean isLayoutHierarchical(final Recycler recycler, final State state) {
            return false;
        }
        
        public boolean isMeasurementCacheEnabled() {
            return this.mMeasurementCacheEnabled;
        }
        
        public boolean isSmoothScrolling() {
            boolean b = false;
            if (this.mSmoothScroller != null && this.mSmoothScroller.isRunning()) {
                b = true;
            }
            return b;
        }
        
        public boolean isViewPartiallyVisible(@NonNull final View view, final boolean b, final boolean b2) {
            final boolean b3 = false;
            final boolean b4 = this.mHorizontalBoundCheck.isViewWithinBoundFlags(view, 24579) && this.mVerticalBoundCheck.isViewWithinBoundFlags(view, 24579);
            if (!b) {
                return !b4 || b3;
            }
            return b4;
        }
        
        public void layoutDecorated(final View view, final int n, final int n2, final int n3, final int n4) {
            final Rect mDecorInsets = ((LayoutParams)view.getLayoutParams()).mDecorInsets;
            view.layout(mDecorInsets.left + n, mDecorInsets.top + n2, n3 - mDecorInsets.right, n4 - mDecorInsets.bottom);
        }
        
        public void layoutDecoratedWithMargins(final View view, final int n, final int n2, final int n3, final int n4) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final Rect mDecorInsets = layoutParams.mDecorInsets;
            view.layout(mDecorInsets.left + n + layoutParams.leftMargin, mDecorInsets.top + n2 + layoutParams.topMargin, n3 - mDecorInsets.right - layoutParams.rightMargin, n4 - mDecorInsets.bottom - layoutParams.bottomMargin);
        }
        
        public void measureChild(final View view, int childMeasureSpec, int childMeasureSpec2) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            final int left = itemDecorInsetsForChild.left;
            final int right = itemDecorInsetsForChild.right;
            final int top = itemDecorInsetsForChild.top;
            final int bottom = itemDecorInsetsForChild.bottom;
            childMeasureSpec = getChildMeasureSpec(this.getWidth(), this.getWidthMode(), left + right + childMeasureSpec + (this.getPaddingLeft() + this.getPaddingRight()), layoutParams.width, this.canScrollHorizontally());
            childMeasureSpec2 = getChildMeasureSpec(this.getHeight(), this.getHeightMode(), bottom + top + childMeasureSpec2 + (this.getPaddingTop() + this.getPaddingBottom()), layoutParams.height, this.canScrollVertically());
            if (this.shouldMeasureChild(view, childMeasureSpec, childMeasureSpec2, layoutParams)) {
                view.measure(childMeasureSpec, childMeasureSpec2);
            }
        }
        
        public void measureChildWithMargins(final View view, int childMeasureSpec, int childMeasureSpec2) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            final int left = itemDecorInsetsForChild.left;
            final int right = itemDecorInsetsForChild.right;
            final int top = itemDecorInsetsForChild.top;
            final int bottom = itemDecorInsetsForChild.bottom;
            childMeasureSpec = getChildMeasureSpec(this.getWidth(), this.getWidthMode(), left + right + childMeasureSpec + (this.getPaddingLeft() + this.getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin), layoutParams.width, this.canScrollHorizontally());
            childMeasureSpec2 = getChildMeasureSpec(this.getHeight(), this.getHeightMode(), bottom + top + childMeasureSpec2 + (this.getPaddingTop() + this.getPaddingBottom() + layoutParams.topMargin + layoutParams.bottomMargin), layoutParams.height, this.canScrollVertically());
            if (this.shouldMeasureChild(view, childMeasureSpec, childMeasureSpec2, layoutParams)) {
                view.measure(childMeasureSpec, childMeasureSpec2);
            }
        }
        
        public void moveView(final int i, final int n) {
            final View child = this.getChildAt(i);
            if (child != null) {
                this.detachViewAt(i);
                this.attachView(child, n);
                return;
            }
            throw new IllegalArgumentException("Cannot move a child from non-existing index:" + i + this.mRecyclerView.toString());
        }
        
        public void offsetChildrenHorizontal(final int n) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenHorizontal(n);
            }
        }
        
        public void offsetChildrenVertical(final int n) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenVertical(n);
            }
        }
        
        public void onAdapterChanged(final Adapter adapter, final Adapter adapter2) {
        }
        
        public boolean onAddFocusables(final RecyclerView recyclerView, final ArrayList<View> list, final int n, final int n2) {
            return false;
        }
        
        @CallSuper
        public void onAttachedToWindow(final RecyclerView recyclerView) {
        }
        
        @Deprecated
        public void onDetachedFromWindow(final RecyclerView recyclerView) {
        }
        
        @CallSuper
        public void onDetachedFromWindow(final RecyclerView recyclerView, final Recycler recycler) {
            this.onDetachedFromWindow(recyclerView);
        }
        
        @Nullable
        public View onFocusSearchFailed(final View view, final int n, final Recycler recycler, final State state) {
            return null;
        }
        
        public void onInitializeAccessibilityEvent(final Recycler recycler, final State state, final AccessibilityEvent accessibilityEvent) {
            boolean scrollable = false;
            if (this.mRecyclerView != null && accessibilityEvent != null) {
                if (this.mRecyclerView.canScrollVertically(1) || this.mRecyclerView.canScrollVertically(-1) || this.mRecyclerView.canScrollHorizontally(-1) || this.mRecyclerView.canScrollHorizontally(1)) {
                    scrollable = true;
                }
                accessibilityEvent.setScrollable(scrollable);
                if (this.mRecyclerView.mAdapter != null) {
                    accessibilityEvent.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
                }
            }
        }
        
        public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
            this.onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityEvent);
        }
        
        void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            this.onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityNodeInfoCompat);
        }
        
        public void onInitializeAccessibilityNodeInfo(final Recycler recycler, final State state, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (this.mRecyclerView.canScrollVertically(-1) || this.mRecyclerView.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            if (this.mRecyclerView.canScrollVertically(1) || this.mRecyclerView.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            accessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(this.getRowCountForAccessibility(recycler, state), this.getColumnCountForAccessibility(recycler, state), this.isLayoutHierarchical(recycler, state), this.getSelectionModeForAccessibility(recycler, state)));
        }
        
        public void onInitializeAccessibilityNodeInfoForItem(final Recycler recycler, final State state, final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            int position;
            if (!this.canScrollVertically()) {
                position = 0;
            }
            else {
                position = this.getPosition(view);
            }
            int position2;
            if (!this.canScrollHorizontally()) {
                position2 = 0;
            }
            else {
                position2 = this.getPosition(view);
            }
            accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(position, 1, position2, 1, false, false));
        }
        
        void onInitializeAccessibilityNodeInfoForItem(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null && !childViewHolderInt.isRemoved() && !this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                this.onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, accessibilityNodeInfoCompat);
            }
        }
        
        public View onInterceptFocusSearch(final View view, final int n) {
            return null;
        }
        
        public void onItemsAdded(final RecyclerView recyclerView, final int n, final int n2) {
        }
        
        public void onItemsChanged(final RecyclerView recyclerView) {
        }
        
        public void onItemsMoved(final RecyclerView recyclerView, final int n, final int n2, final int n3) {
        }
        
        public void onItemsRemoved(final RecyclerView recyclerView, final int n, final int n2) {
        }
        
        public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2) {
        }
        
        public void onItemsUpdated(final RecyclerView recyclerView, final int n, final int n2, final Object o) {
            this.onItemsUpdated(recyclerView, n, n2);
        }
        
        public void onLayoutChildren(final Recycler recycler, final State state) {
            Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) ");
        }
        
        public void onLayoutCompleted(final State state) {
        }
        
        public void onMeasure(final Recycler recycler, final State state, final int n, final int n2) {
            this.mRecyclerView.defaultOnMeasure(n, n2);
        }
        
        public boolean onRequestChildFocus(final RecyclerView recyclerView, final State state, final View view, final View view2) {
            return this.onRequestChildFocus(recyclerView, view, view2);
        }
        
        @Deprecated
        public boolean onRequestChildFocus(final RecyclerView recyclerView, final View view, final View view2) {
            boolean b = false;
            if (this.isSmoothScrolling() || recyclerView.isComputingLayout()) {
                b = true;
            }
            return b;
        }
        
        public void onRestoreInstanceState(final Parcelable parcelable) {
        }
        
        public Parcelable onSaveInstanceState() {
            return null;
        }
        
        public void onScrollStateChanged(final int n) {
        }
        
        boolean performAccessibilityAction(final int n, final Bundle bundle) {
            return this.performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, n, bundle);
        }
        
        public boolean performAccessibilityAction(final Recycler recycler, final State state, int n, final Bundle bundle) {
            if (this.mRecyclerView == null) {
                return false;
            }
            int n2 = 0;
            switch (n) {
                default: {
                    n = 0;
                    n2 = 0;
                    break;
                }
                case 8192: {
                    if (!this.mRecyclerView.canScrollVertically(-1)) {
                        n = 0;
                    }
                    else {
                        n = -(this.getHeight() - this.getPaddingTop() - this.getPaddingBottom());
                    }
                    if (!this.mRecyclerView.canScrollHorizontally(-1)) {
                        n2 = n;
                        n = 0;
                        break;
                    }
                    final int n3 = -(this.getWidth() - this.getPaddingLeft() - this.getPaddingRight());
                    n2 = n;
                    n = n3;
                    break;
                }
                case 4096: {
                    if (!this.mRecyclerView.canScrollVertically(1)) {
                        n = 0;
                    }
                    else {
                        n = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
                    }
                    if (!this.mRecyclerView.canScrollHorizontally(1)) {
                        n2 = n;
                        n = 0;
                        break;
                    }
                    final int width = this.getWidth();
                    final int paddingLeft = this.getPaddingLeft();
                    final int paddingRight = this.getPaddingRight();
                    n2 = n;
                    n = width - paddingLeft - paddingRight;
                    break;
                }
            }
            if (n2 == 0 && n == 0) {
                return false;
            }
            this.mRecyclerView.scrollBy(n, n2);
            return true;
        }
        
        public boolean performAccessibilityActionForItem(final Recycler recycler, final State state, final View view, final int n, final Bundle bundle) {
            return false;
        }
        
        boolean performAccessibilityActionForItem(final View view, final int n, final Bundle bundle) {
            return this.performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, n, bundle);
        }
        
        public void postOnAnimation(final Runnable runnable) {
            if (this.mRecyclerView != null) {
                ViewCompat.postOnAnimation((View)this.mRecyclerView, runnable);
            }
        }
        
        public void removeAllViews() {
            int childCount = this.getChildCount();
            while (--childCount >= 0) {
                this.mChildHelper.removeViewAt(childCount);
            }
        }
        
        public void removeAndRecycleAllViews(final Recycler recycler) {
            int childCount = this.getChildCount();
            while (true) {
                final int n = childCount - 1;
                if (n < 0) {
                    break;
                }
                childCount = n;
                if (RecyclerView.getChildViewHolderInt(this.getChildAt(n)).shouldIgnore()) {
                    continue;
                }
                this.removeAndRecycleViewAt(n, recycler);
                childCount = n;
            }
        }
        
        void removeAndRecycleScrapInt(final Recycler recycler) {
            final int scrapCount = recycler.getScrapCount();
            for (int i = scrapCount - 1; i >= 0; --i) {
                final View scrapView = recycler.getScrapViewAt(i);
                final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(scrapView);
                if (!childViewHolderInt.shouldIgnore()) {
                    childViewHolderInt.setIsRecyclable(false);
                    if (childViewHolderInt.isTmpDetached()) {
                        this.mRecyclerView.removeDetachedView(scrapView, false);
                    }
                    if (this.mRecyclerView.mItemAnimator != null) {
                        this.mRecyclerView.mItemAnimator.endAnimation(childViewHolderInt);
                    }
                    childViewHolderInt.setIsRecyclable(true);
                    recycler.quickRecycleScrapView(scrapView);
                }
            }
            recycler.clearScrap();
            if (scrapCount > 0) {
                this.mRecyclerView.invalidate();
            }
        }
        
        public void removeAndRecycleView(final View view, final Recycler recycler) {
            this.removeView(view);
            recycler.recycleView(view);
        }
        
        public void removeAndRecycleViewAt(final int n, final Recycler recycler) {
            final View child = this.getChildAt(n);
            this.removeViewAt(n);
            recycler.recycleView(child);
        }
        
        public boolean removeCallbacks(final Runnable runnable) {
            return this.mRecyclerView != null && this.mRecyclerView.removeCallbacks(runnable);
        }
        
        public void removeDetachedView(final View view) {
            this.mRecyclerView.removeDetachedView(view, false);
        }
        
        public void removeView(final View view) {
            this.mChildHelper.removeView(view);
        }
        
        public void removeViewAt(final int n) {
            if (this.getChildAt(n) != null) {
                this.mChildHelper.removeViewAt(n);
            }
        }
        
        public boolean requestChildRectangleOnScreen(final RecyclerView recyclerView, final View view, final Rect rect, final boolean b) {
            return this.requestChildRectangleOnScreen(recyclerView, view, rect, b, false);
        }
        
        public boolean requestChildRectangleOnScreen(final RecyclerView recyclerView, final View view, final Rect rect, final boolean b, final boolean b2) {
            final int[] childRectangleOnScreenScrollAmount = this.getChildRectangleOnScreenScrollAmount(recyclerView, view, rect, b);
            final int n = childRectangleOnScreenScrollAmount[0];
            final int n2 = childRectangleOnScreenScrollAmount[1];
            if ((!b2 || this.isFocusedChildVisibleAfterScrolling(recyclerView, n, n2)) && (n != 0 || n2 != 0)) {
                if (!b) {
                    recyclerView.smoothScrollBy(n, n2);
                }
                else {
                    recyclerView.scrollBy(n, n2);
                }
                return true;
            }
            return false;
        }
        
        public void requestLayout() {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.requestLayout();
            }
        }
        
        public void requestSimpleAnimationsInNextLayout() {
            this.mRequestedSimpleAnimations = true;
        }
        
        public int scrollHorizontallyBy(final int n, final Recycler recycler, final State state) {
            return 0;
        }
        
        public void scrollToPosition(final int n) {
        }
        
        public int scrollVerticallyBy(final int n, final Recycler recycler, final State state) {
            return 0;
        }
        
        public void setAutoMeasureEnabled(final boolean mAutoMeasure) {
            this.mAutoMeasure = mAutoMeasure;
        }
        
        void setExactMeasureSpecsFrom(final RecyclerView recyclerView) {
            this.setMeasureSpecs(View$MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), 1073741824), View$MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), 1073741824));
        }
        
        public final void setItemPrefetchEnabled(final boolean mItemPrefetchEnabled) {
            if (mItemPrefetchEnabled != this.mItemPrefetchEnabled) {
                this.mItemPrefetchEnabled = mItemPrefetchEnabled;
                this.mPrefetchMaxCountObserved = 0;
                if (this.mRecyclerView != null) {
                    this.mRecyclerView.mRecycler.updateViewCacheSize();
                }
            }
        }
        
        void setMeasureSpecs(final int n, final int n2) {
            this.mWidth = View$MeasureSpec.getSize(n);
            this.mWidthMode = View$MeasureSpec.getMode(n);
            if (this.mWidthMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mWidth = 0;
            }
            this.mHeight = View$MeasureSpec.getSize(n2);
            this.mHeightMode = View$MeasureSpec.getMode(n2);
            if (this.mHeightMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mHeight = 0;
            }
        }
        
        public void setMeasuredDimension(final int n, final int n2) {
            RecyclerView.access$1200(this.mRecyclerView, n, n2);
        }
        
        public void setMeasuredDimension(final Rect rect, final int n, final int n2) {
            this.setMeasuredDimension(chooseSize(n, rect.width() + this.getPaddingLeft() + this.getPaddingRight(), this.getMinimumWidth()), chooseSize(n2, rect.height() + this.getPaddingTop() + this.getPaddingBottom(), this.getMinimumHeight()));
        }
        
        void setMeasuredDimensionFromChildren(final int n, final int n2) {
            int top = Integer.MAX_VALUE;
            int bottom = Integer.MIN_VALUE;
            final int childCount = this.getChildCount();
            if (childCount != 0) {
                int i = 0;
                int right = Integer.MIN_VALUE;
                int left = Integer.MAX_VALUE;
                while (i < childCount) {
                    final View child = this.getChildAt(i);
                    final Rect mTempRect = this.mRecyclerView.mTempRect;
                    this.getDecoratedBoundsWithMargins(child, mTempRect);
                    if (mTempRect.left < left) {
                        left = mTempRect.left;
                    }
                    if (mTempRect.right > right) {
                        right = mTempRect.right;
                    }
                    if (mTempRect.top < top) {
                        top = mTempRect.top;
                    }
                    if (mTempRect.bottom > bottom) {
                        bottom = mTempRect.bottom;
                    }
                    ++i;
                }
                this.mRecyclerView.mTempRect.set(left, top, right, bottom);
                this.setMeasuredDimension(this.mRecyclerView.mTempRect, n, n2);
                return;
            }
            this.mRecyclerView.defaultOnMeasure(n, n2);
        }
        
        public void setMeasurementCacheEnabled(final boolean mMeasurementCacheEnabled) {
            this.mMeasurementCacheEnabled = mMeasurementCacheEnabled;
        }
        
        void setRecyclerView(final RecyclerView mRecyclerView) {
            if (mRecyclerView != null) {
                this.mRecyclerView = mRecyclerView;
                this.mChildHelper = mRecyclerView.mChildHelper;
                this.mWidth = mRecyclerView.getWidth();
                this.mHeight = mRecyclerView.getHeight();
            }
            else {
                this.mRecyclerView = null;
                this.mChildHelper = null;
                this.mWidth = 0;
                this.mHeight = 0;
            }
            this.mWidthMode = 1073741824;
            this.mHeightMode = 1073741824;
        }
        
        boolean shouldMeasureChild(final View view, final int n, final int n2, final LayoutParams layoutParams) {
            boolean b = false;
            if (view.isLayoutRequested() || !this.mMeasurementCacheEnabled || !isMeasurementUpToDate(view.getWidth(), n, layoutParams.width) || !isMeasurementUpToDate(view.getHeight(), n2, layoutParams.height)) {
                b = true;
            }
            return b;
        }
        
        boolean shouldMeasureTwice() {
            return false;
        }
        
        boolean shouldReMeasureChild(final View view, final int n, final int n2, final LayoutParams layoutParams) {
            boolean b = false;
            if (!this.mMeasurementCacheEnabled || !isMeasurementUpToDate(view.getMeasuredWidth(), n, layoutParams.width) || !isMeasurementUpToDate(view.getMeasuredHeight(), n2, layoutParams.height)) {
                b = true;
            }
            return b;
        }
        
        public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int n) {
            Log.e("RecyclerView", "You must override smoothScrollToPosition to support smooth scrolling");
        }
        
        public void startSmoothScroll(final SmoothScroller mSmoothScroller) {
            if (this.mSmoothScroller != null && mSmoothScroller != this.mSmoothScroller && this.mSmoothScroller.isRunning()) {
                this.mSmoothScroller.stop();
            }
            (this.mSmoothScroller = mSmoothScroller).start(this.mRecyclerView, this);
        }
        
        public void stopIgnoringView(final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.stopIgnoring();
            childViewHolderInt.resetInternal();
            childViewHolderInt.addFlags(4);
        }
        
        void stopSmoothScroller() {
            if (this.mSmoothScroller != null) {
                this.mSmoothScroller.stop();
            }
        }
        
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
        
        public interface LayoutPrefetchRegistry
        {
            void addPosition(final int p0, final int p1);
        }
        
        public static class Properties
        {
            public int orientation;
            public boolean reverseLayout;
            public int spanCount;
            public boolean stackFromEnd;
        }
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        final Rect mDecorInsets;
        boolean mInsetsDirty;
        boolean mPendingInvalidate;
        ViewHolder mViewHolder;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$LayoutParams)layoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = false;
        }
        
        public int getViewAdapterPosition() {
            return this.mViewHolder.getAdapterPosition();
        }
        
        public int getViewLayoutPosition() {
            return this.mViewHolder.getLayoutPosition();
        }
        
        @Deprecated
        public int getViewPosition() {
            return this.mViewHolder.getPosition();
        }
        
        public boolean isItemChanged() {
            return this.mViewHolder.isUpdated();
        }
        
        public boolean isItemRemoved() {
            return this.mViewHolder.isRemoved();
        }
        
        public boolean isViewInvalid() {
            return this.mViewHolder.isInvalid();
        }
        
        public boolean viewNeedsUpdate() {
            return this.mViewHolder.needsUpdate();
        }
    }
    
    public interface OnChildAttachStateChangeListener
    {
        void onChildViewAttachedToWindow(final View p0);
        
        void onChildViewDetachedFromWindow(final View p0);
    }
    
    public abstract static class OnFlingListener
    {
        public abstract boolean onFling(final int p0, final int p1);
    }
    
    public interface OnItemTouchListener
    {
        boolean onInterceptTouchEvent(final RecyclerView p0, final MotionEvent p1);
        
        void onRequestDisallowInterceptTouchEvent(final boolean p0);
        
        void onTouchEvent(final RecyclerView p0, final MotionEvent p1);
    }
    
    public abstract static class OnScrollListener
    {
        public void onScrollStateChanged(final RecyclerView recyclerView, final int n) {
        }
        
        public void onScrolled(final RecyclerView recyclerView, final int n, final int n2) {
        }
    }
    
    public static class RecycledViewPool
    {
        private static final int DEFAULT_MAX_SCRAP = 5;
        private int mAttachCount;
        SparseArray<ScrapData> mScrap;
        
        public RecycledViewPool() {
            this.mScrap = (SparseArray<ScrapData>)new SparseArray();
            this.mAttachCount = 0;
        }
        
        private ScrapData getScrapDataForType(final int n) {
            ScrapData scrapData = (ScrapData)this.mScrap.get(n);
            if (scrapData == null) {
                scrapData = new ScrapData();
                this.mScrap.put(n, (Object)scrapData);
            }
            return scrapData;
        }
        
        void attach(final Adapter adapter) {
            ++this.mAttachCount;
        }
        
        public void clear() {
            for (int i = 0; i < this.mScrap.size(); ++i) {
                ((ScrapData)this.mScrap.valueAt(i)).mScrapHeap.clear();
            }
        }
        
        void detach() {
            --this.mAttachCount;
        }
        
        void factorInBindTime(final int n, final long n2) {
            final ScrapData scrapDataForType = this.getScrapDataForType(n);
            scrapDataForType.mBindRunningAverageNs = this.runningAverage(scrapDataForType.mBindRunningAverageNs, n2);
        }
        
        void factorInCreateTime(final int n, final long n2) {
            final ScrapData scrapDataForType = this.getScrapDataForType(n);
            scrapDataForType.mCreateRunningAverageNs = this.runningAverage(scrapDataForType.mCreateRunningAverageNs, n2);
        }
        
        public ViewHolder getRecycledView(final int n) {
            final ScrapData scrapData = (ScrapData)this.mScrap.get(n);
            if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
                final ArrayList<ViewHolder> mScrapHeap = scrapData.mScrapHeap;
                return mScrapHeap.remove(mScrapHeap.size() - 1);
            }
            return null;
        }
        
        public int getRecycledViewCount(final int n) {
            return this.getScrapDataForType(n).mScrapHeap.size();
        }
        
        void onAdapterChanged(final Adapter adapter, final Adapter adapter2, final boolean b) {
            if (adapter != null) {
                this.detach();
            }
            if (!b && this.mAttachCount == 0) {
                this.clear();
            }
            if (adapter2 != null) {
                this.attach(adapter2);
            }
        }
        
        public void putRecycledView(final ViewHolder e) {
            final int itemViewType = e.getItemViewType();
            final ArrayList<ViewHolder> mScrapHeap = this.getScrapDataForType(itemViewType).mScrapHeap;
            if (((ScrapData)this.mScrap.get(itemViewType)).mMaxScrap > mScrapHeap.size()) {
                e.resetInternal();
                mScrapHeap.add(e);
            }
        }
        
        long runningAverage(final long n, final long n2) {
            if (n == 0L) {
                return n2;
            }
            return n / 4L * 3L + n2 / 4L;
        }
        
        public void setMaxRecycledViews(final int n, final int mMaxScrap) {
            final ScrapData scrapDataForType = this.getScrapDataForType(n);
            scrapDataForType.mMaxScrap = mMaxScrap;
            final ArrayList<ViewHolder> mScrapHeap = scrapDataForType.mScrapHeap;
            if (mScrapHeap != null) {
                while (mScrapHeap.size() > mMaxScrap) {
                    mScrapHeap.remove(mScrapHeap.size() - 1);
                }
            }
        }
        
        int size() {
            int i = 0;
            int n = 0;
            while (i < this.mScrap.size()) {
                final ArrayList<ViewHolder> mScrapHeap = ((ScrapData)this.mScrap.valueAt(i)).mScrapHeap;
                if (mScrapHeap != null) {
                    n += mScrapHeap.size();
                }
                ++i;
            }
            return n;
        }
        
        boolean willBindInTime(int n, final long n2, final long n3) {
            boolean b = false;
            final long mBindRunningAverageNs = this.getScrapDataForType(n).mBindRunningAverageNs;
            if (mBindRunningAverageNs != 0L) {
                if (mBindRunningAverageNs + n2 >= n3) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    return b;
                }
            }
            b = true;
            return b;
        }
        
        boolean willCreateInTime(int n, final long n2, final long n3) {
            boolean b = false;
            final long mCreateRunningAverageNs = this.getScrapDataForType(n).mCreateRunningAverageNs;
            if (mCreateRunningAverageNs != 0L) {
                if (mCreateRunningAverageNs + n2 >= n3) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    return b;
                }
            }
            b = true;
            return b;
        }
        
        static class ScrapData
        {
            long mBindRunningAverageNs;
            long mCreateRunningAverageNs;
            int mMaxScrap;
            ArrayList<ViewHolder> mScrapHeap;
            
            ScrapData() {
                this.mScrapHeap = new ArrayList<ViewHolder>();
                this.mMaxScrap = 5;
                this.mCreateRunningAverageNs = 0L;
                this.mBindRunningAverageNs = 0L;
            }
        }
    }
    
    public final class Recycler
    {
        static final int DEFAULT_CACHE_SIZE = 2;
        final ArrayList<ViewHolder> mAttachedScrap;
        final ArrayList<ViewHolder> mCachedViews;
        ArrayList<ViewHolder> mChangedScrap;
        RecycledViewPool mRecyclerPool;
        private int mRequestedCacheMax;
        private final List<ViewHolder> mUnmodifiableAttachedScrap;
        private ViewCacheExtension mViewCacheExtension;
        int mViewCacheMax;
        
        public Recycler() {
            this.mAttachedScrap = new ArrayList<ViewHolder>();
            this.mChangedScrap = null;
            this.mCachedViews = new ArrayList<ViewHolder>();
            this.mUnmodifiableAttachedScrap = Collections.unmodifiableList((List<? extends ViewHolder>)this.mAttachedScrap);
            this.mRequestedCacheMax = 2;
            this.mViewCacheMax = 2;
        }
        
        private void attachAccessibilityDelegateOnBind(final ViewHolder viewHolder) {
            if (RecyclerView.this.isAccessibilityEnabled()) {
                final View itemView = viewHolder.itemView;
                if (ViewCompat.getImportantForAccessibility(itemView) == 0) {
                    ViewCompat.setImportantForAccessibility(itemView, 1);
                }
                if (!ViewCompat.hasAccessibilityDelegate(itemView)) {
                    viewHolder.addFlags(16384);
                    ViewCompat.setAccessibilityDelegate(itemView, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
                }
            }
        }
        
        private void invalidateDisplayListInt(final ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ViewGroup) {
                this.invalidateDisplayListInt((ViewGroup)viewHolder.itemView, false);
            }
        }
        
        private void invalidateDisplayListInt(final ViewGroup viewGroup, final boolean b) {
            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    this.invalidateDisplayListInt((ViewGroup)child, true);
                }
            }
            if (b) {
                if (viewGroup.getVisibility() != 4) {
                    final int visibility = viewGroup.getVisibility();
                    viewGroup.setVisibility(4);
                    viewGroup.setVisibility(visibility);
                }
                else {
                    viewGroup.setVisibility(0);
                    viewGroup.setVisibility(4);
                }
            }
        }
        
        private boolean tryBindViewHolderByDeadline(final ViewHolder viewHolder, final int n, final int mPreLayoutPosition, long nanoTime) {
            viewHolder.mOwnerRecyclerView = RecyclerView.this;
            final int itemViewType = viewHolder.getItemViewType();
            final long nanoTime2 = RecyclerView.this.getNanoTime();
            if (nanoTime == Long.MAX_VALUE || this.mRecyclerPool.willBindInTime(itemViewType, nanoTime2, nanoTime)) {
                RecyclerView.this.mAdapter.bindViewHolder(viewHolder, n);
                nanoTime = RecyclerView.this.getNanoTime();
                this.mRecyclerPool.factorInBindTime(viewHolder.getItemViewType(), nanoTime - nanoTime2);
                this.attachAccessibilityDelegateOnBind(viewHolder);
                if (RecyclerView.this.mState.isPreLayout()) {
                    viewHolder.mPreLayoutPosition = mPreLayoutPosition;
                }
                return true;
            }
            return false;
        }
        
        void addViewHolderToRecycledViewPool(final ViewHolder viewHolder, final boolean b) {
            RecyclerView.clearNestedRecyclerViewIfNotNested(viewHolder);
            if (viewHolder.hasAnyOfTheFlags(16384)) {
                viewHolder.setFlags(0, 16384);
                ViewCompat.setAccessibilityDelegate(viewHolder.itemView, null);
            }
            if (b) {
                this.dispatchViewRecycled(viewHolder);
            }
            viewHolder.mOwnerRecyclerView = null;
            this.getRecycledViewPool().putRecycledView(viewHolder);
        }
        
        public void bindViewToPosition(final View view, final int i) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt == null) {
                throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter" + RecyclerView.this.exceptionLabel());
            }
            final int positionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            if (positionOffset >= 0 && positionOffset < RecyclerView.this.mAdapter.getItemCount()) {
                this.tryBindViewHolderByDeadline(childViewHolderInt, positionOffset, i, Long.MAX_VALUE);
                final ViewGroup$LayoutParams layoutParams = childViewHolderInt.itemView.getLayoutParams();
                LayoutParams layoutParams2;
                if (layoutParams != null) {
                    if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                        layoutParams2 = (LayoutParams)layoutParams;
                    }
                    else {
                        layoutParams2 = (LayoutParams)RecyclerView.this.generateLayoutParams(layoutParams);
                        childViewHolderInt.itemView.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                    }
                }
                else {
                    layoutParams2 = (LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
                    childViewHolderInt.itemView.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                }
                layoutParams2.mInsetsDirty = true;
                layoutParams2.mViewHolder = childViewHolderInt;
                layoutParams2.mPendingInvalidate = (childViewHolderInt.itemView.getParent() == null);
                return;
            }
            throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + i + "(offset:" + positionOffset + ")." + "state:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
        }
        
        public void clear() {
            this.mAttachedScrap.clear();
            this.recycleAndClearCachedViews();
        }
        
        void clearOldPositions() {
            final int n = 0;
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                this.mCachedViews.get(i).clearOldPosition();
            }
            for (int size2 = this.mAttachedScrap.size(), j = 0; j < size2; ++j) {
                this.mAttachedScrap.get(j).clearOldPosition();
            }
            if (this.mChangedScrap != null) {
                for (int size3 = this.mChangedScrap.size(), k = n; k < size3; ++k) {
                    this.mChangedScrap.get(k).clearOldPosition();
                }
            }
        }
        
        void clearScrap() {
            this.mAttachedScrap.clear();
            if (this.mChangedScrap != null) {
                this.mChangedScrap.clear();
            }
        }
        
        public int convertPreLayoutPositionToPostLayout(final int i) {
            if (i < 0 || i >= RecyclerView.this.mState.getItemCount()) {
                throw new IndexOutOfBoundsException("invalid position " + i + ". State " + "item count is " + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
            }
            if (RecyclerView.this.mState.isPreLayout()) {
                return RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            }
            return i;
        }
        
        void dispatchViewRecycled(final ViewHolder viewHolder) {
            if (RecyclerView.this.mRecyclerListener != null) {
                RecyclerView.this.mRecyclerListener.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mAdapter != null) {
                RecyclerView.this.mAdapter.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mState != null) {
                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
            }
        }
        
        ViewHolder getChangedScrapViewForPosition(int i) {
            final int n = 0;
            if (this.mChangedScrap != null) {
                final int size = this.mChangedScrap.size();
                if (size != 0) {
                    for (int j = 0; j < size; ++j) {
                        final ViewHolder viewHolder = this.mChangedScrap.get(j);
                        if (!viewHolder.wasReturnedFromScrap() && viewHolder.getLayoutPosition() == i) {
                            viewHolder.addFlags(32);
                            return viewHolder;
                        }
                    }
                    if (RecyclerView.this.mAdapter.hasStableIds()) {
                        i = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                        if (i > 0 && i < RecyclerView.this.mAdapter.getItemCount()) {
                            final long itemId = RecyclerView.this.mAdapter.getItemId(i);
                            ViewHolder viewHolder2;
                            for (i = n; i < size; ++i) {
                                viewHolder2 = this.mChangedScrap.get(i);
                                if (!viewHolder2.wasReturnedFromScrap() && viewHolder2.getItemId() == itemId) {
                                    viewHolder2.addFlags(32);
                                    return viewHolder2;
                                }
                            }
                        }
                    }
                    return null;
                }
            }
            return null;
        }
        
        RecycledViewPool getRecycledViewPool() {
            if (this.mRecyclerPool == null) {
                this.mRecyclerPool = new RecycledViewPool();
            }
            return this.mRecyclerPool;
        }
        
        int getScrapCount() {
            return this.mAttachedScrap.size();
        }
        
        public List<ViewHolder> getScrapList() {
            return this.mUnmodifiableAttachedScrap;
        }
        
        ViewHolder getScrapOrCachedViewForId(final long n, final int n2, final boolean b) {
            for (int i = this.mAttachedScrap.size() - 1; i >= 0; --i) {
                final ViewHolder viewHolder = this.mAttachedScrap.get(i);
                if (viewHolder.getItemId() == n && !viewHolder.wasReturnedFromScrap()) {
                    if (n2 == viewHolder.getItemViewType()) {
                        viewHolder.addFlags(32);
                        if (viewHolder.isRemoved() && !RecyclerView.this.mState.isPreLayout()) {
                            viewHolder.setFlags(2, 14);
                        }
                        return viewHolder;
                    }
                    if (!b) {
                        this.mAttachedScrap.remove(i);
                        RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
                        this.quickRecycleScrapView(viewHolder.itemView);
                    }
                }
            }
            for (int j = this.mCachedViews.size() - 1; j >= 0; --j) {
                final ViewHolder viewHolder2 = this.mCachedViews.get(j);
                if (viewHolder2.getItemId() == n) {
                    if (n2 == viewHolder2.getItemViewType()) {
                        if (!b) {
                            this.mCachedViews.remove(j);
                        }
                        return viewHolder2;
                    }
                    if (!b) {
                        this.recycleCachedViewAt(j);
                        return null;
                    }
                }
            }
            return null;
        }
        
        ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int indexOfChild, final boolean b) {
            final int n = 0;
            for (int size = this.mAttachedScrap.size(), i = 0; i < size; ++i) {
                final ViewHolder viewHolder = this.mAttachedScrap.get(i);
                if (!viewHolder.wasReturnedFromScrap() && viewHolder.getLayoutPosition() == indexOfChild && !viewHolder.isInvalid() && (RecyclerView.this.mState.mInPreLayout || !viewHolder.isRemoved())) {
                    viewHolder.addFlags(32);
                    return viewHolder;
                }
            }
            if (!b) {
                final View hiddenNonRemovedView = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(indexOfChild);
                if (hiddenNonRemovedView != null) {
                    final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(hiddenNonRemovedView);
                    RecyclerView.this.mChildHelper.unhide(hiddenNonRemovedView);
                    indexOfChild = RecyclerView.this.mChildHelper.indexOfChild(hiddenNonRemovedView);
                    if (indexOfChild != -1) {
                        RecyclerView.this.mChildHelper.detachViewFromParent(indexOfChild);
                        this.scrapView(hiddenNonRemovedView);
                        childViewHolderInt.addFlags(8224);
                        return childViewHolderInt;
                    }
                    throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + childViewHolderInt + RecyclerView.this.exceptionLabel());
                }
            }
            for (int size2 = this.mCachedViews.size(), j = n; j < size2; ++j) {
                final ViewHolder viewHolder2 = this.mCachedViews.get(j);
                if (!viewHolder2.isInvalid() && viewHolder2.getLayoutPosition() == indexOfChild) {
                    if (!b) {
                        this.mCachedViews.remove(j);
                    }
                    return viewHolder2;
                }
            }
            return null;
        }
        
        View getScrapViewAt(final int index) {
            return this.mAttachedScrap.get(index).itemView;
        }
        
        public View getViewForPosition(final int n) {
            return this.getViewForPosition(n, false);
        }
        
        View getViewForPosition(final int n, final boolean b) {
            return this.tryGetViewHolderForPositionByDeadline(n, b, Long.MAX_VALUE).itemView;
        }
        
        void markItemDecorInsetsDirty() {
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                final LayoutParams layoutParams = (LayoutParams)this.mCachedViews.get(i).itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.mInsetsDirty = true;
                }
            }
        }
        
        void markKnownViewsInvalid() {
            if (RecyclerView.this.mAdapter != null && RecyclerView.this.mAdapter.hasStableIds()) {
                for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                    final ViewHolder viewHolder = this.mCachedViews.get(i);
                    if (viewHolder != null) {
                        viewHolder.addFlags(6);
                        viewHolder.addChangePayload(null);
                    }
                }
            }
            else {
                this.recycleAndClearCachedViews();
            }
        }
        
        void offsetPositionRecordsForInsert(final int n, final int n2) {
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null && viewHolder.mPosition >= n) {
                    viewHolder.offsetPosition(n2, true);
                }
            }
        }
        
        void offsetPositionRecordsForMove(final int n, final int n2) {
            int n3;
            int n4;
            int n5;
            if (n >= n2) {
                n3 = 1;
                n4 = n;
                n5 = n2;
            }
            else {
                n3 = -1;
                n4 = n2;
                n5 = n;
            }
            for (int size = this.mCachedViews.size(), i = 0; i < size; ++i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null && viewHolder.mPosition >= n5 && viewHolder.mPosition <= n4) {
                    if (viewHolder.mPosition != n) {
                        viewHolder.offsetPosition(n3, false);
                    }
                    else {
                        viewHolder.offsetPosition(n2 - n, false);
                    }
                }
            }
        }
        
        void offsetPositionRecordsForRemove(final int n, final int n2, final boolean b) {
            for (int i = this.mCachedViews.size() - 1; i >= 0; --i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null) {
                    if (viewHolder.mPosition < n + n2) {
                        if (viewHolder.mPosition >= n) {
                            viewHolder.addFlags(8);
                            this.recycleCachedViewAt(i);
                        }
                    }
                    else {
                        viewHolder.offsetPosition(-n2, b);
                    }
                }
            }
        }
        
        void onAdapterChanged(final Adapter adapter, final Adapter adapter2, final boolean b) {
            this.clear();
            this.getRecycledViewPool().onAdapterChanged(adapter, adapter2, b);
        }
        
        void quickRecycleScrapView(final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.mScrapContainer = null;
            childViewHolderInt.mInChangeScrap = false;
            childViewHolderInt.clearReturnedFromScrapFlag();
            this.recycleViewHolderInternal(childViewHolderInt);
        }
        
        void recycleAndClearCachedViews() {
            int size = this.mCachedViews.size();
            while (--size >= 0) {
                this.recycleCachedViewAt(size);
            }
            this.mCachedViews.clear();
            if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
            }
        }
        
        void recycleCachedViewAt(final int n) {
            this.addViewHolderToRecycledViewPool(this.mCachedViews.get(n), true);
            this.mCachedViews.remove(n);
        }
        
        public void recycleView(final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, false);
            }
            if (!childViewHolderInt.isScrap()) {
                if (childViewHolderInt.wasReturnedFromScrap()) {
                    childViewHolderInt.clearReturnedFromScrapFlag();
                }
            }
            else {
                childViewHolderInt.unScrap();
            }
            this.recycleViewHolderInternal(childViewHolderInt);
        }
        
        void recycleViewHolderInternal(final ViewHolder viewHolder) {
            final int n = 0;
            if (viewHolder.isScrap() || viewHolder.itemView.getParent() != null) {
                throw new IllegalArgumentException("Scrapped or attached views may not be recycled. isScrap:" + viewHolder.isScrap() + " isAttached:" + (viewHolder.itemView.getParent() != null) + RecyclerView.this.exceptionLabel());
            }
            if (viewHolder.isTmpDetached()) {
                throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + viewHolder + RecyclerView.this.exceptionLabel());
            }
            if (!viewHolder.shouldIgnore()) {
                final boolean access$900 = viewHolder.doesTransientStatePreventRecycling();
                boolean b;
                if (RecyclerView.this.mAdapter != null && access$900 && RecyclerView.this.mAdapter.onFailedToRecycleView(viewHolder)) {
                    b = true;
                }
                else {
                    b = false;
                }
                int n2;
                int n3;
                if (!b && !viewHolder.isRecyclable()) {
                    n2 = 0;
                    n3 = n;
                }
                else {
                    if (this.mViewCacheMax <= 0) {
                        n2 = 0;
                    }
                    else if (viewHolder.hasAnyOfTheFlags(526)) {
                        n2 = 0;
                    }
                    else {
                        final int size = this.mCachedViews.size();
                        int index;
                        if (size < this.mViewCacheMax) {
                            index = size;
                        }
                        else if ((index = size) > 0) {
                            this.recycleCachedViewAt(0);
                            index = size - 1;
                        }
                        int index2;
                        if (!RecyclerView.ALLOW_THREAD_GAP_WORK) {
                            index2 = index;
                        }
                        else if ((index2 = index) > 0) {
                            index2 = index;
                            if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(viewHolder.mPosition)) {
                                --index;
                                while (index >= 0 && RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(this.mCachedViews.get(index).mPosition)) {
                                    --index;
                                }
                                index2 = index + 1;
                            }
                        }
                        this.mCachedViews.add(index2, viewHolder);
                        n2 = 1;
                    }
                    if (n2 != 0) {
                        n3 = n;
                    }
                    else {
                        this.addViewHolderToRecycledViewPool(viewHolder, true);
                        n3 = 1;
                    }
                }
                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
                if (n2 == 0 && n3 == 0 && access$900) {
                    viewHolder.mOwnerRecyclerView = null;
                }
                return;
            }
            throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle." + RecyclerView.this.exceptionLabel());
        }
        
        void recycleViewInternal(final View view) {
            this.recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(view));
        }
        
        void scrapView(final View view) {
            final ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!childViewHolderInt.hasAnyOfTheFlags(12) && childViewHolderInt.isUpdated() && !RecyclerView.this.canReuseUpdatedViewHolder(childViewHolderInt)) {
                if (this.mChangedScrap == null) {
                    this.mChangedScrap = new ArrayList<ViewHolder>();
                }
                childViewHolderInt.setScrapContainer(this, true);
                this.mChangedScrap.add(childViewHolderInt);
            }
            else {
                if (childViewHolderInt.isInvalid() && !childViewHolderInt.isRemoved() && !RecyclerView.this.mAdapter.hasStableIds()) {
                    throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool." + RecyclerView.this.exceptionLabel());
                }
                childViewHolderInt.setScrapContainer(this, false);
                this.mAttachedScrap.add(childViewHolderInt);
            }
        }
        
        void setRecycledViewPool(final RecycledViewPool mRecyclerPool) {
            if (this.mRecyclerPool != null) {
                this.mRecyclerPool.detach();
            }
            if ((this.mRecyclerPool = mRecyclerPool) != null) {
                this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
            }
        }
        
        void setViewCacheExtension(final ViewCacheExtension mViewCacheExtension) {
            this.mViewCacheExtension = mViewCacheExtension;
        }
        
        public void setViewCacheSize(final int mRequestedCacheMax) {
            this.mRequestedCacheMax = mRequestedCacheMax;
            this.updateViewCacheSize();
        }
        
        @Nullable
        ViewHolder tryGetViewHolderForPositionByDeadline(final int n, final boolean b, final long n2) {
            boolean mPendingInvalidate = true;
            if (n >= 0 && n < RecyclerView.this.mState.getItemCount()) {
                Object mViewHolder;
                int n3;
                if (!RecyclerView.this.mState.isPreLayout()) {
                    mViewHolder = null;
                    n3 = 0;
                }
                else {
                    mViewHolder = this.getChangedScrapViewForPosition(n);
                    int n4;
                    if (mViewHolder == null) {
                        n4 = 0;
                    }
                    else {
                        n4 = 1;
                    }
                    n3 = n4;
                }
                int n5;
                if (mViewHolder != null) {
                    n5 = n3;
                }
                else {
                    final ViewHolder viewHolder = (ViewHolder)(mViewHolder = this.getScrapOrHiddenOrCachedHolderForPosition(n, b));
                    n5 = n3;
                    if (viewHolder != null) {
                        if (this.validateViewHolderForOffsetPosition(viewHolder)) {
                            n5 = 1;
                            mViewHolder = viewHolder;
                        }
                        else {
                            if (!b) {
                                viewHolder.addFlags(4);
                                if (!viewHolder.isScrap()) {
                                    if (viewHolder.wasReturnedFromScrap()) {
                                        viewHolder.clearReturnedFromScrapFlag();
                                    }
                                }
                                else {
                                    RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
                                    viewHolder.unScrap();
                                }
                                this.recycleViewHolderInternal(viewHolder);
                            }
                            mViewHolder = null;
                            n5 = n3;
                        }
                    }
                }
                if (mViewHolder == null) {
                    final int positionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(n);
                    if (positionOffset < 0 || positionOffset >= RecyclerView.this.mAdapter.getItemCount()) {
                        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + n + "(offset:" + positionOffset + ")." + "state:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
                    }
                    final int itemViewType = RecyclerView.this.mAdapter.getItemViewType(positionOffset);
                    ViewHolder scrapOrCachedViewForId;
                    if (!RecyclerView.this.mAdapter.hasStableIds()) {
                        scrapOrCachedViewForId = (ViewHolder)mViewHolder;
                    }
                    else {
                        scrapOrCachedViewForId = this.getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(positionOffset), itemViewType, b);
                        if (scrapOrCachedViewForId != null) {
                            scrapOrCachedViewForId.mPosition = positionOffset;
                            n5 = 1;
                        }
                    }
                    if (scrapOrCachedViewForId != null) {
                        mViewHolder = scrapOrCachedViewForId;
                    }
                    else {
                        mViewHolder = scrapOrCachedViewForId;
                        if (this.mViewCacheExtension != null) {
                            final View viewForPositionAndType = this.mViewCacheExtension.getViewForPositionAndType(this, n, itemViewType);
                            mViewHolder = scrapOrCachedViewForId;
                            if (viewForPositionAndType != null) {
                                final ViewHolder childViewHolder = RecyclerView.this.getChildViewHolder(viewForPositionAndType);
                                if (childViewHolder == null) {
                                    throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder" + RecyclerView.this.exceptionLabel());
                                }
                                mViewHolder = childViewHolder;
                                if (childViewHolder.shouldIgnore()) {
                                    throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view." + RecyclerView.this.exceptionLabel());
                                }
                            }
                        }
                    }
                    if (mViewHolder == null) {
                        final ViewHolder recycledView = this.getRecycledViewPool().getRecycledView(itemViewType);
                        if ((mViewHolder = recycledView) != null) {
                            recycledView.resetInternal();
                            mViewHolder = recycledView;
                            if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                                this.invalidateDisplayListInt(recycledView);
                                mViewHolder = recycledView;
                            }
                        }
                    }
                    if (mViewHolder == null) {
                        final long nanoTime = RecyclerView.this.getNanoTime();
                        if (n2 != Long.MAX_VALUE && !this.mRecyclerPool.willCreateInTime(itemViewType, nanoTime, n2)) {
                            return null;
                        }
                        mViewHolder = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, itemViewType);
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                            final RecyclerView nestedRecyclerView = RecyclerView.findNestedRecyclerView(((ViewHolder)mViewHolder).itemView);
                            if (nestedRecyclerView != null) {
                                ((ViewHolder)mViewHolder).mNestedRecyclerView = new WeakReference<RecyclerView>(nestedRecyclerView);
                            }
                        }
                        this.mRecyclerPool.factorInCreateTime(itemViewType, RecyclerView.this.getNanoTime() - nanoTime);
                    }
                }
                if (n5 != 0 && !RecyclerView.this.mState.isPreLayout() && ((ViewHolder)mViewHolder).hasAnyOfTheFlags(8192)) {
                    ((ViewHolder)mViewHolder).setFlags(0, 8192);
                    if (RecyclerView.this.mState.mRunSimpleAnimations) {
                        RecyclerView.this.recordAnimationInfoIfBouncedHiddenView((ViewHolder)mViewHolder, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, (ViewHolder)mViewHolder, ItemAnimator.buildAdapterChangeFlagsForAnimations((ViewHolder)mViewHolder) | 0x1000, ((ViewHolder)mViewHolder).getUnmodifiedPayloads()));
                    }
                }
                boolean b2;
                if (RecyclerView.this.mState.isPreLayout() && ((ViewHolder)mViewHolder).isBound()) {
                    ((ViewHolder)mViewHolder).mPreLayoutPosition = n;
                    b2 = false;
                }
                else {
                    b2 = ((!((ViewHolder)mViewHolder).isBound() || ((ViewHolder)mViewHolder).needsUpdate() || ((ViewHolder)mViewHolder).isInvalid()) && this.tryBindViewHolderByDeadline((ViewHolder)mViewHolder, RecyclerView.this.mAdapterHelper.findPositionOffset(n), n, n2));
                }
                final ViewGroup$LayoutParams layoutParams = ((ViewHolder)mViewHolder).itemView.getLayoutParams();
                LayoutParams layoutParams2;
                if (layoutParams != null) {
                    if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                        layoutParams2 = (LayoutParams)layoutParams;
                    }
                    else {
                        layoutParams2 = (LayoutParams)RecyclerView.this.generateLayoutParams(layoutParams);
                        ((ViewHolder)mViewHolder).itemView.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                    }
                }
                else {
                    layoutParams2 = (LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
                    ((ViewHolder)mViewHolder).itemView.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
                }
                layoutParams2.mViewHolder = (ViewHolder)mViewHolder;
                if (n5 == 0 || !b2) {
                    mPendingInvalidate = false;
                }
                layoutParams2.mPendingInvalidate = mPendingInvalidate;
                return (ViewHolder)mViewHolder;
            }
            throw new IndexOutOfBoundsException("Invalid item position " + n + "(" + n + "). Item count:" + RecyclerView.this.mState.getItemCount() + RecyclerView.this.exceptionLabel());
        }
        
        void unscrapView(final ViewHolder viewHolder) {
            if (!viewHolder.mInChangeScrap) {
                this.mAttachedScrap.remove(viewHolder);
            }
            else {
                this.mChangedScrap.remove(viewHolder);
            }
            viewHolder.mScrapContainer = null;
            viewHolder.mInChangeScrap = false;
            viewHolder.clearReturnedFromScrapFlag();
        }
        
        void updateViewCacheSize() {
            int mPrefetchMaxCountObserved = 0;
            if (RecyclerView.this.mLayout != null) {
                mPrefetchMaxCountObserved = RecyclerView.this.mLayout.mPrefetchMaxCountObserved;
            }
            this.mViewCacheMax = mPrefetchMaxCountObserved + this.mRequestedCacheMax;
            for (int n = this.mCachedViews.size() - 1; n >= 0 && this.mCachedViews.size() > this.mViewCacheMax; --n) {
                this.recycleCachedViewAt(n);
            }
        }
        
        boolean validateViewHolderForOffsetPosition(final ViewHolder obj) {
            boolean b = true;
            if (obj.isRemoved()) {
                return RecyclerView.this.mState.isPreLayout();
            }
            if (obj.mPosition < 0 || obj.mPosition >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + obj + RecyclerView.this.exceptionLabel());
            }
            if (!RecyclerView.this.mState.isPreLayout() && RecyclerView.this.mAdapter.getItemViewType(obj.mPosition) != obj.getItemViewType()) {
                return false;
            }
            if (!RecyclerView.this.mAdapter.hasStableIds()) {
                return true;
            }
            if (obj.getItemId() != RecyclerView.this.mAdapter.getItemId(obj.mPosition)) {
                b = false;
            }
            return b;
        }
        
        void viewRangeUpdate(final int n, final int n2) {
            for (int i = this.mCachedViews.size() - 1; i >= 0; --i) {
                final ViewHolder viewHolder = this.mCachedViews.get(i);
                if (viewHolder != null) {
                    final int mPosition = viewHolder.mPosition;
                    if (mPosition >= n && mPosition < n + n2) {
                        viewHolder.addFlags(2);
                        this.recycleCachedViewAt(i);
                    }
                }
            }
        }
    }
    
    public interface RecyclerListener
    {
        void onViewRecycled(final ViewHolder p0);
    }
    
    private class RecyclerViewDataObserver extends AdapterDataObserver
    {
        RecyclerViewDataObserver() {
        }
        
        @Override
        public void onChanged() {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            RecyclerView.this.mState.mStructureChanged = true;
            RecyclerView.this.setDataSetChangedAfterLayout();
            if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
                RecyclerView.this.requestLayout();
            }
        }
        
        @Override
        public void onItemRangeChanged(final int n, final int n2, final Object o) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(n, n2, o)) {
                this.triggerUpdateProcessor();
            }
        }
        
        @Override
        public void onItemRangeInserted(final int n, final int n2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(n, n2)) {
                this.triggerUpdateProcessor();
            }
        }
        
        @Override
        public void onItemRangeMoved(final int n, final int n2, final int n3) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(n, n2, n3)) {
                this.triggerUpdateProcessor();
            }
        }
        
        @Override
        public void onItemRangeRemoved(final int n, final int n2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(n, n2)) {
                this.triggerUpdateProcessor();
            }
        }
        
        void triggerUpdateProcessor() {
            if (RecyclerView.POST_UPDATES_ON_ANIMATION && RecyclerView.this.mHasFixedSize && RecyclerView.this.mIsAttached) {
                ViewCompat.postOnAnimation((View)RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
            }
            else {
                RecyclerView.this.mAdapterUpdateDuringMeasure = true;
                RecyclerView.this.requestLayout();
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Parcelable mLayoutState;
        
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
        
        SavedState(final Parcel parcel, final ClassLoader classLoader) {
            super(parcel, classLoader);
            ClassLoader classLoader2 = classLoader;
            if (classLoader == null) {
                classLoader2 = LayoutManager.class.getClassLoader();
            }
            this.mLayoutState = parcel.readParcelable(classLoader2);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        void copyFrom(final SavedState savedState) {
            this.mLayoutState = savedState.mLayoutState;
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeParcelable(this.mLayoutState, 0);
        }
    }
    
    public static class SimpleOnItemTouchListener implements OnItemTouchListener
    {
        @Override
        public boolean onInterceptTouchEvent(final RecyclerView recyclerView, final MotionEvent motionEvent) {
            return false;
        }
        
        @Override
        public void onRequestDisallowInterceptTouchEvent(final boolean b) {
        }
        
        @Override
        public void onTouchEvent(final RecyclerView recyclerView, final MotionEvent motionEvent) {
        }
    }
    
    public abstract static class SmoothScroller
    {
        private LayoutManager mLayoutManager;
        private boolean mPendingInitialRun;
        private RecyclerView mRecyclerView;
        private final Action mRecyclingAction;
        private boolean mRunning;
        private int mTargetPosition;
        private View mTargetView;
        
        public SmoothScroller() {
            this.mTargetPosition = -1;
            this.mRecyclingAction = new Action(0, 0);
        }
        
        private void onAnimation(final int n, final int n2) {
            final RecyclerView mRecyclerView = this.mRecyclerView;
            if (!this.mRunning || this.mTargetPosition == -1 || mRecyclerView == null) {
                this.stop();
            }
            this.mPendingInitialRun = false;
            if (this.mTargetView != null) {
                if (this.getChildPosition(this.mTargetView) != this.mTargetPosition) {
                    Log.e("RecyclerView", "Passed over target position while smooth scrolling.");
                    this.mTargetView = null;
                }
                else {
                    this.onTargetFound(this.mTargetView, mRecyclerView.mState, this.mRecyclingAction);
                    this.mRecyclingAction.runIfNecessary(mRecyclerView);
                    this.stop();
                }
            }
            if (this.mRunning) {
                this.onSeekTargetStep(n, n2, mRecyclerView.mState, this.mRecyclingAction);
                final boolean hasJumpTarget = this.mRecyclingAction.hasJumpTarget();
                this.mRecyclingAction.runIfNecessary(mRecyclerView);
                if (hasJumpTarget) {
                    if (!this.mRunning) {
                        this.stop();
                    }
                    else {
                        this.mPendingInitialRun = true;
                        mRecyclerView.mViewFlinger.postOnAnimation();
                    }
                }
            }
        }
        
        public View findViewByPosition(final int n) {
            return this.mRecyclerView.mLayout.findViewByPosition(n);
        }
        
        public int getChildCount() {
            return this.mRecyclerView.mLayout.getChildCount();
        }
        
        public int getChildPosition(final View view) {
            return this.mRecyclerView.getChildLayoutPosition(view);
        }
        
        @Nullable
        public LayoutManager getLayoutManager() {
            return this.mLayoutManager;
        }
        
        public int getTargetPosition() {
            return this.mTargetPosition;
        }
        
        @Deprecated
        public void instantScrollToPosition(final int n) {
            this.mRecyclerView.scrollToPosition(n);
        }
        
        public boolean isPendingInitialRun() {
            return this.mPendingInitialRun;
        }
        
        public boolean isRunning() {
            return this.mRunning;
        }
        
        protected void normalize(final PointF pointF) {
            final float n = (float)Math.sqrt(pointF.x * pointF.x + pointF.y * pointF.y);
            pointF.x /= n;
            pointF.y /= n;
        }
        
        protected void onChildAttachedToWindow(final View mTargetView) {
            if (this.getChildPosition(mTargetView) == this.getTargetPosition()) {
                this.mTargetView = mTargetView;
            }
        }
        
        protected abstract void onSeekTargetStep(final int p0, final int p1, final State p2, final Action p3);
        
        protected abstract void onStart();
        
        protected abstract void onStop();
        
        protected abstract void onTargetFound(final View p0, final State p1, final Action p2);
        
        public void setTargetPosition(final int mTargetPosition) {
            this.mTargetPosition = mTargetPosition;
        }
        
        void start(final RecyclerView mRecyclerView, final LayoutManager mLayoutManager) {
            this.mRecyclerView = mRecyclerView;
            this.mLayoutManager = mLayoutManager;
            if (this.mTargetPosition != -1) {
                this.mRecyclerView.mState.mTargetPosition = this.mTargetPosition;
                this.mRunning = true;
                this.mPendingInitialRun = true;
                this.mTargetView = this.findViewByPosition(this.getTargetPosition());
                this.onStart();
                this.mRecyclerView.mViewFlinger.postOnAnimation();
                return;
            }
            throw new IllegalArgumentException("Invalid target position");
        }
        
        protected final void stop() {
            if (this.mRunning) {
                this.onStop();
                this.mRecyclerView.mState.mTargetPosition = -1;
                this.mTargetView = null;
                this.mTargetPosition = -1;
                this.mPendingInitialRun = false;
                this.mRunning = false;
                this.mLayoutManager.onSmoothScrollerStopped(this);
                this.mLayoutManager = null;
                this.mRecyclerView = null;
            }
        }
        
        public static class Action
        {
            public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
            private boolean mChanged;
            private int mConsecutiveUpdates;
            private int mDuration;
            private int mDx;
            private int mDy;
            private Interpolator mInterpolator;
            private int mJumpToPosition;
            
            public Action(final int n, final int n2) {
                this(n, n2, Integer.MIN_VALUE, null);
            }
            
            public Action(final int n, final int n2, final int n3) {
                this(n, n2, n3, null);
            }
            
            public Action(final int mDx, final int mDy, final int mDuration, final Interpolator mInterpolator) {
                this.mJumpToPosition = -1;
                this.mChanged = false;
                this.mConsecutiveUpdates = 0;
                this.mDx = mDx;
                this.mDy = mDy;
                this.mDuration = mDuration;
                this.mInterpolator = mInterpolator;
            }
            
            private void validate() {
                if (this.mInterpolator != null && this.mDuration < 1) {
                    throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
                }
                if (this.mDuration >= 1) {
                    return;
                }
                throw new IllegalStateException("Scroll duration must be a positive number");
            }
            
            public int getDuration() {
                return this.mDuration;
            }
            
            public int getDx() {
                return this.mDx;
            }
            
            public int getDy() {
                return this.mDy;
            }
            
            public Interpolator getInterpolator() {
                return this.mInterpolator;
            }
            
            boolean hasJumpTarget() {
                boolean b = false;
                if (this.mJumpToPosition >= 0) {
                    b = true;
                }
                return b;
            }
            
            public void jumpTo(final int mJumpToPosition) {
                this.mJumpToPosition = mJumpToPosition;
            }
            
            void runIfNecessary(final RecyclerView recyclerView) {
                if (this.mJumpToPosition < 0) {
                    if (!this.mChanged) {
                        this.mConsecutiveUpdates = 0;
                    }
                    else {
                        this.validate();
                        if (this.mInterpolator != null) {
                            recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
                        }
                        else if (this.mDuration != Integer.MIN_VALUE) {
                            recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
                        }
                        else {
                            recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
                        }
                        ++this.mConsecutiveUpdates;
                        if (this.mConsecutiveUpdates > 10) {
                            Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
                        }
                        this.mChanged = false;
                    }
                    return;
                }
                final int mJumpToPosition = this.mJumpToPosition;
                this.mJumpToPosition = -1;
                recyclerView.jumpToPositionForSmoothScroller(mJumpToPosition);
                this.mChanged = false;
            }
            
            public void setDuration(final int mDuration) {
                this.mChanged = true;
                this.mDuration = mDuration;
            }
            
            public void setDx(final int mDx) {
                this.mChanged = true;
                this.mDx = mDx;
            }
            
            public void setDy(final int mDy) {
                this.mChanged = true;
                this.mDy = mDy;
            }
            
            public void setInterpolator(final Interpolator mInterpolator) {
                this.mChanged = true;
                this.mInterpolator = mInterpolator;
            }
            
            public void update(final int mDx, final int mDy, final int mDuration, final Interpolator mInterpolator) {
                this.mDx = mDx;
                this.mDy = mDy;
                this.mDuration = mDuration;
                this.mInterpolator = mInterpolator;
                this.mChanged = true;
            }
        }
        
        public interface ScrollVectorProvider
        {
            PointF computeScrollVectorForPosition(final int p0);
        }
    }
    
    public static class State
    {
        static final int STEP_ANIMATIONS = 4;
        static final int STEP_LAYOUT = 2;
        static final int STEP_START = 1;
        private SparseArray<Object> mData;
        int mDeletedInvisibleItemCountSincePreviousLayout;
        long mFocusedItemId;
        int mFocusedItemPosition;
        int mFocusedSubChildId;
        boolean mInPreLayout;
        boolean mIsMeasuring;
        int mItemCount;
        int mLayoutStep;
        int mPreviousLayoutItemCount;
        int mRemainingScrollHorizontal;
        int mRemainingScrollVertical;
        boolean mRunPredictiveAnimations;
        boolean mRunSimpleAnimations;
        boolean mStructureChanged;
        private int mTargetPosition;
        boolean mTrackOldChangeHolders;
        
        public State() {
            this.mTargetPosition = -1;
            this.mPreviousLayoutItemCount = 0;
            this.mDeletedInvisibleItemCountSincePreviousLayout = 0;
            this.mLayoutStep = 1;
            this.mItemCount = 0;
            this.mStructureChanged = false;
            this.mInPreLayout = false;
            this.mTrackOldChangeHolders = false;
            this.mIsMeasuring = false;
            this.mRunSimpleAnimations = false;
            this.mRunPredictiveAnimations = false;
        }
        
        void assertLayoutStep(final int i) {
            if ((this.mLayoutStep & i) != 0x0) {
                return;
            }
            throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(i) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
        }
        
        public boolean didStructureChange() {
            return this.mStructureChanged;
        }
        
        public <T> T get(final int n) {
            if (this.mData != null) {
                return (T)this.mData.get(n);
            }
            return null;
        }
        
        public int getItemCount() {
            int mItemCount;
            if (!this.mInPreLayout) {
                mItemCount = this.mItemCount;
            }
            else {
                mItemCount = this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout;
            }
            return mItemCount;
        }
        
        public int getRemainingScrollHorizontal() {
            return this.mRemainingScrollHorizontal;
        }
        
        public int getRemainingScrollVertical() {
            return this.mRemainingScrollVertical;
        }
        
        public int getTargetScrollPosition() {
            return this.mTargetPosition;
        }
        
        public boolean hasTargetScrollPosition() {
            return this.mTargetPosition != -1;
        }
        
        public boolean isMeasuring() {
            return this.mIsMeasuring;
        }
        
        public boolean isPreLayout() {
            return this.mInPreLayout;
        }
        
        void prepareForNestedPrefetch(final Adapter adapter) {
            this.mLayoutStep = 1;
            this.mItemCount = adapter.getItemCount();
            this.mInPreLayout = false;
            this.mTrackOldChangeHolders = false;
            this.mIsMeasuring = false;
        }
        
        public void put(final int n, final Object o) {
            if (this.mData == null) {
                this.mData = (SparseArray<Object>)new SparseArray();
            }
            this.mData.put(n, o);
        }
        
        public void remove(final int n) {
            if (this.mData != null) {
                this.mData.remove(n);
            }
        }
        
        State reset() {
            this.mTargetPosition = -1;
            if (this.mData != null) {
                this.mData.clear();
            }
            this.mItemCount = 0;
            this.mStructureChanged = false;
            this.mIsMeasuring = false;
            return this;
        }
        
        @Override
        public String toString() {
            return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
        }
        
        public boolean willRunPredictiveAnimations() {
            return this.mRunPredictiveAnimations;
        }
        
        public boolean willRunSimpleAnimations() {
            return this.mRunSimpleAnimations;
        }
        
        @Retention(RetentionPolicy.SOURCE)
        @interface LayoutState {
        }
    }
    
    public abstract static class ViewCacheExtension
    {
        public abstract View getViewForPositionAndType(final Recycler p0, final int p1, final int p2);
    }
    
    class ViewFlinger implements Runnable
    {
        private boolean mEatRunOnAnimationRequest;
        Interpolator mInterpolator;
        private int mLastFlingX;
        private int mLastFlingY;
        private boolean mReSchedulePostAnimationCallback;
        private OverScroller mScroller;
        
        ViewFlinger() {
            this.mInterpolator = RecyclerView.sQuinticInterpolator;
            this.mEatRunOnAnimationRequest = false;
            this.mReSchedulePostAnimationCallback = false;
            this.mScroller = new OverScroller(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }
        
        private int computeScrollDuration(int n, int a, int n2, int n3) {
            boolean b = false;
            final int abs = Math.abs(n);
            final int abs2 = Math.abs(a);
            if (abs > abs2) {
                b = true;
            }
            n2 = (int)Math.sqrt(n2 * n2 + n3 * n3);
            a = (int)Math.sqrt(n * n + a * a);
            if (!b) {
                n = RecyclerView.this.getHeight();
            }
            else {
                n = RecyclerView.this.getWidth();
            }
            n3 = n / 2;
            final float min = Math.min(1.0f, a * 1.0f / n);
            final float n4 = (float)n3;
            final float n5 = (float)n3;
            final float distanceInfluenceForSnapDuration = this.distanceInfluenceForSnapDuration(min);
            if (n2 <= 0) {
                if (!b) {
                    a = abs2;
                }
                else {
                    a = abs;
                }
                n = (int)((a / (float)n + 1.0f) * 300.0f);
            }
            else {
                n = Math.round(Math.abs((distanceInfluenceForSnapDuration * n5 + n4) / n2) * 1000.0f) * 4;
            }
            return Math.min(n, 2000);
        }
        
        private void disableRunOnAnimationRequests() {
            this.mReSchedulePostAnimationCallback = false;
            this.mEatRunOnAnimationRequest = true;
        }
        
        private float distanceInfluenceForSnapDuration(final float n) {
            return (float)Math.sin((n - 0.5f) * 0.47123894f);
        }
        
        private void enableRunOnAnimationRequests() {
            this.mEatRunOnAnimationRequest = false;
            if (this.mReSchedulePostAnimationCallback) {
                this.postOnAnimation();
            }
        }
        
        public void fling(final int n, final int n2) {
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.fling(0, 0, n, n2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            this.postOnAnimation();
        }
        
        void postOnAnimation() {
            if (!this.mEatRunOnAnimationRequest) {
                RecyclerView.this.removeCallbacks((Runnable)this);
                ViewCompat.postOnAnimation((View)RecyclerView.this, this);
            }
            else {
                this.mReSchedulePostAnimationCallback = true;
            }
        }
        
        @Override
        public void run() {
            if (RecyclerView.this.mLayout != null) {
                this.disableRunOnAnimationRequests();
                RecyclerView.this.consumePendingUpdateOperations();
                final OverScroller mScroller = this.mScroller;
                final SmoothScroller mSmoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
                if (mScroller.computeScrollOffset()) {
                    final int[] access$500 = RecyclerView.this.mScrollConsumed;
                    final int currX = mScroller.getCurrX();
                    final int currY = mScroller.getCurrY();
                    int n = currX - this.mLastFlingX;
                    int n2 = currY - this.mLastFlingY;
                    this.mLastFlingX = currX;
                    this.mLastFlingY = currY;
                    if (RecyclerView.this.dispatchNestedPreScroll(n, n2, access$500, null, 1)) {
                        final int n3 = access$500[0];
                        n2 -= access$500[1];
                        n -= n3;
                    }
                    int n4;
                    int n5;
                    int n6;
                    int n7;
                    if (RecyclerView.this.mAdapter == null) {
                        n4 = 0;
                        n5 = 0;
                        n6 = 0;
                        n7 = 0;
                    }
                    else {
                        RecyclerView.this.eatRequestLayout();
                        RecyclerView.this.onEnterLayoutOrScroll();
                        TraceCompat.beginSection("RV Scroll");
                        RecyclerView.this.fillRemainingScrollValues(RecyclerView.this.mState);
                        int n8;
                        int scrollHorizontallyBy;
                        if (n == 0) {
                            n8 = 0;
                            scrollHorizontallyBy = 0;
                        }
                        else {
                            scrollHorizontallyBy = RecyclerView.this.mLayout.scrollHorizontallyBy(n, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                            n8 = n - scrollHorizontallyBy;
                        }
                        int scrollVerticallyBy;
                        if (n2 == 0) {
                            n4 = 0;
                            scrollVerticallyBy = 0;
                        }
                        else {
                            scrollVerticallyBy = RecyclerView.this.mLayout.scrollVerticallyBy(n2, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                            n4 = n2 - scrollVerticallyBy;
                        }
                        TraceCompat.endSection();
                        RecyclerView.this.repositionShadowingViews();
                        RecyclerView.this.onExitLayoutOrScroll();
                        RecyclerView.this.resumeRequestLayout(false);
                        if (mSmoothScroller != null && !mSmoothScroller.isPendingInitialRun()) {
                            if (!mSmoothScroller.isRunning()) {
                                final int n9 = scrollVerticallyBy;
                                n7 = scrollHorizontallyBy;
                                n6 = n9;
                                n5 = n8;
                            }
                            else {
                                final int itemCount = RecyclerView.this.mState.getItemCount();
                                if (itemCount != 0) {
                                    if (mSmoothScroller.getTargetPosition() < itemCount) {
                                        mSmoothScroller.onAnimation(n - n8, n2 - n4);
                                        final int n10 = scrollVerticallyBy;
                                        n7 = scrollHorizontallyBy;
                                        n6 = n10;
                                        n5 = n8;
                                    }
                                    else {
                                        mSmoothScroller.setTargetPosition(itemCount - 1);
                                        mSmoothScroller.onAnimation(n - n8, n2 - n4);
                                        final int n11 = scrollHorizontallyBy;
                                        n6 = scrollVerticallyBy;
                                        n7 = n11;
                                        n5 = n8;
                                    }
                                }
                                else {
                                    mSmoothScroller.stop();
                                    final int n12 = scrollVerticallyBy;
                                    n7 = scrollHorizontallyBy;
                                    n6 = n12;
                                    n5 = n8;
                                }
                            }
                        }
                        else {
                            final int n13 = scrollVerticallyBy;
                            n7 = scrollHorizontallyBy;
                            n6 = n13;
                            n5 = n8;
                        }
                    }
                    if (!RecyclerView.this.mItemDecorations.isEmpty()) {
                        RecyclerView.this.invalidate();
                    }
                    if (RecyclerView.this.getOverScrollMode() != 2) {
                        RecyclerView.this.considerReleasingGlowsOnScroll(n, n2);
                    }
                    if (!RecyclerView.this.dispatchNestedScroll(n7, n6, n5, n4, null, 1)) {
                        if (n5 != 0 || n4 != 0) {
                            int n14 = (int)mScroller.getCurrVelocity();
                            int n15 = 0;
                            if (n5 != currX) {
                                if (n5 >= 0) {
                                    if (n5 <= 0) {
                                        n15 = 0;
                                    }
                                    else {
                                        n15 = n14;
                                    }
                                }
                                else {
                                    n15 = -n14;
                                }
                            }
                            if (n4 == currY) {
                                n14 = 0;
                            }
                            else if (n4 >= 0) {
                                if (n4 <= 0) {
                                    n14 = 0;
                                }
                            }
                            else {
                                n14 = -n14;
                            }
                            if (RecyclerView.this.getOverScrollMode() != 2) {
                                RecyclerView.this.absorbGlows(n15, n14);
                            }
                            if (n15 != 0 || n5 == currX || mScroller.getFinalX() == 0) {
                                if (n14 != 0 || n4 == currY || mScroller.getFinalY() == 0) {
                                    mScroller.abortAnimation();
                                }
                            }
                        }
                    }
                    if (n7 != 0 || n6 != 0) {
                        RecyclerView.this.dispatchOnScrolled(n7, n6);
                    }
                    if (!RecyclerView.access$700(RecyclerView.this)) {
                        RecyclerView.this.invalidate();
                    }
                    final boolean b = n2 != 0 && RecyclerView.this.mLayout.canScrollVertically() && n6 == n2;
                    boolean b2;
                    if (n != 0 && RecyclerView.this.mLayout.canScrollHorizontally() && n7 == n) {
                        b2 = true;
                    }
                    else {
                        b2 = false;
                    }
                    boolean b3 = false;
                    Label_0246: {
                        if (n != 0 || n2 != 0) {
                            if (!b2 && !b) {
                                b3 = false;
                                break Label_0246;
                            }
                        }
                        b3 = true;
                    }
                    if (!mScroller.isFinished() && (b3 || RecyclerView.this.hasNestedScrollingParent(1))) {
                        this.postOnAnimation();
                        if (RecyclerView.this.mGapWorker != null) {
                            RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, n, n2);
                        }
                    }
                    else {
                        RecyclerView.this.setScrollState(0);
                        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
                            RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
                        }
                        RecyclerView.this.stopNestedScroll(1);
                    }
                }
                if (mSmoothScroller != null) {
                    if (mSmoothScroller.isPendingInitialRun()) {
                        mSmoothScroller.onAnimation(0, 0);
                    }
                    if (!this.mReSchedulePostAnimationCallback) {
                        mSmoothScroller.stop();
                    }
                }
                this.enableRunOnAnimationRequests();
                return;
            }
            this.stop();
        }
        
        public void smoothScrollBy(final int n, final int n2) {
            this.smoothScrollBy(n, n2, 0, 0);
        }
        
        public void smoothScrollBy(final int n, final int n2, final int n3) {
            this.smoothScrollBy(n, n2, n3, RecyclerView.sQuinticInterpolator);
        }
        
        public void smoothScrollBy(final int n, final int n2, final int n3, final int n4) {
            this.smoothScrollBy(n, n2, this.computeScrollDuration(n, n2, n3, n4));
        }
        
        public void smoothScrollBy(final int n, final int n2, final int n3, final Interpolator mInterpolator) {
            if (this.mInterpolator != mInterpolator) {
                this.mInterpolator = mInterpolator;
                this.mScroller = new OverScroller(RecyclerView.this.getContext(), mInterpolator);
            }
            RecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            this.mScroller.startScroll(0, 0, n, n2, n3);
            if (Build$VERSION.SDK_INT < 23) {
                this.mScroller.computeScrollOffset();
            }
            this.postOnAnimation();
        }
        
        public void smoothScrollBy(final int n, final int n2, Interpolator sQuinticInterpolator) {
            final int computeScrollDuration = this.computeScrollDuration(n, n2, 0, 0);
            if (sQuinticInterpolator == null) {
                sQuinticInterpolator = RecyclerView.sQuinticInterpolator;
            }
            this.smoothScrollBy(n, n2, computeScrollDuration, sQuinticInterpolator);
        }
        
        public void stop() {
            RecyclerView.this.removeCallbacks((Runnable)this);
            this.mScroller.abortAnimation();
        }
    }
    
    public abstract static class ViewHolder
    {
        static final int FLAG_ADAPTER_FULLUPDATE = 1024;
        static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
        static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
        static final int FLAG_BOUND = 1;
        static final int FLAG_IGNORE = 128;
        static final int FLAG_INVALID = 4;
        static final int FLAG_MOVED = 2048;
        static final int FLAG_NOT_RECYCLABLE = 16;
        static final int FLAG_REMOVED = 8;
        static final int FLAG_RETURNED_FROM_SCRAP = 32;
        static final int FLAG_SET_A11Y_ITEM_DELEGATE = 16384;
        static final int FLAG_TMP_DETACHED = 256;
        static final int FLAG_UPDATE = 2;
        private static final List<Object> FULLUPDATE_PAYLOADS;
        static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
        public final View itemView;
        private int mFlags;
        private boolean mInChangeScrap;
        private int mIsRecyclableCount;
        long mItemId;
        int mItemViewType;
        WeakReference<RecyclerView> mNestedRecyclerView;
        int mOldPosition;
        RecyclerView mOwnerRecyclerView;
        List<Object> mPayloads;
        @VisibleForTesting
        int mPendingAccessibilityState;
        int mPosition;
        int mPreLayoutPosition;
        private Recycler mScrapContainer;
        ViewHolder mShadowedHolder;
        ViewHolder mShadowingHolder;
        List<Object> mUnmodifiedPayloads;
        private int mWasImportantForAccessibilityBeforeHidden;
        
        static {
            FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
        }
        
        public ViewHolder(final View itemView) {
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1L;
            this.mItemViewType = -1;
            this.mPreLayoutPosition = -1;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            this.mPayloads = null;
            this.mUnmodifiedPayloads = null;
            this.mIsRecyclableCount = 0;
            this.mScrapContainer = null;
            this.mInChangeScrap = false;
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            if (itemView != null) {
                this.itemView = itemView;
                return;
            }
            throw new IllegalArgumentException("itemView may not be null");
        }
        
        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                this.mPayloads = new ArrayList<Object>();
                this.mUnmodifiedPayloads = Collections.unmodifiableList((List<?>)this.mPayloads);
            }
        }
        
        private boolean doesTransientStatePreventRecycling() {
            boolean b = false;
            if ((this.mFlags & 0x10) == 0x0 && ViewCompat.hasTransientState(this.itemView)) {
                b = true;
            }
            return b;
        }
        
        private void onEnteredHiddenState(final RecyclerView recyclerView) {
            this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
            recyclerView.setChildImportantForAccessibilityInternal(this, 4);
        }
        
        private void onLeftHiddenState(final RecyclerView recyclerView) {
            recyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = 0;
        }
        
        private boolean shouldBeKeptAsChild() {
            boolean b = false;
            if ((this.mFlags & 0x10) != 0x0) {
                b = true;
            }
            return b;
        }
        
        void addChangePayload(final Object o) {
            if (o != null) {
                if ((this.mFlags & 0x400) == 0x0) {
                    this.createPayloadsIfNeeded();
                    this.mPayloads.add(o);
                }
            }
            else {
                this.addFlags(1024);
            }
        }
        
        void addFlags(final int n) {
            this.mFlags |= n;
        }
        
        void clearOldPosition() {
            this.mOldPosition = -1;
            this.mPreLayoutPosition = -1;
        }
        
        void clearPayload() {
            if (this.mPayloads != null) {
                this.mPayloads.clear();
            }
            this.mFlags &= 0xFFFFFBFF;
        }
        
        void clearReturnedFromScrapFlag() {
            this.mFlags &= 0xFFFFFFDF;
        }
        
        void clearTmpDetachFlag() {
            this.mFlags &= 0xFFFFFEFF;
        }
        
        void flagRemovedAndOffsetPosition(final int mPosition, final int n, final boolean b) {
            this.addFlags(8);
            this.offsetPosition(n, b);
            this.mPosition = mPosition;
        }
        
        public final int getAdapterPosition() {
            if (this.mOwnerRecyclerView != null) {
                return this.mOwnerRecyclerView.getAdapterPositionFor(this);
            }
            return -1;
        }
        
        public final long getItemId() {
            return this.mItemId;
        }
        
        public final int getItemViewType() {
            return this.mItemViewType;
        }
        
        public final int getLayoutPosition() {
            int n;
            if (this.mPreLayoutPosition != -1) {
                n = this.mPreLayoutPosition;
            }
            else {
                n = this.mPosition;
            }
            return n;
        }
        
        public final int getOldPosition() {
            return this.mOldPosition;
        }
        
        @Deprecated
        public final int getPosition() {
            int n;
            if (this.mPreLayoutPosition != -1) {
                n = this.mPreLayoutPosition;
            }
            else {
                n = this.mPosition;
            }
            return n;
        }
        
        List<Object> getUnmodifiedPayloads() {
            if ((this.mFlags & 0x400) != 0x0) {
                return ViewHolder.FULLUPDATE_PAYLOADS;
            }
            if (this.mPayloads != null && this.mPayloads.size() != 0) {
                return this.mUnmodifiedPayloads;
            }
            return ViewHolder.FULLUPDATE_PAYLOADS;
        }
        
        boolean hasAnyOfTheFlags(final int n) {
            boolean b = false;
            if ((this.mFlags & n) != 0x0) {
                b = true;
            }
            return b;
        }
        
        boolean isAdapterPositionUnknown() {
            boolean b = false;
            if ((this.mFlags & 0x200) != 0x0 || this.isInvalid()) {
                b = true;
            }
            return b;
        }
        
        boolean isBound() {
            boolean b = false;
            if ((this.mFlags & 0x1) != 0x0) {
                b = true;
            }
            return b;
        }
        
        boolean isInvalid() {
            boolean b = false;
            if ((this.mFlags & 0x4) != 0x0) {
                b = true;
            }
            return b;
        }
        
        public final boolean isRecyclable() {
            boolean b = false;
            if ((this.mFlags & 0x10) == 0x0 && !ViewCompat.hasTransientState(this.itemView)) {
                b = true;
            }
            return b;
        }
        
        boolean isRemoved() {
            boolean b = false;
            if ((this.mFlags & 0x8) != 0x0) {
                b = true;
            }
            return b;
        }
        
        boolean isScrap() {
            return this.mScrapContainer != null;
        }
        
        boolean isTmpDetached() {
            boolean b = false;
            if ((this.mFlags & 0x100) != 0x0) {
                b = true;
            }
            return b;
        }
        
        boolean isUpdated() {
            boolean b = false;
            if ((this.mFlags & 0x2) != 0x0) {
                b = true;
            }
            return b;
        }
        
        boolean needsUpdate() {
            boolean b = false;
            if ((this.mFlags & 0x2) != 0x0) {
                b = true;
            }
            return b;
        }
        
        void offsetPosition(final int n, final boolean b) {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == -1) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (b) {
                this.mPreLayoutPosition += n;
            }
            this.mPosition += n;
            if (this.itemView.getLayoutParams() != null) {
                ((LayoutParams)this.itemView.getLayoutParams()).mInsetsDirty = true;
            }
        }
        
        void resetInternal() {
            this.mFlags = 0;
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1L;
            this.mPreLayoutPosition = -1;
            this.mIsRecyclableCount = 0;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            this.clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            RecyclerView.clearNestedRecyclerViewIfNotNested(this);
        }
        
        void saveOldPosition() {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
        }
        
        void setFlags(final int n, final int n2) {
            this.mFlags = ((this.mFlags & ~n2) | (n & n2));
        }
        
        public final void setIsRecyclable(final boolean b) {
            int mIsRecyclableCount;
            if (!b) {
                mIsRecyclableCount = this.mIsRecyclableCount + 1;
            }
            else {
                mIsRecyclableCount = this.mIsRecyclableCount - 1;
            }
            this.mIsRecyclableCount = mIsRecyclableCount;
            if (this.mIsRecyclableCount >= 0) {
                if (!b && this.mIsRecyclableCount == 1) {
                    this.mFlags |= 0x10;
                }
                else if (b && this.mIsRecyclableCount == 0) {
                    this.mFlags &= 0xFFFFFFEF;
                }
            }
            else {
                this.mIsRecyclableCount = 0;
                Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
            }
        }
        
        void setScrapContainer(final Recycler mScrapContainer, final boolean mInChangeScrap) {
            this.mScrapContainer = mScrapContainer;
            this.mInChangeScrap = mInChangeScrap;
        }
        
        boolean shouldIgnore() {
            boolean b = false;
            if ((this.mFlags & 0x80) != 0x0) {
                b = true;
            }
            return b;
        }
        
        void stopIgnoring() {
            this.mFlags &= 0xFFFFFF7F;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ViewHolder{" + Integer.toHexString(this.hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
            if (this.isScrap()) {
                final StringBuilder append = sb.append(" scrap ");
                String str;
                if (!this.mInChangeScrap) {
                    str = "[attachedScrap]";
                }
                else {
                    str = "[changeScrap]";
                }
                append.append(str);
            }
            if (this.isInvalid()) {
                sb.append(" invalid");
            }
            if (!this.isBound()) {
                sb.append(" unbound");
            }
            if (this.needsUpdate()) {
                sb.append(" update");
            }
            if (this.isRemoved()) {
                sb.append(" removed");
            }
            if (this.shouldIgnore()) {
                sb.append(" ignored");
            }
            if (this.isTmpDetached()) {
                sb.append(" tmpDetached");
            }
            if (!this.isRecyclable()) {
                sb.append(" not recyclable(" + this.mIsRecyclableCount + ")");
            }
            if (this.isAdapterPositionUnknown()) {
                sb.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                sb.append(" no parent");
            }
            sb.append("}");
            return sb.toString();
        }
        
        void unScrap() {
            this.mScrapContainer.unscrapView(this);
        }
        
        boolean wasReturnedFromScrap() {
            boolean b = false;
            if ((this.mFlags & 0x20) != 0x0) {
                b = true;
            }
            return b;
        }
    }
}
