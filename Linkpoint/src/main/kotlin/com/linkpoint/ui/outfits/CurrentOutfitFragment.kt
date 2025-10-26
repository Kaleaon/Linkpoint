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

class CurrentOutfitFragment : Fragment(), LoadableMonitor.OnLoadableDataChangedListener, AdapterView.OnItemClickListener {
    private val SubscriptionData<UUID, SLAgentCircuit> agentCircuit = SubscriptionData<>(UIThreadExecutor.getInstance())
    private CurrentOutfitAdapter listAdapter
    private val LoadableMonitor loadableMonitor = LoadableMonitor(this.wornItems).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this)
    private val SubscriptionData<SubscriptionSingleKey, ImmutableList<SLAvatarAppearance.WornItem>> wornItems = SubscriptionData<>(UIThreadExecutor.getInstance())

    @JvmStatic
     fun makeSelection(uuid: UUID): Bundle {
        val bundle: Bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        return bundle
    }

     public fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        val inflate: View = layoutInflater.inflate(R.layout.current_outfit_fragment, viewGroup, false)
        this.listAdapter = CurrentOutfitAdapter(layoutInflater.getContext())
        val listView: ListView = (ListView) inflate.findViewById(R.id.currentOutfitListView)
        listView.setAdapter(this.listAdapter)
        listView.setOnItemClickListener(this)
        listView.setEmptyView(inflate.findViewById(16908292))
        val swipeDismissListViewTouchListener: SwipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(listView, SwipeDismissListViewTouchListener.DismissCallbacks() {
             public fun canDismiss(listView: ListView, i: Int): Boolean {
                val adapter: ListAdapter = listView.getAdapter()
                if (adapter instanceof DismissableAdapter) {
                    return ((DismissableAdapter) adapter).canDismiss(i)
                }
                return false
            }

            fun onDismiss(listView: ListView, i: Int) {
                val adapter: ListAdapter = listView.getAdapter()
                if (adapter instanceof DismissableAdapter) {
                    ((DismissableAdapter) adapter).onDismiss(i)
                }
            }
        listView.setOnTouchListener(swipeDismissListViewTouchListener)
        listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
        return inflate
    }

    fun onItemClick(adapterView: AdapterView<?>, view: View, i: Int, j: Long) {
        SLAvatarAppearance.WornItem item
        val data: SLAgentCircuit = this.agentCircuit.getData()
        if (this.listAdapter != null && data != null && (item = this.listAdapter.getItem(i)) != null && item.getIsTouchable() && item.getWornOn() == null) {
            data.TouchObject(item.getObjectLocalID())
        }
    }

    fun onLoadableDataChanged() {
        if (this.listAdapter != null) {
            val data: SLAgentCircuit = this.agentCircuit.getData()
            this.listAdapter.setAvatarAppearance(data != null ? data.getModules().avatarAppearance : null)
            this.listAdapter.setData(this.wornItems.getData())
        }
    }

    fun onStart() {
        super.onStart()
        val userManager: UserManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID())
            this.wornItems.subscribe(userManager.wornItems(), SubscriptionSingleKey.Value)
            return
        }
        this.loadableMonitor.unsubscribeAll()
    }

    fun onStop() {
        this.loadableMonitor.unsubscribeAll()
        super.onStop()
    }
}
