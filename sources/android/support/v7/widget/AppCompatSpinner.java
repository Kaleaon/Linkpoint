package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ThemedSpinnerAdapter;

public class AppCompatSpinner extends Spinner implements TintableBackgroundView {
    private static final int[] ATTRS_ANDROID_SPINNERMODE = {16843505};
    private static final int MAX_ITEMS_MEASURED = 15;
    private static final int MODE_DIALOG = 0;
    private static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "AppCompatSpinner";
    private final AppCompatBackgroundHelper mBackgroundTintHelper;
    /* access modifiers changed from: private */
    public int mDropDownWidth;
    private ForwardingListener mForwardingListener;
    /* access modifiers changed from: private */
    public DropdownPopup mPopup;
    private final Context mPopupContext;
    private final boolean mPopupSet;
    private SpinnerAdapter mTempAdapter;
    /* access modifiers changed from: private */
    public final Rect mTempRect;

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        public DropDownAdapter(@Nullable SpinnerAdapter spinnerAdapter, @Nullable Resources.Theme theme) {
            this.mAdapter = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) spinnerAdapter;
            }
            if (theme != null) {
                if (Build.VERSION.SDK_INT >= 23 && (spinnerAdapter instanceof ThemedSpinnerAdapter)) {
                    ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter.getDropDownViewTheme() != theme) {
                        themedSpinnerAdapter.setDropDownViewTheme(theme);
                    }
                } else if (spinnerAdapter instanceof ThemedSpinnerAdapter) {
                    ThemedSpinnerAdapter themedSpinnerAdapter2 = (ThemedSpinnerAdapter) spinnerAdapter;
                    if (themedSpinnerAdapter2.getDropDownViewTheme() == null) {
                        themedSpinnerAdapter2.setDropDownViewTheme(theme);
                    }
                }
            }
        }

        public boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter == null) {
                return true;
            }
            return listAdapter.areAllItemsEnabled();
        }

        public int getCount() {
            if (this.mAdapter != null) {
                return this.mAdapter.getCount();
            }
            return 0;
        }

        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            if (this.mAdapter != null) {
                return this.mAdapter.getDropDownView(i, view, viewGroup);
            }
            return null;
        }

        public Object getItem(int i) {
            if (this.mAdapter != null) {
                return this.mAdapter.getItem(i);
            }
            return null;
        }

        public long getItemId(int i) {
            if (this.mAdapter != null) {
                return this.mAdapter.getItemId(i);
            }
            return -1;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            return getDropDownView(i, view, viewGroup);
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean hasStableIds() {
            return this.mAdapter != null && this.mAdapter.hasStableIds();
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }

        public boolean isEnabled(int i) {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter == null) {
                return true;
            }
            return listAdapter.isEnabled(i);
        }

        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    private class DropdownPopup extends ListPopupWindow {
        ListAdapter mAdapter;
        private CharSequence mHintText;
        private final Rect mVisibleRect = new Rect();

        public DropdownPopup(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            setAnchorView(AppCompatSpinner.this);
            setModal(true);
            setPromptPosition(0);
            setOnItemClickListener(new AdapterView.OnItemClickListener(AppCompatSpinner.this) {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    AppCompatSpinner.this.setSelection(i);
                    if (AppCompatSpinner.this.getOnItemClickListener() != null) {
                        AppCompatSpinner.this.performItemClick(view, i, DropdownPopup.this.mAdapter.getItemId(i));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }

        /* access modifiers changed from: package-private */
        public void computeContentWidth() {
            int i;
            Drawable background = getBackground();
            if (background == null) {
                Rect access$100 = AppCompatSpinner.this.mTempRect;
                AppCompatSpinner.this.mTempRect.right = 0;
                access$100.left = 0;
                i = 0;
            } else {
                background.getPadding(AppCompatSpinner.this.mTempRect);
                i = !ViewUtils.isLayoutRtl(AppCompatSpinner.this) ? -AppCompatSpinner.this.mTempRect.left : AppCompatSpinner.this.mTempRect.right;
            }
            int paddingLeft = AppCompatSpinner.this.getPaddingLeft();
            int paddingRight = AppCompatSpinner.this.getPaddingRight();
            int width = AppCompatSpinner.this.getWidth();
            if (AppCompatSpinner.this.mDropDownWidth == -2) {
                int compatMeasureContentWidth = AppCompatSpinner.this.compatMeasureContentWidth((SpinnerAdapter) this.mAdapter, getBackground());
                int i2 = (AppCompatSpinner.this.getContext().getResources().getDisplayMetrics().widthPixels - AppCompatSpinner.this.mTempRect.left) - AppCompatSpinner.this.mTempRect.right;
                if (compatMeasureContentWidth > i2) {
                    compatMeasureContentWidth = i2;
                }
                setContentWidth(Math.max(compatMeasureContentWidth, (width - paddingLeft) - paddingRight));
            } else if (AppCompatSpinner.this.mDropDownWidth != -1) {
                setContentWidth(AppCompatSpinner.this.mDropDownWidth);
            } else {
                setContentWidth((width - paddingLeft) - paddingRight);
            }
            setHorizontalOffset(!ViewUtils.isLayoutRtl(AppCompatSpinner.this) ? i + paddingLeft : ((width - paddingRight) - getWidth()) + i);
        }

        public CharSequence getHintText() {
            return this.mHintText;
        }

        /* access modifiers changed from: package-private */
        public boolean isVisibleToUser(View view) {
            return ViewCompat.isAttachedToWindow(view) && view.getGlobalVisibleRect(this.mVisibleRect);
        }

        public void setAdapter(ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }

        public void setPromptText(CharSequence charSequence) {
            this.mHintText = charSequence;
        }

        public void show() {
            ViewTreeObserver viewTreeObserver;
            boolean isShowing = isShowing();
            computeContentWidth();
            setInputMethodMode(2);
            super.show();
            getListView().setChoiceMode(1);
            setSelection(AppCompatSpinner.this.getSelectedItemPosition());
            if (!isShowing && (viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver()) != null) {
                final AnonymousClass2 r1 = new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (DropdownPopup.this.isVisibleToUser(AppCompatSpinner.this)) {
                            DropdownPopup.this.computeContentWidth();
                            DropdownPopup.super.show();
                            return;
                        }
                        DropdownPopup.this.dismiss();
                    }
                };
                viewTreeObserver.addOnGlobalLayoutListener(r1);
                setOnDismissListener(new PopupWindow.OnDismissListener() {
                    public void onDismiss() {
                        ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
                        if (viewTreeObserver != null) {
                            viewTreeObserver.removeGlobalOnLayoutListener(r1);
                        }
                    }
                });
            }
        }
    }

    public AppCompatSpinner(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppCompatSpinner(Context context, int i) {
        this(context, (AttributeSet) null, R.attr.spinnerStyle, i);
    }

    public AppCompatSpinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.spinnerStyle);
    }

    public AppCompatSpinner(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, -1);
    }

    public AppCompatSpinner(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, i2, (Resources.Theme) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AppCompatSpinner(android.content.Context r9, android.util.AttributeSet r10, int r11, int r12, android.content.res.Resources.Theme r13) {
        /*
            r8 = this;
            r3 = 1
            r7 = 0
            r1 = 0
            r8.<init>(r9, r10, r11)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r8.mTempRect = r0
            int[] r0 = android.support.v7.appcompat.R.styleable.Spinner
            android.support.v7.widget.TintTypedArray r4 = android.support.v7.widget.TintTypedArray.obtainStyledAttributes(r9, r10, r0, r11, r7)
            android.support.v7.widget.AppCompatBackgroundHelper r0 = new android.support.v7.widget.AppCompatBackgroundHelper
            r0.<init>(r8)
            r8.mBackgroundTintHelper = r0
            if (r13 != 0) goto L_0x0048
            int r0 = android.support.v7.appcompat.R.styleable.Spinner_popupTheme
            int r0 = r4.getResourceId(r0, r7)
            if (r0 != 0) goto L_0x0050
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 23
            if (r0 < r2) goto L_0x0058
            r0 = r1
        L_0x002b:
            r8.mPopupContext = r0
        L_0x002d:
            android.content.Context r0 = r8.mPopupContext
            if (r0 != 0) goto L_0x005a
        L_0x0031:
            int r0 = android.support.v7.appcompat.R.styleable.Spinner_android_entries
            java.lang.CharSequence[] r0 = r4.getTextArray(r0)
            if (r0 != 0) goto L_0x00d3
        L_0x0039:
            r4.recycle()
            r8.mPopupSet = r3
            android.widget.SpinnerAdapter r0 = r8.mTempAdapter
            if (r0 != 0) goto L_0x00e5
        L_0x0042:
            android.support.v7.widget.AppCompatBackgroundHelper r0 = r8.mBackgroundTintHelper
            r0.loadFromAttributes(r10, r11)
            return
        L_0x0048:
            android.support.v7.view.ContextThemeWrapper r0 = new android.support.v7.view.ContextThemeWrapper
            r0.<init>((android.content.Context) r9, (android.content.res.Resources.Theme) r13)
            r8.mPopupContext = r0
            goto L_0x002d
        L_0x0050:
            android.support.v7.view.ContextThemeWrapper r2 = new android.support.v7.view.ContextThemeWrapper
            r2.<init>((android.content.Context) r9, (int) r0)
            r8.mPopupContext = r2
            goto L_0x002d
        L_0x0058:
            r0 = r9
            goto L_0x002b
        L_0x005a:
            r0 = -1
            if (r12 == r0) goto L_0x0096
        L_0x005d:
            if (r12 != r3) goto L_0x0031
            android.support.v7.widget.AppCompatSpinner$DropdownPopup r0 = new android.support.v7.widget.AppCompatSpinner$DropdownPopup
            android.content.Context r2 = r8.mPopupContext
            r0.<init>(r2, r10, r11)
            android.content.Context r2 = r8.mPopupContext
            int[] r5 = android.support.v7.appcompat.R.styleable.Spinner
            android.support.v7.widget.TintTypedArray r2 = android.support.v7.widget.TintTypedArray.obtainStyledAttributes(r2, r10, r5, r11, r7)
            int r5 = android.support.v7.appcompat.R.styleable.Spinner_android_dropDownWidth
            r6 = -2
            int r5 = r2.getLayoutDimension((int) r5, (int) r6)
            r8.mDropDownWidth = r5
            int r5 = android.support.v7.appcompat.R.styleable.Spinner_android_popupBackground
            android.graphics.drawable.Drawable r5 = r2.getDrawable(r5)
            r0.setBackgroundDrawable(r5)
            int r5 = android.support.v7.appcompat.R.styleable.Spinner_android_prompt
            java.lang.String r5 = r4.getString(r5)
            r0.setPromptText(r5)
            r2.recycle()
            r8.mPopup = r0
            android.support.v7.widget.AppCompatSpinner$1 r2 = new android.support.v7.widget.AppCompatSpinner$1
            r2.<init>(r8, r0)
            r8.mForwardingListener = r2
            goto L_0x0031
        L_0x0096:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 11
            if (r0 >= r2) goto L_0x009e
            r12 = r3
            goto L_0x005d
        L_0x009e:
            int[] r0 = ATTRS_ANDROID_SPINNERMODE     // Catch:{ Exception -> 0x00b9, all -> 0x00ca }
            r2 = 0
            android.content.res.TypedArray r2 = r9.obtainStyledAttributes(r10, r0, r11, r2)     // Catch:{ Exception -> 0x00b9, all -> 0x00ca }
            r0 = 0
            boolean r0 = r2.hasValue(r0)     // Catch:{ Exception -> 0x00f0 }
            if (r0 != 0) goto L_0x00b2
        L_0x00ac:
            if (r2 == 0) goto L_0x005d
            r2.recycle()
            goto L_0x005d
        L_0x00b2:
            r0 = 0
            r5 = 0
            int r12 = r2.getInt(r0, r5)     // Catch:{ Exception -> 0x00f0 }
            goto L_0x00ac
        L_0x00b9:
            r0 = move-exception
            r2 = r1
        L_0x00bb:
            java.lang.String r5 = "AppCompatSpinner"
            java.lang.String r6 = "Could not read android:spinnerMode"
            android.util.Log.i(r5, r6, r0)     // Catch:{ all -> 0x00ee }
            if (r2 == 0) goto L_0x005d
            r2.recycle()
            goto L_0x005d
        L_0x00ca:
            r0 = move-exception
            r2 = r1
        L_0x00cc:
            if (r2 != 0) goto L_0x00cf
        L_0x00ce:
            throw r0
        L_0x00cf:
            r2.recycle()
            goto L_0x00ce
        L_0x00d3:
            android.widget.ArrayAdapter r2 = new android.widget.ArrayAdapter
            r5 = 17367048(0x1090008, float:2.5162948E-38)
            r2.<init>(r9, r5, r0)
            int r0 = android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item
            r2.setDropDownViewResource(r0)
            r8.setAdapter((android.widget.SpinnerAdapter) r2)
            goto L_0x0039
        L_0x00e5:
            android.widget.SpinnerAdapter r0 = r8.mTempAdapter
            r8.setAdapter((android.widget.SpinnerAdapter) r0)
            r8.mTempAdapter = r1
            goto L_0x0042
        L_0x00ee:
            r0 = move-exception
            goto L_0x00cc
        L_0x00f0:
            r0 = move-exception
            goto L_0x00bb
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatSpinner.<init>(android.content.Context, android.util.AttributeSet, int, int, android.content.res.Resources$Theme):void");
    }

    /* access modifiers changed from: package-private */
    public int compatMeasureContentWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        View view;
        if (spinnerAdapter == null) {
            return 0;
        }
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
        int max = Math.max(0, getSelectedItemPosition());
        int min = Math.min(spinnerAdapter.getCount(), max + 15);
        View view2 = null;
        int i = 0;
        int i2 = 0;
        for (int max2 = Math.max(0, max - (15 - (min - max))); max2 < min; max2++) {
            int itemViewType = spinnerAdapter.getItemViewType(max2);
            if (itemViewType == i2) {
                view = view2;
            } else {
                i2 = itemViewType;
                view = null;
            }
            view2 = spinnerAdapter.getView(max2, view, this);
            if (view2.getLayoutParams() == null) {
                view2.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            view2.measure(makeMeasureSpec, makeMeasureSpec2);
            i = Math.max(i, view2.getMeasuredWidth());
        }
        if (drawable == null) {
            return i;
        }
        drawable.getPadding(this.mTempRect);
        return this.mTempRect.left + this.mTempRect.right + i;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.applySupportBackgroundTint();
        }
    }

    public int getDropDownHorizontalOffset() {
        if (this.mPopup != null) {
            return this.mPopup.getHorizontalOffset();
        }
        if (Build.VERSION.SDK_INT < 16) {
            return 0;
        }
        return super.getDropDownHorizontalOffset();
    }

    public int getDropDownVerticalOffset() {
        if (this.mPopup != null) {
            return this.mPopup.getVerticalOffset();
        }
        if (Build.VERSION.SDK_INT < 16) {
            return 0;
        }
        return super.getDropDownVerticalOffset();
    }

    public int getDropDownWidth() {
        if (this.mPopup != null) {
            return this.mDropDownWidth;
        }
        if (Build.VERSION.SDK_INT < 16) {
            return 0;
        }
        return super.getDropDownWidth();
    }

    public Drawable getPopupBackground() {
        if (this.mPopup != null) {
            return this.mPopup.getBackground();
        }
        if (Build.VERSION.SDK_INT < 16) {
            return null;
        }
        return super.getPopupBackground();
    }

    public Context getPopupContext() {
        if (this.mPopup != null) {
            return this.mPopupContext;
        }
        if (Build.VERSION.SDK_INT < 23) {
            return null;
        }
        return super.getPopupContext();
    }

    public CharSequence getPrompt() {
        return this.mPopup == null ? super.getPrompt() : this.mPopup.getHintText();
    }

    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public ColorStateList getSupportBackgroundTintList() {
        if (this.mBackgroundTintHelper == null) {
            return null;
        }
        return this.mBackgroundTintHelper.getSupportBackgroundTintList();
    }

    @Nullable
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        if (this.mBackgroundTintHelper == null) {
            return null;
        }
        return this.mBackgroundTintHelper.getSupportBackgroundTintMode();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPopup != null && this.mPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mPopup != null && View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
            setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), compatMeasureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(i)), getMeasuredHeight());
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mForwardingListener != null && this.mForwardingListener.onTouch(this, motionEvent)) {
            return true;
        }
        return super.onTouchEvent(motionEvent);
    }

    public boolean performClick() {
        if (this.mPopup == null) {
            return super.performClick();
        }
        if (this.mPopup.isShowing()) {
            return true;
        }
        this.mPopup.show();
        return true;
    }

    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        if (this.mPopupSet) {
            super.setAdapter(spinnerAdapter);
            if (this.mPopup != null) {
                this.mPopup.setAdapter(new DropDownAdapter(spinnerAdapter, (this.mPopupContext != null ? this.mPopupContext : getContext()).getTheme()));
                return;
            }
            return;
        }
        this.mTempAdapter = spinnerAdapter;
    }

    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundDrawable(drawable);
        }
    }

    public void setBackgroundResource(@DrawableRes int i) {
        super.setBackgroundResource(i);
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.onSetBackgroundResource(i);
        }
    }

    public void setDropDownHorizontalOffset(int i) {
        if (this.mPopup != null) {
            this.mPopup.setHorizontalOffset(i);
        } else if (Build.VERSION.SDK_INT >= 16) {
            super.setDropDownHorizontalOffset(i);
        }
    }

    public void setDropDownVerticalOffset(int i) {
        if (this.mPopup != null) {
            this.mPopup.setVerticalOffset(i);
        } else if (Build.VERSION.SDK_INT >= 16) {
            super.setDropDownVerticalOffset(i);
        }
    }

    public void setDropDownWidth(int i) {
        if (this.mPopup != null) {
            this.mDropDownWidth = i;
        } else if (Build.VERSION.SDK_INT >= 16) {
            super.setDropDownWidth(i);
        }
    }

    public void setPopupBackgroundDrawable(Drawable drawable) {
        if (this.mPopup != null) {
            this.mPopup.setBackgroundDrawable(drawable);
        } else if (Build.VERSION.SDK_INT >= 16) {
            super.setPopupBackgroundDrawable(drawable);
        }
    }

    public void setPopupBackgroundResource(@DrawableRes int i) {
        setPopupBackgroundDrawable(AppCompatResources.getDrawable(getPopupContext(), i));
    }

    public void setPrompt(CharSequence charSequence) {
        if (this.mPopup == null) {
            super.setPrompt(charSequence);
        } else {
            this.mPopup.setPromptText(charSequence);
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setSupportBackgroundTintList(@Nullable ColorStateList colorStateList) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintList(colorStateList);
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        if (this.mBackgroundTintHelper != null) {
            this.mBackgroundTintHelper.setSupportBackgroundTintMode(mode);
        }
    }
}
