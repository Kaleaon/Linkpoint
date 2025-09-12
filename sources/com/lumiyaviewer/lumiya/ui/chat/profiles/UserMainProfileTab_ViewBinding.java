// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserMainProfileTab

public class UserMainProfileTab_ViewBinding
    implements Unbinder
{

    private UserMainProfileTab target;
    private View view2131755698;
    private View view2131755706;
    private View view2131755715;
    private View view2131755720;
    private View view2131755724;

    public UserMainProfileTab_ViewBinding(final UserMainProfileTab target, View view)
    {
        this.target = target;
        target.textProfilePrimaryName = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002bc, "field 'textProfilePrimaryName'", android/widget/TextView);
        target.userProfileNotesCaption = Utils.findRequiredView(view, 0x7f1002ca, "field 'userProfileNotesCaption'");
        target.textProfileOnline = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002be, "field 'textProfileOnline'", android/widget/TextView);
        target.userProfilePartnerPic = (ChatterPicView)Utils.findRequiredViewAsType(view, 0x7f1002c2, "field 'userProfilePartnerPic'", com/lumiyaviewer/lumiya/ui/chat/ChatterPicView);
        target.userProfilePartnerName = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002c1, "field 'userProfilePartnerName'", android/widget/TextView);
        target.userWebProfileCardView = Utils.findRequiredView(view, 0x7f1002c4, "field 'userWebProfileCardView'");
        target.swipeRefreshLayout = (SwipeRefreshLayout)Utils.findRequiredViewAsType(view, 0x7f1000bb, "field 'swipeRefreshLayout'", android/support/v4/widget/SwipeRefreshLayout);
        target.textProfileSecondaryName = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002bd, "field 'textProfileSecondaryName'", android/widget/TextView);
        View view1 = Utils.findRequiredView(view, 0x7f1002b2, "field 'changePicButton' and method 'onChangePicClicked'");
        target.changePicButton = (Button)Utils.castView(view1, 0x7f1002b2, "field 'changePicButton'", android/widget/Button);
        view2131755698 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserMainProfileTab_ViewBinding this$0;
            final UserMainProfileTab val$target;

            public void doClick(View view2)
            {
                target.onChangePicClicked(view2);
            }

            
            {
                this$0 = UserMainProfileTab_ViewBinding.this;
                target = usermainprofiletab;
                super();
            }
        });
        target.userPicView = (ImageAssetView)Utils.findRequiredViewAsType(view, 0x7f1002b6, "field 'userPicView'", com/lumiyaviewer/lumiya/ui/common/ImageAssetView);
        target.userProfileAboutText = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002b9, "field 'userProfileAboutText'", android/widget/TextView);
        target.textProfileAge = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002bf, "field 'textProfileAge'", android/widget/TextView);
        target.loadingLayout = (LoadingLayout)Utils.findRequiredViewAsType(view, 0x7f1000bd, "field 'loadingLayout'", com/lumiyaviewer/lumiya/ui/common/LoadingLayout);
        target.textProfileAgentKey = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002c7, "field 'textProfileAgentKey'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f1002ba, "field 'aboutEditButton' and method 'onAboutEditClicked'");
        target.aboutEditButton = (Button)Utils.castView(view1, 0x7f1002ba, "field 'aboutEditButton'", android/widget/Button);
        view2131755706 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserMainProfileTab_ViewBinding this$0;
            final UserMainProfileTab val$target;

            public void doClick(View view2)
            {
                target.onAboutEditClicked(view2);
            }

            
            {
                this$0 = UserMainProfileTab_ViewBinding.this;
                target = usermainprofiletab;
                super();
            }
        });
        target.userWebProfileLink = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002c5, "field 'userWebProfileLink'", android/widget/TextView);
        target.userPartnerCardView = Utils.findRequiredView(view, 0x7f1002c0, "field 'userPartnerCardView'");
        target.textProfileNotesText = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002cb, "field 'textProfileNotesText'", android/widget/TextView);
        view1 = Utils.findRequiredView(view, 0x7f1002c3, "method 'onViewProfileClicked'");
        view2131755715 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserMainProfileTab_ViewBinding this$0;
            final UserMainProfileTab val$target;

            public void doClick(View view2)
            {
                target.onViewProfileClicked(view2);
            }

            
            {
                this$0 = UserMainProfileTab_ViewBinding.this;
                target = usermainprofiletab;
                super();
            }
        });
        view1 = Utils.findRequiredView(view, 0x7f1002cc, "method 'onEditNotesClicked'");
        view2131755724 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserMainProfileTab_ViewBinding this$0;
            final UserMainProfileTab val$target;

            public void doClick(View view2)
            {
                target.onEditNotesClicked(view2);
            }

            
            {
                this$0 = UserMainProfileTab_ViewBinding.this;
                target = usermainprofiletab;
                super();
            }
        });
        view = Utils.findRequiredView(view, 0x7f1002c8, "method 'onCopyAgentKeyClicked'");
        view2131755720 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {

            final UserMainProfileTab_ViewBinding this$0;
            final UserMainProfileTab val$target;

            public void doClick(View view2)
            {
                target.onCopyAgentKeyClicked(view2);
            }

            
            {
                this$0 = UserMainProfileTab_ViewBinding.this;
                target = usermainprofiletab;
                super();
            }
        });
    }

    public void unbind()
    {
        UserMainProfileTab usermainprofiletab = target;
        if (usermainprofiletab == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            usermainprofiletab.textProfilePrimaryName = null;
            usermainprofiletab.userProfileNotesCaption = null;
            usermainprofiletab.textProfileOnline = null;
            usermainprofiletab.userProfilePartnerPic = null;
            usermainprofiletab.userProfilePartnerName = null;
            usermainprofiletab.userWebProfileCardView = null;
            usermainprofiletab.swipeRefreshLayout = null;
            usermainprofiletab.textProfileSecondaryName = null;
            usermainprofiletab.changePicButton = null;
            usermainprofiletab.userPicView = null;
            usermainprofiletab.userProfileAboutText = null;
            usermainprofiletab.textProfileAge = null;
            usermainprofiletab.loadingLayout = null;
            usermainprofiletab.textProfileAgentKey = null;
            usermainprofiletab.aboutEditButton = null;
            usermainprofiletab.userWebProfileLink = null;
            usermainprofiletab.userPartnerCardView = null;
            usermainprofiletab.textProfileNotesText = null;
            view2131755698.setOnClickListener(null);
            view2131755698 = null;
            view2131755706.setOnClickListener(null);
            view2131755706 = null;
            view2131755715.setOnClickListener(null);
            view2131755715 = null;
            view2131755724.setOnClickListener(null);
            view2131755724 = null;
            view2131755720.setOnClickListener(null);
            view2131755720 = null;
            return;
        }
    }
}
