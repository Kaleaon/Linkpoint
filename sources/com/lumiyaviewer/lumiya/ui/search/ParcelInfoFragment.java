// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ParcelInfoReply;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class ParcelInfoFragment extends FragmentWithTitle
    implements ReloadableFragment, com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener, com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
{

    private static final String PARCEL_UUID_KEY = "parcelUUID";
    private final LoadableMonitor loadableMonitor;
    private ChatterNameRetriever ownerGroupNameRetriever;
    private ChatterNameRetriever ownerNameRetriever;
    TextView parcelDetailsDescription;
    TextView parcelDetailsName;
    ImageAssetView parcelImageView;
    private final SubscriptionData parcelInfoReply = new SubscriptionData(UIThreadExecutor.getInstance());
    TextView parcelLocation;
    TextView parcelOwnerName;
    ChatterPicView parcelOwnerPic;
    TextView parcelSimName;
    private Unbinder unbinder;

    public ParcelInfoFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            parcelInfoReply
        })).withDataChangedListener(this);
        ownerNameRetriever = null;
        ownerGroupNameRetriever = null;
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_search_ParcelInfoFragment_9648(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid1)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putString("parcelUUID", uuid1.toString());
        return bundle;
    }

    private void showParcelInfo(UUID uuid)
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null && uuid != null)
        {
            Debug.Printf("ParcelInfo: subscribing for UUID %s", new Object[] {
                uuid
            });
            parcelInfoReply.subscribe(usermanager.parcelInfoData().getPool(), uuid);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_search_ParcelInfoFragment_9137(UserManager usermanager, LLVector3 llvector3, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        dialoginterface = usermanager.getActiveAgentCircuit();
        if (dialoginterface != null)
        {
            (new TeleportProgressDialog(getContext(), usermanager, 0x7f090350)).show();
            dialoginterface.TeleportToGlobalPosition(llvector3);
        }
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        if ((chatternameretriever == ownerNameRetriever || chatternameretriever == ownerGroupNameRetriever) && unbinder != null && ownerGroupNameRetriever != null && ownerNameRetriever != null)
        {
            String s;
            String s1;
            TextView textview;
            if (ownerGroupNameRetriever.getResolvedName() != null)
            {
                chatternameretriever = ownerGroupNameRetriever;
            } else
            {
                chatternameretriever = ownerNameRetriever;
            }
            s1 = chatternameretriever.getResolvedName();
            textview = parcelOwnerName;
            if (s1 != null)
            {
                s = s1;
            } else
            {
                s = getString(0x7f0901c8);
            }
            textview.setText(s);
            parcelOwnerPic.setVisibility(0);
            parcelOwnerPic.setChatterID(chatternameretriever.chatterID, s1);
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f040080, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901ed), getString(0x7f09011d));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        parcelImageView.setAlignTop(true);
        parcelImageView.setVerticalFit(true);
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
        ParcelInfoReply parcelinforeply = (ParcelInfoReply)parcelInfoReply.getData();
        Debug.Printf("ParcelInfo: loadable data %s", new Object[] {
            parcelinforeply
        });
        UUID uuid = ActivityUtils.getActiveAgentID(getArguments());
        if (unbinder != null && parcelinforeply != null && uuid != null)
        {
            if (ownerNameRetriever != null)
            {
                ownerNameRetriever.dispose();
                ownerNameRetriever = null;
            }
            if (ownerGroupNameRetriever != null)
            {
                ownerGroupNameRetriever.dispose();
                ownerGroupNameRetriever = null;
            }
            String s = SLMessage.stringFromVariableOEM(parcelinforeply.Data_Field.Name);
            setTitle(s, null);
            parcelDetailsName.setText(s);
            s = SLMessage.stringFromVariableOEM(parcelinforeply.Data_Field.Desc).trim();
            TextView textview = parcelDetailsDescription;
            if (Strings.isNullOrEmpty(s))
            {
                s = getString(0x7f09005b);
            }
            textview.setText(s);
            Debug.Printf("ParcelInfo: ownerID = %s", new Object[] {
                parcelinforeply.Data_Field.OwnerID
            });
            if (UUIDPool.ZeroUUID.equals(parcelinforeply.Data_Field.OwnerID))
            {
                parcelOwnerName.setText(0x7f09014f);
                parcelOwnerPic.setVisibility(8);
            } else
            {
                ownerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(uuid, parcelinforeply.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance());
                ownerGroupNameRetriever = new ChatterNameRetriever(ChatterID.getGroupChatterID(uuid, parcelinforeply.Data_Field.OwnerID), this, UIThreadExecutor.getSerialInstance());
            }
            parcelImageView.setAssetID(parcelinforeply.Data_Field.SnapshotID);
            parcelSimName.setText(SLMessage.stringFromVariableOEM(parcelinforeply.Data_Field.SimName));
            parcelLocation.setText(getString(0x7f09025a, new Object[] {
                Float.valueOf(parcelinforeply.Data_Field.GlobalX % 256F), Float.valueOf(parcelinforeply.Data_Field.GlobalY % 256F), Float.valueOf(parcelinforeply.Data_Field.GlobalZ)
            }));
        }
    }

    public void onParcelOwnerProfileClick()
    {
        UUID uuid;
        ParcelInfoReply parcelinforeply;
label0:
        {
            uuid = ActivityUtils.getActiveAgentID(getArguments());
            parcelinforeply = (ParcelInfoReply)parcelInfoReply.getData();
            if (uuid != null && parcelinforeply != null)
            {
                if (ownerGroupNameRetriever == null || ownerGroupNameRetriever.getResolvedName() == null)
                {
                    break label0;
                }
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment, GroupProfileFragment.makeSelection(ownerGroupNameRetriever.chatterID));
            }
            return;
        }
        if (ownerNameRetriever != null && ownerNameRetriever.getResolvedName() != null)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(ownerNameRetriever.chatterID));
            return;
        } else
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(uuid, parcelinforeply.Data_Field.OwnerID)));
            return;
        }
    }

    public void onParcelTeleportButton()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        Object obj = (ParcelInfoReply)parcelInfoReply.getData();
        if (obj != null && usermanager != null)
        {
            obj = new LLVector3(((ParcelInfoReply) (obj)).Data_Field.GlobalX, ((ParcelInfoReply) (obj)).Data_Field.GlobalY, ((ParcelInfoReply) (obj)).Data_Field.GlobalZ);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getString(0x7f09034a)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda._cls5Jqy4HmgAu6T9fnroWh_2D_Zqm3eJE._cls1(this, usermanager, obj)).setNegativeButton("No", new _2D_.Lambda._cls5Jqy4HmgAu6T9fnroWh_2D_Zqm3eJE());
            builder.create().show();
        }
    }

    public void onStart()
    {
        super.onStart();
        showParcelInfo(UUIDPool.getUUID(getArguments().getString("parcelUUID")));
    }

    public void onStop()
    {
        loadableMonitor.unsubscribeAll();
        if (ownerNameRetriever != null)
        {
            ownerNameRetriever.dispose();
            ownerNameRetriever = null;
        }
        if (ownerGroupNameRetriever != null)
        {
            ownerGroupNameRetriever.dispose();
            ownerGroupNameRetriever = null;
        }
        if (unbinder != null)
        {
            parcelOwnerPic.setChatterID(null, null);
            parcelImageView.setAssetID(null);
        }
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle)
    {
        getArguments().putAll(bundle);
        if (isFragmentStarted())
        {
            showParcelInfo(UUIDPool.getUUID(bundle.getString("parcelUUID")));
        }
    }
}
