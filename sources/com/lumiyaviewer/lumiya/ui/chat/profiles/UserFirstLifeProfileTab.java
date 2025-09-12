// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserAboutTextEditFragment

public class UserFirstLifeProfileTab extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{

    Button aboutEditButton;
    private final SubscriptionData avatarProperties = new SubscriptionData(UIThreadExecutor.getInstance());
    Button changePicButton;
    private final LoadableMonitor loadableMonitor;
    LoadingLayout loadingLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    private Unbinder unbinder;
    ImageAssetView userPicView;
    TextView userProfileAboutText;
    TextView userProfilePaymentInfo;

    public UserFirstLifeProfileTab()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            avatarProperties
        })).withDataChangedListener(this);
    }

    protected void onAboutEditClicked(View view)
    {
        if (chatterID != null)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserAboutTextEditFragment, UserAboutTextEditFragment.makeSelection(chatterID, true));
        }
    }

    protected void onChangePicClicked(View view)
    {
        view = (AvatarPropertiesReply)avatarProperties.getData();
        if (chatterID != null && view != null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("oldProfileData", view);
            view = InventoryActivity.makeSelectActionIntent(getContext(), chatterID.agentUUID, com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity.SelectAction.applyFirstLife, bundle, SLAssetType.AT_TEXTURE);
            getContext().startActivity(view);
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f0400b6, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        loadingLayout.setSwipeRefreshLayout(swipeRefreshLayout);
        loadableMonitor.setLoadingLayout(loadingLayout, getString(0x7f0901f1), getString(0x7f090374));
        loadableMonitor.setSwipeRefreshLayout(swipeRefreshLayout);
        return layoutinflater;
    }

    public void onDestroyView()
    {
        if (unbinder != null)
        {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public void onLoadableDataChanged()
    {
        boolean flag;
        flag = true;
        if (getView() == null)
        {
            break MISSING_BLOCK_LABEL_95;
        }
        Object obj = (AvatarPropertiesReply)avatarProperties.get();
        int i;
        if ((((AvatarPropertiesReply) (obj)).PropertiesData_Field.Flags & 8) != 0)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        if ((((AvatarPropertiesReply) (obj)).PropertiesData_Field.Flags & 4) == 0)
        {
            flag = false;
        }
        userPicView.setAssetID(((AvatarPropertiesReply) (obj)).PropertiesData_Field.FLImageID);
        userProfileAboutText.setText(SLMessage.stringFromVariableUTF(((AvatarPropertiesReply) (obj)).PropertiesData_Field.FLAboutText));
        obj = userProfilePaymentInfo;
        if (i != 0)
        {
            i = 0x7f09026b;
        } else
        if (flag)
        {
            i = 0x7f090268;
        } else
        {
            i = 0x7f090269;
        }
        ((TextView) (obj)).setText(i);
        return;
        com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception;
        datanotreadyexception;
        Debug.Warning(datanotreadyexception);
        return;
    }

    protected void onShowUser(ChatterID chatterid)
    {
        boolean flag = false;
        loadableMonitor.unsubscribeAll();
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            chatterid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID();
            avatarProperties.subscribe(userManager.getAvatarProperties().getPool(), chatterid);
            if (unbinder != null)
            {
                boolean flag1 = chatterid.equals(userManager.getUserID());
                chatterid = aboutEditButton;
                int i;
                if (flag1)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                chatterid.setVisibility(i);
                chatterid = changePicButton;
                if (flag1)
                {
                    i = ((flag) ? 1 : 0);
                } else
                {
                    i = 8;
                }
                chatterid.setVisibility(i);
            }
        }
    }
}
