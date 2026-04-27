// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.view.menu;

import android.view.MotionEvent;
import android.os.Parcelable;
import android.view.View$MeasureSpec;
import android.view.View;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.ForwardingListener;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.ActionMenuView;
import android.view.View$OnClickListener;
import android.support.v7.widget.AppCompatTextView;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class ActionMenuItemView extends AppCompatTextView implements ItemView, View$OnClickListener, ActionMenuChildView
{
    private static final int MAX_ICON_SIZE = 32;
    private static final String TAG = "ActionMenuItemView";
    private boolean mAllowTextWithIcon;
    private boolean mExpandedFormat;
    private ForwardingListener mForwardingListener;
    private Drawable mIcon;
    MenuItemImpl mItemData;
    ItemInvoker mItemInvoker;
    private int mMaxIconSize;
    private int mMinWidth;
    PopupCallback mPopupCallback;
    private int mSavedPaddingLeft;
    private CharSequence mTitle;
    
    public ActionMenuItemView(final Context context) {
        this(context, null);
    }
    
    public ActionMenuItemView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ActionMenuItemView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        final Resources resources = context.getResources();
        this.mAllowTextWithIcon = this.shouldAllowTextWithIcon();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ActionMenuItemView, n, 0);
        this.mMinWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.ActionMenuItemView_android_minWidth, 0);
        obtainStyledAttributes.recycle();
        this.mMaxIconSize = (int)(resources.getDisplayMetrics().density * 32.0f + 0.5f);
        this.setOnClickListener((View$OnClickListener)this);
        this.mSavedPaddingLeft = -1;
        this.setSaveEnabled(false);
    }
    
    private boolean shouldAllowTextWithIcon() {
        final Configuration configuration = this.getContext().getResources().getConfiguration();
        final int screenWidthDp = configuration.screenWidthDp;
        final int screenHeightDp = configuration.screenHeightDp;
        if (screenWidthDp < 480) {
            if (screenWidthDp < 640 || screenHeightDp < 480) {
                if (configuration.orientation != 2) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void updateTextButtonVisibility() {
        final CharSequence charSequence = null;
        final int n = 0;
        boolean b;
        if (TextUtils.isEmpty(this.mTitle)) {
            b = false;
        }
        else {
            b = true;
        }
        int n2 = 0;
        Label_0026: {
            if (this.mIcon != null) {
                n2 = n;
                if (!this.mItemData.showsTextAsAction()) {
                    break Label_0026;
                }
                if (!this.mAllowTextWithIcon && !this.mExpandedFormat) {
                    n2 = n;
                    break Label_0026;
                }
            }
            n2 = 1;
        }
        final int n3 = n2 & (b ? 1 : 0);
        CharSequence mTitle;
        if (n3 == 0) {
            mTitle = null;
        }
        else {
            mTitle = this.mTitle;
        }
        this.setText(mTitle);
        final CharSequence contentDescription = this.mItemData.getContentDescription();
        if (!TextUtils.isEmpty(contentDescription)) {
            this.setContentDescription(contentDescription);
        }
        else {
            CharSequence title;
            if (n3 == 0) {
                title = this.mItemData.getTitle();
            }
            else {
                title = null;
            }
            this.setContentDescription(title);
        }
        final CharSequence tooltipText = this.mItemData.getTooltipText();
        if (!TextUtils.isEmpty(tooltipText)) {
            TooltipCompat.setTooltipText((View)this, tooltipText);
        }
        else {
            CharSequence title2 = charSequence;
            if (n3 == 0) {
                title2 = this.mItemData.getTitle();
            }
            TooltipCompat.setTooltipText((View)this, title2);
        }
    }
    
    @Override
    public MenuItemImpl getItemData() {
        return this.mItemData;
    }
    
    public boolean hasText() {
        boolean b = false;
        if (!TextUtils.isEmpty(this.getText())) {
            b = true;
        }
        return b;
    }
    
    @Override
    public void initialize(final MenuItemImpl mItemData, int visibility) {
        visibility = 0;
        this.mItemData = mItemData;
        this.setIcon(mItemData.getIcon());
        this.setTitle(mItemData.getTitleForItemView(this));
        this.setId(mItemData.getItemId());
        if (!mItemData.isVisible()) {
            visibility = 8;
        }
        this.setVisibility(visibility);
        ((MenuView.ItemView)this).setEnabled(mItemData.isEnabled());
        if (mItemData.hasSubMenu() && this.mForwardingListener == null) {
            this.mForwardingListener = new ActionMenuItemForwardingListener();
        }
    }
    
    public boolean needsDividerAfter() {
        return this.hasText();
    }
    
    public boolean needsDividerBefore() {
        boolean b = false;
        if (this.hasText() && this.mItemData.getIcon() == null) {
            b = true;
        }
        return b;
    }
    
    public void onClick(final View view) {
        if (this.mItemInvoker != null) {
            this.mItemInvoker.invokeItem(this.mItemData);
        }
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mAllowTextWithIcon = this.shouldAllowTextWithIcon();
        this.updateTextButtonVisibility();
    }
    
    protected void onMeasure(int a, final int n) {
        final boolean hasText = this.hasText();
        if (hasText && this.mSavedPaddingLeft >= 0) {
            super.setPadding(this.mSavedPaddingLeft, this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
        }
        super.onMeasure(a, n);
        final int mode = View$MeasureSpec.getMode(a);
        a = View$MeasureSpec.getSize(a);
        final int measuredWidth = this.getMeasuredWidth();
        if (mode != Integer.MIN_VALUE) {
            a = this.mMinWidth;
        }
        else {
            a = Math.min(a, this.mMinWidth);
        }
        if (mode != 1073741824 && this.mMinWidth > 0 && measuredWidth < a) {
            super.onMeasure(View$MeasureSpec.makeMeasureSpec(a, 1073741824), n);
        }
        if (!hasText && this.mIcon != null) {
            super.setPadding((this.getMeasuredWidth() - this.mIcon.getBounds().width()) / 2, this.getPaddingTop(), this.getPaddingRight(), this.getPaddingBottom());
        }
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        super.onRestoreInstanceState((Parcelable)null);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return (this.mItemData.hasSubMenu() && this.mForwardingListener != null && this.mForwardingListener.onTouch((View)this, motionEvent)) || super.onTouchEvent(motionEvent);
    }
    
    @Override
    public boolean prefersCondensedTitle() {
        return true;
    }
    
    @Override
    public void setCheckable(final boolean b) {
    }
    
    @Override
    public void setChecked(final boolean b) {
    }
    
    public void setExpandedFormat(final boolean mExpandedFormat) {
        if (this.mExpandedFormat != mExpandedFormat) {
            this.mExpandedFormat = mExpandedFormat;
            if (this.mItemData != null) {
                this.mItemData.actionFormatChanged();
            }
        }
    }
    
    @Override
    public void setIcon(final Drawable mIcon) {
        this.mIcon = mIcon;
        if (mIcon != null) {
            int n = mIcon.getIntrinsicWidth();
            int n2 = mIcon.getIntrinsicHeight();
            if (n > this.mMaxIconSize) {
                final float n3 = this.mMaxIconSize / (float)n;
                n = this.mMaxIconSize;
                n2 *= (int)n3;
            }
            if (n2 > this.mMaxIconSize) {
                final float n4 = this.mMaxIconSize / (float)n2;
                n2 = this.mMaxIconSize;
                n *= (int)n4;
            }
            mIcon.setBounds(0, 0, n, n2);
        }
        this.setCompoundDrawables(mIcon, (Drawable)null, (Drawable)null, (Drawable)null);
        this.updateTextButtonVisibility();
    }
    
    public void setItemInvoker(final ItemInvoker mItemInvoker) {
        this.mItemInvoker = mItemInvoker;
    }
    
    public void setPadding(final int mSavedPaddingLeft, final int n, final int n2, final int n3) {
        super.setPadding(this.mSavedPaddingLeft = mSavedPaddingLeft, n, n2, n3);
    }
    
    public void setPopupCallback(final PopupCallback mPopupCallback) {
        this.mPopupCallback = mPopupCallback;
    }
    
    @Override
    public void setShortcut(final boolean b, final char c) {
    }
    
    @Override
    public void setTitle(final CharSequence mTitle) {
        this.mTitle = mTitle;
        this.updateTextButtonVisibility();
    }
    
    @Override
    public boolean showsIcon() {
        return true;
    }
    
    private class ActionMenuItemForwardingListener extends ForwardingListener
    {
        public ActionMenuItemForwardingListener() {
            super((View)ActionMenuItemView.this);
        }
        
        @Override
        public ShowableListMenu getPopup() {
            if (ActionMenuItemView.this.mPopupCallback == null) {
                return null;
            }
            return ActionMenuItemView.this.mPopupCallback.getPopup();
        }
        
        @Override
        protected boolean onForwardingStarted() {
            boolean b = false;
            if (ActionMenuItemView.this.mItemInvoker != null && ActionMenuItemView.this.mItemInvoker.invokeItem(ActionMenuItemView.this.mItemData)) {
                final ShowableListMenu popup = this.getPopup();
                if (popup != null && popup.isShowing()) {
                    b = true;
                }
                return b;
            }
            return false;
        }
    }
    
    public abstract static class PopupCallback
    {
        public abstract ShowableListMenu getPopup();
    }
}
