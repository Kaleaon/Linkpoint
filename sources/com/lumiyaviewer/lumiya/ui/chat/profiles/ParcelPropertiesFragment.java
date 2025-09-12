// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupProfileFragment, UserProfileFragment

public class ParcelPropertiesFragment extends FragmentWithTitle
{
    private class SetHomeLocationAsyncTask extends AsyncTask
    {

        private ProgressDialog progressDialog;
        final ParcelPropertiesFragment this$0;

        protected transient Boolean doInBackground(Void avoid[])
        {
            avoid = (SLAgentCircuit)ParcelPropertiesFragment._2D_get0(ParcelPropertiesFragment.this).getData();
            boolean flag;
            if (avoid != null)
            {
                flag = avoid.getModules().userProfiles.SetHomeLocation();
            } else
            {
                flag = false;
            }
            return Boolean.valueOf(flag);
        }

        protected volatile Object doInBackground(Object aobj[])
        {
            return doInBackground((Void[])aobj);
        }

        protected void onPostExecute(Boolean boolean1)
        {
            progressDialog.dismiss();
            if (boolean1 == null || boolean1.booleanValue() ^ true)
            {
                (new android.support.v7.app.AlertDialog.Builder(getContext())).setMessage(0x7f0902fc).setCancelable(true).create().show();
                return;
            } else
            {
                (new android.support.v7.app.AlertDialog.Builder(getContext())).setMessage(0x7f0902fd).setCancelable(true).create().show();
                return;
            }
        }

        protected volatile void onPostExecute(Object obj)
        {
            onPostExecute((Boolean)obj);
        }

        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(getContext(), null, getString(0x7f090302), true);
        }

        private SetHomeLocationAsyncTask()
        {
            this$0 = ParcelPropertiesFragment.this;
            super();
        }

