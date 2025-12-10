package com.linkpoint.ui.objects

import android.os.Bundle
import androidx.fragment.app.Fragment
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

class TouchableObjectsFragment : Fragment : AdapterView.OnItemClickListener {
    private val OBJECT_UUID_KEY: String = "objectUUID"
    private TouchableObjectListAdapter listAdapter
    private SubscriptionData<UUID, ImmutableList<SLObjectInfo>> touchableObjects = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$LilZ3G1QEr_14fK4lPNJzUyzlBg(this))

    private UUID getObjectUUID() {
        Bundle arguments = getArguments()
        if (arguments == null || !arguments.containsKey(OBJECT_UUID_KEY)) {
            return null
        }
        return UUIDPool.getUUID(arguments.getString(OBJECT_UUID_KEY))
    }

    fun makeSelection(UUID uuid, UUID uuid2): Bundle {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        bundle.putString(OBJECT_UUID_KEY, uuid2.toString())
        return bundle
    }

    /* access modifiers changed from: private */
    /* renamed from: onTouchableObjects */
    fun m691com_lumiyaviewer_lumiya_ui_objects_TouchableObjectsFragmentmthref0(ImmutableList<SLObjectInfo> immutableList): Unit {
        if (this.listAdapter != null) {
            this.listAdapter.setData(immutableList)
        }
    }

    fun onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.touchable_object_list, viewGroup, false)
        this.listAdapter = TouchableObjectListAdapter(layoutInflater.getContext())
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setAdapter(this.listAdapter)
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setOnItemClickListener(this)
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setEmptyView(inflate.findViewById(16908292))
        return inflate
    }

    fun onItemClick(AdapterView<?> adapterView, View view, Int i, Long j): Unit {
        SLAgentCircuit sLAgentCircuit = null
        if (this.listAdapter != null) {
            SLObjectInfo item = this.listAdapter.getItem(i)
            UserManager userManager = ActivityUtils.getUserManager(getArguments())
            if (userManager != null) {
                sLAgentCircuit = userManager.getActiveAgentCircuit()
            }
            if (item != null && sLAgentCircuit != null) {
                sLAgentCircuit.TouchObject(item.localID)
            }
        }
    }

    fun onStart(): Unit {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        UUID objectUUID = getObjectUUID()
        if (userManager == null || objectUUID == null) {
            this.touchableObjects.unsubscribe()
        } else {
            this.touchableObjects.subscribe(userManager.getObjectsManager().touchableObjects(), objectUUID)
        }
    }

    fun onStop(): Unit {
        this.touchableObjects.unsubscribe()
        super.onStop()
    }
}
