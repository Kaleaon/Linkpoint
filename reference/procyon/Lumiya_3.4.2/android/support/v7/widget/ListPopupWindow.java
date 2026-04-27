// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.MotionEvent;
import android.widget.AbsListView;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.widget.PopupWindow$OnDismissListener;
import android.view.KeyEvent$DispatcherState;
import android.view.KeyEvent;
import android.support.annotation.RestrictTo;
import android.widget.ListView;
import android.view.View$OnTouchListener;
import android.view.ViewParent;
import android.view.View$MeasureSpec;
import android.view.ViewGroup$LayoutParams;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.AbsListView$OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout$LayoutParams;
import android.view.ViewGroup;
import android.content.res.TypedArray;
import android.support.annotation.StyleRes;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.appcompat.R;
import android.support.annotation.NonNull;
import android.widget.PopupWindow;
import android.database.DataSetObserver;
import android.widget.AdapterView$OnItemSelectedListener;
import android.widget.AdapterView$OnItemClickListener;
import android.os.Handler;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.content.Context;
import android.widget.ListAdapter;
import java.lang.reflect.Method;
import android.support.v7.view.menu.ShowableListMenu;

public class ListPopupWindow implements ShowableListMenu
{
    private static final boolean DEBUG = false;
    static final int EXPAND_LIST_TIMEOUT = 250;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    public static final int MATCH_PARENT = -1;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;
    private static final String TAG = "ListPopupWindow";
    public static final int WRAP_CONTENT = -2;
    private static Method sClipToWindowEnabledMethod;
    private static Method sGetMaxAvailableHeightMethod;
    private static Method sSetEpicenterBoundsMethod;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownGravity;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private int mDropDownWindowLayoutType;
    private Rect mEpicenterBounds;
    private boolean mForceIgnoreOutsideTouch;
    final Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private boolean mIsAnimatedFromAnchor;
    private AdapterView$OnItemClickListener mItemClickListener;
    private AdapterView$OnItemSelectedListener mItemSelectedListener;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    private boolean mOverlapAnchor;
    private boolean mOverlapAnchorSet;
    PopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private Runnable mShowDropDownRunnable;
    private final Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: ldc             "setClipToScreenEnabled"
        //     4: iconst_1       
        //     5: anewarray       Ljava/lang/Class;
        //     8: dup            
        //     9: iconst_0       
        //    10: getstatic       java/lang/Boolean.TYPE:Ljava/lang/Class;
        //    13: aastore        
        //    14: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    17: putstatic       android/support/v7/widget/ListPopupWindow.sClipToWindowEnabledMethod:Ljava/lang/reflect/Method;
        //    20: ldc             Landroid/widget/PopupWindow;.class
        //    22: ldc             "getMaxAvailableHeight"
        //    24: iconst_3       
        //    25: anewarray       Ljava/lang/Class;
        //    28: dup            
        //    29: iconst_0       
        //    30: ldc             Landroid/view/View;.class
        //    32: aastore        
        //    33: dup            
        //    34: iconst_1       
        //    35: getstatic       java/lang/Integer.TYPE:Ljava/lang/Class;
        //    38: aastore        
        //    39: dup            
        //    40: iconst_2       
        //    41: getstatic       java/lang/Boolean.TYPE:Ljava/lang/Class;
        //    44: aastore        
        //    45: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    48: putstatic       android/support/v7/widget/ListPopupWindow.sGetMaxAvailableHeightMethod:Ljava/lang/reflect/Method;
        //    51: ldc             Landroid/widget/PopupWindow;.class
        //    53: ldc             "setEpicenterBounds"
        //    55: iconst_1       
        //    56: anewarray       Ljava/lang/Class;
        //    59: dup            
        //    60: iconst_0       
        //    61: ldc             Landroid/graphics/Rect;.class
        //    63: aastore        
        //    64: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    67: putstatic       android/support/v7/widget/ListPopupWindow.sSetEpicenterBoundsMethod:Ljava/lang/reflect/Method;
        //    70: return         
        //    71: astore_0       
        //    72: ldc             "ListPopupWindow"
        //    74: ldc             "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well."
        //    76: invokestatic    android/util/Log.i:(Ljava/lang/String;Ljava/lang/String;)I
        //    79: pop            
        //    80: goto            20
        //    83: astore_0       
        //    84: ldc             "ListPopupWindow"
        //    86: ldc             "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well."
        //    88: invokestatic    android/util/Log.i:(Ljava/lang/String;Ljava/lang/String;)I
        //    91: pop            
        //    92: goto            51
        //    95: astore_0       
        //    96: ldc             "ListPopupWindow"
        //    98: ldc             "Could not find method setEpicenterBounds(Rect) on PopupWindow. Oh well."
        //   100: invokestatic    android/util/Log.i:(Ljava/lang/String;Ljava/lang/String;)I
        //   103: pop            
        //   104: goto            70
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  0      20     71     83     Ljava/lang/NoSuchMethodException;
        //  20     51     83     95     Ljava/lang/NoSuchMethodException;
        //  51     70     95     107    Ljava/lang/NoSuchMethodException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 57 out of bounds for length 57
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
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
    
