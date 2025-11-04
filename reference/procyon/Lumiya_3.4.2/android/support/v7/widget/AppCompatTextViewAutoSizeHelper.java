// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.support.annotation.RestrictTo;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.Log;
import android.support.annotation.NonNull;
import android.os.Build$VERSION;
import android.annotation.TargetApi;
import android.text.StaticLayout$Builder;
import android.text.TextDirectionHeuristics;
import android.text.TextDirectionHeuristic;
import android.text.StaticLayout;
import android.text.Layout$Alignment;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import android.widget.TextView;
import android.text.TextPaint;
import android.content.Context;
import java.lang.reflect.Method;
import java.util.Hashtable;
import android.graphics.RectF;

class AppCompatTextViewAutoSizeHelper
{
    private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
    private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
    private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
    private static final String TAG = "ACTVAutoSizeHelper";
    private static final RectF TEMP_RECTF;
    static final float UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE = -1.0f;
    private static final int VERY_WIDE = 1048576;
    private static Hashtable<String, Method> sTextViewMethodByNameCache;
    private float mAutoSizeMaxTextSizeInPx;
    private float mAutoSizeMinTextSizeInPx;
    private float mAutoSizeStepGranularityInPx;
    private int[] mAutoSizeTextSizesInPx;
    private int mAutoSizeTextType;
    private final Context mContext;
    private boolean mHasPresetAutoSizeValues;
    private boolean mNeedsAutoSizeText;
    private TextPaint mTempTextPaint;
    private final TextView mTextView;
    
    static {
        TEMP_RECTF = new RectF();
        AppCompatTextViewAutoSizeHelper.sTextViewMethodByNameCache = new Hashtable<String, Method>();
    }
    
    AppCompatTextViewAutoSizeHelper(final TextView mTextView) {
        this.mAutoSizeTextType = 0;
        this.mNeedsAutoSizeText = false;
        this.mAutoSizeStepGranularityInPx = -1.0f;
        this.mAutoSizeMinTextSizeInPx = -1.0f;
        this.mAutoSizeMaxTextSizeInPx = -1.0f;
        this.mAutoSizeTextSizesInPx = new int[0];
        this.mHasPresetAutoSizeValues = false;
        this.mTextView = mTextView;
        this.mContext = this.mTextView.getContext();
    }
    
    private int[] cleanupAutoSizePresetSizes(int[] a) {
        final int length = a.length;
        if (length == 0) {
            return a;
        }
        Arrays.sort(a);
        final ArrayList list = new ArrayList();
        for (final int n : a) {
            if (n > 0 && Collections.binarySearch(list, n) < 0) {
                list.add(n);
            }
        }
        if (length != list.size()) {
            final int size = list.size();
            a = new int[size];
            for (int j = 0; j < size; ++j) {
                a[j] = (int)list.get(j);
            }
            return a;
        }
        return a;
    }
    
    private void clearAutoSizeConfiguration() {
        this.mAutoSizeTextType = 0;
        this.mAutoSizeMinTextSizeInPx = -1.0f;
        this.mAutoSizeMaxTextSizeInPx = -1.0f;
        this.mAutoSizeStepGranularityInPx = -1.0f;
        this.mAutoSizeTextSizesInPx = new int[0];
        this.mNeedsAutoSizeText = false;
    }
    
    @TargetApi(23)
    private StaticLayout createStaticLayoutForMeasuring(final CharSequence charSequence, final Layout$Alignment alignment, final int n, int maxLines) {
        final TextDirectionHeuristic textDirection = this.invokeAndReturnWithDefault(this.mTextView, "getTextDirectionHeuristic", TextDirectionHeuristics.FIRSTSTRONG_LTR);
        final StaticLayout$Builder setHyphenationFrequency = StaticLayout$Builder.obtain(charSequence, 0, charSequence.length(), this.mTempTextPaint, n).setAlignment(alignment).setLineSpacing(this.mTextView.getLineSpacingExtra(), this.mTextView.getLineSpacingMultiplier()).setIncludePad(this.mTextView.getIncludeFontPadding()).setBreakStrategy(this.mTextView.getBreakStrategy()).setHyphenationFrequency(this.mTextView.getHyphenationFrequency());
        if (maxLines == -1) {
            maxLines = Integer.MAX_VALUE;
        }
        return setHyphenationFrequency.setMaxLines(maxLines).setTextDirection(textDirection).build();
    }
    
