// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.support.v4.app.FragmentManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.support.v4.app.Fragment;

public class MinimapFragment extends Fragment implements OnUserClickListener
{
    private final SubscriptionData<SubscriptionSingleKey, SLMinimap.MinimapBitmap> minimapBitmap;
    private final SubscriptionData<SubscriptionSingleKey, SLMinimap.UserLocations> userLocations;
    
    public MinimapFragment() {
        this.minimapBitmap = new SubscriptionData<SubscriptionSingleKey, SLMinimap.MinimapBitmap>(UIThreadExecutor.getInstance(), new _$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM(this));
        this.userLocations = new SubscriptionData<SubscriptionSingleKey, SLMinimap.UserLocations>(UIThreadExecutor.getInstance(), new _$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM$1(this));
    }
    
    static Fragment newInstance(final UUID uuid) {
        final MinimapFragment minimapFragment = new MinimapFragment();
        minimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return minimapFragment;
    }
    
    private void onMinimapBitmap(final SLMinimap.MinimapBitmap minimapBitmap) {
        final View view = this.getView();
        if (view != null) {
            ((MinimapView)view.findViewById(2131755485)).setMinimapBitmap(minimapBitmap);
        }
    }
    
    private void onUserLocations(final SLMinimap.UserLocations userLocations) {
        final View view = this.getView();
        if (view != null) {
            ((MinimapView)view.findViewById(2131755485)).setUserLocations(userLocations);
        }
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968670, viewGroup, false);
        ((MinimapView)inflate.findViewById(2131755485)).setOnUserClickListener((MinimapView.OnUserClickListener)this);
        return inflate;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            this.minimapBitmap.subscribe((Subscribable<SubscriptionSingleKey, SLMinimap.MinimapBitmap>)userManager.getMinimapBitmapPool(), SubscriptionSingleKey.Value);
            this.userLocations.subscribe(userManager.getUserLocationsPool(), SubscriptionSingleKey.Value);
        }
        else {
            this.minimapBitmap.unsubscribe();
            this.userLocations.unsubscribe();
        }
    }
    
    @Override
    public void onStop() {
        this.minimapBitmap.unsubscribe();
        this.userLocations.unsubscribe();
        super.onStop();
    }
    
    @Override
    public void onUserClick(final UUID uuid) {
        final FragmentManager fragmentManager = this.getFragmentManager();
        if (fragmentManager != null) {
            final Fragment fragmentById = fragmentManager.findFragmentById(2131755284);
            if (fragmentById instanceof NearbyPeopleMinimapFragment) {
                ((NearbyPeopleMinimapFragment)fragmentById).setSelectedUser(uuid);
            }
        }
        final View view = this.getView();
        if (view != null) {
            ((MinimapView)view.findViewById(2131755485)).setSelectedUser(uuid);
        }
    }
}