    public ListPopupWindow(@NonNull final Context context) {
        this(context, null, R.attr.listPopupWindowStyle);
    }
    
    public ListPopupWindow(@NonNull final Context context, @Nullable final AttributeSet set) {
        this(context, set, R.attr.listPopupWindowStyle);
    }
    
    public ListPopupWindow(@NonNull final Context context, @Nullable final AttributeSet set, @AttrRes final int n) {
        this(context, set, n, 0);
    }
    
    public ListPopupWindow(@NonNull final Context mContext, @Nullable final AttributeSet set, @AttrRes final int n, @StyleRes final int n2) {
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownWindowLayoutType = 1002;
        this.mIsAnimatedFromAnchor = true;
        this.mDropDownGravity = 0;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mListItemExpandMaximum = Integer.MAX_VALUE;
        this.mPromptPosition = 0;
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mTempRect = new Rect();
        this.mContext = mContext;
        this.mHandler = new Handler(mContext.getMainLooper());
        final TypedArray obtainStyledAttributes = mContext.obtainStyledAttributes(set, R.styleable.ListPopupWindow, n, n2);
        this.mDropDownHorizontalOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
        this.mDropDownVerticalOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
        if (this.mDropDownVerticalOffset != 0) {
            this.mDropDownVerticalOffsetSet = true;
        }
        obtainStyledAttributes.recycle();
        (this.mPopup = new AppCompatPopupWindow(mContext, set, n, n2)).setInputMethodMode(1);
    }
    
