// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            MuteListAdapter, AvatarPickerForMute

public class MuteListFragment extends FragmentWithTitle
{

    private MuteListAdapter adapter;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance());
    ListView muteList;
    private final SubscriptionData muteListData = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.dntbaqhB2OOLQW5t89NMwUjCLX4._cls2(this));
    private Unbinder unbinder;

    static void _2D_wrap0(MuteListFragment mutelistfragment, MuteListEntry mutelistentry)
    {
        mutelistfragment.doUnblock(mutelistentry);
    }

    public MuteListFragment()
    {
    }

    private void askForUnblock(MuteListEntry mutelistentry)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(0x7f090360), new Object[] {
            mutelistentry.name
        })).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.dntbaqhB2OOLQW5t89NMwUjCLX4._cls3(this, mutelistentry)).setNegativeButton("No", new _2D_.Lambda.dntbaqhB2OOLQW5t89NMwUjCLX4());
        builder.create().show();
    }

    private void doUnblock(MuteListEntry mutelistentry)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().muteList.Unblock(mutelistentry);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (MuteListEntry mutelistentry)
        {
            Debug.Warning(mutelistentry);
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_6021(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(UUID uuid)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    private void onMuteList(ImmutableList immutablelist)
    {
        if (adapter != null)
        {
            adapter.setData(immutablelist);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_2D_mthref_2D_0(ImmutableList immutablelist)
    {
        onMuteList(immutablelist);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_3737(AdapterView adapterview, View view, int i, long l)
    {
        Toast.makeText(getContext(), getString(0x7f0901c0), 0).show();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_5870(MuteListEntry mutelistentry, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        doUnblock(mutelistentry);
    }

    public void onAddMuteListButtonClick()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity != null && usermanager != null)
        {
            DetailsActivity.showEmbeddedDetails(fragmentactivity, com/lumiyaviewer/lumiya/ui/myava/AvatarPickerForMute, AvatarPickerForMute.makeArguments(usermanager.getUserID()));
        }
    }

    public boolean onContextItemSelected(MenuItem menuitem)
    {
        Object obj = (android.widget.AdapterView.AdapterContextMenuInfo)menuitem.getMenuInfo();
        if (obj == null || adapter == null) goto _L2; else goto _L1
_L1:
        obj = adapter.getItem(((android.widget.AdapterView.AdapterContextMenuInfo) (obj)).position);
        if (obj == null) goto _L2; else goto _L3
_L3:
        menuitem.getItemId();
        JVM INSTR tableswitch 2131755825 2131755825: default 60
    //                   2131755825 62;
           goto _L2 _L4
_L2:
        return false;
_L4:
        askForUnblock(((MuteListEntry) (obj)));
        return true;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        viewgroup = layoutinflater.inflate(0x7f040061, viewgroup, false);
        unbinder = ButterKnife.bind(this, viewgroup);
        adapter = new MuteListAdapter(layoutinflater.getContext());
        muteList.setAdapter(adapter);
        layoutinflater = new SwipeDismissListViewTouchListener(muteList, new com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks() {

            final MuteListFragment this$0;

            public boolean canDismiss(ListView listview, int i)
            {
                return true;
            }

            public void onDismiss(ListView listview, int i)
            {
                listview = listview.getAdapter();
                if (listview instanceof MuteListAdapter)
                {
                    listview = ((MuteListAdapter)listview).getItem(i);
                    if (listview != null)
                    {
                        MuteListFragment._2D_wrap0(MuteListFragment.this, listview);
                    }
                }
            }

            
            {
                this$0 = MuteListFragment.this;
                super();
            }
        });
        muteList.setOnTouchListener(layoutinflater);
        muteList.setOnScrollListener(layoutinflater.makeScrollListener());
        muteList.setOnItemClickListener(new _2D_.Lambda.dntbaqhB2OOLQW5t89NMwUjCLX4._cls1(this));
        registerForContextMenu(muteList);
        return viewgroup;
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

    public void onStart()
    {
        super.onStart();
        setTitle(getString(0x7f0901c1), null);
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            muteListData.subscribe(usermanager.muteListPool(), SubscriptionSingleKey.Value);
            agentCircuit.subscribe(UserManager.agentCircuits(), usermanager.getUserID());
        }
    }

    public void onStop()
    {
        muteListData.unsubscribe();
        agentCircuit.unsubscribe();
        super.onStop();
    }
}
