package com.lumiyaviewer.lumiya.ui.outfits;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import java.util.UUID;

/* loaded from: classes.dex */
public class CurrentOutfitFragment extends Fragment implements LoadableMonitor.OnLoadableDataChangedListener, AdapterView.OnItemClickListener {
    private CurrentOutfitAdapter listAdapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.wornItems).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this);

    public static Bundle makeSelection(UUID uuid) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.current_outfit_fragment, viewGroup, false);
        this.listAdapter = new CurrentOutfitAdapter(layoutInflater.getContext());
        ListView listView = (ListView) inflate.findViewById(R.id.currentOutfitListView);
        listView.setAdapter((ListAdapter) this.listAdapter);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(inflate.findViewById(android.R.id.empty));
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.DismissCallbacks() { // from class: com.lumiyaviewer.lumiya.ui.outfits.CurrentOutfitFragment.1
            @Override // com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks
            public boolean canDismiss(ListView listView2, int i) {
                ListAdapter adapter = listView2.getAdapter();
                if (adapter instanceof DismissableAdapter) {
                    return ((DismissableAdapter) adapter).canDismiss(i);
                }
                return false;
            }

            @Override // com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks
            public void onDismiss(ListView listView2, int i) {
                ListAdapter adapter = listView2.getAdapter();
                if (adapter instanceof DismissableAdapter) {
                    ((DismissableAdapter) adapter).onDismiss(i);
                }
            }
        });
        listView.setOnTouchListener(swipeDismissListViewTouchListener);
        listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener());
        return inflate;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        SLAvatarAppearance.WornItem item;
        SLAgentCircuit data = this.agentCircuit.getData();
        if (this.listAdapter == null || data == null || (item = this.listAdapter.getItem(i)) == null || !item.getIsTouchable() || item.getWornOn() != null) {
            return;
        }
        data.TouchObject(item.getObjectLocalID());
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        if (this.listAdapter != null) {
            SLAgentCircuit data = this.agentCircuit.getData();
            this.listAdapter.setAvatarAppearance(data != null ? data.getModules().avatarAppearance : null);
            this.listAdapter.setData(this.wornItems.getData());
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager == null) {
            this.loadableMonitor.unsubscribeAll();
        } else {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
            this.wornItems.subscribe(userManager.wornItems(), SubscriptionSingleKey.Value);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        this.loadableMonitor.unsubscribeAll();
        super.onStop();
    }
}
