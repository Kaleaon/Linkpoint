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
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserFirstLifeProfileTab

public class UserFirstLifeProfileTab_ViewBinding
    implements Unbinder
{

    private UserFirstLifeProfileTab target;
    private View view2131755698;
    private View view2131755706;

    public UserFirstLifeProfileTab_ViewBinding(final UserFirstLifeProfileTab target, View view)
    {
        this.target = target;
        target.userProfilePaymentInfo = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002b7, "field 'userProfilePaymentInfo'", android/widget/TextView);
        target.swipeRefreshLayout = (SwipeRefreshLayout)Utils.findRequiredViewAsType(view, 0x7f1000bb, "field 'swipeRefreshLayout'", android/support/v4/widget/SwipeRefreshLayout);
        View view1 = Utils.findRequiredView(view, 0x7f1002ba, "field 'aboutEditButton' and method 'onAboutEditClicked'");
        target.aboutEditButton = (Button)Utils.castView(view1, 0x7f1002ba, "field 'aboutEditButton'", android/widget/Button);
        view2131755706 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserFirstLifeProfileTab_ViewBinding this$0;
            final UserFirstLifeProfileTab val$target;

            public void doClick(View view2)
            {
                target.onAboutEditClicked(view2);
            }

            
            {
                this$0 = UserFirstLifeProfileTab_ViewBinding.this;
                target = userfirstlifeprofiletab;
                super();
            }
        });
        target.loadingLayout = (LoadingLayout)Utils.findRequiredViewAsType(view, 0x7f1000bd, "field 'loadingLayout'", com/lumiyaviewer/lumiya/ui/common/LoadingLayout);
        view1 = Utils.findRequiredView(view, 0x7f1002b2, "field 'changePicButton' and method 'onChangePicClicked'");
        target.changePicButton = (Button)Utils.castView(view1, 0x7f1002b2, "field 'changePicButton'", android/widget/Button);
        view2131755698 = view1;
        view1.setOnClickListener(new DebouncingOnClickListener() {

            final UserFirstLifeProfileTab_ViewBinding this$0;
            final UserFirstLifeProfileTab val$target;

            public void doClick(View view2)
            {
                target.onChangePicClicked(view2);
            }

            
            {
                this$0 = UserFirstLifeProfileTab_ViewBinding.this;
                target = userfirstlifeprofiletab;
                super();
            }
        });
        target.userProfileAboutText = (TextView)Utils.findRequiredViewAsType(view, 0x7f1002b9, "field 'userProfileAboutText'", android/widget/TextView);
        target.userPicView = (ImageAssetView)Utils.findRequiredViewAsType(view, 0x7f1002b6, "field 'userPicView'", com/lumiyaviewer/lumiya/ui/common/ImageAssetView);
    }

    public void unbind()
    {
        UserFirstLifeProfileTab userfirstlifeprofiletab = target;
        if (userfirstlifeprofiletab == null)
        {
            throw new IllegalStateException("Bindings already cleared.");
        } else
        {
            target = null;
            userfirstlifeprofiletab.userProfilePaymentInfo = null;
            userfirstlifeprofiletab.swipeRefreshLayout = null;
            userfirstlifeprofiletab.aboutEditButton = null;
            userfirstlifeprofiletab.loadingLayout = null;
            userfirstlifeprofiletab.changePicButton = null;
            userfirstlifeprofiletab.userProfileAboutText = null;
            userfirstlifeprofiletab.userPicView = null;
            view2131755706.setOnClickListener(null);
            view2131755706 = null;
            view2131755698.setOnClickListener(null);
            view2131755698 = null;
            return;
        }
    }
}