    @TargetApi(14)
    private StaticLayout createStaticLayoutForMeasuringPre23(final CharSequence charSequence, final Layout$Alignment layout$Alignment, final int n) {
        float n2;
        float n3;
        boolean b;
        if (Build$VERSION.SDK_INT < 16) {
            n2 = this.invokeAndReturnWithDefault(this.mTextView, "getLineSpacingMultiplier", 1.0f);
            n3 = this.invokeAndReturnWithDefault(this.mTextView, "getLineSpacingExtra", 0.0f);
            b = this.invokeAndReturnWithDefault(this.mTextView, "getIncludeFontPadding", true);
        }
        else {
            n2 = this.mTextView.getLineSpacingMultiplier();
            n3 = this.mTextView.getLineSpacingExtra();
            b = this.mTextView.getIncludeFontPadding();
        }
        return new StaticLayout(charSequence, this.mTempTextPaint, n, layout$Alignment, n2, n3, b);
    }
    
    private int findLargestTextSizeWhichFits(final RectF rectF) {
        int n = 0;
        int length = this.mAutoSizeTextSizesInPx.length;
        if (length != 0) {
            int i = 1;
            --length;
            while (i <= length) {
                final int n2 = (i + length) / 2;
                if (!this.suggestedSizeFitsInSpace(this.mAutoSizeTextSizesInPx[n2], rectF)) {
                    length = (n = n2 - 1);
                }
                else {
                    n = i;
                    i = n2 + 1;
                }
            }
            return this.mAutoSizeTextSizesInPx[n];
        }
        throw new IllegalStateException("No available text sizes to choose from.");
    }
    
    @Nullable
    private Method getTextViewMethod(@NonNull final String s) {
        try {
            Method method = AppCompatTextViewAutoSizeHelper.sTextViewMethodByNameCache.get(s);
            if (method == null) {
                final Method declaredMethod = TextView.class.getDeclaredMethod(s, (Class<?>[])new Class[0]);
                if ((method = declaredMethod) != null) {
                    declaredMethod.setAccessible(true);
                    AppCompatTextViewAutoSizeHelper.sTextViewMethodByNameCache.put(s, declaredMethod);
                    method = declaredMethod;
                }
            }
            return method;
        }
        catch (final Exception ex) {
            Log.w("ACTVAutoSizeHelper", "Failed to retrieve TextView#" + s + "() method", (Throwable)ex);
            return null;
        }
    }
    
    private <T> T invokeAndReturnWithDefault(@NonNull Object obj, @NonNull final String str, @NonNull T invoke) {
        try {
            obj = (invoke = (T)this.getTextViewMethod(str).invoke(obj, new Object[0]));
            return invoke;
        }
        catch (final Exception ex) {
            Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#" + str + "() method", (Throwable)ex);
            if (false) {
                invoke = null;
                return invoke;
            }
            return invoke;
        }
    }
    
    private void setRawTextSize(final float textSize) {
        if (textSize != this.mTextView.getPaint().getTextSize()) {
            this.mTextView.getPaint().setTextSize(textSize);
            final boolean b = Build$VERSION.SDK_INT >= 18 && this.mTextView.isInLayout();
            if (this.mTextView.getLayout() != null) {
            Label_0086_Outer:
                while (true) {
                    this.mNeedsAutoSizeText = false;
                    while (true) {
                    Label_0126:
                        while (true) {
                            try {
                                final Method textViewMethod = this.getTextViewMethod("nullLayouts");
                                if (textViewMethod != null) {
                                    textViewMethod.invoke(this.mTextView, new Object[0]);
                                }
                                if (b) {
                                    this.mTextView.forceLayout();
                                    this.mTextView.invalidate();
                                    break;
                                }
                                break Label_0126;
                            }
                            catch (final Exception ex) {
                                Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#nullLayouts() method", (Throwable)ex);
                                continue Label_0086_Outer;
                            }
                            continue Label_0086_Outer;
                        }
                        this.mTextView.requestLayout();
                        continue;
                    }
                }
            }
        }
    }
    
