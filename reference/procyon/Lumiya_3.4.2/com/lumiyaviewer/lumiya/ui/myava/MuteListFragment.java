// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import com.lumiyaviewer.lumiya.react.Subscribable;
import android.widget.AdapterView$OnItemClickListener;
import android.view.View$OnTouchListener;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import android.widget.ListAdapter;
import butterknife.ButterKnife;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.annotation.Nullable;
import android.widget.AdapterView$AdapterContextMenuInfo;
import android.view.MenuItem;
import butterknife.OnClick;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import java.util.List;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.Debug;
import android.content.DialogInterface$OnClickListener;
import android.content.Context;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import butterknife.BindView;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class MuteListFragment extends FragmentWithTitle
{
    private MuteListAdapter adapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    @BindView(2131755491)
    ListView muteList;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListData;
    private Unbinder unbinder;
    
    public MuteListFragment() {
        this.muteListData = new SubscriptionData<SubscriptionSingleKey, ImmutableList<MuteListEntry>>(UIThreadExecutor.getInstance(), new _$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4$2(this));
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance());
    }
    
    private void askForUnblock(final MuteListEntry muteListEntry) {
        final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this.getActivity());
        alertDialog$Builder.setMessage((CharSequence)String.format(this.getString(2131297120), muteListEntry.name)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4$3(this, muteListEntry)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4());
        alertDialog$Builder.create().show();
    }
    
    private void doUnblock(final MuteListEntry muteListEntry) {
        try {
            this.agentCircuit.get().getModules().muteList.Unblock(muteListEntry);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    public static Bundle makeSelection(final UUID uuid) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }
    
    private void onMuteList(final ImmutableList<MuteListEntry> data) {
        if (this.adapter != null) {
            this.adapter.setData(data);
        }
    }
    
    @OnClick({ 2131755492 })
    public void onAddMuteListButtonClick() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final FragmentActivity activity = this.getActivity();
        if (activity != null && userManager != null) {
            DetailsActivity.showEmbeddedDetails(activity, AvatarPickerForMute.class, AvatarPickerForMute.makeArguments(userManager.getUserID()));
        }
    }
    
    @Override
    public boolean onContextItemSelected(final MenuItem menuItem) {
        final AdapterView$AdapterContextMenuInfo adapterView$AdapterContextMenuInfo = (AdapterView$AdapterContextMenuInfo)menuItem.getMenuInfo();
        if (adapterView$AdapterContextMenuInfo != null && this.adapter != null) {
            final MuteListEntry item = this.adapter.getItem(adapterView$AdapterContextMenuInfo.position);
            if (item != null) {
                switch (menuItem.getItemId()) {
                    case 2131755825: {
                        this.askForUnblock(item);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968673, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.adapter = new MuteListAdapter(layoutInflater.getContext());
        this.muteList.setAdapter((ListAdapter)this.adapter);
        final SwipeDismissListViewTouchListener onTouchListener = new SwipeDismissListViewTouchListener(this.muteList, (SwipeDismissListViewTouchListener.DismissCallbacks)new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(final ListView listView, final int n) {
                return true;
            }
            
            @Override
            public void onDismiss(final ListView listView, final int n) {
                final ListAdapter adapter = listView.getAdapter();
                if (adapter instanceof MuteListAdapter) {
                    final MuteListEntry item = ((MuteListAdapter)adapter).getItem(n);
                    if (item != null) {
                        MuteListFragment.this.doUnblock(item);
                    }
                }
            }
        });
        this.muteList.setOnTouchListener((View$OnTouchListener)onTouchListener);
        this.muteList.setOnScrollListener(onTouchListener.makeScrollListener());
        this.muteList.setOnItemClickListener((AdapterView$OnItemClickListener)new _$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4$1(this));
        this.registerForContextMenu((View)this.muteList);
        return inflate;
    }
    
    @Override
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        this.setTitle(this.getString(2131296705), null);
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            this.muteListData.subscribe(userManager.muteListPool(), SubscriptionSingleKey.Value);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
        }
    }
    
    @Override
    public void onStop() {
        this.muteListData.unsubscribe();
        this.agentCircuit.unsubscribe();
        super.onStop();
    }
}
