// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.BalanceManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            MyAvatarDetailsPages, MyProfileFragment, MuteListFragment, TransactionLogFragment

public class MyAvatarFragment extends FragmentWithTitle
    implements android.widget.AdapterView.OnItemClickListener, com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
{
    private class MyAvatarPagesAdapter extends ArrayAdapter
    {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$ui$myava$MyAvatarDetailsPages[];
        final MyAvatarFragment this$0;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues;
            }
            int ai[] = new int[MyAvatarDetailsPages.values().length];
            try
            {
                ai[MyAvatarDetailsPages.pageBalance.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[MyAvatarDetailsPages.pageProfile.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues = ai;
            return ai;
        }

        public View getView(int i, View view, ViewGroup viewgroup)
        {
label0:
            {
                viewgroup = super.getView(i, view, viewgroup);
                view = (MyAvatarDetailsPages)getItem(i);
                if ((viewgroup instanceof TextView) && view != null)
                {
                    switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues()[view.ordinal()])
                    {
                    default:
                        view = getString(view.getTitleResource());
                        ((TextView)viewgroup).setText(view);
                        break;

                    case 1: // '\001'
                        break label0;
                    }
                }
                return viewgroup;
            }
            view = (Integer)MyAvatarFragment._2D_get0(MyAvatarFragment.this).getData();
            if (view != null)
            {
                view = getString(0x7f0901c3, new Object[] {
                    view
                });
            } else
            {
                view = getString(0x7f0901c4);
            }
            ((TextView)viewgroup).setText(view);
            return viewgroup;
        }

        public MyAvatarPagesAdapter(Context context)
        {
            this$0 = MyAvatarFragment.this;
            super(context, 0x1090003, MyAvatarDetailsPages.values());
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues[];
    TextView myAvatarName;
    private ChatterNameRetriever myAvatarNameRetriever;
    ListView myAvatarOptionsList;
    ChatterPicView myAvatarPic;
    private final SubscriptionData myBalance = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.E97LbIKTNF028fQGuPv0gXqIQrc(this));
    private Unbinder unbinder;

    static SubscriptionData _2D_get0(MyAvatarFragment myavatarfragment)
    {
        return myavatarfragment.myBalance;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues;
        }
        int ai[] = new int[MyAvatarDetailsPages.values().length];
        try
        {
            ai[MyAvatarDetailsPages.pageBalance.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[MyAvatarDetailsPages.pageProfile.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues = ai;
        return ai;
    }

    public MyAvatarFragment()
    {
        myAvatarNameRetriever = null;
    }

    private UUID getAgentUUID()
    {
        return ActivityUtils.getActiveAgentID(getArguments());
    }

    public static Bundle makeSelection(UUID uuid)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    public static MyAvatarFragment newInstance(UUID uuid)
    {
        MyAvatarFragment myavatarfragment = new MyAvatarFragment();
        myavatarfragment.setArguments(makeSelection(uuid));
        return myavatarfragment;
    }

    private void onMyBalance(Integer integer)
    {
        if (unbinder != null)
        {
            integer = myAvatarOptionsList.getAdapter();
            if (integer instanceof MyAvatarPagesAdapter)
            {
                ((MyAvatarPagesAdapter)integer).notifyDataSetChanged();
            }
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_myava_MyAvatarFragment_2D_mthref_2D_0(Integer integer)
    {
        onMyBalance(integer);
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        String s1 = chatternameretriever.getResolvedName();
        if (unbinder != null)
        {
            TextView textview = myAvatarName;
            String s;
            if (s1 != null)
            {
                s = s1;
            } else
            {
                s = getString(0x7f0901c8);
            }
            textview.setText(s);
            myAvatarPic.setChatterID(chatternameretriever.chatterID, s1);
        }
        setTitle(s1, null);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f040063, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        myAvatarOptionsList.setAdapter(new MyAvatarPagesAdapter(viewgroup.getContext()));
        myAvatarOptionsList.setOnItemClickListener(this);
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

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        view = getAgentUUID();
        adapterview = ((AdapterView) (adapterview.getItemAtPosition(i)));
        if (!(adapterview instanceof MyAvatarDetailsPages) || view == null) goto _L2; else goto _L1
_L1:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_myava_2D_MyAvatarDetailsPagesSwitchesValues()[((MyAvatarDetailsPages)adapterview).ordinal()];
        JVM INSTR tableswitch 1 4: default 64
    //                   1 115
    //                   2 100
    //                   3 84
    //                   4 65;
           goto _L2 _L3 _L4 _L5 _L6
_L2:
        return;
_L6:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/myava/MyProfileFragment, MyProfileFragment.makeSelection(ChatterID.getUserChatterID(view, view)));
        return;
_L5:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/outfits/OutfitsFragment, OutfitsFragment.makeSelection(view, null));
        return;
_L4:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/myava/MuteListFragment, MuteListFragment.makeSelection(view));
        return;
_L3:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/myava/TransactionLogFragment, TransactionLogFragment.makeSelection(view));
        return;
    }

    public void onStart()
    {
        super.onStart();
        UUID uuid = getAgentUUID();
        UserManager usermanager = UserManager.getUserManager(uuid);
        if (usermanager != null)
        {
            myBalance.subscribe(usermanager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
        if (uuid != null)
        {
            myAvatarNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(uuid, uuid), this, UIThreadExecutor.getSerialInstance());
        }
    }

    public void onStop()
    {
        if (myAvatarNameRetriever != null)
        {
            myAvatarNameRetriever.dispose();
            myAvatarNameRetriever = null;
        }
        myBalance.unsubscribe();
        super.onStop();
    }
}
