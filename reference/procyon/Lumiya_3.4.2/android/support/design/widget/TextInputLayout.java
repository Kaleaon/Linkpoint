// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;
import android.os.Parcel;
import android.os.Parcelable$ClassLoaderCreator;
import android.os.Parcelable$Creator;
import android.support.v4.view.AbsSavedState;
import android.support.v7.content.res.AppCompatResources;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.annotation.NonNull;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ViewStructure;
import android.support.annotation.VisibleForTesting;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.widget.FrameLayout$LayoutParams;
import android.graphics.drawable.ColorDrawable;
import android.view.View$OnClickListener;
import android.view.LayoutInflater;
import android.support.v4.widget.TextViewCompat;
import android.graphics.ColorFilter;
import android.support.v7.widget.AppCompatDrawableManager;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.text.method.TransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.graphics.drawable.DrawableContainer;
import android.os.Build$VERSION;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.support.v4.widget.Space;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.TintTypedArray;
import android.support.design.R;
import android.view.animation.Interpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.PorterDuff$Mode;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.EditText;
import android.content.res.ColorStateList;
import android.widget.TextView;
import android.animation.ValueAnimator;
import android.widget.LinearLayout;

public class TextInputLayout extends LinearLayout
{
    private static final int ANIMATION_DURATION = 200;
    private static final int INVALID_MAX_LENGTH = -1;
    private static final String LOG_TAG = "TextInputLayout";
    private ValueAnimator mAnimator;
    final CollapsingTextHelper mCollapsingTextHelper;
    boolean mCounterEnabled;
    private int mCounterMaxLength;
    private int mCounterOverflowTextAppearance;
    private boolean mCounterOverflowed;
    private int mCounterTextAppearance;
    private TextView mCounterView;
    private ColorStateList mDefaultTextColor;
    EditText mEditText;
    private CharSequence mError;
    private boolean mErrorEnabled;
    private boolean mErrorShown;
    private int mErrorTextAppearance;
    TextView mErrorView;
    private ColorStateList mFocusedTextColor;
    private boolean mHasPasswordToggleTintList;
    private boolean mHasPasswordToggleTintMode;
    private boolean mHasReconstructedEditTextBackground;
    private CharSequence mHint;
    private boolean mHintAnimationEnabled;
    private boolean mHintEnabled;
    private boolean mHintExpanded;
    private boolean mInDrawableStateChanged;
    private LinearLayout mIndicatorArea;
    private int mIndicatorsAdded;
    private final FrameLayout mInputFrame;
    private Drawable mOriginalEditTextEndDrawable;
    private CharSequence mOriginalHint;
    private CharSequence mPasswordToggleContentDesc;
    private Drawable mPasswordToggleDrawable;
    private Drawable mPasswordToggleDummyDrawable;
    private boolean mPasswordToggleEnabled;
    private ColorStateList mPasswordToggleTintList;
    private PorterDuff$Mode mPasswordToggleTintMode;
    private CheckableImageButton mPasswordToggleView;
    private boolean mPasswordToggledVisible;
    private boolean mRestoringSavedState;
    private Paint mTmpPaint;
    private final Rect mTmpRect;
    private Typeface mTypeface;
    
    public TextInputLayout(final Context context) {
        this(context, null);
    }
    
