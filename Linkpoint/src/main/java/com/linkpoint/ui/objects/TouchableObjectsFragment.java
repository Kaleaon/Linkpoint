package com.lumiyaviewer.lumiya.ui.objects;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class TouchableObjectsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String OBJECT_UUID_KEY = "objectUUID";
    private TouchableObjectListAdapter listAdapter;
    private final SubscriptionData<UUID, ImmutableList<SLObjectInfo>> touchableObjects = new SubscriptionData<>(UIThreadExecutor.getInstance(), new $Lambda$LilZ3G1QEr_14fK4lPNJzUyzlBg(this));

    private UUID getObjectUUID() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(OBJECT_UUID_KEY)) {
            return null;
        }
        return UUIDPool.getUUID(arguments.getString(OBJECT_UUID_KEY));
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid2) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putString(OBJECT_UUID_KEY, uuid2.toString());
        return bundle;
    }

    /* access modifiers changed from: private */
    /* renamed from: onTouchableObjects */
    public void m691com_lumiyaviewer_lumiya_ui_objects_TouchableObjectsFragmentmthref0(ImmutableList<SLObjectInfo> immutableList) {
        if (this.listAdapter != null) {
            this.listAdapter.setData(immutableList);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.touchable_object_list, viewGroup, false);
        this.listAdapter = new TouchableObjectListAdapter(layoutInflater.getContext());
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setAdapter(this.listAdapter);
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setOnItemClickListener(this);
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setEmptyView(inflate.findViewById(16908292));
        return inflate;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        SLAgentCircuit sLAgentCircuit = null;
        if (this.listAdapter != null) {
            SLObjectInfo item = this.listAdapter.getItem(i);
            UserManager userManager = ActivityUtils.getUserManager(getArguments());
            if (userManager != null) {
                sLAgentCircuit = userManager.getActiveAgentCircuit();
            }
            if (item != null && sLAgentCircuit != null) {
                sLAgentCircuit.TouchObject(item.localID);
            }
        }
    }

    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        UUID objectUUID = getObjectUUID();
        if (userManager == null || objectUUID == null) {
            this.touchableObjects.unsubscribe();
        } else {
            this.touchableObjects.subscribe(userManager.getObjectsManager().touchableObjects(), objectUUID);
        }
    }

    public void onStop() {
        this.touchableObjects.unsubscribe();
        super.onStop();
    }
}