    private int buildDropDown() {
        final boolean b = true;
        int n;
        if (this.mDropDownList != null) {
            final ViewGroup viewGroup = (ViewGroup)this.mPopup.getContentView();
            final View mPromptView = this.mPromptView;
            if (mPromptView == null) {
                n = 0;
            }
            else {
                final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)mPromptView.getLayoutParams();
                n = mPromptView.getMeasuredHeight() + linearLayout$LayoutParams.topMargin + linearLayout$LayoutParams.bottomMargin;
            }
        }
        else {
            final Context mContext = this.mContext;
            this.mShowDropDownRunnable = new Runnable() {
                @Override
                public void run() {
                    final View anchorView = ListPopupWindow.this.getAnchorView();
                    if (anchorView != null && anchorView.getWindowToken() != null) {
                        ListPopupWindow.this.show();
                    }
                }
            };
            this.mDropDownList = this.createDropDownListView(mContext, !this.mModal);
            if (this.mDropDownListHighlight != null) {
                this.mDropDownList.setSelector(this.mDropDownListHighlight);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener((AdapterView$OnItemSelectedListener)new AdapterView$OnItemSelectedListener() {
                public void onItemSelected(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                    if (n != -1) {
                        final DropDownListView mDropDownList = ListPopupWindow.this.mDropDownList;
                        if (mDropDownList != null) {
                            mDropDownList.setListSelectionHidden(false);
                        }
                    }
                }
                
                public void onNothingSelected(final AdapterView<?> adapterView) {
                }
            });
            this.mDropDownList.setOnScrollListener((AbsListView$OnScrollListener)this.mScrollListener);
            if (this.mItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(this.mItemSelectedListener);
            }
            final DropDownListView mDropDownList = this.mDropDownList;
            final View mPromptView2 = this.mPromptView;
            Object contentView;
            if (mPromptView2 == null) {
                n = 0;
                contentView = mDropDownList;
            }
            else {
                contentView = new LinearLayout(mContext);
                ((LinearLayout)contentView).setOrientation(1);
                final LinearLayout$LayoutParams linearLayout$LayoutParams2 = new LinearLayout$LayoutParams(-1, 0, 1.0f);
                switch (this.mPromptPosition) {
                    default: {
                        Log.e("ListPopupWindow", "Invalid hint position " + this.mPromptPosition);
                        break;
                    }
                    case 1: {
                        ((LinearLayout)contentView).addView((View)mDropDownList, (ViewGroup$LayoutParams)linearLayout$LayoutParams2);
                        ((LinearLayout)contentView).addView(mPromptView2);
                        break;
                    }
                    case 0: {
                        ((LinearLayout)contentView).addView(mPromptView2);
                        ((LinearLayout)contentView).addView((View)mDropDownList, (ViewGroup$LayoutParams)linearLayout$LayoutParams2);
                        break;
                    }
                }
                int mDropDownWidth;
                int n2;
                if (this.mDropDownWidth < 0) {
                    mDropDownWidth = 0;
                    n2 = 0;
                }
                else {
                    mDropDownWidth = this.mDropDownWidth;
                    n2 = Integer.MIN_VALUE;
                }
                mPromptView2.measure(View$MeasureSpec.makeMeasureSpec(mDropDownWidth, n2), 0);
                final LinearLayout$LayoutParams linearLayout$LayoutParams3 = (LinearLayout$LayoutParams)mPromptView2.getLayoutParams();
                n = linearLayout$LayoutParams3.bottomMargin + (mPromptView2.getMeasuredHeight() + linearLayout$LayoutParams3.topMargin);
            }
            this.mPopup.setContentView((View)contentView);
        }
        final Drawable background = this.mPopup.getBackground();
        int n3;
        if (background == null) {
            this.mTempRect.setEmpty();
            n3 = 0;
        }
        else {
            background.getPadding(this.mTempRect);
            n3 = this.mTempRect.top + this.mTempRect.bottom;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -this.mTempRect.top;
            }
        }
        boolean b2 = b;
        if (this.mPopup.getInputMethodMode() != 2) {
            b2 = false;
        }
        final int maxAvailableHeight = this.getMaxAvailableHeight(this.getAnchorView(), this.mDropDownVerticalOffset, b2);
        if (!this.mDropDownAlwaysVisible && this.mDropDownHeight != -1) {
            int n4 = 0;
            switch (this.mDropDownWidth) {
                default: {
                    n4 = View$MeasureSpec.makeMeasureSpec(this.mDropDownWidth, 1073741824);
                    break;
                }
                case -2: {
                    n4 = View$MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
                    break;
                }
                case -1: {
                    n4 = View$MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
                    break;
                }
            }
            final int measureHeightOfChildrenCompat = this.mDropDownList.measureHeightOfChildrenCompat(n4, 0, -1, maxAvailableHeight - n, -1);
            if (measureHeightOfChildrenCompat > 0) {
                n += this.mDropDownList.getPaddingTop() + this.mDropDownList.getPaddingBottom() + n3;
            }
            return measureHeightOfChildrenCompat + n;
        }
        return maxAvailableHeight + n3;
    }
    
    private int getMaxAvailableHeight(final View view, final int i, final boolean b) {
        if (ListPopupWindow.sGetMaxAvailableHeightMethod != null) {
            try {
                return (int)ListPopupWindow.sGetMaxAvailableHeightMethod.invoke(this.mPopup, view, i, b);
            }
            catch (final Exception ex) {
                Log.i("ListPopupWindow", "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
            }
        }
        return this.mPopup.getMaxAvailableHeight(view, i);
    }
    
    private static boolean isConfirmKey(final int n) {
        return n == 66 || n == 23;
    }
    
    private void removePromptView() {
        if (this.mPromptView != null) {
            final ViewParent parent = this.mPromptView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup)parent).removeView(this.mPromptView);
            }
        }
    }
    
    private void setPopupClipToScreenEnabled(final boolean b) {
        if (ListPopupWindow.sClipToWindowEnabledMethod != null) {
            try {
                ListPopupWindow.sClipToWindowEnabledMethod.invoke(this.mPopup, b);
            }
            catch (final Exception ex) {
                Log.i("ListPopupWindow", "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
            }
        }
    }
    
    public void clearListSelection() {
        final DropDownListView mDropDownList = this.mDropDownList;
        if (mDropDownList != null) {
            mDropDownList.setListSelectionHidden(true);
            mDropDownList.requestLayout();
        }
    }
    
    public View$OnTouchListener createDragToOpenListener(final View view) {
        return (View$OnTouchListener)new ForwardingListener(view) {
            @Override
            public ListPopupWindow getPopup() {
                return ListPopupWindow.this;
            }
        };
    }
    
    @NonNull
    DropDownListView createDropDownListView(final Context context, final boolean b) {
        return new DropDownListView(context, b);
    }
    
    @Override
    public void dismiss() {
        this.mPopup.dismiss();
        this.removePromptView();
        this.mPopup.setContentView((View)null);
        this.mDropDownList = null;
        this.mHandler.removeCallbacks((Runnable)this.mResizePopupRunnable);
    }
    
    @Nullable
    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }
    
    @StyleRes
    public int getAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }
    
    @Nullable
    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }
    
    public int getHeight() {
        return this.mDropDownHeight;
    }
    
    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }
    
    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }
    
    @Nullable
    @Override
    public ListView getListView() {
        return this.mDropDownList;
    }
    
    public int getPromptPosition() {
        return this.mPromptPosition;
    }
    
    @Nullable
    public Object getSelectedItem() {
        if (this.isShowing()) {
            return this.mDropDownList.getSelectedItem();
        }
        return null;
    }
    
    public long getSelectedItemId() {
        if (this.isShowing()) {
            return this.mDropDownList.getSelectedItemId();
        }
        return Long.MIN_VALUE;
    }
    
    public int getSelectedItemPosition() {
        if (this.isShowing()) {
            return this.mDropDownList.getSelectedItemPosition();
        }
        return -1;
    }
    
    @Nullable
    public View getSelectedView() {
        if (this.isShowing()) {
            return this.mDropDownList.getSelectedView();
        }
        return null;
    }
    
    public int getSoftInputMode() {
        return this.mPopup.getSoftInputMode();
    }
    
    public int getVerticalOffset() {
        if (this.mDropDownVerticalOffsetSet) {
            return this.mDropDownVerticalOffset;
        }
        return 0;
    }
    
    public int getWidth() {
        return this.mDropDownWidth;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public boolean isDropDownAlwaysVisible() {
        return this.mDropDownAlwaysVisible;
    }
    
    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }
    
    public boolean isModal() {
        return this.mModal;
    }
    
    @Override
    public boolean isShowing() {
        return this.mPopup.isShowing();
    }
    
    public boolean onKeyDown(final int n, @NonNull final KeyEvent keyEvent) {
        if (this.isShowing() && n != 62) {
            if (this.mDropDownList.getSelectedItemPosition() >= 0 || !isConfirmKey(n)) {
                final int selectedItemPosition = this.mDropDownList.getSelectedItemPosition();
                boolean b;
                if (this.mPopup.isAboveAnchor()) {
                    b = false;
                }
                else {
                    b = true;
                }
                final ListAdapter mAdapter = this.mAdapter;
                int n2 = Integer.MAX_VALUE;
                int n3 = Integer.MIN_VALUE;
                if (mAdapter != null) {
                    final boolean allItemsEnabled = mAdapter.areAllItemsEnabled();
                    int lookForSelectablePosition;
                    if (!allItemsEnabled) {
                        lookForSelectablePosition = this.mDropDownList.lookForSelectablePosition(0, true);
                    }
                    else {
                        lookForSelectablePosition = 0;
                    }
                    if (!allItemsEnabled) {
                        final int lookForSelectablePosition2 = this.mDropDownList.lookForSelectablePosition(mAdapter.getCount() - 1, false);
                        n2 = lookForSelectablePosition;
                        n3 = lookForSelectablePosition2;
                    }
                    else {
                        final int n4 = mAdapter.getCount() - 1;
                        n2 = lookForSelectablePosition;
                        n3 = n4;
                    }
                }
                if ((b && n == 19 && selectedItemPosition <= n2) || (!b && n == 20 && selectedItemPosition >= n3)) {
                    this.clearListSelection();
                    this.mPopup.setInputMethodMode(1);
                    this.show();
                    return true;
                }
                this.mDropDownList.setListSelectionHidden(false);
                if (!this.mDropDownList.onKeyDown(n, keyEvent)) {
                    if (b && n == 20) {
                        if (selectedItemPosition == n3) {
                            return true;
                        }
                    }
                    else if (!b && n == 19 && selectedItemPosition == n2) {
                        return true;
                    }
                }
                else {
                    this.mPopup.setInputMethodMode(2);
                    this.mDropDownList.requestFocusFromTouch();
                    this.show();
                    switch (n) {
                        case 19:
                        case 20:
                        case 23:
                        case 66: {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean onKeyPreIme(final int n, @NonNull final KeyEvent keyEvent) {
        if (n == 4 && this.isShowing()) {
            final View mDropDownAnchorView = this.mDropDownAnchorView;
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                final KeyEvent$DispatcherState keyDispatcherState = mDropDownAnchorView.getKeyDispatcherState();
                if (keyDispatcherState != null) {
                    keyDispatcherState.startTracking(keyEvent, (Object)this);
                }
                return true;
            }
            if (keyEvent.getAction() == 1) {
                final KeyEvent$DispatcherState keyDispatcherState2 = mDropDownAnchorView.getKeyDispatcherState();
                if (keyDispatcherState2 != null) {
                    keyDispatcherState2.handleUpEvent(keyEvent);
                }
                if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                    this.dismiss();
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean onKeyUp(final int n, @NonNull final KeyEvent keyEvent) {
        if (this.isShowing() && this.mDropDownList.getSelectedItemPosition() >= 0) {
            final boolean onKeyUp = this.mDropDownList.onKeyUp(n, keyEvent);
            if (onKeyUp && isConfirmKey(n)) {
                this.dismiss();
            }
            return onKeyUp;
        }
        return false;
    }
    
    public boolean performItemClick(final int n) {
        if (!this.isShowing()) {
            return false;
        }
        if (this.mItemClickListener != null) {
            final DropDownListView mDropDownList = this.mDropDownList;
            this.mItemClickListener.onItemClick((AdapterView)mDropDownList, mDropDownList.getChildAt(n - mDropDownList.getFirstVisiblePosition()), n, mDropDownList.getAdapter().getItemId(n));
        }
        return true;
    }
    
    public void postShow() {
        this.mHandler.post(this.mShowDropDownRunnable);
    }
    
    public void setAdapter(@Nullable final ListAdapter mAdapter) {
        if (this.mObserver != null) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(this.mObserver);
            }
        }
        else {
            this.mObserver = new PopupDataSetObserver();
        }
        this.mAdapter = mAdapter;
        if (this.mAdapter != null) {
            mAdapter.registerDataSetObserver(this.mObserver);
        }
        if (this.mDropDownList != null) {
            this.mDropDownList.setAdapter(this.mAdapter);
        }
    }
    
    public void setAnchorView(@Nullable final View mDropDownAnchorView) {
        this.mDropDownAnchorView = mDropDownAnchorView;
    }
    
    public void setAnimationStyle(@StyleRes final int animationStyle) {
        this.mPopup.setAnimationStyle(animationStyle);
    }
    
    public void setBackgroundDrawable(@Nullable final Drawable backgroundDrawable) {
        this.mPopup.setBackgroundDrawable(backgroundDrawable);
    }
    
    public void setContentWidth(final int width) {
        final Drawable background = this.mPopup.getBackground();
        if (background == null) {
            this.setWidth(width);
        }
        else {
            background.getPadding(this.mTempRect);
            this.mDropDownWidth = this.mTempRect.left + this.mTempRect.right + width;
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setDropDownAlwaysVisible(final boolean mDropDownAlwaysVisible) {
        this.mDropDownAlwaysVisible = mDropDownAlwaysVisible;
    }
    
    public void setDropDownGravity(final int mDropDownGravity) {
        this.mDropDownGravity = mDropDownGravity;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setEpicenterBounds(final Rect mEpicenterBounds) {
        this.mEpicenterBounds = mEpicenterBounds;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setForceIgnoreOutsideTouch(final boolean mForceIgnoreOutsideTouch) {
        this.mForceIgnoreOutsideTouch = mForceIgnoreOutsideTouch;
    }
    
    public void setHeight(final int mDropDownHeight) {
        if (mDropDownHeight < 0 && -2 != mDropDownHeight && -1 != mDropDownHeight) {
            throw new IllegalArgumentException("Invalid height. Must be a positive value, MATCH_PARENT, or WRAP_CONTENT.");
        }
        this.mDropDownHeight = mDropDownHeight;
    }
    
    public void setHorizontalOffset(final int mDropDownHorizontalOffset) {
        this.mDropDownHorizontalOffset = mDropDownHorizontalOffset;
    }
    
    public void setInputMethodMode(final int inputMethodMode) {
        this.mPopup.setInputMethodMode(inputMethodMode);
    }
    
    void setListItemExpandMax(final int mListItemExpandMaximum) {
        this.mListItemExpandMaximum = mListItemExpandMaximum;
    }
    
    public void setListSelector(final Drawable mDropDownListHighlight) {
        this.mDropDownListHighlight = mDropDownListHighlight;
    }
    
    public void setModal(final boolean b) {
        this.mModal = b;
        this.mPopup.setFocusable(b);
    }
    
    public void setOnDismissListener(@Nullable final PopupWindow$OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }
    
    public void setOnItemClickListener(@Nullable final AdapterView$OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public void setOnItemSelectedListener(@Nullable final AdapterView$OnItemSelectedListener mItemSelectedListener) {
        this.mItemSelectedListener = mItemSelectedListener;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setOverlapAnchor(final boolean mOverlapAnchor) {
        this.mOverlapAnchorSet = true;
        this.mOverlapAnchor = mOverlapAnchor;
    }
    
    public void setPromptPosition(final int mPromptPosition) {
        this.mPromptPosition = mPromptPosition;
    }
    
    public void setPromptView(@Nullable final View mPromptView) {
        final boolean showing = this.isShowing();
        if (showing) {
            this.removePromptView();
        }
        this.mPromptView = mPromptView;
        if (showing) {
            this.show();
        }
    }
    
    public void setSelection(final int selection) {
        final DropDownListView mDropDownList = this.mDropDownList;
        if (this.isShowing() && mDropDownList != null) {
            mDropDownList.setListSelectionHidden(false);
            mDropDownList.setSelection(selection);
            if (mDropDownList.getChoiceMode() != 0) {
                mDropDownList.setItemChecked(selection, true);
            }
        }
    }
    
    public void setSoftInputMode(final int softInputMode) {
        this.mPopup.setSoftInputMode(softInputMode);
    }
    
    public void setVerticalOffset(final int mDropDownVerticalOffset) {
        this.mDropDownVerticalOffset = mDropDownVerticalOffset;
        this.mDropDownVerticalOffsetSet = true;
    }
    
    public void setWidth(final int mDropDownWidth) {
        this.mDropDownWidth = mDropDownWidth;
    }
    
    public void setWindowLayoutType(final int mDropDownWindowLayoutType) {
        this.mDropDownWindowLayoutType = mDropDownWindowLayoutType;
    }
    
    @Override
    public void show() {
        final boolean b = true;
        final int n = -1;
        boolean outsideTouchable = false;
        int height = this.buildDropDown();
        final boolean inputMethodNotNeeded = this.isInputMethodNotNeeded();
        PopupWindowCompat.setWindowLayoutType(this.mPopup, this.mDropDownWindowLayoutType);
        if (!this.mPopup.isShowing()) {
            int width;
            if (this.mDropDownWidth != -1) {
                if (this.mDropDownWidth != -2) {
                    width = this.mDropDownWidth;
                }
                else {
                    width = this.getAnchorView().getWidth();
                }
            }
            else {
                width = -1;
            }
            if (this.mDropDownHeight != -1) {
                if (this.mDropDownHeight != -2) {
                    height = this.mDropDownHeight;
                }
            }
            else {
                height = -1;
            }
            this.mPopup.setWidth(width);
            this.mPopup.setHeight(height);
            this.setPopupClipToScreenEnabled(true);
            final PopupWindow mPopup = this.mPopup;
            boolean outsideTouchable2 = false;
            Label_0123: {
                if (!this.mForceIgnoreOutsideTouch) {
                    outsideTouchable2 = b;
                    if (!this.mDropDownAlwaysVisible) {
                        break Label_0123;
                    }
                }
                outsideTouchable2 = false;
            }
            mPopup.setOutsideTouchable(outsideTouchable2);
            this.mPopup.setTouchInterceptor((View$OnTouchListener)this.mTouchInterceptor);
            if (this.mOverlapAnchorSet) {
                PopupWindowCompat.setOverlapAnchor(this.mPopup, this.mOverlapAnchor);
            }
            if (ListPopupWindow.sSetEpicenterBoundsMethod != null) {
                try {
                    ListPopupWindow.sSetEpicenterBoundsMethod.invoke(this.mPopup, this.mEpicenterBounds);
                }
                catch (final Exception ex) {
                    Log.e("ListPopupWindow", "Could not invoke setEpicenterBounds on PopupWindow", (Throwable)ex);
                }
            }
            PopupWindowCompat.showAsDropDown(this.mPopup, this.getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
            this.mDropDownList.setSelection(-1);
            if (!this.mModal || this.mDropDownList.isInTouchMode()) {
                this.clearListSelection();
            }
            if (!this.mModal) {
                this.mHandler.post((Runnable)this.mHideSelector);
            }
        }
        else {
            if (!ViewCompat.isAttachedToWindow(this.getAnchorView())) {
                return;
            }
            int n2;
            if (this.mDropDownWidth != -1) {
                if (this.mDropDownWidth != -2) {
                    n2 = this.mDropDownWidth;
                }
                else {
                    n2 = this.getAnchorView().getWidth();
                }
            }
            else {
                n2 = -1;
            }
            if (this.mDropDownHeight != -1) {
                if (this.mDropDownHeight != -2) {
                    height = this.mDropDownHeight;
                }
            }
            else {
                if (!inputMethodNotNeeded) {
                    height = -1;
                }
                if (!inputMethodNotNeeded) {
                    final PopupWindow mPopup2 = this.mPopup;
                    int width2;
                    if (this.mDropDownWidth != -1) {
                        width2 = 0;
                    }
                    else {
                        width2 = -1;
                    }
                    mPopup2.setWidth(width2);
                    this.mPopup.setHeight(-1);
                }
                else {
                    final PopupWindow mPopup3 = this.mPopup;
                    int width3;
                    if (this.mDropDownWidth != -1) {
                        width3 = 0;
                    }
                    else {
                        width3 = -1;
                    }
                    mPopup3.setWidth(width3);
                    this.mPopup.setHeight(0);
                }
            }
            final PopupWindow mPopup4 = this.mPopup;
            if (!this.mForceIgnoreOutsideTouch && !this.mDropDownAlwaysVisible) {
                outsideTouchable = true;
            }
            mPopup4.setOutsideTouchable(outsideTouchable);
            final PopupWindow mPopup5 = this.mPopup;
            final View anchorView = this.getAnchorView();
            final int mDropDownHorizontalOffset = this.mDropDownHorizontalOffset;
            final int mDropDownVerticalOffset = this.mDropDownVerticalOffset;
            if (n2 < 0) {
                n2 = -1;
            }
            int n3 = n;
            if (height >= 0) {
                n3 = height;
            }
            mPopup5.update(anchorView, mDropDownHorizontalOffset, mDropDownVerticalOffset, n2, n3);
        }
    }
    
    private class ListSelectorHider implements Runnable
    {
        ListSelectorHider() {
        }
        
        @Override
        public void run() {
            ListPopupWindow.this.clearListSelection();
        }
    }
    
    private class PopupDataSetObserver extends DataSetObserver
    {
        PopupDataSetObserver() {
        }
        
        public void onChanged() {
            if (ListPopupWindow.this.isShowing()) {
                ListPopupWindow.this.show();
            }
        }
        
        public void onInvalidated() {
            ListPopupWindow.this.dismiss();
        }
    }
    
    private class PopupScrollListener implements AbsListView$OnScrollListener
    {
        PopupScrollListener() {
        }
        
        public void onScroll(final AbsListView absListView, final int n, final int n2, final int n3) {
        }
        
        public void onScrollStateChanged(final AbsListView absListView, final int n) {
            if (n == 1 && !ListPopupWindow.this.isInputMethodNotNeeded() && ListPopupWindow.this.mPopup.getContentView() != null) {
                ListPopupWindow.this.mHandler.removeCallbacks((Runnable)ListPopupWindow.this.mResizePopupRunnable);
                ListPopupWindow.this.mResizePopupRunnable.run();
            }
        }
    }
    
    private class PopupTouchInterceptor implements View$OnTouchListener
    {
        PopupTouchInterceptor() {
        }
        
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            final int action = motionEvent.getAction();
            final int n = (int)motionEvent.getX();
            final int n2 = (int)motionEvent.getY();
            if (action == 0 && ListPopupWindow.this.mPopup != null && ListPopupWindow.this.mPopup.isShowing() && n >= 0 && n < ListPopupWindow.this.mPopup.getWidth() && n2 >= 0 && n2 < ListPopupWindow.this.mPopup.getHeight()) {
                ListPopupWindow.this.mHandler.postDelayed((Runnable)ListPopupWindow.this.mResizePopupRunnable, 250L);
            }
            else if (action == 1) {
                ListPopupWindow.this.mHandler.removeCallbacks((Runnable)ListPopupWindow.this.mResizePopupRunnable);
            }
            return false;
        }
    }
    
    private class ResizePopupRunnable implements Runnable
    {
        ResizePopupRunnable() {
        }
        
        @Override
        public void run() {
            if (ListPopupWindow.this.mDropDownList != null && ViewCompat.isAttachedToWindow((View)ListPopupWindow.this.mDropDownList) && ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount() && ListPopupWindow.this.mDropDownList.getChildCount() <= ListPopupWindow.this.mListItemExpandMaximum) {
                ListPopupWindow.this.mPopup.setInputMethodMode(2);
                ListPopupWindow.this.show();
            }
        }
    }
}
