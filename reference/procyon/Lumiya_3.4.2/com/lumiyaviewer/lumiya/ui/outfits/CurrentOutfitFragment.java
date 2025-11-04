// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.outfits;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.widget.AdapterView;
import android.view.View$OnTouchListener;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.widget.AdapterView$OnItemClickListener;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import android.support.v4.app.Fragment;

public class CurrentOutfitFragment extends Fragment implements OnLoadableDataChangedListener, AdapterView$OnItemClickListener
{
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private CurrentOutfitAdapter listAdapter;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems;
    
    public CurrentOutfitFragment() {
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance());
        this.wornItems = new SubscriptionData<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.wornItems }).withOptionalLoadables(this.agentCircuit).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
    }
    
    public static Bundle makeSelection(final UUID uuid) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968624, viewGroup, false);
        this.listAdapter = new CurrentOutfitAdapter(layoutInflater.getContext());
        final ListView listView = (ListView)inflate.findViewById(2131755340);
        listView.setAdapter((ListAdapter)this.listAdapter);
        listView.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        listView.setEmptyView(inflate.findViewById(16908292));
        final SwipeDismissListViewTouchListener onTouchListener = new SwipeDismissListViewTouchListener(listView, (SwipeDismissListViewTouchListener.DismissCallbacks)new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(final ListView listView, final int n) {
                final ListAdapter adapter = listView.getAdapter();
                return adapter instanceof DismissableAdapter && ((DismissableAdapter)adapter).canDismiss(n);
            }
            
            @Override
            public void onDismiss(final ListView listView, final int n) {
                final ListAdapter adapter = listView.getAdapter();
                if (adapter instanceof DismissableAdapter) {
                    ((DismissableAdapter)adapter).onDismiss(n);
                }
            }
        });
        listView.setOnTouchListener((View$OnTouchListener)onTouchListener);
        listView.setOnScrollListener(onTouchListener.makeScrollListener());
        return inflate;
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
        if (this.listAdapter != null && slAgentCircuit != null) {
            final SLAvatarAppearance.WornItem item = this.listAdapter.getItem(n);
            if (item != null && item.getIsTouchable() && item.getWornOn() == null) {
                slAgentCircuit.TouchObject(item.getObjectLocalID());
            }
        }
    }
    
    @Override
    public void onLoadableDataChanged() {
        if (this.listAdapter != null) {
            final SLAgentCircuit slAgentCircuit = this.agentCircuit.getData();
            final CurrentOutfitAdapter listAdapter = this.listAdapter;
            SLAvatarAppearance avatarAppearance;
            if (slAgentCircuit != null) {
                avatarAppearance = slAgentCircuit.getModules().avatarAppearance;
            }
            else {
                avatarAppearance = null;
            }
            listAdapter.setAvatarAppearance(avatarAppearance);
            this.listAdapter.setData(this.wornItems.getData());
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            this.wornItems.subscribe(userManager.wornItems(), SubscriptionSingleKey.Value);
        }
        else {
            this.loadableMonitor.unsubscribeAll();
        }
    }
    
    @Override
    public void onStop() {
        this.loadableMonitor.unsubscribeAll();
        super.onStop();
    }
}
