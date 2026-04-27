// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ListAdapter;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.widget.ListView;
import butterknife.BindView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.widget.AdapterView$OnItemClickListener;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class MyAvatarFragment extends FragmentWithTitle implements AdapterView$OnItemClickListener, OnChatterNameUpdated
{
    @BindView(2131755497)
    TextView myAvatarName;
    private ChatterNameRetriever myAvatarNameRetriever;
    @BindView(2131755499)
    ListView myAvatarOptionsList;
    @BindView(2131755498)
    ChatterPicView myAvatarPic;
    private final SubscriptionData<SubscriptionSingleKey, Integer> myBalance;
    private Unbinder unbinder;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues() {
        if (MyAvatarFragment.-com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues != null) {
            return MyAvatarFragment.-com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues = new int[MyAvatarDetailsPages.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageBalance.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageProfile.ordinal()] = 4;
                            return -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues = -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues;
                        }
                        catch (final NoSuchFieldError noSuchFieldError) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError2) {}
                }
                catch (final NoSuchFieldError noSuchFieldError3) {}
            }
            catch (final NoSuchFieldError noSuchFieldError4) {
                continue;
            }
            break;
        }
    }
    
    public MyAvatarFragment() {
        this.myAvatarNameRetriever = null;
        this.myBalance = new SubscriptionData<SubscriptionSingleKey, Integer>(UIThreadExecutor.getInstance(), new _$Lambda$E97LbIKTNF028fQGuPv0gXqIQrc(this));
    }
    
    private UUID getAgentUUID() {
        return ActivityUtils.getActiveAgentID(this.getArguments());
    }
    
    public static Bundle makeSelection(final UUID uuid) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }
    
    public static MyAvatarFragment newInstance(final UUID uuid) {
        final MyAvatarFragment myAvatarFragment = new MyAvatarFragment();
        myAvatarFragment.setArguments(makeSelection(uuid));
        return myAvatarFragment;
    }
    
    private void onMyBalance(final Integer n) {
        if (this.unbinder != null) {
            final ListAdapter adapter = this.myAvatarOptionsList.getAdapter();
            if (adapter instanceof MyAvatarPagesAdapter) {
                ((MyAvatarPagesAdapter)adapter).notifyDataSetChanged();
            }
        }
    }
    
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        final String resolvedName = chatterNameRetriever.getResolvedName();
        if (this.unbinder != null) {
            final TextView myAvatarName = this.myAvatarName;
            String string;
            if (resolvedName != null) {
                string = resolvedName;
            }
            else {
                string = this.getString(2131296712);
            }
            myAvatarName.setText((CharSequence)string);
            this.myAvatarPic.setChatterID(chatterNameRetriever.chatterID, resolvedName);
        }
        this.setTitle(resolvedName, null);
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968675, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.myAvatarOptionsList.setAdapter((ListAdapter)new MyAvatarPagesAdapter(viewGroup.getContext()));
        this.myAvatarOptionsList.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        return inflate;
    }
    
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        final UUID agentUUID = this.getAgentUUID();
        final Object itemAtPosition = adapterView.getItemAtPosition(n);
        if (itemAtPosition instanceof MyAvatarDetailsPages && agentUUID != null) {
            switch (-getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues()[((MyAvatarDetailsPages)itemAtPosition).ordinal()]) {
                case 4: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), MyProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(agentUUID, agentUUID)));
                    break;
                }
                case 3: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), OutfitsFragment.class, OutfitsFragment.makeSelection(agentUUID, null));
                    break;
                }
                case 2: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), MuteListFragment.class, MuteListFragment.makeSelection(agentUUID));
                    break;
                }
                case 1: {
                    DetailsActivity.showEmbeddedDetails(this.getActivity(), TransactionLogFragment.class, TransactionLogFragment.makeSelection(agentUUID));
                    break;
                }
            }
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UUID agentUUID = this.getAgentUUID();
        final UserManager userManager = UserManager.getUserManager(agentUUID);
        if (userManager != null) {
            this.myBalance.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
        if (agentUUID != null) {
            this.myAvatarNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(agentUUID, agentUUID), (ChatterNameRetriever.OnChatterNameUpdated)this, UIThreadExecutor.getSerialInstance());
        }
    }
    
    public void onStop() {
        if (this.myAvatarNameRetriever != null) {
            this.myAvatarNameRetriever.dispose();
            this.myAvatarNameRetriever = null;
        }
        this.myBalance.unsubscribe();
        super.onStop();
    }
    
    private class MyAvatarPagesAdapter extends ArrayAdapter<MyAvatarDetailsPages>
    {
        private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues() {
            if (MyAvatarPagesAdapter.-com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues != null) {
                return MyAvatarPagesAdapter.-com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues;
            }
            int[] -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues = new int[MyAvatarDetailsPages.values().length];
            while (true) {
                try {
                    -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageBalance.ordinal()] = 1;
                    try {
                        -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2;
                        try {
                            -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3;
                            try {
                                -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues[MyAvatarDetailsPages.pageProfile.ordinal()] = 4;
                                return -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues = -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {
                    continue;
                }
                break;
            }
        }
        
        public MyAvatarPagesAdapter(final Context context) {
            super(context, 17367043, (Object[])MyAvatarDetailsPages.values());
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            final View view2 = super.getView(n, view, viewGroup);
            final MyAvatarDetailsPages myAvatarDetailsPages = (MyAvatarDetailsPages)this.getItem(n);
            if (view2 instanceof TextView && myAvatarDetailsPages != null) {
                switch (-getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues()[myAvatarDetailsPages.ordinal()]) {
                    default: {
                        ((TextView)view2).setText((CharSequence)MyAvatarFragment.this.getString(myAvatarDetailsPages.getTitleResource()));
                        break;
                    }
                    case 1: {
                        final Integer n2 = MyAvatarFragment.this.myBalance.getData();
                        String text;
                        if (n2 != null) {
                            text = MyAvatarFragment.this.getString(2131296707, new Object[] { n2 });
                        }
                        else {
                            text = MyAvatarFragment.this.getString(2131296708);
                        }
                        ((TextView)view2).setText((CharSequence)text);
                        break;
                    }
                }
            }
            return view2;
        }
    }
}
