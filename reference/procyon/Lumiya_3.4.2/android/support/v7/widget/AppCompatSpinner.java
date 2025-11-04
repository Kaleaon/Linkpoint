// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.ViewTreeObserver;
import android.widget.PopupWindow$OnDismissListener;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.support.v4.view.ViewCompat;
import android.widget.AdapterView;
import android.widget.AdapterView$OnItemClickListener;
import android.database.DataSetObserver;
import android.widget.ThemedSpinnerAdapter;
import android.support.v7.content.res.AppCompatResources;
import android.support.annotation.DrawableRes;
import android.widget.ListAdapter;
import android.widget.Adapter;
import android.view.MotionEvent;
import android.graphics.PorterDuff$Mode;
import android.support.annotation.RestrictTo;
import android.support.annotation.Nullable;
import android.content.res.ColorStateList;
import android.os.Build$VERSION;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup;
import android.view.View$MeasureSpec;
import android.graphics.drawable.Drawable;
import android.content.res.Resources$Theme;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.graphics.Rect;
import android.widget.SpinnerAdapter;
import android.content.Context;
import android.support.v4.view.TintableBackgroundView;
import android.widget.Spinner;

public class AppCompatSpinner extends Spinner implements TintableBackgroundView
{
    private static final int[] ATTRS_ANDROID_SPINNERMODE;
    private static final int MAX_ITEMS_MEASURED = 15;
    private static final int MODE_DIALOG = 0;
    private static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "AppCompatSpinner";
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    private int mDropDownWidth;
    private ForwardingListener mForwardingListener;
    private DropdownPopup mPopup;
    private final Context mPopupContext;
    private final boolean mPopupSet;
    private SpinnerAdapter mTempAdapter;
    private final Rect mTempRect;
    
    static {
        ATTRS_ANDROID_SPINNERMODE = new int[] { 16843505 };
    }
    
    public AppCompatSpinner(final Context context) {
        this(context, null);
    }
    
    public AppCompatSpinner(final Context context, final int n) {
        this(context, null, R.attr.spinnerStyle, n);
    }
    
    public AppCompatSpinner(final Context context, final AttributeSet set) {
        this(context, set, R.attr.spinnerStyle);
    }
    
