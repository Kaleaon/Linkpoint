// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import android.view.View$OnClickListener;
import butterknife.internal.DebouncingOnClickListener;
import android.widget.Button;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import butterknife.internal.Utils;
import android.widget.TextView;
import android.view.View;
import butterknife.Unbinder;

public class UserMainProfileTab_ViewBinding implements Unbinder
{
    private UserMainProfileTab target;
    private View view2131755698;
    private View view2131755706;
    private View view2131755715;
    private View view2131755720;
    private View view2131755724;
    
    @UiThread
    public UserMainProfileTab_ViewBinding(final UserMainProfileTab target, View requiredView) {
        this.target = target;
        target.textProfilePrimaryName = Utils.findRequiredViewAsType(requiredView, 2131755708, "field 'textProfilePrimaryName'", TextView.class);
        target.userProfileNotesCaption = Utils.findRequiredView(requiredView, 2131755722, "field 'userProfileNotesCaption'");
        target.textProfileOnline = Utils.findRequiredViewAsType(requiredView, 2131755710, "field 'textProfileOnline'", TextView.class);
        target.userProfilePartnerPic = Utils.findRequiredViewAsType(requiredView, 2131755714, "field 'userProfilePartnerPic'", ChatterPicView.class);
        target.userProfilePartnerName = Utils.findRequiredViewAsType(requiredView, 2131755713, "field 'userProfilePartnerName'", TextView.class);
        target.userWebProfileCardView = Utils.findRequiredView(requiredView, 2131755716, "field 'userWebProfileCardView'");
        target.swipeRefreshLayout = Utils.findRequiredViewAsType(requiredView, 2131755195, "field 'swipeRefreshLayout'", SwipeRefreshLayout.class);
        target.textProfileSecondaryName = Utils.findRequiredViewAsType(requiredView, 2131755709, "field 'textProfileSecondaryName'", TextView.class);
        final View requiredView2 = Utils.findRequiredView(requiredView, 2131755698, "field 'changePicButton' and method 'onChangePicClicked'");
        target.changePicButton = Utils.castView(requiredView2, 2131755698, "field 'changePicButton'", Button.class);
        (this.view2131755698 = requiredView2).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onChangePicClicked(view);
            }
        });
        target.userPicView = Utils.findRequiredViewAsType(requiredView, 2131755702, "field 'userPicView'", ImageAssetView.class);
        target.userProfileAboutText = Utils.findRequiredViewAsType(requiredView, 2131755705, "field 'userProfileAboutText'", TextView.class);
        target.textProfileAge = Utils.findRequiredViewAsType(requiredView, 2131755711, "field 'textProfileAge'", TextView.class);
        target.loadingLayout = Utils.findRequiredViewAsType(requiredView, 2131755197, "field 'loadingLayout'", LoadingLayout.class);
        target.textProfileAgentKey = Utils.findRequiredViewAsType(requiredView, 2131755719, "field 'textProfileAgentKey'", TextView.class);
        final View requiredView3 = Utils.findRequiredView(requiredView, 2131755706, "field 'aboutEditButton' and method 'onAboutEditClicked'");
        target.aboutEditButton = Utils.castView(requiredView3, 2131755706, "field 'aboutEditButton'", Button.class);
        (this.view2131755706 = requiredView3).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onAboutEditClicked(view);
            }
        });
        target.userWebProfileLink = Utils.findRequiredViewAsType(requiredView, 2131755717, "field 'userWebProfileLink'", TextView.class);
        target.userPartnerCardView = Utils.findRequiredView(requiredView, 2131755712, "field 'userPartnerCardView'");
        target.textProfileNotesText = Utils.findRequiredViewAsType(requiredView, 2131755723, "field 'textProfileNotesText'", TextView.class);
        (this.view2131755715 = Utils.findRequiredView(requiredView, 2131755715, "method 'onViewProfileClicked'")).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onViewProfileClicked(view);
            }
        });
        (this.view2131755724 = Utils.findRequiredView(requiredView, 2131755724, "method 'onEditNotesClicked'")).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onEditNotesClicked(view);
            }
        });
        requiredView = Utils.findRequiredView(requiredView, 2131755720, "method 'onCopyAgentKeyClicked'");
        (this.view2131755720 = requiredView).setOnClickListener((View$OnClickListener)new DebouncingOnClickListener() {
            @Override
            public void doClick(final View view) {
                target.onCopyAgentKeyClicked(view);
            }
        });
    }
    
    @CallSuper
    @Override
    public void unbind() {
        final UserMainProfileTab target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.textProfilePrimaryName = null;
        target.userProfileNotesCaption = null;
        target.textProfileOnline = null;
        target.userProfilePartnerPic = null;
        target.userProfilePartnerName = null;
        target.userWebProfileCardView = null;
        target.swipeRefreshLayout = null;
        target.textProfileSecondaryName = null;
        target.changePicButton = null;
        target.userPicView = null;
        target.userProfileAboutText = null;
        target.textProfileAge = null;
        target.loadingLayout = null;
        target.textProfileAgentKey = null;
        target.aboutEditButton = null;
        target.userWebProfileLink = null;
        target.userPartnerCardView = null;
        target.textProfileNotesText = null;
        this.view2131755698.setOnClickListener((View$OnClickListener)null);
        this.view2131755698 = null;
        this.view2131755706.setOnClickListener((View$OnClickListener)null);
        this.view2131755706 = null;
        this.view2131755715.setOnClickListener((View$OnClickListener)null);
        this.view2131755715 = null;
        this.view2131755724.setOnClickListener((View$OnClickListener)null);
        this.view2131755724 = null;
        this.view2131755720.setOnClickListener((View$OnClickListener)null);
        this.view2131755720 = null;
    }
}
