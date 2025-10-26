package com.lumiyaviewer.lumiya.ui.objects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.react.SubscriptionData
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID

class TouchableObjectsFragment : Fragment : AdapterView.OnItemClickListener {
    private String OBJECT_UUID_KEY = "objectUUID"
    private TouchableObjectListAdapter listAdapter
    private SubscriptionData<UUID, ImmutableList<SLObjectInfo>> touchableObjects = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$LilZ3G1QEr_14fK4lPNJzUyzlBg(this))

    private UUID getObjectUUID() {
        Bundle arguments = getArguments()
        if (arguments == null || !arguments.containsKey(OBJECT_UUID_KEY)) {
            return null
        }
        return UUIDPool.getUUID(arguments.getString(OBJECT_UUID_KEY))
    }

    Bundle makeSelection(UUID uuid, UUID uuid2) {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        bundle.putString(OBJECT_UUID_KEY, uuid2.toString())
        return bundle
    }

    /* access modifiers changed from: private */
    /* renamed from: onTouchableObjects */
    Unit m691com_lumiyaviewer_lumiya_ui_objects_TouchableObjectsFragmentmthref0(ImmutableList<SLObjectInfo> immutableList) {
        if (this.listAdapter != null) {
            this.listAdapter.setData(immutableList)
        }
    }

    View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.touchable_object_list, viewGroup, false)
        this.listAdapter = TouchableObjectListAdapter(layoutInflater.getContext())
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setAdapter(this.listAdapter)
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setOnItemClickListener(this)
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setEmptyView(inflate.findViewById(16908292))
        return inflate
    }

    Unit onItemClick(AdapterView<?> adapterView, View view, Int i, Long j) {
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

    Unit onStart() {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        UUID objectUUID = getObjectUUID()
        if (userManager == null || objectUUID == null) {
            this.touchableObjects.unsubscribe()
        } else {
            this.touchableObjects.subscribe(userManager.getObjectsManager().touchableObjects(), objectUUID)
        }
    }

    Unit onStop() {
        this.touchableObjects.unsubscribe()
        super.onStop()
    }
}
