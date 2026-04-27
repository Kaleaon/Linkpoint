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
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;

public class UserMainProfileTab_ViewBinding implements Unbinder {
    private UserMainProfileTab target;
    private View view2131755698;
    private View view2131755706;
    private View view2131755715;
    private View view2131755720;
    private View view2131755724;

    @UiThread
    public UserMainProfileTab_ViewBinding(final UserMainProfileTab userMainProfileTab, View view) {
        this.target = userMainProfileTab;
        userMainProfileTab.textProfilePrimaryName = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_primary_name, "field 'textProfilePrimaryName'", TextView.class);
        userMainProfileTab.userProfileNotesCaption = Utils.findRequiredView(view, R.id.user_profile_notes_caption, "field 'userProfileNotesCaption'");
        userMainProfileTab.textProfileOnline = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_online, "field 'textProfileOnline'", TextView.class);
        userMainProfileTab.userProfilePartnerPic = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.user_profile_partner_pic, "field 'userProfilePartnerPic'", ChatterPicView.class);
        userMainProfileTab.userProfilePartnerName = (TextView) Utils.findRequiredViewAsType(view, R.id.user_profile_partner_name, "field 'userProfilePartnerName'", TextView.class);
        userMainProfileTab.userWebProfileCardView = Utils.findRequiredView(view, R.id.user_web_profile_card_view, "field 'userWebProfileCardView'");
        userMainProfileTab.swipeRefreshLayout = (SwipeRefreshLayout) Utils.findRequiredViewAsType(view, R.id.swipe_refresh_layout, "field 'swipeRefreshLayout'", SwipeRefreshLayout.class);
        userMainProfileTab.textProfileSecondaryName = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_secondary_name, "field 'textProfileSecondaryName'", TextView.class);
        View findRequiredView = Utils.findRequiredView(view, R.id.change_pic_button, "field 'changePicButton' and method 'onChangePicClicked'");
        userMainProfileTab.changePicButton = (Button) Utils.castView(findRequiredView, R.id.change_pic_button, "field 'changePicButton'", Button.class);
        this.view2131755698 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                userMainProfileTab.onChangePicClicked(view);
            }
        });
        userMainProfileTab.userPicView = (ImageAssetView) Utils.findRequiredViewAsType(view, R.id.user_pic_view, "field 'userPicView'", ImageAssetView.class);
        userMainProfileTab.userProfileAboutText = (TextView) Utils.findRequiredViewAsType(view, R.id.user_profile_about_text, "field 'userProfileAboutText'", TextView.class);
        userMainProfileTab.textProfileAge = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_age, "field 'textProfileAge'", TextView.class);
        userMainProfileTab.loadingLayout = (LoadingLayout) Utils.findRequiredViewAsType(view, R.id.loading_layout, "field 'loadingLayout'", LoadingLayout.class);
        userMainProfileTab.textProfileAgentKey = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_agent_key, "field 'textProfileAgentKey'", TextView.class);
        View findRequiredView2 = Utils.findRequiredView(view, R.id.about_edit_button, "field 'aboutEditButton' and method 'onAboutEditClicked'");
        userMainProfileTab.aboutEditButton = (Button) Utils.castView(findRequiredView2, R.id.about_edit_button, "field 'aboutEditButton'", Button.class);
        this.view2131755706 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                userMainProfileTab.onAboutEditClicked(view);
            }
        });
        userMainProfileTab.userWebProfileLink = (TextView) Utils.findRequiredViewAsType(view, R.id.user_web_profile_link, "field 'userWebProfileLink'", TextView.class);
        userMainProfileTab.userPartnerCardView = Utils.findRequiredView(view, R.id.user_partner_card_view, "field 'userPartnerCardView'");
        userMainProfileTab.textProfileNotesText = (TextView) Utils.findRequiredViewAsType(view, R.id.text_profile_notes_text, "field 'textProfileNotesText'", TextView.class);
        View findRequiredView3 = Utils.findRequiredView(view, R.id.user_profile_view_partner_button, "method 'onViewProfileClicked'");
        this.view2131755715 = findRequiredView3;
        findRequiredView3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                userMainProfileTab.onViewProfileClicked(view);
            }
        });
        View findRequiredView4 = Utils.findRequiredView(view, R.id.button_edit_notes, "method 'onEditNotesClicked'");
        this.view2131755724 = findRequiredView4;
        findRequiredView4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                userMainProfileTab.onEditNotesClicked(view);
            }
        });
        View findRequiredView5 = Utils.findRequiredView(view, R.id.button_copy_agent_key, "method 'onCopyAgentKeyClicked'");
        this.view2131755720 = findRequiredView5;
        findRequiredView5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                userMainProfileTab.onCopyAgentKeyClicked(view);
            }
        });
    }

    @CallSuper
    public void unbind() {
        UserMainProfileTab userMainProfileTab = this.target;
        if (userMainProfileTab == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        userMainProfileTab.textProfilePrimaryName = null;
        userMainProfileTab.userProfileNotesCaption = null;
        userMainProfileTab.textProfileOnline = null;
        userMainProfileTab.userProfilePartnerPic = null;
        userMainProfileTab.userProfilePartnerName = null;
        userMainProfileTab.userWebProfileCardView = null;
        userMainProfileTab.swipeRefreshLayout = null;
        userMainProfileTab.textProfileSecondaryName = null;
        userMainProfileTab.changePicButton = null;
        userMainProfileTab.userPicView = null;
        userMainProfileTab.userProfileAboutText = null;
        userMainProfileTab.textProfileAge = null;
        userMainProfileTab.loadingLayout = null;
        userMainProfileTab.textProfileAgentKey = null;
        userMainProfileTab.aboutEditButton = null;
        userMainProfileTab.userWebProfileLink = null;
        userMainProfileTab.userPartnerCardView = null;
        userMainProfileTab.textProfileNotesText = null;
        this.view2131755698.setOnClickListener((View.OnClickListener) null);
        this.view2131755698 = null;
        this.view2131755706.setOnClickListener((View.OnClickListener) null);
        this.view2131755706 = null;
        this.view2131755715.setOnClickListener((View.OnClickListener) null);
        this.view2131755715 = null;
        this.view2131755724.setOnClickListener((View.OnClickListener) null);
        this.view2131755724 = null;
        this.view2131755720.setOnClickListener((View.OnClickListener) null);
        this.view2131755720 = null;
    }
}
