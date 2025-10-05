package com.linkpoint.ui.chat.profiles
import java.util.*

import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.Unbinder
import butterknife.internal.DebouncingOnClickListener
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.ui.common.ImageAssetView
import com.linkpoint.ui.common.LoadingLayout

class UserFirstLifeProfileTab_ViewBinding : Unbinder {
    private UserFirstLifeProfileTab target
    private View view2131755698
    private View view2131755706

    @UiThread
    public UserFirstLifeProfileTab_ViewBinding(final UserFirstLifeProfileTab userFirstLifeProfileTab, View view) {
        this.target = userFirstLifeProfileTab
        userFirstLifeProfileTab.userProfilePaymentInfo = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_payment_info, "field 'userProfilePaymentInfo'", TextView.class)
        userFirstLifeProfileTab.swipeRefreshLayout = (SwipeRefreshLayout) Utils.findRequiredViewAsType(view, R.id.swipe_refresh_layout, "field 'swipeRefreshLayout'", SwipeRefreshLayout.class)
        View findRequiredView = Utils.findRequiredView(view, R.id.about_edit_button, "field 'aboutEditButton' and method 'onAboutEditClicked'")
        userFirstLifeProfileTab.aboutEditButton = (Button) Utils.castView(findRequiredView, R.id.about_edit_button, "field 'aboutEditButton'", Button.class)
        this.view2131755706 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            public Unit doClick(View view) {
                userFirstLifeProfileTab.onAboutEditClicked(view)
            }
        userFirstLifeProfileTab.loadingLayout = (LoadingLayout) Utils.findRequiredViewAsType(view, R.id.loading_layout, "field 'loadingLayout'", LoadingLayout.class)
        View findRequiredView2 = Utils.findRequiredView(view, R.id.change_pic_button, "field 'changePicButton' and method 'onChangePicClicked'")
        userFirstLifeProfileTab.changePicButton = (Button) Utils.castView(findRequiredView2, R.id.change_pic_button, "field 'changePicButton'", Button.class)
        this.view2131755698 = findRequiredView2
        findRequiredView2.setOnClickListener(DebouncingOnClickListener() {
            public Unit doClick(View view) {
                userFirstLifeProfileTab.onChangePicClicked(view)
            }
        userFirstLifeProfileTab.userProfileAboutText = (TextView) Utils.findRequiredViewAsType(view, R.id.user_profile_about_text, "field 'userProfileAboutText'", TextView.class)
        userFirstLifeProfileTab.userPicView = (ImageAssetView) Utils.findRequiredViewAsType(view, R.id.user_pic_view, "field 'userPicView'", ImageAssetView.class)
    }

    @CallSuper
    public Unit unbind() {
        UserFirstLifeProfileTab userFirstLifeProfileTab = this.target
        if (userFirstLifeProfileTab == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        userFirstLifeProfileTab.userProfilePaymentInfo = null
        userFirstLifeProfileTab.swipeRefreshLayout = null
        userFirstLifeProfileTab.aboutEditButton = null
        userFirstLifeProfileTab.loadingLayout = null
        userFirstLifeProfileTab.changePicButton = null
        userFirstLifeProfileTab.userProfileAboutText = null
        userFirstLifeProfileTab.userPicView = null
        this.view2131755706.setOnClickListener((View.OnClickListener) null)
        this.view2131755706 = null
        this.view2131755698.setOnClickListener((View.OnClickListener) null)
        this.view2131755698 = null
    }
}