    private boolean setupAutoSizeText() {
        if (this.supportsAutoSizeText() && this.mAutoSizeTextType == 1) {
            if (!this.mHasPresetAutoSizeValues || this.mAutoSizeTextSizesInPx.length == 0) {
                float n = (float)Math.round(this.mAutoSizeMinTextSizeInPx);
                int n2 = 1;
                while (Math.round(this.mAutoSizeStepGranularityInPx + n) <= Math.round(this.mAutoSizeMaxTextSizeInPx)) {
                    ++n2;
                    n += this.mAutoSizeStepGranularityInPx;
                }
                final int[] array = new int[n2];
                float mAutoSizeMinTextSizeInPx = this.mAutoSizeMinTextSizeInPx;
                for (int i = 0; i < n2; ++i) {
                    array[i] = Math.round(mAutoSizeMinTextSizeInPx);
                    mAutoSizeMinTextSizeInPx += this.mAutoSizeStepGranularityInPx;
                }
                this.mAutoSizeTextSizesInPx = this.cleanupAutoSizePresetSizes(array);
            }
            this.mNeedsAutoSizeText = true;
        }
        else {
            this.mNeedsAutoSizeText = false;
        }
        return this.mNeedsAutoSizeText;
    }
    
    private void setupAutoSizeUniformPresetSizes(final TypedArray typedArray) {
        int i = 0;
        final int length = typedArray.length();
        final int[] array = new int[length];
        if (length > 0) {
            while (i < length) {
                array[i] = typedArray.getDimensionPixelSize(i, -1);
                ++i;
            }
            this.mAutoSizeTextSizesInPx = this.cleanupAutoSizePresetSizes(array);
            this.setupAutoSizeUniformPresetSizesConfiguration();
        }
    }
    
    private boolean setupAutoSizeUniformPresetSizesConfiguration() {
        final int length = this.mAutoSizeTextSizesInPx.length;
        if (this.mHasPresetAutoSizeValues = (length > 0)) {
            this.mAutoSizeTextType = 1;
            this.mAutoSizeMinTextSizeInPx = (float)this.mAutoSizeTextSizesInPx[0];
            this.mAutoSizeMaxTextSizeInPx = (float)this.mAutoSizeTextSizesInPx[length - 1];
            this.mAutoSizeStepGranularityInPx = -1.0f;
        }
        return this.mHasPresetAutoSizeValues;
    }
    
    private boolean suggestedSizeFitsInSpace(final int n, final RectF rectF) {
        final CharSequence text = this.mTextView.getText();
        int maxLines;
        if (Build$VERSION.SDK_INT < 16) {
            maxLines = -1;
        }
        else {
            maxLines = this.mTextView.getMaxLines();
        }
        if (this.mTempTextPaint != null) {
            this.mTempTextPaint.reset();
        }
        else {
            this.mTempTextPaint = new TextPaint();
        }
        this.mTempTextPaint.set(this.mTextView.getPaint());
        this.mTempTextPaint.setTextSize((float)n);
        final Layout$Alignment layout$Alignment = this.invokeAndReturnWithDefault(this.mTextView, "getLayoutAlignment", Layout$Alignment.ALIGN_NORMAL);
        StaticLayout staticLayout;
        if (Build$VERSION.SDK_INT < 23) {
            staticLayout = this.createStaticLayoutForMeasuringPre23(text, layout$Alignment, Math.round(rectF.right));
        }
        else {
            staticLayout = this.createStaticLayoutForMeasuring(text, layout$Alignment, Math.round(rectF.right), maxLines);
        }
        return (maxLines == -1 || (staticLayout.getLineCount() <= maxLines && staticLayout.getLineEnd(staticLayout.getLineCount() - 1) == text.length())) && staticLayout.getHeight() <= rectF.bottom;
    }
    
    private boolean supportsAutoSizeText() {
        boolean b = false;
        if (!(this.mTextView instanceof AppCompatEditText)) {
            b = true;
        }
        return b;
    }
    