    public TextInputLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public TextInputLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set);
        this.mTmpRect = new Rect();
        this.mCollapsingTextHelper = new CollapsingTextHelper((View)this);
        ThemeUtils.checkAppCompatTheme(context);
        this.setOrientation(1);
        this.setWillNotDraw(false);
        this.setAddStatesFromChildren(true);
        (this.mInputFrame = new FrameLayout(context)).setAddStatesFromChildren(true);
        this.addView((View)this.mInputFrame);
        this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        this.mCollapsingTextHelper.setPositionInterpolator((Interpolator)new AccelerateInterpolator());
        this.mCollapsingTextHelper.setCollapsedTextGravity(8388659);
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, set, R.styleable.TextInputLayout, n, R.style.Widget_Design_TextInputLayout);
        this.mHintEnabled = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_hintEnabled, true);
        this.setHint(obtainStyledAttributes.getText(R.styleable.TextInputLayout_android_hint));
        this.mHintAnimationEnabled = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_hintAnimationEnabled, true);
        if (obtainStyledAttributes.hasValue(R.styleable.TextInputLayout_android_textColorHint)) {
            final ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R.styleable.TextInputLayout_android_textColorHint);
            this.mFocusedTextColor = colorStateList;
            this.mDefaultTextColor = colorStateList;
        }
        if (obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, -1) != -1) {
            this.setHintTextAppearance(obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, 0));
        }
        this.mErrorTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
        final boolean boolean1 = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_errorEnabled, false);
        final boolean boolean2 = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_counterEnabled, false);
        this.setCounterMaxLength(obtainStyledAttributes.getInt(R.styleable.TextInputLayout_counterMaxLength, -1));
        this.mCounterTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_counterTextAppearance, 0);
        this.mCounterOverflowTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.TextInputLayout_counterOverflowTextAppearance, 0);
        this.mPasswordToggleEnabled = obtainStyledAttributes.getBoolean(R.styleable.TextInputLayout_passwordToggleEnabled, false);
        this.mPasswordToggleDrawable = obtainStyledAttributes.getDrawable(R.styleable.TextInputLayout_passwordToggleDrawable);
        this.mPasswordToggleContentDesc = obtainStyledAttributes.getText(R.styleable.TextInputLayout_passwordToggleContentDescription);
        if (obtainStyledAttributes.hasValue(R.styleable.TextInputLayout_passwordToggleTint)) {
            this.mHasPasswordToggleTintList = true;
            this.mPasswordToggleTintList = obtainStyledAttributes.getColorStateList(R.styleable.TextInputLayout_passwordToggleTint);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.TextInputLayout_passwordToggleTintMode)) {
            this.mHasPasswordToggleTintMode = true;
            this.mPasswordToggleTintMode = ViewUtils.parseTintMode(obtainStyledAttributes.getInt(R.styleable.TextInputLayout_passwordToggleTintMode, -1), null);
        }
        obtainStyledAttributes.recycle();
        this.setErrorEnabled(boolean1);
        this.setCounterEnabled(boolean2);
        this.applyPasswordToggleTint();
        if (ViewCompat.getImportantForAccessibility((View)this) == 0) {
            ViewCompat.setImportantForAccessibility((View)this, 1);
        }
        ViewCompat.setAccessibilityDelegate((View)this, new TextInputAccessibilityDelegate());
    }
    
    private void addIndicator(final TextView textView, final int n) {
        if (this.mIndicatorArea == null) {
            (this.mIndicatorArea = new LinearLayout(this.getContext())).setOrientation(0);
            this.addView((View)this.mIndicatorArea, -1, -2);
            this.mIndicatorArea.addView((View)new Space(this.getContext()), (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(0, 0, 1.0f));
            if (this.mEditText != null) {
                this.adjustIndicatorPadding();
            }
        }
        this.mIndicatorArea.setVisibility(0);
        this.mIndicatorArea.addView((View)textView, n);
        ++this.mIndicatorsAdded;
    }
    
    private void adjustIndicatorPadding() {
        ViewCompat.setPaddingRelative((View)this.mIndicatorArea, ViewCompat.getPaddingStart((View)this.mEditText), 0, ViewCompat.getPaddingEnd((View)this.mEditText), this.mEditText.getPaddingBottom());
    }
    
    private void applyPasswordToggleTint() {
        if (this.mPasswordToggleDrawable != null) {
            if (this.mHasPasswordToggleTintList || this.mHasPasswordToggleTintMode) {
                this.mPasswordToggleDrawable = DrawableCompat.wrap(this.mPasswordToggleDrawable).mutate();
                if (this.mHasPasswordToggleTintList) {
                    DrawableCompat.setTintList(this.mPasswordToggleDrawable, this.mPasswordToggleTintList);
                }
                if (this.mHasPasswordToggleTintMode) {
                    DrawableCompat.setTintMode(this.mPasswordToggleDrawable, this.mPasswordToggleTintMode);
                }
                if (this.mPasswordToggleView != null && this.mPasswordToggleView.getDrawable() != this.mPasswordToggleDrawable) {
                    this.mPasswordToggleView.setImageDrawable(this.mPasswordToggleDrawable);
                }
            }
        }
    }
    
    private static boolean arrayContains(final int[] array, final int n) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    private void collapseHint(final boolean b) {
        if (this.mAnimator != null && this.mAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (b && this.mHintAnimationEnabled) {
            this.animateToExpansionFraction(1.0f);
        }
        else {
            this.mCollapsingTextHelper.setExpansionFraction(1.0f);
        }
        this.mHintExpanded = false;
    }
    
    private void ensureBackgroundDrawableStateWorkaround() {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT != 21 && sdk_INT != 22) {
            return;
        }
        final Drawable background = this.mEditText.getBackground();
        if (background != null) {
            if (!this.mHasReconstructedEditTextBackground) {
                final Drawable drawable = background.getConstantState().newDrawable();
                if (background instanceof DrawableContainer) {
                    this.mHasReconstructedEditTextBackground = DrawableUtils.setContainerConstantState((DrawableContainer)background, drawable.getConstantState());
                }
                if (!this.mHasReconstructedEditTextBackground) {
                    ViewCompat.setBackground((View)this.mEditText, drawable);
                    this.mHasReconstructedEditTextBackground = true;
                }
            }
        }
    }
    
    private void expandHint(final boolean b) {
        if (this.mAnimator != null && this.mAnimator.isRunning()) {
            this.mAnimator.cancel();
        }
        if (b && this.mHintAnimationEnabled) {
            this.animateToExpansionFraction(0.0f);
        }
        else {
            this.mCollapsingTextHelper.setExpansionFraction(0.0f);
        }
        this.mHintExpanded = true;
    }
    
    private boolean hasPasswordTransformation() {
        boolean b = false;
        if (this.mEditText != null && this.mEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            b = true;
        }
        return b;
    }
    
    private void passwordVisibilityToggleRequested(final boolean b) {
        if (this.mPasswordToggleEnabled) {
            final int selectionEnd = this.mEditText.getSelectionEnd();
            if (!this.hasPasswordTransformation()) {
                this.mEditText.setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
                this.mPasswordToggledVisible = false;
            }
            else {
                this.mEditText.setTransformationMethod((TransformationMethod)null);
                this.mPasswordToggledVisible = true;
            }
            this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
            if (b) {
                this.mPasswordToggleView.jumpDrawablesToCurrentState();
            }
            this.mEditText.setSelection(selectionEnd);
        }
    }
    
    private static void recursiveSetEnabled(final ViewGroup viewGroup, final boolean enabled) {
        for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup) {
                recursiveSetEnabled((ViewGroup)child, enabled);
            }
        }
    }
    
    private void removeIndicator(final TextView textView) {
        if (this.mIndicatorArea != null) {
            this.mIndicatorArea.removeView((View)textView);
            if (--this.mIndicatorsAdded == 0) {
                this.mIndicatorArea.setVisibility(8);
            }
        }
    }
    
    private void setEditText(final EditText mEditText) {
        if (this.mEditText == null) {
            if (!(mEditText instanceof TextInputEditText)) {
                Log.i("TextInputLayout", "EditText added is not a TextInputEditText. Please switch to using that class instead.");
            }
            this.mEditText = mEditText;
            if (!this.hasPasswordTransformation()) {
                this.mCollapsingTextHelper.setTypefaces(this.mEditText.getTypeface());
            }
            this.mCollapsingTextHelper.setExpandedTextSize(this.mEditText.getTextSize());
            final int gravity = this.mEditText.getGravity();
            this.mCollapsingTextHelper.setCollapsedTextGravity((gravity & 0xFFFFFF8F) | 0x30);
            this.mCollapsingTextHelper.setExpandedTextGravity(gravity);
            this.mEditText.addTextChangedListener((TextWatcher)new TextWatcher() {
                public void afterTextChanged(final Editable editable) {
                    boolean b = false;
                    final TextInputLayout this$0 = TextInputLayout.this;
                    if (!TextInputLayout.this.mRestoringSavedState) {
                        b = true;
                    }
                    this$0.updateLabelState(b);
                    if (TextInputLayout.this.mCounterEnabled) {
                        TextInputLayout.this.updateCounter(editable.length());
                    }
                }
                
                public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
                }
                
                public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
                }
            });
            if (this.mDefaultTextColor == null) {
                this.mDefaultTextColor = this.mEditText.getHintTextColors();
            }
            if (this.mHintEnabled && TextUtils.isEmpty(this.mHint)) {
                this.setHint(this.mOriginalHint = this.mEditText.getHint());
                this.mEditText.setHint((CharSequence)null);
            }
            if (this.mCounterView != null) {
                this.updateCounter(this.mEditText.getText().length());
            }
            if (this.mIndicatorArea != null) {
                this.adjustIndicatorPadding();
            }
            this.updatePasswordToggleView();
            this.updateLabelState(false, true);
            return;
        }
        throw new IllegalArgumentException("We already have an EditText, can only have one");
    }
    
    private void setError(@Nullable final CharSequence text, final boolean b) {
        boolean mErrorShown = true;
        this.mError = text;
        if (!this.mErrorEnabled) {
            if (TextUtils.isEmpty(text)) {
                return;
            }
            this.setErrorEnabled(true);
        }
        if (TextUtils.isEmpty(text)) {
            mErrorShown = false;
        }
        this.mErrorShown = mErrorShown;
        this.mErrorView.animate().cancel();
        if (!this.mErrorShown) {
            if (this.mErrorView.getVisibility() == 0) {
                if (!b) {
                    this.mErrorView.setText(text);
                    this.mErrorView.setVisibility(4);
                }
                else {
                    this.mErrorView.animate().alpha(0.0f).setDuration(200L).setInterpolator((TimeInterpolator)AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                        public void onAnimationEnd(final Animator animator) {
                            TextInputLayout.this.mErrorView.setText(text);
                            TextInputLayout.this.mErrorView.setVisibility(4);
                        }
                    }).start();
                }
            }
        }
        else {
            this.mErrorView.setText(text);
            this.mErrorView.setVisibility(0);
            if (!b) {
                this.mErrorView.setAlpha(1.0f);
            }
            else {
                if (this.mErrorView.getAlpha() == 1.0f) {
                    this.mErrorView.setAlpha(0.0f);
                }
                this.mErrorView.animate().alpha(1.0f).setDuration(200L).setInterpolator((TimeInterpolator)AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                    public void onAnimationStart(final Animator animator) {
                        TextInputLayout.this.mErrorView.setVisibility(0);
                    }
                }).start();
            }
        }
        this.updateEditTextBackground();
        this.updateLabelState(b);
    }
    
    private void setHintInternal(final CharSequence charSequence) {
        this.mHint = charSequence;
        this.mCollapsingTextHelper.setText(charSequence);
    }
    
    private boolean shouldShowPasswordIcon() {
        boolean b = false;
        if (this.mPasswordToggleEnabled) {
            if (this.hasPasswordTransformation() || this.mPasswordToggledVisible) {
                b = true;
            }
        }
        return b;
    }
    
    private void updateEditTextBackground() {
        if (this.mEditText == null) {
            return;
        }
        Drawable drawable = this.mEditText.getBackground();
        if (drawable != null) {
            this.ensureBackgroundDrawableStateWorkaround();
            if (android.support.v7.widget.DrawableUtils.canSafelyMutateDrawable(drawable)) {
                drawable = drawable.mutate();
            }
            if (this.mErrorShown && this.mErrorView != null) {
                drawable.setColorFilter((ColorFilter)AppCompatDrawableManager.getPorterDuffColorFilter(this.mErrorView.getCurrentTextColor(), PorterDuff$Mode.SRC_IN));
            }
            else if (this.mCounterOverflowed && this.mCounterView != null) {
                drawable.setColorFilter((ColorFilter)AppCompatDrawableManager.getPorterDuffColorFilter(this.mCounterView.getCurrentTextColor(), PorterDuff$Mode.SRC_IN));
            }
            else {
                DrawableCompat.clearColorFilter(drawable);
                this.mEditText.refreshDrawableState();
            }
        }
    }
    
    private void updateInputLayoutMargins() {
        int topMargin = 0;
        final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)this.mInputFrame.getLayoutParams();
        if (this.mHintEnabled) {
            if (this.mTmpPaint == null) {
                this.mTmpPaint = new Paint();
            }
            this.mTmpPaint.setTypeface(this.mCollapsingTextHelper.getCollapsedTypeface());
            this.mTmpPaint.setTextSize(this.mCollapsingTextHelper.getCollapsedTextSize());
            topMargin = (int)(-this.mTmpPaint.ascent());
        }
        if (topMargin != linearLayout$LayoutParams.topMargin) {
            linearLayout$LayoutParams.topMargin = topMargin;
            this.mInputFrame.requestLayout();
        }
    }
    
    private void updatePasswordToggleView() {
        if (this.mEditText != null) {
            if (!this.shouldShowPasswordIcon()) {
                if (this.mPasswordToggleView != null && this.mPasswordToggleView.getVisibility() == 0) {
                    this.mPasswordToggleView.setVisibility(8);
                }
                if (this.mPasswordToggleDummyDrawable != null) {
                    final Drawable[] compoundDrawablesRelative = TextViewCompat.getCompoundDrawablesRelative((TextView)this.mEditText);
                    if (compoundDrawablesRelative[2] == this.mPasswordToggleDummyDrawable) {
                        TextViewCompat.setCompoundDrawablesRelative((TextView)this.mEditText, compoundDrawablesRelative[0], compoundDrawablesRelative[1], this.mOriginalEditTextEndDrawable, compoundDrawablesRelative[3]);
                        this.mPasswordToggleDummyDrawable = null;
                    }
                }
            }
            else {
                if (this.mPasswordToggleView == null) {
                    (this.mPasswordToggleView = (CheckableImageButton)LayoutInflater.from(this.getContext()).inflate(R.layout.design_text_input_password_icon, (ViewGroup)this.mInputFrame, false)).setImageDrawable(this.mPasswordToggleDrawable);
                    this.mPasswordToggleView.setContentDescription(this.mPasswordToggleContentDesc);
                    this.mInputFrame.addView((View)this.mPasswordToggleView);
                    this.mPasswordToggleView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                        public void onClick(final View view) {
                            TextInputLayout.this.passwordVisibilityToggleRequested(false);
                        }
                    });
                }
                if (this.mEditText != null && ViewCompat.getMinimumHeight((View)this.mEditText) <= 0) {
                    this.mEditText.setMinimumHeight(ViewCompat.getMinimumHeight((View)this.mPasswordToggleView));
                }
                this.mPasswordToggleView.setVisibility(0);
                this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
                if (this.mPasswordToggleDummyDrawable == null) {
                    this.mPasswordToggleDummyDrawable = (Drawable)new ColorDrawable();
                }
                this.mPasswordToggleDummyDrawable.setBounds(0, 0, this.mPasswordToggleView.getMeasuredWidth(), 1);
                final Drawable[] compoundDrawablesRelative2 = TextViewCompat.getCompoundDrawablesRelative((TextView)this.mEditText);
                if (compoundDrawablesRelative2[2] != this.mPasswordToggleDummyDrawable) {
                    this.mOriginalEditTextEndDrawable = compoundDrawablesRelative2[2];
                }
                TextViewCompat.setCompoundDrawablesRelative((TextView)this.mEditText, compoundDrawablesRelative2[0], compoundDrawablesRelative2[1], this.mPasswordToggleDummyDrawable, compoundDrawablesRelative2[3]);
                this.mPasswordToggleView.setPadding(this.mEditText.getPaddingLeft(), this.mEditText.getPaddingTop(), this.mEditText.getPaddingRight(), this.mEditText.getPaddingBottom());
            }
        }
    }
    
    public void addView(final View view, final int n, final ViewGroup$LayoutParams layoutParams) {
        if (!(view instanceof EditText)) {
            super.addView(view, n, layoutParams);
        }
        else {
            final FrameLayout$LayoutParams frameLayout$LayoutParams = new FrameLayout$LayoutParams(layoutParams);
            frameLayout$LayoutParams.gravity = ((frameLayout$LayoutParams.gravity & 0xFFFFFF8F) | 0x10);
            this.mInputFrame.addView(view, (ViewGroup$LayoutParams)frameLayout$LayoutParams);
            this.mInputFrame.setLayoutParams(layoutParams);
            this.updateInputLayoutMargins();
            this.setEditText((EditText)view);
        }
    }
    
    @VisibleForTesting
    void animateToExpansionFraction(final float n) {
        if (this.mCollapsingTextHelper.getExpansionFraction() == n) {
            return;
        }
        if (this.mAnimator == null) {
            (this.mAnimator = new ValueAnimator()).setInterpolator((TimeInterpolator)AnimationUtils.LINEAR_INTERPOLATOR);
            this.mAnimator.setDuration(200L);
            this.mAnimator.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    TextInputLayout.this.mCollapsingTextHelper.setExpansionFraction((float)valueAnimator.getAnimatedValue());
                }
            });
        }
        this.mAnimator.setFloatValues(new float[] { this.mCollapsingTextHelper.getExpansionFraction(), n });
        this.mAnimator.start();
    }
    
    public void dispatchProvideAutofillStructure(final ViewStructure viewStructure, final int n) {
        if (this.mOriginalHint == null || this.mEditText == null) {
            super.dispatchProvideAutofillStructure(viewStructure, n);
            return;
        }
        final CharSequence hint = this.mEditText.getHint();
        this.mEditText.setHint(this.mOriginalHint);
        try {
            super.dispatchProvideAutofillStructure(viewStructure, n);
        }
        finally {
            this.mEditText.setHint(hint);
        }
    }
    
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> sparseArray) {
        this.mRestoringSavedState = true;
        super.dispatchRestoreInstanceState((SparseArray)sparseArray);
        this.mRestoringSavedState = false;
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        if (this.mHintEnabled) {
            this.mCollapsingTextHelper.draw(canvas);
        }
    }
    
    protected void drawableStateChanged() {
        boolean b = true;
        if (!this.mInDrawableStateChanged) {
            this.mInDrawableStateChanged = true;
            super.drawableStateChanged();
            final int[] drawableState = this.getDrawableState();
            if (!ViewCompat.isLaidOut((View)this) || !this.isEnabled()) {
                b = false;
            }
            this.updateLabelState(b);
            this.updateEditTextBackground();
            if (this.mCollapsingTextHelper != null && (this.mCollapsingTextHelper.setState(drawableState) | false)) {
                this.invalidate();
            }
            this.mInDrawableStateChanged = false;
        }
    }
    
    public int getCounterMaxLength() {
        return this.mCounterMaxLength;
    }
    
    @Nullable
    public EditText getEditText() {
        return this.mEditText;
    }
    
    @Nullable
    public CharSequence getError() {
        CharSequence mError;
        if (!this.mErrorEnabled) {
            mError = null;
        }
        else {
            mError = this.mError;
        }
        return mError;
    }
    
    @Nullable
    public CharSequence getHint() {
        CharSequence mHint;
        if (!this.mHintEnabled) {
            mHint = null;
        }
        else {
            mHint = this.mHint;
        }
        return mHint;
    }
    
    @Nullable
    public CharSequence getPasswordVisibilityToggleContentDescription() {
        return this.mPasswordToggleContentDesc;
    }
    
    @Nullable
    public Drawable getPasswordVisibilityToggleDrawable() {
        return this.mPasswordToggleDrawable;
    }
    
    @NonNull
    public Typeface getTypeface() {
        return this.mTypeface;
    }
    
    public boolean isCounterEnabled() {
        return this.mCounterEnabled;
    }
    
    public boolean isErrorEnabled() {
        return this.mErrorEnabled;
    }
    
    public boolean isHintAnimationEnabled() {
        return this.mHintAnimationEnabled;
    }
    
    public boolean isHintEnabled() {
        return this.mHintEnabled;
    }
    
    @VisibleForTesting
    final boolean isHintExpanded() {
        return this.mHintExpanded;
    }
    
    public boolean isPasswordVisibilityToggleEnabled() {
        return this.mPasswordToggleEnabled;
    }
    
    protected void onLayout(final boolean b, int n, final int n2, int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        if (this.mHintEnabled && this.mEditText != null) {
            final Rect mTmpRect = this.mTmpRect;
            ViewGroupUtils.getDescendantRect((ViewGroup)this, (View)this.mEditText, mTmpRect);
            n3 = mTmpRect.left + this.mEditText.getCompoundPaddingLeft();
            n = mTmpRect.right - this.mEditText.getCompoundPaddingRight();
            this.mCollapsingTextHelper.setExpandedBounds(n3, mTmpRect.top + this.mEditText.getCompoundPaddingTop(), n, mTmpRect.bottom - this.mEditText.getCompoundPaddingBottom());
            this.mCollapsingTextHelper.setCollapsedBounds(n3, this.getPaddingTop(), n, n4 - n2 - this.getPaddingBottom());
            this.mCollapsingTextHelper.recalculate();
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        this.updatePasswordToggleView();
        super.onMeasure(n, n2);
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            final SavedState savedState = (SavedState)parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.setError(savedState.error);
            if (savedState.isPasswordToggledVisible) {
                this.passwordVisibilityToggleRequested(true);
            }
            this.requestLayout();
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mErrorShown) {
            savedState.error = this.getError();
        }
        savedState.isPasswordToggledVisible = this.mPasswordToggledVisible;
        return (Parcelable)savedState;
    }
    
    public void setCounterEnabled(final boolean mCounterEnabled) {
        if (this.mCounterEnabled != mCounterEnabled) {
            Label_0026: {
                if (!mCounterEnabled) {
                    this.removeIndicator(this.mCounterView);
                    this.mCounterView = null;
                }
                else {
                    (this.mCounterView = new AppCompatTextView(this.getContext())).setId(R.id.textinput_counter);
                    Label_0120: {
                        if (this.mTypeface != null) {
                            break Label_0120;
                        }
                    Label_0085_Outer:
                        while (true) {
                            this.mCounterView.setMaxLines(1);
                            while (true) {
                                try {
                                    TextViewCompat.setTextAppearance(this.mCounterView, this.mCounterTextAppearance);
                                    this.addIndicator(this.mCounterView, -1);
                                    if (this.mEditText != null) {
                                        this.updateCounter(this.mEditText.getText().length());
                                        break Label_0026;
                                    }
                                    break;
                                    this.mCounterView.setTypeface(this.mTypeface);
                                    continue Label_0085_Outer;
                                }
                                catch (final Exception ex) {
                                    TextViewCompat.setTextAppearance(this.mCounterView, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Caption);
                                    this.mCounterView.setTextColor(ContextCompat.getColor(this.getContext(), android.support.v7.appcompat.R.color.error_color_material));
                                    continue;
                                }
                                break;
                            }
                            break;
                        }
                    }
                    this.updateCounter(0);
                }
            }
            this.mCounterEnabled = mCounterEnabled;
        }
    }
    
    public void setCounterMaxLength(int length) {
        final int n = 0;
        if (this.mCounterMaxLength != length) {
            if (length <= 0) {
                this.mCounterMaxLength = -1;
            }
            else {
                this.mCounterMaxLength = length;
            }
            if (this.mCounterEnabled) {
                length = n;
                if (this.mEditText != null) {
                    length = this.mEditText.getText().length();
                }
                this.updateCounter(length);
            }
        }
    }
    
    public void setEnabled(final boolean enabled) {
        recursiveSetEnabled((ViewGroup)this, enabled);
        super.setEnabled(enabled);
    }
    
    public void setError(@Nullable final CharSequence charSequence) {
        final boolean b = false;
        boolean b2;
        if (!ViewCompat.isLaidOut((View)this)) {
            b2 = b;
        }
        else {
            b2 = b;
            if (this.isEnabled()) {
                b2 = (this.mErrorView == null || !TextUtils.equals(this.mErrorView.getText(), charSequence) || b);
            }
        }
        this.setError(charSequence, b2);
    }
    
    public void setErrorEnabled(final boolean mErrorEnabled) {
        if (this.mErrorEnabled != mErrorEnabled) {
            if (this.mErrorView != null) {
                this.mErrorView.animate().cancel();
            }
            if (!mErrorEnabled) {
                this.mErrorShown = false;
                this.updateEditTextBackground();
                this.removeIndicator(this.mErrorView);
                this.mErrorView = null;
            }
            else {
                (this.mErrorView = new AppCompatTextView(this.getContext())).setId(R.id.textinput_error);
                Label_0150: {
                    if (this.mTypeface != null) {
                        break Label_0150;
                    }
                    int n;
                    Label_0118_Outer:Label_0122_Outer:
                    while (true) {
                        while (true) {
                        Label_0198:
                            while (true) {
                                try {
                                    TextViewCompat.setTextAppearance(this.mErrorView, this.mErrorTextAppearance);
                                    if (Build$VERSION.SDK_INT < 23) {
                                        n = 0;
                                    }
                                    else if (this.mErrorView.getTextColors().getDefaultColor() != -65281) {
                                        n = 0;
                                    }
                                    else {
                                        n = 1;
                                    }
                                    if (n == 0) {
                                        this.mErrorView.setVisibility(4);
                                        ViewCompat.setAccessibilityLiveRegion((View)this.mErrorView, 1);
                                        this.addIndicator(this.mErrorView, 0);
                                        break;
                                    }
                                    break Label_0198;
                                    this.mErrorView.setTypeface(this.mTypeface);
                                    continue Label_0118_Outer;
                                }
                                catch (final Exception ex) {
                                    n = 1;
                                    continue Label_0122_Outer;
                                }
                                continue Label_0122_Outer;
                            }
                            TextViewCompat.setTextAppearance(this.mErrorView, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Caption);
                            this.mErrorView.setTextColor(ContextCompat.getColor(this.getContext(), android.support.v7.appcompat.R.color.error_color_material));
                            continue;
                        }
                    }
                }
            }
            this.mErrorEnabled = mErrorEnabled;
        }
    }
    
    public void setErrorTextAppearance(@StyleRes final int mErrorTextAppearance) {
        this.mErrorTextAppearance = mErrorTextAppearance;
        if (this.mErrorView != null) {
            TextViewCompat.setTextAppearance(this.mErrorView, mErrorTextAppearance);
        }
    }
    
    public void setHint(@Nullable final CharSequence hintInternal) {
        if (this.mHintEnabled) {
            this.setHintInternal(hintInternal);
            this.sendAccessibilityEvent(2048);
        }
    }
    
    public void setHintAnimationEnabled(final boolean mHintAnimationEnabled) {
        this.mHintAnimationEnabled = mHintAnimationEnabled;
    }
    
    public void setHintEnabled(final boolean mHintEnabled) {
        if (mHintEnabled != this.mHintEnabled) {
            this.mHintEnabled = mHintEnabled;
            final CharSequence hint = this.mEditText.getHint();
            if (this.mHintEnabled) {
                if (!TextUtils.isEmpty(hint)) {
                    if (TextUtils.isEmpty(this.mHint)) {
                        this.setHint(hint);
                    }
                    this.mEditText.setHint((CharSequence)null);
                }
            }
            else {
                if (!TextUtils.isEmpty(this.mHint) && TextUtils.isEmpty(hint)) {
                    this.mEditText.setHint(this.mHint);
                }
                this.setHintInternal(null);
            }
            if (this.mEditText != null) {
                this.updateInputLayoutMargins();
            }
        }
    }
    
    public void setHintTextAppearance(@StyleRes final int collapsedTextAppearance) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(collapsedTextAppearance);
        this.mFocusedTextColor = this.mCollapsingTextHelper.getCollapsedTextColor();
        if (this.mEditText != null) {
            this.updateLabelState(false);
            this.updateInputLayoutMargins();
        }
    }
    
    public void setPasswordVisibilityToggleContentDescription(@StringRes final int n) {
        CharSequence text;
        if (n == 0) {
            text = null;
        }
        else {
            text = this.getResources().getText(n);
        }
        this.setPasswordVisibilityToggleContentDescription(text);
    }
    
    public void setPasswordVisibilityToggleContentDescription(@Nullable final CharSequence charSequence) {
        this.mPasswordToggleContentDesc = charSequence;
        if (this.mPasswordToggleView != null) {
            this.mPasswordToggleView.setContentDescription(charSequence);
        }
    }
    
    public void setPasswordVisibilityToggleDrawable(@DrawableRes final int n) {
        Drawable drawable;
        if (n == 0) {
            drawable = null;
        }
        else {
            drawable = AppCompatResources.getDrawable(this.getContext(), n);
        }
        this.setPasswordVisibilityToggleDrawable(drawable);
    }
    
    public void setPasswordVisibilityToggleDrawable(@Nullable final Drawable drawable) {
        this.mPasswordToggleDrawable = drawable;
        if (this.mPasswordToggleView != null) {
            this.mPasswordToggleView.setImageDrawable(drawable);
        }
    }
    
    public void setPasswordVisibilityToggleEnabled(final boolean mPasswordToggleEnabled) {
        if (this.mPasswordToggleEnabled != mPasswordToggleEnabled) {
            this.mPasswordToggleEnabled = mPasswordToggleEnabled;
            if (!mPasswordToggleEnabled && this.mPasswordToggledVisible && this.mEditText != null) {
                this.mEditText.setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
            }
            this.mPasswordToggledVisible = false;
            this.updatePasswordToggleView();
        }
    }
    
    public void setPasswordVisibilityToggleTintList(@Nullable final ColorStateList mPasswordToggleTintList) {
        this.mPasswordToggleTintList = mPasswordToggleTintList;
        this.mHasPasswordToggleTintList = true;
        this.applyPasswordToggleTint();
    }
    
    public void setPasswordVisibilityToggleTintMode(@Nullable final PorterDuff$Mode mPasswordToggleTintMode) {
        this.mPasswordToggleTintMode = mPasswordToggleTintMode;
        this.mHasPasswordToggleTintMode = true;
        this.applyPasswordToggleTint();
    }
    
    public void setTypeface(@Nullable final Typeface typeface) {
        Label_0026: {
            if (this.mTypeface != null && !this.mTypeface.equals((Object)typeface)) {
                break Label_0026;
            }
            if (this.mTypeface == null) {
                if (typeface != null) {
                    break Label_0026;
                }
            }
            return;
        }
        this.mTypeface = typeface;
        this.mCollapsingTextHelper.setTypefaces(typeface);
        if (this.mCounterView != null) {
            this.mCounterView.setTypeface(typeface);
        }
        if (this.mErrorView != null) {
            this.mErrorView.setTypeface(typeface);
        }
    }
    
    void updateCounter(final int n) {
        final boolean mCounterOverflowed = this.mCounterOverflowed;
        if (this.mCounterMaxLength != -1) {
            this.mCounterOverflowed = (n > this.mCounterMaxLength);
            if (mCounterOverflowed != this.mCounterOverflowed) {
                final TextView mCounterView = this.mCounterView;
                int n2;
                if (!this.mCounterOverflowed) {
                    n2 = this.mCounterTextAppearance;
                }
                else {
                    n2 = this.mCounterOverflowTextAppearance;
                }
                TextViewCompat.setTextAppearance(mCounterView, n2);
            }
            this.mCounterView.setText((CharSequence)this.getContext().getString(R.string.character_counter_pattern, new Object[] { n, this.mCounterMaxLength }));
        }
        else {
            this.mCounterView.setText((CharSequence)String.valueOf(n));
            this.mCounterOverflowed = false;
        }
        if (this.mEditText != null && mCounterOverflowed != this.mCounterOverflowed) {
            this.updateLabelState(false);
            this.updateEditTextBackground();
        }
    }
    
    void updateLabelState(final boolean b) {
        this.updateLabelState(b, false);
    }
    
    void updateLabelState(final boolean b, final boolean b2) {
        boolean b3 = false;
        final boolean enabled = this.isEnabled();
        boolean b4;
        if (this.mEditText != null && !TextUtils.isEmpty((CharSequence)this.mEditText.getText())) {
            b4 = true;
        }
        else {
            b4 = false;
        }
        final boolean arrayContains = arrayContains(this.getDrawableState(), 16842908);
        if (!TextUtils.isEmpty(this.getError())) {
            b3 = true;
        }
        if (this.mDefaultTextColor != null) {
            this.mCollapsingTextHelper.setExpandedTextColor(this.mDefaultTextColor);
        }
        if (enabled && this.mCounterOverflowed && this.mCounterView != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(this.mCounterView.getTextColors());
        }
        else if (enabled && arrayContains && this.mFocusedTextColor != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(this.mFocusedTextColor);
        }
        else if (this.mDefaultTextColor != null) {
            this.mCollapsingTextHelper.setCollapsedTextColor(this.mDefaultTextColor);
        }
        if (!b4 && (this.isEnabled() && (arrayContains || b3))) {
            if (b2 || !this.mHintExpanded) {
                this.expandHint(b);
            }
        }
        else if (b2 || this.mHintExpanded) {
            this.collapseHint(b);
        }
    }
    
    static class SavedState extends AbsSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        CharSequence error;
        boolean isPasswordToggledVisible;
        
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
            this.error = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.isPasswordToggledVisible = (parcel.readInt() == 1);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        @Override
        public String toString() {
            return "TextInputLayout.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " error=" + (Object)this.error + "}";
        }
        
        @Override
        public void writeToParcel(final Parcel parcel, int n) {
            final int n2 = 0;
            super.writeToParcel(parcel, n);
            TextUtils.writeToParcel(this.error, parcel, n);
            if (!this.isPasswordToggledVisible) {
                n = n2;
            }
            else {
                n = 1;
            }
            parcel.writeInt(n);
        }
    }
    
    private class TextInputAccessibilityDelegate extends AccessibilityDelegateCompat
    {
        TextInputAccessibilityDelegate() {
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)TextInputLayout.class.getSimpleName());
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final CharSequence charSequence = null;
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(TextInputLayout.class.getSimpleName());
            final CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityNodeInfoCompat.setText(text);
            }
            if (TextInputLayout.this.mEditText != null) {
                accessibilityNodeInfoCompat.setLabelFor((View)TextInputLayout.this.mEditText);
            }
            CharSequence text2;
            if (TextInputLayout.this.mErrorView == null) {
                text2 = charSequence;
            }
            else {
                text2 = TextInputLayout.this.mErrorView.getText();
            }
            if (!TextUtils.isEmpty(text2)) {
                accessibilityNodeInfoCompat.setContentInvalid(true);
                accessibilityNodeInfoCompat.setError(text2);
            }
        }
        
        @Override
        public void onPopulateAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onPopulateAccessibilityEvent(view, accessibilityEvent);
            final CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityEvent.getText().add(text);
            }
        }
    }
}