    public AppCompatSpinner(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, -1);
    }
    
    public AppCompatSpinner(final Context context, final AttributeSet set, final int n, final int n2) {
        this(context, set, n, n2, null);
    }
    
    public AppCompatSpinner(final Context p0, final AttributeSet p1, final int p2, final int p3, final Resources$Theme p4) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1        
        //     2: aload_2        
        //     3: iload_3        
        //     4: invokespecial   android/widget/Spinner.<init>:(Landroid/content/Context;Landroid/util/AttributeSet;I)V
        //     7: aload_0        
        //     8: new             Landroid/graphics/Rect;
        //    11: dup            
        //    12: invokespecial   android/graphics/Rect.<init>:()V
        //    15: putfield        android/support/v7/widget/AppCompatSpinner.mTempRect:Landroid/graphics/Rect;
        //    18: aload_1        
        //    19: aload_2        
        //    20: getstatic       android/support/v7/appcompat/R$styleable.Spinner:[I
        //    23: iload_3        
        //    24: iconst_0       
        //    25: invokestatic    android/support/v7/widget/TintTypedArray.obtainStyledAttributes:(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/widget/TintTypedArray;
        //    28: astore          6
        //    30: aload_0        
        //    31: new             Landroid/support/v7/widget/AppCompatBackgroundHelper;
        //    34: dup            
        //    35: aload_0        
        //    36: invokespecial   android/support/v7/widget/AppCompatBackgroundHelper.<init>:(Landroid/view/View;)V
        //    39: putfield        android/support/v7/widget/AppCompatSpinner.mBackgroundTintHelper:Landroid/support/v7/widget/AppCompatBackgroundHelper;
        //    42: aload           5
        //    44: ifnonnull       129
        //    47: aload           6
        //    49: getstatic       android/support/v7/appcompat/R$styleable.Spinner_popupTheme:I
        //    52: iconst_0       
        //    53: invokevirtual   android/support/v7/widget/TintTypedArray.getResourceId:(II)I
        //    56: istore          7
        //    58: iload           7
        //    60: ifne            146
        //    63: getstatic       android/os/Build$VERSION.SDK_INT:I
        //    66: bipush          23
        //    68: if_icmplt       163
        //    71: aconst_null    
        //    72: astore          5
        //    74: aload_0        
        //    75: aload           5
        //    77: putfield        android/support/v7/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //    80: aload_0        
        //    81: getfield        android/support/v7/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //    84: ifnonnull       169
        //    87: aload           6
        //    89: getstatic       android/support/v7/appcompat/R$styleable.Spinner_android_entries:I
        //    92: invokevirtual   android/support/v7/widget/TintTypedArray.getTextArray:(I)[Ljava/lang/CharSequence;
        //    95: astore          5
        //    97: aload           5
        //    99: ifnonnull       428
        //   102: aload           6
        //   104: invokevirtual   android/support/v7/widget/TintTypedArray.recycle:()V
        //   107: aload_0        
        //   108: iconst_1       
        //   109: putfield        android/support/v7/widget/AppCompatSpinner.mPopupSet:Z
        //   112: aload_0        
        //   113: getfield        android/support/v7/widget/AppCompatSpinner.mTempAdapter:Landroid/widget/SpinnerAdapter;
        //   116: ifnonnull       456
        //   119: aload_0        
        //   120: getfield        android/support/v7/widget/AppCompatSpinner.mBackgroundTintHelper:Landroid/support/v7/widget/AppCompatBackgroundHelper;
        //   123: aload_2        
        //   124: iload_3        
        //   125: invokevirtual   android/support/v7/widget/AppCompatBackgroundHelper.loadFromAttributes:(Landroid/util/AttributeSet;I)V
        //   128: return         
        //   129: aload_0        
        //   130: new             Landroid/support/v7/view/ContextThemeWrapper;
        //   133: dup            
        //   134: aload_1        
        //   135: aload           5
        //   137: invokespecial   android/support/v7/view/ContextThemeWrapper.<init>:(Landroid/content/Context;Landroid/content/res/Resources$Theme;)V
        //   140: putfield        android/support/v7/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   143: goto            80
        //   146: aload_0        
        //   147: new             Landroid/support/v7/view/ContextThemeWrapper;
        //   150: dup            
        //   151: aload_1        
        //   152: iload           7
        //   154: invokespecial   android/support/v7/view/ContextThemeWrapper.<init>:(Landroid/content/Context;I)V
        //   157: putfield        android/support/v7/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   160: goto            80
        //   163: aload_1        
        //   164: astore          5
        //   166: goto            74
        //   169: iload           4
        //   171: iconst_m1      
        //   172: if_icmpeq       285
        //   175: iload           4
        //   177: istore          7
        //   179: iload           7
        //   181: iconst_1       
        //   182: if_icmpne       87
        //   185: new             Landroid/support/v7/widget/AppCompatSpinner$DropdownPopup;
        //   188: dup            
        //   189: aload_0        
        //   190: aload_0        
        //   191: getfield        android/support/v7/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   194: aload_2        
        //   195: iload_3        
        //   196: invokespecial   android/support/v7/widget/AppCompatSpinner$DropdownPopup.<init>:(Landroid/support/v7/widget/AppCompatSpinner;Landroid/content/Context;Landroid/util/AttributeSet;I)V
        //   199: astore          8
        //   201: aload_0        
        //   202: getfield        android/support/v7/widget/AppCompatSpinner.mPopupContext:Landroid/content/Context;
        //   205: aload_2        
        //   206: getstatic       android/support/v7/appcompat/R$styleable.Spinner:[I
        //   209: iload_3        
        //   210: iconst_0       
        //   211: invokestatic    android/support/v7/widget/TintTypedArray.obtainStyledAttributes:(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/widget/TintTypedArray;
        //   214: astore          5
        //   216: aload_0        
        //   217: aload           5
        //   219: getstatic       android/support/v7/appcompat/R$styleable.Spinner_android_dropDownWidth:I
        //   222: bipush          -2
        //   224: invokevirtual   android/support/v7/widget/TintTypedArray.getLayoutDimension:(II)I
        //   227: putfield        android/support/v7/widget/AppCompatSpinner.mDropDownWidth:I
        //   230: aload           8
        //   232: aload           5
        //   234: getstatic       android/support/v7/appcompat/R$styleable.Spinner_android_popupBackground:I
        //   237: invokevirtual   android/support/v7/widget/TintTypedArray.getDrawable:(I)Landroid/graphics/drawable/Drawable;
        //   240: invokevirtual   android/support/v7/widget/AppCompatSpinner$DropdownPopup.setBackgroundDrawable:(Landroid/graphics/drawable/Drawable;)V
        //   243: aload           8
        //   245: aload           6
        //   247: getstatic       android/support/v7/appcompat/R$styleable.Spinner_android_prompt:I
        //   250: invokevirtual   android/support/v7/widget/TintTypedArray.getString:(I)Ljava/lang/String;
        //   253: invokevirtual   android/support/v7/widget/AppCompatSpinner$DropdownPopup.setPromptText:(Ljava/lang/CharSequence;)V
        //   256: aload           5
        //   258: invokevirtual   android/support/v7/widget/TintTypedArray.recycle:()V
        //   261: aload_0        
        //   262: aload           8
        //   264: putfield        android/support/v7/widget/AppCompatSpinner.mPopup:Landroid/support/v7/widget/AppCompatSpinner$DropdownPopup;
        //   267: aload_0        
        //   268: new             Landroid/support/v7/widget/AppCompatSpinner$1;
        //   271: dup            
        //   272: aload_0        
        //   273: aload_0        
        //   274: aload           8
        //   276: invokespecial   android/support/v7/widget/AppCompatSpinner$1.<init>:(Landroid/support/v7/widget/AppCompatSpinner;Landroid/view/View;Landroid/support/v7/widget/AppCompatSpinner$DropdownPopup;)V
        //   279: putfield        android/support/v7/widget/AppCompatSpinner.mForwardingListener:Landroid/support/v7/widget/ForwardingListener;
        //   282: goto            87
        //   285: getstatic       android/os/Build$VERSION.SDK_INT:I
        //   288: bipush          11
        //   290: if_icmpge       299
        //   293: iconst_1       
        //   294: istore          7
        //   296: goto            179
        //   299: aload_1        
        //   300: aload_2        
        //   301: getstatic       android/support/v7/widget/AppCompatSpinner.ATTRS_ANDROID_SPINNERMODE:[I
        //   304: iload_3        
        //   305: iconst_0       
        //   306: invokevirtual   android/content/Context.obtainStyledAttributes:(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;
        //   309: astore          8
        //   311: aload           8
        //   313: astore          5
        //   315: aload           8
        //   317: iconst_0       
        //   318: invokevirtual   android/content/res/TypedArray.hasValue:(I)Z
        //   321: istore          9
        //   323: iload           9
        //   325: ifne            349
        //   328: iload           4
        //   330: istore          7
        //   332: aload           8
        //   334: ifnull          179
        //   337: aload           8
        //   339: invokevirtual   android/content/res/TypedArray.recycle:()V
        //   342: iload           4
        //   344: istore          7
        //   346: goto            179
        //   349: aload           8
        //   351: astore          5
        //   353: aload           8
        //   355: iconst_0       
        //   356: iconst_0       
        //   357: invokevirtual   android/content/res/TypedArray.getInt:(II)I
        //   360: istore          7
        //   362: iload           7
        //   364: istore          4
        //   366: goto            328
        //   369: astore          10
        //   371: aconst_null    
        //   372: astore          8
        //   374: aload           8
        //   376: astore          5
        //   378: ldc             "AppCompatSpinner"
        //   380: ldc             "Could not read android:spinnerMode"
        //   382: aload           10
        //   384: invokestatic    android/util/Log.i:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   387: pop            
        //   388: iload           4
        //   390: istore          7
        //   392: aload           8
        //   394: ifnull          179
        //   397: aload           8
        //   399: invokevirtual   android/content/res/TypedArray.recycle:()V
        //   402: iload           4
        //   404: istore          7
        //   406: goto            179
        //   409: astore_1       
        //   410: aconst_null    
        //   411: astore          5
        //   413: aload           5
        //   415: ifnonnull       420
        //   418: aload_1        
        //   419: athrow         
        //   420: aload           5
        //   422: invokevirtual   android/content/res/TypedArray.recycle:()V
        //   425: goto            418
        //   428: new             Landroid/widget/ArrayAdapter;
        //   431: dup            
        //   432: aload_1        
        //   433: ldc             17367048
        //   435: aload           5
        //   437: invokespecial   android/widget/ArrayAdapter.<init>:(Landroid/content/Context;I[Ljava/lang/Object;)V
        //   440: astore_1       
        //   441: aload_1        
        //   442: getstatic       android/support/v7/appcompat/R$layout.support_simple_spinner_dropdown_item:I
        //   445: invokevirtual   android/widget/ArrayAdapter.setDropDownViewResource:(I)V
        //   448: aload_0        
        //   449: aload_1        
        //   450: invokevirtual   android/support/v7/widget/AppCompatSpinner.setAdapter:(Landroid/widget/SpinnerAdapter;)V
        //   453: goto            102
        //   456: aload_0        
        //   457: aload_0        
        //   458: getfield        android/support/v7/widget/AppCompatSpinner.mTempAdapter:Landroid/widget/SpinnerAdapter;
        //   461: invokevirtual   android/support/v7/widget/AppCompatSpinner.setAdapter:(Landroid/widget/SpinnerAdapter;)V
        //   464: aload_0        
        //   465: aconst_null    
        //   466: putfield        android/support/v7/widget/AppCompatSpinner.mTempAdapter:Landroid/widget/SpinnerAdapter;
        //   469: goto            119
        //   472: astore_1       
        //   473: goto            413
        //   476: astore          10
        //   478: goto            374
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  299    311    369    374    Ljava/lang/Exception;
        //  299    311    409    413    Any
        //  315    323    476    481    Ljava/lang/Exception;
        //  315    323    472    476    Any
        //  353    362    476    481    Ljava/lang/Exception;
        //  353    362    472    476    Any
        //  378    388    472    476    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0328:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
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
    
    int compatMeasureContentWidth(final SpinnerAdapter spinnerAdapter, final Drawable drawable) {
        if (spinnerAdapter != null) {
            final int measureSpec = View$MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), 0);
            final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), 0);
            final int max = Math.max(0, this.getSelectedItemPosition());
            final int min = Math.min(spinnerAdapter.getCount(), max + 15);
            int i = Math.max(0, max - (15 - (min - max)));
            View view = null;
            int max2 = 0;
            int n = 0;
            while (i < min) {
                final int itemViewType = spinnerAdapter.getItemViewType(i);
                if (itemViewType != n) {
                    n = itemViewType;
                    view = null;
                }
                view = spinnerAdapter.getView(i, view, (ViewGroup)this);
                if (view.getLayoutParams() == null) {
                    view.setLayoutParams(new ViewGroup$LayoutParams(-2, -2));
                }
                view.measure(measureSpec, measureSpec2);
                max2 = Math.max(max2, view.getMeasuredWidth());
                ++i;
            }
            if (drawable != null) {
                drawable.getPadding(this.mTempRect);
                max2 += this.mTempRect.left + this.mTempRect.right;
            }
            return max2;
        }
        return 0;
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySupportBackgroundTint();
        }
    }
    
    public int getDropDownHorizontalOffset() {
        if (this.mPopup != null) {
            return this.mPopup.getHorizontalOffset();
        }
        if (Build$VERSION.SDK_INT < 16) {
            return 0;
        }
        return super.getDropDownHorizontalOffset();
    }
    
    public int getDropDownVerticalOffset() {
        if (this.mPopup != null) {
            return this.mPopup.getVerticalOffset();
        }
        if (Build$VERSION.SDK_INT < 16) {
            return 0;
        }
        return super.getDropDownVerticalOffset();
    }
    
    public int getDropDownWidth() {
        if (this.mPopup != null) {
            return this.mDropDownWidth;
        }
        if (Build$VERSION.SDK_INT < 16) {
            return 0;
        }
        return super.getDropDownWidth();
    }
    
    public Drawable getPopupBackground() {
        if (this.mPopup != null) {
            return this.mPopup.getBackground();
        }
        if (Build$VERSION.SDK_INT < 16) {
            return null;
        }
        return super.getPopupBackground();
    }
    
    public Context getPopupContext() {
        if (this.mPopup != null) {
            return this.mPopupContext;
        }
        if (Build$VERSION.SDK_INT < 23) {
            return null;
        }
        return super.getPopupContext();
    }
    
    public CharSequence getPrompt() {
        CharSequence charSequence;
        if (this.mPopup == null) {
            charSequence = super.getPrompt();
        }
        else {
            charSequence = this.mPopup.getHintText();
        }
        return charSequence;
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public ColorStateList getSupportBackgroundTintList() {
        ColorStateList supportBackgroundTintList = null;
        if (this.mBackgroundTintHelper != null) {
            supportBackgroundTintList = this.mBackgroundTintHelper.getSupportBackgroundTintList();
        }
        return supportBackgroundTintList;
    }
    
    @Nullable
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public PorterDuff$Mode getSupportBackgroundTintMode() {
        PorterDuff$Mode supportBackgroundTintMode = null;
        if (this.mBackgroundTintHelper != null) {
            supportBackgroundTintMode = this.mBackgroundTintHelper.getSupportBackgroundTintMode();
        }
        return supportBackgroundTintMode;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPopup != null && this.mPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        if (this.mPopup != null && View$MeasureSpec.getMode(n) == Integer.MIN_VALUE) {
            this.setMeasuredDimension(Math.min(Math.max(this.getMeasuredWidth(), this.compatMeasureContentWidth(this.getAdapter(), this.getBackground())), View$MeasureSpec.getSize(n)), this.getMeasuredHeight());
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return (this.mForwardingListener != null && this.mForwardingListener.onTouch((View)this, motionEvent)) || super.onTouchEvent(motionEvent);
    }
    
    public boolean performClick() {
        if (this.mPopup == null) {
            return super.performClick();
        }
        if (!this.mPopup.isShowing()) {
            this.mPopup.show();
        }
        return true;
    }
    
    public void setAdapter(final SpinnerAdapter spinnerAdapter) {
        if (this.mPopupSet) {
            super.setAdapter(spinnerAdapter);
            if (this.mPopup != null) {
                Context context;
                if (this.mPopupContext != null) {
                    context = this.mPopupContext;
                }
                else {
                    context = this.getContext();
                }
                this.mPopup.setAdapter((ListAdapter)new DropDownAdapter(spinnerAdapter, context.getTheme()));
            }
            return;
        }
        this.mTempAdapter = spinnerAdapter;
    }
    
    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        super.setBackgroundDrawable(backgroundDrawable);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundDrawable(backgroundDrawable);
        }
    }
    
    public void setBackgroundResource(@DrawableRes final int backgroundResource) {
        super.setBackgroundResource(backgroundResource);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundResource(backgroundResource);
        }
    }
    
    public void setDropDownHorizontalOffset(final int n) {
        if (this.mPopup == null) {
            if (Build$VERSION.SDK_INT >= 16) {
                super.setDropDownHorizontalOffset(n);
            }
        }
        else {
            this.mPopup.setHorizontalOffset(n);
        }
    }
    
    public void setDropDownVerticalOffset(final int n) {
        if (this.mPopup == null) {
            if (Build$VERSION.SDK_INT >= 16) {
                super.setDropDownVerticalOffset(n);
            }
        }
        else {
            this.mPopup.setVerticalOffset(n);
        }
    }
    
    public void setDropDownWidth(final int n) {
        if (this.mPopup == null) {
            if (Build$VERSION.SDK_INT >= 16) {
                super.setDropDownWidth(n);
            }
        }
        else {
            this.mDropDownWidth = n;
        }
    }
    
    public void setPopupBackgroundDrawable(final Drawable drawable) {
        if (this.mPopup == null) {
            if (Build$VERSION.SDK_INT >= 16) {
                super.setPopupBackgroundDrawable(drawable);
            }
        }
        else {
            this.mPopup.setBackgroundDrawable(drawable);
        }
    }
    
    public void setPopupBackgroundResource(@DrawableRes final int n) {
        this.setPopupBackgroundDrawable(AppCompatResources.getDrawable(this.getPopupContext(), n));
    }
    
    public void setPrompt(final CharSequence charSequence) {
        if (this.mPopup == null) {
            super.setPrompt(charSequence);
        }
        else {
            this.mPopup.setPromptText(charSequence);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setSupportBackgroundTintList(@Nullable final ColorStateList supportBackgroundTintList) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintList(supportBackgroundTintList);
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void setSupportBackgroundTintMode(@Nullable final PorterDuff$Mode supportBackgroundTintMode) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintMode(supportBackgroundTintMode);
        }
    }
    
    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter
    {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;
        
        public DropDownAdapter(@Nullable final SpinnerAdapter mAdapter, @Nullable final Resources$Theme resources$Theme) {
            this.mAdapter = mAdapter;
            if (mAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter)mAdapter;
            }
            if (resources$Theme != null) {
                if (Build$VERSION.SDK_INT >= 23 && mAdapter instanceof ThemedSpinnerAdapter) {
                    final ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter)mAdapter;
                    if (themedSpinnerAdapter.getDropDownViewTheme() != resources$Theme) {
                        themedSpinnerAdapter.setDropDownViewTheme(resources$Theme);
                    }
                }
                else if (mAdapter instanceof android.support.v7.widget.ThemedSpinnerAdapter) {
                    final android.support.v7.widget.ThemedSpinnerAdapter themedSpinnerAdapter2 = (android.support.v7.widget.ThemedSpinnerAdapter)mAdapter;
                    if (themedSpinnerAdapter2.getDropDownViewTheme() == null) {
                        themedSpinnerAdapter2.setDropDownViewTheme(resources$Theme);
                    }
                }
            }
        }
        
        public boolean areAllItemsEnabled() {
            final ListAdapter mListAdapter = this.mListAdapter;
            return mListAdapter == null || mListAdapter.areAllItemsEnabled();
        }
        
        public int getCount() {
            int count;
            if (this.mAdapter != null) {
                count = this.mAdapter.getCount();
            }
            else {
                count = 0;
            }
            return count;
        }
        
        public View getDropDownView(final int n, final View view, final ViewGroup viewGroup) {
            View dropDownView = null;
            if (this.mAdapter != null) {
                dropDownView = this.mAdapter.getDropDownView(n, view, viewGroup);
            }
            return dropDownView;
        }
        
        public Object getItem(final int n) {
            Object item = null;
            if (this.mAdapter != null) {
                item = this.mAdapter.getItem(n);
            }
            return item;
        }
        
        public long getItemId(final int n) {
            long itemId;
            if (this.mAdapter != null) {
                itemId = this.mAdapter.getItemId(n);
            }
            else {
                itemId = -1L;
            }
            return itemId;
        }
        
        public int getItemViewType(final int n) {
            return 0;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            return this.getDropDownView(n, view, viewGroup);
        }
        
        public int getViewTypeCount() {
            return 1;
        }
        
        public boolean hasStableIds() {
            boolean b = false;
            if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
                b = true;
            }
            return b;
        }
        
        public boolean isEmpty() {
            boolean b = false;
            if (this.getCount() == 0) {
                b = true;
            }
            return b;
        }
        
        public boolean isEnabled(final int n) {
            final ListAdapter mListAdapter = this.mListAdapter;
            return mListAdapter == null || mListAdapter.isEnabled(n);
        }
        
        public void registerDataSetObserver(final DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(dataSetObserver);
            }
        }
        
        public void unregisterDataSetObserver(final DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }
    
    private class DropdownPopup extends ListPopupWindow
    {
        ListAdapter mAdapter;
        private CharSequence mHintText;
        private final Rect mVisibleRect;
        
        public DropdownPopup(final Context context, final AttributeSet set, final int n) {
            super(context, set, n);
            this.mVisibleRect = new Rect();
            this.setAnchorView((View)AppCompatSpinner.this);
            this.setModal(true);
            this.setPromptPosition(0);
            this.setOnItemClickListener((AdapterView$OnItemClickListener)new AdapterView$OnItemClickListener() {
                public void onItemClick(final AdapterView<?> adapterView, final View view, final int selection, final long n) {
                    AppCompatSpinner.this.setSelection(selection);
                    if (AppCompatSpinner.this.getOnItemClickListener() != null) {
                        AppCompatSpinner.this.performItemClick(view, selection, DropdownPopup.this.mAdapter.getItemId(selection));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }
        
        void computeContentWidth() {
            final Drawable background = this.getBackground();
            int right;
            if (background == null) {
                final Rect access$100 = AppCompatSpinner.this.mTempRect;
                AppCompatSpinner.this.mTempRect.right = 0;
                access$100.left = 0;
                right = 0;
            }
            else {
                background.getPadding(AppCompatSpinner.this.mTempRect);
                if (!ViewUtils.isLayoutRtl((View)AppCompatSpinner.this)) {
                    right = -AppCompatSpinner.this.mTempRect.left;
                }
                else {
                    right = AppCompatSpinner.this.mTempRect.right;
                }
            }
            final int paddingLeft = AppCompatSpinner.this.getPaddingLeft();
            final int paddingRight = AppCompatSpinner.this.getPaddingRight();
            final int width = AppCompatSpinner.this.getWidth();
            if (AppCompatSpinner.this.mDropDownWidth != -2) {
                if (AppCompatSpinner.this.mDropDownWidth != -1) {
                    this.setContentWidth(AppCompatSpinner.this.mDropDownWidth);
                }
                else {
                    this.setContentWidth(width - paddingLeft - paddingRight);
                }
            }
            else {
                final int compatMeasureContentWidth = AppCompatSpinner.this.compatMeasureContentWidth((SpinnerAdapter)this.mAdapter, this.getBackground());
                int a = AppCompatSpinner.this.getContext().getResources().getDisplayMetrics().widthPixels - AppCompatSpinner.this.mTempRect.left - AppCompatSpinner.this.mTempRect.right;
                if (compatMeasureContentWidth <= a) {
                    a = compatMeasureContentWidth;
                }
                this.setContentWidth(Math.max(a, width - paddingLeft - paddingRight));
            }
            int horizontalOffset;
            if (!ViewUtils.isLayoutRtl((View)AppCompatSpinner.this)) {
                horizontalOffset = right + paddingLeft;
            }
            else {
                horizontalOffset = width - paddingRight - this.getWidth() + right;
            }
            this.setHorizontalOffset(horizontalOffset);
        }
        
        public CharSequence getHintText() {
            return this.mHintText;
        }
        
        boolean isVisibleToUser(final View view) {
            boolean b = false;
            if (ViewCompat.isAttachedToWindow(view) && view.getGlobalVisibleRect(this.mVisibleRect)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public void setAdapter(final ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }
        
        public void setPromptText(final CharSequence mHintText) {
            this.mHintText = mHintText;
        }
        
        @Override
        public void show() {
            final boolean showing = this.isShowing();
            this.computeContentWidth();
            this.setInputMethodMode(2);
            super.show();
            this.getListView().setChoiceMode(1);
            this.setSelection(AppCompatSpinner.this.getSelectedItemPosition());
            if (!showing) {
                final ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
                if (viewTreeObserver != null) {
                    final ViewTreeObserver$OnGlobalLayoutListener viewTreeObserver$OnGlobalLayoutListener = (ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            if (DropdownPopup.this.isVisibleToUser((View)AppCompatSpinner.this)) {
                                DropdownPopup.this.computeContentWidth();
                                ListPopupWindow.this.show();
                            }
                            else {
                                DropdownPopup.this.dismiss();
                            }
                        }
                    };
                    viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)viewTreeObserver$OnGlobalLayoutListener);
                    this.setOnDismissListener((PopupWindow$OnDismissListener)new PopupWindow$OnDismissListener() {
                        public void onDismiss() {
                            final ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
                            if (viewTreeObserver != null) {
                                viewTreeObserver.removeGlobalOnLayoutListener(viewTreeObserver$OnGlobalLayoutListener);
                            }
                        }
                    });
                }
            }
        }
    }
}
