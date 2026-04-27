// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.support.v4.app.FragmentManager;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import android.widget.FrameLayout;
import android.support.v7.widget.CardView;
import android.view.View$OnClickListener;
import java.util.Collection;
import com.google.common.base.Objects;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.util.TypedValue;
import butterknife.ButterKnife;
import javax.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.support.v4.app.Fragment;

public class NearbyPeopleMinimapFragment extends Fragment
{
    private NearbyUserRecyclerAdapter adapter;
    private int cardSelectedColor;
    private final SubscriptionData<ChatterListType, ImmutableList<ChatterDisplayData>> chatterList;
    @BindView(16908292)
    View emptyView;
    @BindView(2131755490)
    RecyclerView userListView;
    
    public NearbyPeopleMinimapFragment() {
        this.chatterList = new SubscriptionData<ChatterListType, ImmutableList<ChatterDisplayData>>(UIThreadExecutor.getInstance(), new _$Lambda$0SrW7eOJ5Pm_SVTDQOmxGjUXtco(this));
        this.adapter = null;
        this.cardSelectedColor = 0;
    }
    
    static Fragment newInstance(final UUID uuid) {
        final NearbyPeopleMinimapFragment nearbyPeopleMinimapFragment = new NearbyPeopleMinimapFragment();
        nearbyPeopleMinimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return nearbyPeopleMinimapFragment;
    }
    
