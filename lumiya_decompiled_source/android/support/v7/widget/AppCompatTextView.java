package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.widget.AutoSizeableTextView;
import android.util.AttributeSet;
import android.widget.TextView;
/* loaded from: classes.dex */
public class AppCompatTextView extends TextView implements TintableBackgroundView, AutoSizeableTextView {
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    private final AppCompatTextHelper mTextHelper;

    public AppCompatTextView(Context context) {
        this(context, null);
    }

    public AppCompatTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public AppCompatTextView(Context context, AttributeSet attributeSet, int i) {
        super(TintContextWrapper.wrap(context), attributeSet, i);
        this.mBackgroundTintHelper = new AppCompatBackgroundHelper(this);
        this.mBackgroundTintHelper.loadFromAttributes(attributeSet, i);
        this.mTextHelper = AppCompatTextHelper.create(this);
        this.mTextHelper.loadFromAttributes(attributeSet, i);
        this.mTextHelper.applyCompoundDrawablesTints();
    }

    @Override // android.widget.TextView, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySupportBackgroundTint();
        }
        if (this.mTextHelper == null) {
            return;
        }
        this.mTextHelper.applyCompoundDrawablesTints();
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeMaxTextSize() {
        if (Build.VERSION.SDK_INT < 26) {
            if (this.mTextHelper == null) {
                return -1;
            }
            return this.mTextHelper.getAutoSizeMaxTextSize();
        }
        return super.getAutoSizeMaxTextSize();
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeMinTextSize() {
        if (Build.VERSION.SDK_INT < 26) {
            if (this.mTextHelper == null) {
                return -1;
            }
            return this.mTextHelper.getAutoSizeMinTextSize();
        }
        return super.getAutoSizeMinTextSize();
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeStepGranularity() {
        if (Build.VERSION.SDK_INT < 26) {
            if (this.mTextHelper == null) {
                return -1;
            }
            return this.mTextHelper.getAutoSizeStepGranularity();
        }
        return super.getAutoSizeStepGranularity();
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int[] getAutoSizeTextAvailableSizes() {
        return Build.VERSION.SDK_INT < 26 ? this.mTextHelper == null ? new int[0] : this.mTextHelper.getAutoSizeTextAvailableSizes() : super.getAutoSizeTextAvailableSizes();
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public int getAutoSizeTextType() {
        if (Build.VERSION.SDK_INT >= 26) {
            return super.getAutoSizeTextType() != 1 ? 0 : 1;
        } else if (this.mTextHelper == null) {
            return 0;
        } else {
            return this.mTextHelper.getAutoSizeTextType();
        }
    }

    @Override // android.support.v4.view.TintableBackgroundView
    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public ColorStateList getSupportBackgroundTintList() {
        if (this.mBackgroundTintHelper == null) {
            return null;
        }
        return this.mBackgroundTintHelper.getSupportBackgroundTintList();
    }

    @Override // android.support.v4.view.TintableBackgroundView
    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        if (this.mBackgroundTintHelper == null) {
            return null;
        }
        return this.mBackgroundTintHelper.getSupportBackgroundTintMode();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mTextHelper == null) {
            return;
        }
        this.mTextHelper.onLayout(z, i, i2, i3, i4);
    }

    @Override // android.widget.TextView
    protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        if (this.mTextHelper != null && Build.VERSION.SDK_INT < 26 && this.mTextHelper.isAutoSizeEnabled()) {
            this.mTextHelper.autoSizeText();
        }
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setAutoSizeTextTypeUniformWithConfiguration(int i, int i2, int i3, int i4) throws IllegalArgumentException {
        if (Build.VERSION.SDK_INT >= 26) {
            super.setAutoSizeTextTypeUniformWithConfiguration(i, i2, i3, i4);
        } else if (this.mTextHelper == null) {
        } else {
            this.mTextHelper.setAutoSizeTextTypeUniformWithConfiguration(i, i2, i3, i4);
        }
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] iArr, int i) throws IllegalArgumentException {
        if (Build.VERSION.SDK_INT >= 26) {
            super.setAutoSizeTextTypeUniformWithPresetSizes(iArr, i);
        } else if (this.mTextHelper == null) {
        } else {
            this.mTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(iArr, i);
        }
    }

    @Override // android.widget.TextView, android.support.v4.widget.AutoSizeableTextView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setAutoSizeTextTypeWithDefaults(int i) {
        if (Build.VERSION.SDK_INT >= 26) {
            super.setAutoSizeTextTypeWithDefaults(i);
        } else if (this.mTextHelper == null) {
        } else {
            this.mTextHelper.setAutoSizeTextTypeWithDefaults(i);
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        if (this.mBackgroundTintHelper == null) {
            return;
        }
        this.mBackgroundTintHelper.onSetBackgroundDrawable(drawable);
    }

    @Override // android.view.View
    public void setBackgroundResource(@DrawableRes int i) {
        super.setBackgroundResource(i);
        if (this.mBackgroundTintHelper == null) {
            return;
        }
        this.mBackgroundTintHelper.onSetBackgroundResource(i);
    }

    @Override // android.support.v4.view.TintableBackgroundView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setSupportBackgroundTintList(@Nullable ColorStateList colorStateList) {
        if (this.mBackgroundTintHelper == null) {
            return;
        }
        this.mBackgroundTintHelper.setSupportBackgroundTintList(colorStateList);
    }

    @Override // android.support.v4.view.TintableBackgroundView
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        if (this.mBackgroundTintHelper == null) {
            return;
        }
        this.mBackgroundTintHelper.setSupportBackgroundTintMode(mode);
    }

    @Override // android.widget.TextView
    public void setTextAppearance(Context context, int i) {
        super.setTextAppearance(context, i);
        if (this.mTextHelper == null) {
            return;
        }
        this.mTextHelper.onSetTextAppearance(context, i);
    }

    @Override // android.widget.TextView
    public void setTextSize(int i, float f) {
        if (Build.VERSION.SDK_INT >= 26) {
            super.setTextSize(i, f);
        } else if (this.mTextHelper == null) {
        } else {
            this.mTextHelper.setTextSize(i, f);
        }
    }
}
