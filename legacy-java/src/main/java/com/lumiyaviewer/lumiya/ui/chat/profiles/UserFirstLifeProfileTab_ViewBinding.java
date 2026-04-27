package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;

/* loaded from: classes.dex */
public class UserFirstLifeProfileTab_ViewBinding implements Unbinder {
    private UserFirstLifeProfileTab target;
    private View view2131755698;
    private View view2131755706;

    @UiThread
    public UserFirstLifeProfileTab_ViewBinding(final UserFirstLifeProfileTab userFirstLifeProfileTab, View view) {
        this.target = userFirstLifeProfileTab;
        userFirstLifeProfileTab.userProfilePaymentInfo = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_payment_info, "field 'userProfilePaymentInfo'", TextView.class);
        userFirstLifeProfileTab.swipeRefreshLayout = (SwipeRefreshLayout) Utils.findRequiredViewAsType(view, R.id.swipe_refresh_layout, "field 'swipeRefreshLayout'", SwipeRefreshLayout.class);
        View findRequiredView = Utils.findRequiredView(view, R.id.about_edit_button, "field 'aboutEditButton' and method 'onAboutEditClicked'");
        userFirstLifeProfileTab.aboutEditButton = (Button) Utils.castView(findRequiredView, R.id.about_edit_button, "field 'aboutEditButton'", Button.class);
        this.view2131755706 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.UserFirstLifeProfileTab_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                userFirstLifeProfileTab.onAboutEditClicked(view2);
            }
        });
        userFirstLifeProfileTab.loadingLayout = (LoadingLayout) Utils.findRequiredViewAsType(view, R.id.loading_layout, "field 'loadingLayout'", LoadingLayout.class);
        View findRequiredView2 = Utils.findRequiredView(view, R.id.change_pic_button, "field 'changePicButton' and method 'onChangePicClicked'");
        userFirstLifeProfileTab.changePicButton = (Button) Utils.castView(findRequiredView2, R.id.change_pic_button, "field 'changePicButton'", Button.class);
        this.view2131755698 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.UserFirstLifeProfileTab_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View view2) {
                userFirstLifeProfileTab.onChangePicClicked(view2);
            }
        });
        userFirstLifeProfileTab.userProfileAboutText = (TextView) Utils.findRequiredViewAsType(view, R.id.user_profile_about_text, "field 'userProfileAboutText'", TextView.class);
        userFirstLifeProfileTab.userPicView = (ImageAssetView) Utils.findRequiredViewAsType(view, R.id.user_pic_view, "field 'userPicView'", ImageAssetView.class);
    }

    @Override // butterknife.Unbinder
    @CallSuper
    public void unbind() {
        UserFirstLifeProfileTab userFirstLifeProfileTab = this.target;
        if (userFirstLifeProfileTab == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        userFirstLifeProfileTab.userProfilePaymentInfo = null;
        userFirstLifeProfileTab.swipeRefreshLayout = null;
        userFirstLifeProfileTab.aboutEditButton = null;
        userFirstLifeProfileTab.loadingLayout = null;
        userFirstLifeProfileTab.changePicButton = null;
        userFirstLifeProfileTab.userProfileAboutText = null;
        userFirstLifeProfileTab.userPicView = null;
        this.view2131755706.setOnClickListener(null);
        this.view2131755706 = null;
        this.view2131755698.setOnClickListener(null);
        this.view2131755698 = null;
    }
}