    private void validateAndSetAutoSizeTextTypeUniformConfiguration(final float mAutoSizeMinTextSizeInPx, final float n, final float n2) throws IllegalArgumentException {
        if (mAutoSizeMinTextSizeInPx <= 0.0f) {
            throw new IllegalArgumentException("Minimum auto-size text size (" + mAutoSizeMinTextSizeInPx + "px) is less or equal to (0px)");
        }
        if (n <= mAutoSizeMinTextSizeInPx) {
            throw new IllegalArgumentException("Maximum auto-size text size (" + n + "px) is less or equal to minimum auto-size " + "text size (" + mAutoSizeMinTextSizeInPx + "px)");
        }
        if (n2 <= 0.0f) {
            throw new IllegalArgumentException("The auto-size step granularity (" + n2 + "px) is less or equal to (0px)");
        }
        this.mAutoSizeTextType = 1;
        this.mAutoSizeMinTextSizeInPx = mAutoSizeMinTextSizeInPx;
        this.mAutoSizeMaxTextSizeInPx = n;
        this.mAutoSizeStepGranularityInPx = n2;
        this.mHasPresetAutoSizeValues = false;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    void autoSizeText() {
        if (this.isAutoSizeEnabled()) {
            if (this.mNeedsAutoSizeText) {
                if (this.mTextView.getMeasuredHeight() <= 0 || this.mTextView.getMeasuredWidth() <= 0) {
                    return;
                }
                int n;
                if (!this.invokeAndReturnWithDefault(this.mTextView, "getHorizontallyScrolling", false)) {
                    n = this.mTextView.getMeasuredWidth() - this.mTextView.getTotalPaddingLeft() - this.mTextView.getTotalPaddingRight();
                }
                else {
                    n = 1048576;
                }
                final int n2 = this.mTextView.getHeight() - this.mTextView.getCompoundPaddingBottom() - this.mTextView.getCompoundPaddingTop();
                if (n <= 0 || n2 <= 0) {
                    return;
                }
                synchronized (AppCompatTextViewAutoSizeHelper.TEMP_RECTF) {
                    AppCompatTextViewAutoSizeHelper.TEMP_RECTF.setEmpty();
                    AppCompatTextViewAutoSizeHelper.TEMP_RECTF.right = (float)n;
                    AppCompatTextViewAutoSizeHelper.TEMP_RECTF.bottom = (float)n2;
                    final float n3 = (float)this.findLargestTextSizeWhichFits(AppCompatTextViewAutoSizeHelper.TEMP_RECTF);
                    if (n3 != this.mTextView.getTextSize()) {
                        this.setTextSizeInternal(0, n3);
                    }
                }
            }
            this.mNeedsAutoSizeText = true;
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int getAutoSizeMaxTextSize() {
        return Math.round(this.mAutoSizeMaxTextSizeInPx);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int getAutoSizeMinTextSize() {
        return Math.round(this.mAutoSizeMinTextSizeInPx);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int getAutoSizeStepGranularity() {
        return Math.round(this.mAutoSizeStepGranularityInPx);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextSizesInPx;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    int getAutoSizeTextType() {
        return this.mAutoSizeTextType;
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    boolean isAutoSizeEnabled() {
        boolean b = false;
        if (this.supportsAutoSizeText() && this.mAutoSizeTextType != 0) {
            b = true;
        }
        return b;
    }
    
    void loadFromAttributes(final AttributeSet set, int resourceId) {
        final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(set, R.styleable.AppCompatTextView, resourceId, 0);
        if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTextView_autoSizeTextType)) {
            this.mAutoSizeTextType = obtainStyledAttributes.getInt(R.styleable.AppCompatTextView_autoSizeTextType, 0);
        }
        float dimension;
        if (!obtainStyledAttributes.hasValue(R.styleable.AppCompatTextView_autoSizeStepGranularity)) {
            dimension = -1.0f;
        }
        else {
            dimension = obtainStyledAttributes.getDimension(R.styleable.AppCompatTextView_autoSizeStepGranularity, -1.0f);
        }
        float dimension2;
        if (!obtainStyledAttributes.hasValue(R.styleable.AppCompatTextView_autoSizeMinTextSize)) {
            dimension2 = -1.0f;
        }
        else {
            dimension2 = obtainStyledAttributes.getDimension(R.styleable.AppCompatTextView_autoSizeMinTextSize, -1.0f);
        }
        float dimension3;
        if (!obtainStyledAttributes.hasValue(R.styleable.AppCompatTextView_autoSizeMaxTextSize)) {
            dimension3 = -1.0f;
        }
        else {
            dimension3 = obtainStyledAttributes.getDimension(R.styleable.AppCompatTextView_autoSizeMaxTextSize, -1.0f);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTextView_autoSizePresetSizes)) {
            resourceId = obtainStyledAttributes.getResourceId(R.styleable.AppCompatTextView_autoSizePresetSizes, 0);
            if (resourceId > 0) {
                final TypedArray obtainTypedArray = obtainStyledAttributes.getResources().obtainTypedArray(resourceId);
                this.setupAutoSizeUniformPresetSizes(obtainTypedArray);
                obtainTypedArray.recycle();
            }
        }
        obtainStyledAttributes.recycle();
        if (!this.supportsAutoSizeText()) {
            this.mAutoSizeTextType = 0;
        }
        else if (this.mAutoSizeTextType == 1) {
            if (!this.mHasPresetAutoSizeValues) {
                final DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                float applyDimension = dimension2;
                if (dimension2 == -1.0f) {
                    applyDimension = TypedValue.applyDimension(2, 12.0f, displayMetrics);
                }
                float applyDimension2 = dimension3;
                if (dimension3 == -1.0f) {
                    applyDimension2 = TypedValue.applyDimension(2, 112.0f, displayMetrics);
                }
                float n = dimension;
                if (dimension == -1.0f) {
                    n = 1.0f;
                }
                this.validateAndSetAutoSizeTextTypeUniformConfiguration(applyDimension, applyDimension2, n);
            }
            this.setupAutoSizeText();
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    void setAutoSizeTextTypeUniformWithConfiguration(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        if (this.supportsAutoSizeText()) {
            final DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
            this.validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(n4, (float)n, displayMetrics), TypedValue.applyDimension(n4, (float)n2, displayMetrics), TypedValue.applyDimension(n4, (float)n3, displayMetrics));
            if (this.setupAutoSizeText()) {
                this.autoSizeText();
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull final int[] array, final int n) throws IllegalArgumentException {
        int i = 0;
        if (this.supportsAutoSizeText()) {
            final int length = array.length;
            if (length <= 0) {
                this.mHasPresetAutoSizeValues = false;
            }
            else {
                int[] copy = new int[length];
                if (n != 0) {
                    final DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                    while (i < length) {
                        copy[i] = Math.round(TypedValue.applyDimension(n, (float)array[i], displayMetrics));
                        ++i;
                    }
                }
                else {
                    copy = Arrays.copyOf(array, length);
                }
                this.mAutoSizeTextSizesInPx = this.cleanupAutoSizePresetSizes(copy);
                if (!this.setupAutoSizeUniformPresetSizesConfiguration()) {
                    throw new IllegalArgumentException("None of the preset sizes is valid: " + Arrays.toString(array));
                }
            }
            if (this.setupAutoSizeText()) {
                this.autoSizeText();
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    void setAutoSizeTextTypeWithDefaults(final int i) {
        if (this.supportsAutoSizeText()) {
            switch (i) {
                default: {
                    throw new IllegalArgumentException("Unknown auto-size text type: " + i);
                }
                case 0: {
                    this.clearAutoSizeConfiguration();
                    break;
                }
                case 1: {
                    final DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
                    this.validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(2, 12.0f, displayMetrics), TypedValue.applyDimension(2, 112.0f, displayMetrics), 1.0f);
                    if (this.setupAutoSizeText()) {
                        this.autoSizeText();
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    void setTextSizeInternal(final int n, final float n2) {
        Resources resources;
        if (this.mContext != null) {
            resources = this.mContext.getResources();
        }
        else {
            resources = Resources.getSystem();
        }
        this.setRawTextSize(TypedValue.applyDimension(n, n2, resources.getDisplayMetrics()));
    }
}
