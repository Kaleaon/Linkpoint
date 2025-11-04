// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.view;

import android.view.ViewConfiguration;
import android.os.Build$VERSION;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.support.v7.appcompat.R;
import android.content.res.Configuration;
import android.content.Context;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class ActionBarPolicy
{
    private Context mContext;
    
    private ActionBarPolicy(final Context mContext) {
        this.mContext = mContext;
    }
    
    public static ActionBarPolicy get(final Context context) {
        return new ActionBarPolicy(context);
    }
    
    public boolean enableHomeButtonByDefault() {
        return this.mContext.getApplicationInfo().targetSdkVersion < 14;
    }
    
    public int getEmbeddedMenuWidthLimit() {
        return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
    }
    
    public int getMaxActionButtons() {
        final Configuration configuration = this.mContext.getResources().getConfiguration();
        final int screenWidthDp = configuration.screenWidthDp;
        final int screenHeightDp = configuration.screenHeightDp;
        if (configuration.smallestScreenWidthDp <= 600 && screenWidthDp <= 600) {
            if (screenWidthDp <= 960 || screenHeightDp <= 720) {
                if (screenWidthDp <= 720 || screenHeightDp <= 960) {
                    if (screenWidthDp < 500) {
                        if (screenWidthDp <= 640 || screenHeightDp <= 480) {
                            if (screenWidthDp <= 480 || screenHeightDp <= 640) {
                                if (screenWidthDp < 360) {
                                    return 2;
                                }
                                return 3;
                            }
                        }
                    }
                    return 4;
                }
            }
        }
        return 5;
    }
    
    public int getStackedTabMaxWidth() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_stacked_tab_max_width);
    }
    
    public int getTabContainerHeight() {
        final TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes((AttributeSet)null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        int a = obtainStyledAttributes.getLayoutDimension(R.styleable.ActionBar_height, 0);
        final Resources resources = this.mContext.getResources();
        if (!this.hasEmbeddedTabs()) {
            a = Math.min(a, resources.getDimensionPixelSize(R.dimen.abc_action_bar_stacked_max_height));
        }
        obtainStyledAttributes.recycle();
        return a;
    }
    
    public boolean hasEmbeddedTabs() {
        return this.mContext.getResources().getBoolean(R.bool.abc_action_bar_embed_tabs);
    }
    
    public boolean showsOverflowMenuButton() {
        boolean b = false;
        if (Build$VERSION.SDK_INT < 19) {
            if (!ViewConfiguration.get(this.mContext).hasPermanentMenuKey()) {
                b = true;
            }
            return b;
        }
        return true;
    }
}
