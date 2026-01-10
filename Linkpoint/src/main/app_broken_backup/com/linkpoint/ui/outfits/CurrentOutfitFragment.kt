package com.linkpoint.ui.outfits

import android.os.Bundle
import androidx.fragment.app.Fragment
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

class CurrentOutfitFragment : Fragment : LoadableMonitor.OnLoadableDataChangedListener, AdapterView.OnItemClickListener {
    private SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance())
    private CurrentOutfitAdapter listAdapter
    private LoadableMonitor loadableMonitor = LoadableMonitor(this.wornItems).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this)
    private SubscriptionData<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems = SubscriptionData<>(UIThreadExecutor.getInstance())

    fun makeSelection(UUID uuid): Bundle {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        return bundle
    }

    fun onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.current_outfit_fragment, viewGroup, false)
        this.listAdapter = CurrentOutfitAdapter(layoutInflater.getContext())
        ListView listView = (inflate as ListView).findViewById(R.id.currentOutfitListView)
        listView.setAdapter(this.listAdapter)
        listView.setOnItemClickListener(this)
        listView.setEmptyView(inflate.findViewById(16908292))
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(listView, SwipeDismissListViewTouchListener.DismissCallbacks() {
            fun canDismiss(ListView listView, Int i): Boolean {
                ListAdapter adapter = listView.getAdapter()
                if (adapter is DismissableAdapter) {
                    return ((DismissableAdapter) adapter).canDismiss(i)
                }
                return false
            }

            fun onDismiss(ListView listView, Int i)  {
                ListAdapter adapter = listView.getAdapter()
                if (adapter is DismissableAdapter) {
                    ((DismissableAdapter) adapter).onDismiss(i)
                }
            }
        listView.setOnTouchListener(swipeDismissListViewTouchListener)
        listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
        return inflate
    }

    fun onItemClick(AdapterView<?> adapterView, View view, Int i, Long j)  {
        SLAvatarAppearance.WornItem item
        SLAgentCircuit data = this.agentCircuit.getData()
        if (this.listAdapter != null && data != null && (item = this.listAdapter.getItem(i)) != null && item.getIsTouchable() && item.getWornOn() == null) {
            data.TouchObject(item.getObjectLocalID())
        }
    }

    fun onLoadableDataChanged()  {
        if (this.listAdapter != null) {
            SLAgentCircuit data = this.agentCircuit.getData()
            this.listAdapter.setAvatarAppearance(data != null ? data.getModules().avatarAppearance : null)
            this.listAdapter.setData(this.wornItems.getData())
        }
    }

    fun onStart()  {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID())
            this.wornItems.subscribe(userManager.wornItems(), SubscriptionSingleKey.Value)
            return
        }
        this.loadableMonitor.unsubscribeAll()
    }

    fun onStop()  {
        this.loadableMonitor.unsubscribeAll()
        super.onStop()
    }
}
