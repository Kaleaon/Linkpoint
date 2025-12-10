package com.linkpoint.ui.chat.profiles
import java.util.*

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.core.widget.SwipeRefreshLayout
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
    UserFirstLifeProfileTab_ViewBinding(UserFirstLifeProfileTab userFirstLifeProfileTab, View view) {
        this.target = userFirstLifeProfileTab
        userFirstLifeProfileTab.userProfilePaymentInfo = (Utils as TextView).findRequiredViewAsType(view, R.id.text_profile_payment_info, "field 'userProfilePaymentInfo'", TextView.class)
        userFirstLifeProfileTab.swipeRefreshLayout = (Utils as SwipeRefreshLayout).findRequiredViewAsType(view, R.id.swipe_refresh_layout, "field 'swipeRefreshLayout'", SwipeRefreshLayout.class)
        View findRequiredView = Utils.findRequiredView(view, R.id.about_edit_button, "field 'aboutEditButton' and method 'onAboutEditClicked'")
        userFirstLifeProfileTab.aboutEditButton = (Utils as Button).castView(findRequiredView, R.id.about_edit_button, "field 'aboutEditButton'", Button.class)
        this.view2131755706 = findRequiredView
        findRequiredView.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                userFirstLifeProfileTab.onAboutEditClicked(view)
            }
        userFirstLifeProfileTab.loadingLayout = (Utils as LoadingLayout).findRequiredViewAsType(view, R.id.loading_layout, "field 'loadingLayout'", LoadingLayout.class)
        View findRequiredView2 = Utils.findRequiredView(view, R.id.change_pic_button, "field 'changePicButton' and method 'onChangePicClicked'")
        userFirstLifeProfileTab.changePicButton = (Utils as Button).castView(findRequiredView2, R.id.change_pic_button, "field 'changePicButton'", Button.class)
        this.view2131755698 = findRequiredView2
        findRequiredView2.setOnClickListener(DebouncingOnClickListener() {
            fun doClick(View view): Unit {
                userFirstLifeProfileTab.onChangePicClicked(view)
            }
        userFirstLifeProfileTab.userProfileAboutText = (Utils as TextView).findRequiredViewAsType(view, R.id.user_profile_about_text, "field 'userProfileAboutText'", TextView.class)
        userFirstLifeProfileTab.userPicView = (Utils as ImageAssetView).findRequiredViewAsType(view, R.id.user_pic_view, "field 'userPicView'", ImageAssetView.class)
    }

    @CallSuper
    fun unbind(): Unit {
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
