package com.lumiyaviewer.lumiya.ui.minimap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.minimap.MinimapView;
import java.util.UUID;
/* loaded from: classes.dex */
public class MinimapFragment extends Fragment implements MinimapView.OnUserClickListener {
    private final SubscriptionData<SubscriptionSingleKey, SLMinimap.MinimapBitmap> minimapBitmap = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM
        private final /* synthetic */ void $m$0(Object obj) {
            ((MinimapFragment) this).m651com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref0((SLMinimap.MinimapBitmap) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<SubscriptionSingleKey, SLMinimap.UserLocations> userLocations = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM.1
        private final /* synthetic */ void $m$0(Object obj) {
            ((MinimapFragment) this).m652com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref1((SLMinimap.UserLocations) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Fragment newInstance(UUID uuid) {
        MinimapFragment minimapFragment = new MinimapFragment();
        minimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return minimapFragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onMinimapBitmap */
    public void m651com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref0(SLMinimap.MinimapBitmap minimapBitmap) {
        View view = getView();
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setMinimapBitmap(minimapBitmap);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onUserLocations */
    public void m652com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref1(SLMinimap.UserLocations userLocations) {
        View view = getView();
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setUserLocations(userLocations);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.minimap_fragment, viewGroup, false);
        ((MinimapView) inflate.findViewById(R.id.minimapView)).setOnUserClickListener(this);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            this.minimapBitmap.subscribe(userManager.getMinimapBitmapPool(), SubscriptionSingleKey.Value);
            this.userLocations.subscribe(userManager.getUserLocationsPool(), SubscriptionSingleKey.Value);
            return;
        }
        this.minimapBitmap.unsubscribe();
        this.userLocations.unsubscribe();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        this.minimapBitmap.unsubscribe();
        this.userLocations.unsubscribe();
        super.onStop();
    }

    @Override // com.lumiyaviewer.lumiya.ui.minimap.MinimapView.OnUserClickListener
    public void onUserClick(UUID uuid) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            Fragment findFragmentById = fragmentManager.findFragmentById(R.id.details);
            if (findFragmentById instanceof NearbyPeopleMinimapFragment) {
                ((NearbyPeopleMinimapFragment) findFragmentById).setSelectedUser(uuid);
            }
        }
        View view = getView();
        if (view != null) {
            ((MinimapView) view.findViewById(R.id.minimapView)).setSelectedUser(uuid);
        }
    }
}
