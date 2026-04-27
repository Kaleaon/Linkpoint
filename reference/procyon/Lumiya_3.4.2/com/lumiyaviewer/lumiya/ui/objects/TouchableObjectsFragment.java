// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.widget.AdapterView$OnItemClickListener;
import android.support.v4.app.Fragment;

public class TouchableObjectsFragment extends Fragment implements AdapterView$OnItemClickListener
{
    private static final String OBJECT_UUID_KEY = "objectUUID";
    private TouchableObjectListAdapter listAdapter;
    private final SubscriptionData<UUID, ImmutableList<SLObjectInfo>> touchableObjects;
    
    public TouchableObjectsFragment() {
        this.touchableObjects = new SubscriptionData<UUID, ImmutableList<SLObjectInfo>>(UIThreadExecutor.getInstance(), new _$Lambda$LilZ3G1QEr_14fK4lPNJzUyzlBg(this));
    }
    
    private UUID getObjectUUID() {
        final Bundle arguments = this.getArguments();
        if (arguments != null && arguments.containsKey("objectUUID")) {
            return UUIDPool.getUUID(arguments.getString("objectUUID"));
        }
        return null;
    }
    
    public static Bundle makeSelection(final UUID uuid, final UUID uuid2) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putString("objectUUID", uuid2.toString());
        return bundle;
    }
    
    private void onTouchableObjects(final ImmutableList<SLObjectInfo> data) {
        if (this.listAdapter != null) {
            this.listAdapter.setData(data);
        }
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968749, viewGroup, false);
        this.listAdapter = new TouchableObjectListAdapter(layoutInflater.getContext());
        ((ListView)inflate.findViewById(2131755672)).setAdapter((ListAdapter)this.listAdapter);
        ((ListView)inflate.findViewById(2131755672)).setOnItemClickListener((AdapterView$OnItemClickListener)this);
        ((ListView)inflate.findViewById(2131755672)).setEmptyView(inflate.findViewById(16908292));
        return inflate;
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        SLAgentCircuit activeAgentCircuit = null;
        if (this.listAdapter != null) {
            final SLObjectInfo item = this.listAdapter.getItem(n);
            final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
            if (userManager != null) {
                activeAgentCircuit = userManager.getActiveAgentCircuit();
            }
            if (item != null && activeAgentCircuit != null) {
                activeAgentCircuit.TouchObject(item.localID);
            }
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final UUID objectUUID = this.getObjectUUID();
        if (userManager != null && objectUUID != null) {
            this.touchableObjects.subscribe(userManager.getObjectsManager().touchableObjects(), objectUUID);
        }
        else {
            this.touchableObjects.unsubscribe();
        }
    }
    
    @Override
    public void onStop() {
        this.touchableObjects.unsubscribe();
        super.onStop();
    }
}
