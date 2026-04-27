package com.lumiyaviewer.lumiya.ui.objects;

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
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

/* loaded from: classes.dex */
public class TouchableObjectsFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String OBJECT_UUID_KEY = "objectUUID";
    private TouchableObjectListAdapter listAdapter;
    private final SubscriptionData<UUID, ImmutableList<SLObjectInfo>> touchableObjects = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$LilZ3G1QEr_14fK4lPNJzUyzlBg
        private final /* synthetic */ void $m$0(Object obj) {
            ((TouchableObjectsFragment) this).m701x2ceac41e((ImmutableList) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

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

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onTouchableObjects, reason: merged with bridge method [inline-methods] */
    public void m701x2ceac41e(ImmutableList<SLObjectInfo> immutableList) {
        if (this.listAdapter != null) {
            this.listAdapter.setData(immutableList);
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.touchable_object_list, viewGroup, false);
        this.listAdapter = new TouchableObjectListAdapter(layoutInflater.getContext());
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setAdapter((ListAdapter) this.listAdapter);
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setOnItemClickListener(this);
        ((ListView) inflate.findViewById(R.id.touchableObjectListView)).setEmptyView(inflate.findViewById(android.R.id.empty));
        return inflate;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.listAdapter != null) {
            SLObjectInfo item = this.listAdapter.getItem(i);
            UserManager userManager = ActivityUtils.getUserManager(getArguments());
            SLAgentCircuit activeAgentCircuit = userManager != null ? userManager.getActiveAgentCircuit() : null;
            if (item == null || activeAgentCircuit == null) {
                return;
            }
            activeAgentCircuit.TouchObject(item.localID);
        }
    }

    @Override // android.support.v4.app.Fragment
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

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        this.touchableObjects.unsubscribe();
        super.onStop();
    }
}
