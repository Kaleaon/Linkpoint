package com.linkpoint.ui.outfits

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.ListView
import com.google.common.collect.ImmutableList
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DismissableAdapter
import com.linkpoint.ui.common.SwipeDismissListViewTouchListener
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import java.util.UUID

class CurrentOutfitFragment : Fragment() : LoadableMonitor.OnLoadableDataChangedListener, AdapterView.OnItemClickListener {
    private val SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance())
    private CurrentOutfitAdapter listAdapter
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.wornItems).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this)
    private val SubscriptionData<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems = SubscriptionData<>(UIThreadExecutor.getInstance())

    @JvmStatic
    Bundle makeSelection(UUID uuid) {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        return bundle
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.current_outfit_fragment, viewGroup, false)
        this.listAdapter = CurrentOutfitAdapter(layoutInflater.getContext())
        ListView listView = (ListView) inflate.findViewById(R.id.currentOutfitListView)
        listView.setAdapter(this.listAdapter)
        listView.setOnItemClickListener(this)
        listView.setEmptyView(inflate.findViewById(16908292))
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(listView, SwipeDismissListViewTouchListener.DismissCallbacks() {
            public Boolean canDismiss(ListView listView, Int i) {
                ListAdapter adapter = listView.getAdapter()
                if (adapter instanceof DismissableAdapter) {
                    return ((DismissableAdapter) adapter).canDismiss(i)
                }
                return false
            }

            public Unit onDismiss(ListView listView, Int i) {
                ListAdapter adapter = listView.getAdapter()
                if (adapter instanceof DismissableAdapter) {
                    ((DismissableAdapter) adapter).onDismiss(i)
                }
            }
        listView.setOnTouchListener(swipeDismissListViewTouchListener)
        listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
        return inflate
    }

    public Unit onItemClick(AdapterView<?> adapterView, View view, Int i, Long j) {
        SLAvatarAppearance.WornItem item
        SLAgentCircuit data = this.agentCircuit.getData()
        if (this.listAdapter != null && data != null && (item = this.listAdapter.getItem(i)) != null && item.getIsTouchable() && item.getWornOn() == null) {
            data.TouchObject(item.getObjectLocalID())
        }
    }

    public Unit onLoadableDataChanged() {
        if (this.listAdapter != null) {
            SLAgentCircuit data = this.agentCircuit.getData()
            this.listAdapter.setAvatarAppearance(data != null ? data.getModules().avatarAppearance : null)
            this.listAdapter.setData(this.wornItems.getData())
        }
    }

    public Unit onStart() {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID())
            this.wornItems.subscribe(userManager.wornItems(), SubscriptionSingleKey.Value)
            return
        }
        this.loadableMonitor.unsubscribeAll()
    }

    public Unit onStop() {
        this.loadableMonitor.unsubscribeAll()
        super.onStop()
    }
}