    private void onChatterList(final ImmutableList<ChatterDisplayData> chatters) {
        final int n = 8;
        if (this.adapter != null) {
            this.adapter.setChatters(chatters);
        }
        if (this.getView() != null) {
            final boolean empty = chatters.isEmpty();
            final View emptyView = this.emptyView;
            int visibility;
            if (empty) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            emptyView.setVisibility(visibility);
            final RecyclerView userListView = this.userListView;
            int visibility2;
            if (empty) {
                visibility2 = n;
            }
            else {
                visibility2 = 0;
            }
            userListView.setVisibility(visibility2);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968672, viewGroup, false);
        ButterKnife.bind(this, inflate);
        final TypedValue typedValue = new TypedValue();
        layoutInflater.getContext().getTheme().resolveAttribute(2130771970, typedValue, true);
        this.cardSelectedColor = typedValue.data;
        this.adapter = new NearbyUserRecyclerAdapter(this.getContext(), ActivityUtils.getUserManager(this.getArguments()));
        this.userListView.setAdapter((RecyclerView.Adapter)this.adapter);
        return inflate;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            this.chatterList.subscribe(userManager.getChatterList().getChatterList(), ChatterListType.Nearby);
        }
        else {
            this.chatterList.unsubscribe();
        }
    }
    
    @Override
    public void onStop() {
        this.chatterList.unsubscribe();
        super.onStop();
    }
    
    public void setSelectedUser(final UUID selected) {
        if (this.adapter != null) {
            this.adapter.setSelected(selected);
        }
    }
    
    private class NearbyUserRecyclerAdapter extends Adapter<NearbyUserViewHolder>
    {
        @Nonnull
        private ImmutableList<ChatterDisplayData> chatters;
        private final Context context;
        private final LayoutInflater layoutInflater;
        private long nextStableId;
        private int selectedPosition;
        @Nullable
        private UUID selectedUUID;
        private final Map<UUID, Long> stableIds;
        private final UserManager userManager;
        
        NearbyUserRecyclerAdapter(final Context context, final UserManager userManager) {
            this.stableIds = new HashMap<UUID, Long>();
            this.nextStableId = 1L;
            this.chatters = ImmutableList.of();
            this.selectedPosition = -1;
            this.context = context;
            this.userManager = userManager;
            this.layoutInflater = LayoutInflater.from(context);
            ((RecyclerView.Adapter)this).setHasStableIds(true);
        }
        
        @Override
        public int getItemCount() {
            return this.chatters.size();
        }
        
        @Override
        public long getItemId(final int n) {
            if (n >= 0 && n < this.chatters.size()) {
                final UUID optionalChatterUUID = this.chatters.get(n).chatterID.getOptionalChatterUUID();
                if (optionalChatterUUID != null) {
                    final Long n2 = this.stableIds.get(optionalChatterUUID);
                    if (n2 != null) {
                        return n2;
                    }
                }
            }
            return -1L;
        }
        
        public void onBindViewHolder(final NearbyUserViewHolder nearbyUserViewHolder, final int n) {
            boolean b = false;
            if (n >= 0 && n < this.chatters.size()) {
                final Context context = this.context;
                final LayoutInflater layoutInflater = this.layoutInflater;
                final UserManager userManager = this.userManager;
                final ChatterDisplayData chatterDisplayData = this.chatters.get(n);
                if (n == this.selectedPosition) {
                    b = true;
                }
                nearbyUserViewHolder.bindToData(context, layoutInflater, userManager, chatterDisplayData, b);
            }
        }
        
        public NearbyUserViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
            return new NearbyUserViewHolder(this.layoutInflater.inflate(2130968671, viewGroup, false));
        }
        
        public void setChatters(@Nullable ImmutableList<ChatterDisplayData> of) {
            if (of == null) {
                of = ImmutableList.of();
            }
            this.chatters = of;
            this.selectedPosition = -1;
            final HashSet set = new HashSet();
            for (int i = 0; i < this.chatters.size(); ++i) {
                final UUID optionalChatterUUID = this.chatters.get(i).chatterID.getOptionalChatterUUID();
                if (optionalChatterUUID != null) {
                    set.add(optionalChatterUUID);
                    if (!this.stableIds.containsKey(optionalChatterUUID)) {
                        this.stableIds.put(optionalChatterUUID, this.nextStableId);
                        ++this.nextStableId;
                    }
                    if (Objects.equal(optionalChatterUUID, this.selectedUUID)) {
                        this.selectedPosition = i;
                    }
                }
            }
            this.stableIds.keySet().retainAll(set);
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
        
        public void setSelected(final UUID selectedUUID) {
            this.selectedUUID = selectedUUID;
        Label_0052:
            while (true) {
                if (selectedUUID != null) {
                    for (int i = 0; i < this.chatters.size(); ++i) {
                        final UUID optionalChatterUUID = this.chatters.get(i).chatterID.getOptionalChatterUUID();
                        if (optionalChatterUUID != null && Objects.equal(selectedUUID, optionalChatterUUID)) {
                            break Label_0052;
                        }
                    }
                }
                Label_0092: {
                    break Label_0092;
                    final int i;
                    if (i != this.selectedPosition) {
                        final int selectedPosition = this.selectedPosition;
                        ((RecyclerView.Adapter)this).notifyItemChanged(this.selectedPosition = i);
                        ((RecyclerView.Adapter)this).notifyItemChanged(selectedPosition);
                    }
                    return;
                }
                int i = -1;
                continue Label_0052;
            }
        }
    }
    
    private class NearbyUserViewHolder extends ViewHolder implements View$OnClickListener
    {
        private final float cardSelectedElevation;
        private final CardView cardView;
        private ChatterDisplayData chatterDisplayData;
        private final View selectedLayout;
        private final FrameLayout userItemViewHolder;
        private final ChatterItemViewBuilder viewBuilder;
        
        public NearbyUserViewHolder(final View view) {
            super(view);
            this.viewBuilder = new ChatterItemViewBuilder();
            this.chatterDisplayData = null;
            this.userItemViewHolder = (FrameLayout)view.findViewById(2131755487);
            this.cardView = (CardView)view.findViewById(2131755486);
            this.cardSelectedElevation = this.cardView.getCardElevation();
            this.selectedLayout = view.findViewById(2131755488);
            this.userItemViewHolder.setOnClickListener((View$OnClickListener)this);
            view.findViewById(2131755489).setOnClickListener((View$OnClickListener)this);
        }
        
        public void bindToData(final Context context, final LayoutInflater layoutInflater, final UserManager userManager, final ChatterDisplayData chatterDisplayData, final boolean b) {
            this.viewBuilder.reset();
            chatterDisplayData.buildView(context, this.viewBuilder, userManager);
            final View child = this.userItemViewHolder.getChildAt(0);
            final View view = this.viewBuilder.getView(layoutInflater, child, (ViewGroup)this.userItemViewHolder, true);
            if (view != child) {
                if (child != null) {
                    this.userItemViewHolder.removeView(child);
                }
                this.userItemViewHolder.addView(view);
            }
            if (b) {
                this.cardView.setCardElevation(this.cardSelectedElevation);
                this.cardView.setCardBackgroundColor(NearbyPeopleMinimapFragment.this.cardSelectedColor);
                this.selectedLayout.setVisibility(0);
            }
            else {
                this.cardView.setCardElevation(0.0f);
                this.cardView.setCardBackgroundColor(0);
                this.selectedLayout.setVisibility(8);
            }
            this.chatterDisplayData = chatterDisplayData;
        }
        
        public void onClick(final View view) {
            switch (view.getId()) {
                case 2131755487: {
                    final FragmentManager fragmentManager = NearbyPeopleMinimapFragment.this.getFragmentManager();
                    if (fragmentManager == null) {
                        break;
                    }
                    final Fragment fragmentById = fragmentManager.findFragmentById(2131755654);
                    if (fragmentById instanceof MinimapView.OnUserClickListener) {
                        ((MinimapView.OnUserClickListener)fragmentById).onUserClick(this.chatterDisplayData.chatterID.getOptionalChatterUUID());
                        break;
                    }
                    break;
                }
                case 2131755489: {
                    if (this.chatterDisplayData != null) {
                        DetailsActivity.showDetails(NearbyPeopleMinimapFragment.this.getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(this.chatterDisplayData.chatterID));
                        break;
                    }
                    break;
                }
            }
        }
    }
}