        SetHomeLocationAsyncTask(SetHomeLocationAsyncTask sethomelocationasynctask)
        {
            this();
        }
    }


    public static final String PARCEL_DATA_KEY = "parcelData";
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda._cls3KadVkUh82bQPaUr2S81wOMi_ug._cls4(this));
    private final SubscriptionData isPlayingMedia = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda._cls3KadVkUh82bQPaUr2S81wOMi_ug._cls3(this));
    Button mediaPlayButton;
    Button mediaStopButton;
    private final ChatterNameDisplayer ownerNameDisplayer = new ChatterNameDisplayer();
    TextView parcelArea;
    private ParcelData parcelData;
    TextView parcelDescription;
    ImageAssetView parcelImageView;
    CardView parcelMediaCardView;
    TextView parcelMediaURL;
    TextView parcelName;
    TextView parcelOwnerName;
    ChatterPicView parcelOwnerPic;
    CardView simRestartCardView;
    private Unbinder unbinder;
    private UserManager userManager;

    static SubscriptionData _2D_get0(ParcelPropertiesFragment parcelpropertiesfragment)
    {
        return parcelpropertiesfragment.agentCircuit;
    }

    public ParcelPropertiesFragment()
    {
        unbinder = null;
        parcelData = null;
        userManager = null;
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_8505(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_9312(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(UUID uuid, ParcelData parceldata)
    {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putSerializable("parcelData", parceldata);
        return bundle;
    }

    private void onAgentCircuit(SLAgentCircuit slagentcircuit)
    {
        updateSimOptions();
    }

    private void onIsPlayingMedia(Boolean boolean1)
    {
        updatePlayingStatus();
    }

    private void updatePlayingStatus()
    {
        boolean flag = false;
        if (unbinder != null)
        {
            Object obj = (Boolean)isPlayingMedia.getData();
            int i;
            boolean flag1;
            if (obj != null)
            {
                flag1 = ((Boolean) (obj)).booleanValue();
            } else
            {
                flag1 = false;
            }
            obj = mediaPlayButton;
            if (flag1)
            {
                i = 8;
            } else
            {
                i = 0;
            }
            ((Button) (obj)).setVisibility(i);
            obj = mediaStopButton;
            if (flag1)
            {
                i = ((flag) ? 1 : 0);
            } else
            {
                i = 8;
            }
            ((Button) (obj)).setVisibility(i);
        }
    }

    private void updateSimOptions()
    {
        int i = 0;
        if (unbinder != null)
        {
            Object obj = (SLAgentCircuit)agentCircuit.getData();
            boolean flag;
            if (obj != null)
            {
                flag = ((SLAgentCircuit) (obj)).getIsEstateManager();
            } else
            {
                flag = false;
            }
            obj = simRestartCardView;
            if (!flag)
            {
                i = 8;
            }
            ((CardView) (obj)).setVisibility(i);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_2D_mthref_2D_0(Boolean boolean1)
    {
        onIsPlayingMedia(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_2D_mthref_2D_1(SLAgentCircuit slagentcircuit)
    {
        onAgentCircuit(slagentcircuit);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_8181(SLAgentCircuit slagentcircuit, DialogInterface dialoginterface, int i)
    {
        slagentcircuit.RestartRegion(120);
        Toast.makeText(getContext(), 0x7f09029f, 1).show();
        dialoginterface.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_ParcelPropertiesFragment_9097(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        (new SetHomeLocationAsyncTask(null)).execute(new Void[0]);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f040081, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        ownerNameDisplayer.bindViews(parcelOwnerName, parcelOwnerPic);
        parcelImageView.setVerticalFit(true);
        parcelImageView.setAlignTop(true);
        return layoutinflater;
    }

    public void onDestroyView()
    {
        if (unbinder != null)
        {
            unbinder.unbind();
            ownerNameDisplayer.unbindViews();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public void onOwnerProfileButton()
    {
label0:
        {
            if (parcelData != null)
            {
                if (!parcelData.isGroupOwned())
                {
                    break label0;
                }
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment, GroupProfileFragment.makeSelection(ownerNameDisplayer.getChatterID()));
            }
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(ownerNameDisplayer.getChatterID()));
    }

    public void onParcelMediaPlay()
    {
        if (parcelData != null && Strings.isNullOrEmpty(parcelData.getMediaURL()) ^ true && userManager != null)
        {
            Intent intent = new Intent(getContext(), com/lumiyaviewer/lumiya/StreamingMediaService);
            intent.setAction("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA");
            ActivityUtils.setActiveAgentID(intent, userManager.getUserID());
            intent.putExtra("parcelData", parcelData);
            intent.putExtra("media_url", parcelData.getMediaURL());
            intent.putExtra("location_name", parcelData.getName());
            getContext().startService(intent);
        }
    }

    public void onParcelMediaStop()
    {
        Intent intent = new Intent(getContext(), com/lumiyaviewer/lumiya/StreamingMediaService);
        intent.setAction("com.lumiyaviewer.lumiya.ACTION_STOP_MEDIA");
        getContext().startService(intent);
    }

    public void onSetHomeButton()
    {
        if ((SLAgentCircuit)agentCircuit.getData() != null)
        {
            (new android.support.v7.app.AlertDialog.Builder(getContext())).setMessage(0x7f0902fb).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda._cls3KadVkUh82bQPaUr2S81wOMi_ug._cls2(this)).setNegativeButton("No", new _2D_.Lambda._cls3KadVkUh82bQPaUr2S81wOMi_ug()).create().show();
        }
    }

    public void onSimRestartButton()
    {
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        if (slagentcircuit != null)
        {
            (new android.support.v7.app.AlertDialog.Builder(getContext())).setMessage(0x7f0902a7).setPositiveButton("Yes", new _2D_.Lambda._cls3KadVkUh82bQPaUr2S81wOMi_ug._cls5(this, slagentcircuit)).setNegativeButton("No", new _2D_.Lambda._cls3KadVkUh82bQPaUr2S81wOMi_ug._cls1()).setCancelable(true).create().show();
        }
    }

    public void onStart()
    {
        super.onStart();
        userManager = UserManager.getUserManager(UUIDPool.getUUID(getArguments().getString("activeAgentUUID")));
        parcelData = (ParcelData)getArguments().getSerializable("parcelData");
        isPlayingMedia.subscribe(StreamingMediaService.isPlayingMedia, SubscriptionSingleKey.Value);
        if (userManager != null)
        {
            agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
        }
        if (parcelData != null && userManager != null && unbinder != null)
        {
            Object obj1 = ownerNameDisplayer;
            Object obj;
            byte byte0;
            if (parcelData.isGroupOwned())
            {
                obj = ChatterID.getGroupChatterID(userManager.getUserID(), parcelData.getOwnerID());
            } else
            {
                obj = ChatterID.getUserChatterID(userManager.getUserID(), parcelData.getOwnerID());
            }
            ((ChatterNameDisplayer) (obj1)).setChatterID(((ChatterID) (obj)));
            parcelImageView.setAssetID(parcelData.getSnapshotUUID());
            parcelName.setText(parcelData.getName());
            parcelArea.setText(getString(0x7f090258, new Object[] {
                Integer.valueOf(parcelData.getArea())
            }));
            obj1 = parcelDescription;
            if (Strings.isNullOrEmpty(parcelData.getDescription()))
            {
                obj = getString(0x7f09005b);
            } else
            {
                obj = parcelData.getDescription();
            }
            ((TextView) (obj1)).setText(((CharSequence) (obj)));
            obj = parcelMediaCardView;
            if (Strings.isNullOrEmpty(parcelData.getMediaURL()))
            {
                byte0 = 8;
            } else
            {
                byte0 = 0;
            }
            ((CardView) (obj)).setVisibility(byte0);
            parcelMediaURL.setText(parcelData.getMediaURL());
            updatePlayingStatus();
            updateSimOptions();
        }
    }

    public void onStop()
    {
        userManager = null;
        parcelData = null;
        ownerNameDisplayer.setChatterID(null);
        parcelImageView.setAssetID(null);
        isPlayingMedia.unsubscribe();
        agentCircuit.unsubscribe();
        super.onStop();
    }
}
