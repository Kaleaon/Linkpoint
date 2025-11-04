// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;
import android.view.View;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.content.Context;
import android.support.annotation.RestrictTo;
import android.widget.Checkable;
import android.support.v7.widget.AppCompatImageButton;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class CheckableImageButton extends AppCompatImageButton implements Checkable
{
    private static final int[] DRAWABLE_STATE_CHECKED;
    private boolean mChecked;
    
    static {
        DRAWABLE_STATE_CHECKED = new int[] { 16842912 };
    }
    
    public CheckableImageButton(final Context context) {
        this(context, null);
    }
    
    public CheckableImageButton(final Context context, final AttributeSet set) {
        this(context, set, R.attr.imageButtonStyle);
    }
    
    public CheckableImageButton(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        ViewCompat.setAccessibilityDelegate((View)this, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
                super.onInitializeAccessibilityEvent(view, accessibilityEvent);
                accessibilityEvent.setChecked(CheckableImageButton.this.isChecked());
            }
            
            @Override
            public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCheckable(true);
                accessibilityNodeInfoCompat.setChecked(CheckableImageButton.this.isChecked());
            }
        });
    }
    
    public boolean isChecked() {
        return this.mChecked;
    }
    
    public int[] onCreateDrawableState(final int n) {
        if (!this.mChecked) {
            return super.onCreateDrawableState(n);
        }
        return mergeDrawableStates(super.onCreateDrawableState(CheckableImageButton.DRAWABLE_STATE_CHECKED.length + n), CheckableImageButton.DRAWABLE_STATE_CHECKED);
    }
    
    public void setChecked(final boolean mChecked) {
        if (this.mChecked != mChecked) {
            this.mChecked = mChecked;
            this.refreshDrawableState();
            this.sendAccessibilityEvent(2048);
        }
    }
    
    public void toggle() {
        boolean checked = false;
        if (!this.mChecked) {
            checked = true;
        }
        this.setChecked(checked);
    }
}
