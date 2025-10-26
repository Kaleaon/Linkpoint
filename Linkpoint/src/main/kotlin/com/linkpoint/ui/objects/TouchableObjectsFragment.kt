package com.linkpoint.ui.objects

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.google.common.collect.ImmutableList
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.utils.UUIDPool
import java.util.UUID

class TouchableObjectsFragment : Fragment(), AdapterView.OnItemClickListener {
    private const val OBJECT_UUID_KEY: String = "objectUUID"
    private TouchableObjectListAdapter listAdapter
    private val SubscriptionData<UUID, ImmutableList<SLObjectInfo>> touchableObjects = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$LilZ3G1QEr_14fK4lPNJzUyzlBg(this))

     private fun getObjectUUID(): UUID {
        val arguments: Bundle = getArguments()
        if (arguments == null || !arguments.containsKey(OBJECT_UUID_KEY)) {
            return null
        }
        return UUIDPool.getUUID(arguments.getString(OBJECT_UUID_KEY))
    }

    @JvmStatic
     fun makeSelection(uuid: UUID, uuid2: UUID): Bundle {
        val bundle: Bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        bundle.putString(OBJECT_UUID_KEY, uuid2.toString())
        return bundle
    }

    /* access modifiers changed from: private */
    /* renamed from: onTouchableObjects */
    fun m691com_lumiyaviewer_lumiya_ui_objects_TouchableObjectsFragmentmthref0(immutableList: ImmutableList<SLObjectInfo>) {
        if (this.listAdapter != null) {
            this.listAdapter.setData(immutableList)
        }
    }

     public override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        val inflate: View = layoutInflater.inflate(R.layout.touchable_object_list, viewGroup, false)
        this.listAdapter = TouchableObjectListAdapter(layoutInflater.getContext())
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setAdapter(this.listAdapter)
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setOnItemClickListener(this)
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setEmptyView(inflate.findViewById(16908292))
        return inflate
    }

    fun onItemClick(adapterView: AdapterView<?>, view: View, i: Int, j: Long) {
        val sLAgentCircuit: SLAgentCircuit = null
        if (this.listAdapter != null) {
            val item: SLObjectInfo = this.listAdapter.getItem(i)
            val userManager: UserManager = ActivityUtils.getUserManager(getArguments())
            if (userManager != null) {
                sLAgentCircuit = userManager.getActiveAgentCircuit()
            }
            if (item != null && sLAgentCircuit != null) {
                sLAgentCircuit.TouchObject(item.localID)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val userManager: UserManager = ActivityUtils.getUserManager(getArguments())
        val objectUUID: UUID = getObjectUUID()
        if (userManager == null || objectUUID == null) {
            this.touchableObjects.unsubscribe()
        } else {
            this.touchableObjects.subscribe(userManager.getObjectsManager().touchableObjects(), objectUUID)
        }
    }

    override fun onStop() {
        this.touchableObjects.unsubscribe()
        super.onStop()
    }
}
