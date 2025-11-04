// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.Button;
import android.support.v4.widget.SwipeRefreshLayout;
import butterknife.internal.Utils;
import android.widget.TextView;
import android.view.View;
import butterknife.Unbinder;

public class UserFirstLifeProfileTab_ViewBinding implements Unbinder
{
    private UserFirstLifeProfileTab target;
    private View view2131755698;
    private View view2131755706;
    
    @UiThread
    public UserFirstLifeProfileTab_ViewBinding(final UserFirstLifeProfileTab target, final View view) {
        this.target = target;
        target.userProfilePaymentInfo = Utils.findRequiredViewAsType(view, 2131755703, "field 'userProfilePaymentInfo'", TextView.class);
        target.swipeRefreshLayout = Utils.findRequiredViewAsType(view, 2131755195, "field 'swipeRefreshLayout'", SwipeRefreshLayout.class);
        final View requiredView = Utils.findRequiredView(view, 2131755706, "field 'aboutEditButton' and method 'onAboutEditClicked'");
        target.aboutEditButton = Utils.castView(requiredView, 2131755706, "field 'aboutEditButton'", Button.class);
        (this.view2131755706 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onAboutEditClicked(view);
            }
        });
        target.loadingLayout = Utils.findRequiredViewAsType(view, 2131755197, "field 'loadingLayout'", LoadingLayout.class);
        final View requiredView2 = Utils.findRequiredView(view, 2131755698, "field 'changePicButton' and method 'onChangePicClicked'");
        target.changePicButton = Utils.castView(requiredView2, 2131755698, "field 'changePicButton'", Button.class);
        (this.view2131755698 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onChangePicClicked(view);
            }
        });
        target.userProfileAboutText = Utils.findRequiredViewAsType(view, 2131755705, "field 'userProfileAboutText'", TextView.class);
        target.userPicView = Utils.findRequiredViewAsType(view, 2131755702, "field 'userPicView'", ImageAssetView.class);
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final UserFirstLifeProfileTab target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.userProfilePaymentInfo = null;
        target.swipeRefreshLayout = null;
        target.aboutEditButton = null;
        target.loadingLayout = null;
        target.changePicButton = null;
        target.userProfileAboutText = null;
        target.userPicView = null;
        this.view2131755706.setOnClickListener((View$OnClickListener)null);
        this.view2131755706 = null;
        this.view2131755698.setOnClickListener((View$OnClickListener)null);
        this.view2131755698 = null;
    }
}
