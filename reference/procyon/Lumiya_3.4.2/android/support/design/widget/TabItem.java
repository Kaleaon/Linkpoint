// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.support.v7.widget.TintTypedArray;
import android.support.design.R;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

public final class TabItem extends View
{
    final int mCustomLayout;
    final Drawable mIcon;
    final CharSequence mText;
    
    public TabItem(final Context context) {
        this(context, null);
    }
    
    public TabItem(final Context context, final AttributeSet set) {
        super(context, set);
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, set, R.styleable.TabItem);
        this.mText = obtainStyledAttributes.getText(R.styleable.TabItem_android_text);
        this.mIcon = obtainStyledAttributes.getDrawable(R.styleable.TabItem_android_icon);
        this.mCustomLayout = obtainStyledAttributes.getResourceId(R.styleable.TabItem_android_layout, 0);
        obtainStyledAttributes.recycle();
    }
}
