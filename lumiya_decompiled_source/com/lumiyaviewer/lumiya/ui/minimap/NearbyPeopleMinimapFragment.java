package com.lumiyaviewer.lumiya.ui.minimap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.minimap.MinimapView;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class NearbyPeopleMinimapFragment extends Fragment {
    @BindView(16908292)
    View emptyView;
    @BindView(R.id.minimap_users_list)
    RecyclerView userListView;
    private final SubscriptionData<ChatterListType, ImmutableList<ChatterDisplayData>> chatterList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$0SrW7eOJ5Pm_SVTDQOmxGjUXtco
        private final /* synthetic */ void $m$0(Object obj) {
            ((NearbyPeopleMinimapFragment) this).m656xe5efdfa3((ImmutableList) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private NearbyUserRecyclerAdapter adapter = null;
    private int cardSelectedColor = 0;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NearbyUserRecyclerAdapter extends RecyclerView.Adapter<NearbyUserViewHolder> {
        private final Context context;
        private final LayoutInflater layoutInflater;
        @Nullable
        private UUID selectedUUID;
        private final UserManager userManager;
        private final Map<UUID, Long> stableIds = new HashMap();
        private long nextStableId = 1;
        @Nonnull
        private ImmutableList<ChatterDisplayData> chatters = ImmutableList.of();
        private int selectedPosition = -1;

        NearbyUserRecyclerAdapter(Context context, UserManager userManager) {
            this.context = context;
            this.userManager = userManager;
            this.layoutInflater = LayoutInflater.from(context);
            setHasStableIds(true);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.chatters.size();
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            UUID optionalChatterUUID;
            Long l;
            if (i < 0 || i >= this.chatters.size() || (optionalChatterUUID = this.chatters.get(i).chatterID.getOptionalChatterUUID()) == null || (l = this.stableIds.get(optionalChatterUUID)) == null) {
                return -1L;
            }
            return l.longValue();
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(NearbyUserViewHolder nearbyUserViewHolder, int i) {
            if (i < 0 || i >= this.chatters.size()) {
                return;
            }
            nearbyUserViewHolder.bindToData(this.context, this.layoutInflater, this.userManager, this.chatters.get(i), i == this.selectedPosition);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public NearbyUserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new NearbyUserViewHolder(this.layoutInflater.inflate(R.layout.minimap_user_item, viewGroup, false));
        }

        public void setChatters(@Nullable ImmutableList<ChatterDisplayData> immutableList) {
            if (immutableList == null) {
                immutableList = ImmutableList.of();
            }
            this.chatters = immutableList;
            this.selectedPosition = -1;
            HashSet hashSet = new HashSet();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= this.chatters.size()) {
                    this.stableIds.keySet().retainAll(hashSet);
                    notifyDataSetChanged();
                    return;
                }
                UUID optionalChatterUUID = this.chatters.get(i2).chatterID.getOptionalChatterUUID();
                if (optionalChatterUUID != null) {
                    hashSet.add(optionalChatterUUID);
                    if (!this.stableIds.containsKey(optionalChatterUUID)) {
                        this.stableIds.put(optionalChatterUUID, Long.valueOf(this.nextStableId));
                        this.nextStableId++;
                    }
                    if (Objects.equal(optionalChatterUUID, this.selectedUUID)) {
                        this.selectedPosition = i2;
                    }
                }
                i = i2 + 1;
            }
        }

        public void setSelected(UUID uuid) {
            int i;
            this.selectedUUID = uuid;
            if (uuid != null) {
                int i2 = 0;
                while (true) {
                    i = i2;
                    if (i >= this.chatters.size()) {
                        break;
                    }
                    UUID optionalChatterUUID = this.chatters.get(i).chatterID.getOptionalChatterUUID();
                    if (optionalChatterUUID != null && Objects.equal(uuid, optionalChatterUUID)) {
                        break;
                    }
                    i2 = i + 1;
                }
            }
            i = -1;
            if (i != this.selectedPosition) {
                int i3 = this.selectedPosition;
                this.selectedPosition = i;
                notifyItemChanged(this.selectedPosition);
                notifyItemChanged(i3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NearbyUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final float cardSelectedElevation;
        private final CardView cardView;
        private ChatterDisplayData chatterDisplayData;
        private final View selectedLayout;
        private final FrameLayout userItemViewHolder;
        private final ChatterItemViewBuilder viewBuilder;

        public NearbyUserViewHolder(View view) {
            super(view);
            this.viewBuilder = new ChatterItemViewBuilder();
            this.chatterDisplayData = null;
            this.userItemViewHolder = (FrameLayout) view.findViewById(R.id.user_item_view_holder);
            this.cardView = (CardView) view.findViewById(R.id.user_card_view);
            this.cardSelectedElevation = this.cardView.getCardElevation();
            this.selectedLayout = view.findViewById(R.id.user_item_selected_layout);
            this.userItemViewHolder.setOnClickListener(this);
            view.findViewById(R.id.user_item_chat_button).setOnClickListener(this);
        }

        public void bindToData(Context context, LayoutInflater layoutInflater, UserManager userManager, ChatterDisplayData chatterDisplayData, boolean z) {
            this.viewBuilder.reset();
            chatterDisplayData.buildView(context, this.viewBuilder, userManager);
            View childAt = this.userItemViewHolder.getChildAt(0);
            View view = this.viewBuilder.getView(layoutInflater, childAt, this.userItemViewHolder, true);
            if (view != childAt) {
                if (childAt != null) {
                    this.userItemViewHolder.removeView(childAt);
                }
                this.userItemViewHolder.addView(view);
            }
            if (z) {
                this.cardView.setCardElevation(this.cardSelectedElevation);
                this.cardView.setCardBackgroundColor(NearbyPeopleMinimapFragment.this.cardSelectedColor);
                this.selectedLayout.setVisibility(0);
            } else {
                this.cardView.setCardElevation(0.0f);
                this.cardView.setCardBackgroundColor(0);
                this.selectedLayout.setVisibility(8);
            }
            this.chatterDisplayData = chatterDisplayData;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_item_view_holder /* 2131755487 */:
                    FragmentManager fragmentManager = NearbyPeopleMinimapFragment.this.getFragmentManager();
                    if (fragmentManager != null) {
                        Fragment findFragmentById = fragmentManager.findFragmentById(R.id.selector);
                        if (findFragmentById instanceof MinimapView.OnUserClickListener) {
                            ((MinimapView.OnUserClickListener) findFragmentById).onUserClick(this.chatterDisplayData.chatterID.getOptionalChatterUUID());
                            return;
                        }
                        return;
                    }
                    return;
                case R.id.user_item_selected_layout /* 2131755488 */:
                default:
                    return;
                case R.id.user_item_chat_button /* 2131755489 */:
                    if (this.chatterDisplayData != null) {
                        DetailsActivity.showDetails(NearbyPeopleMinimapFragment.this.getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(this.chatterDisplayData.chatterID));
                        return;
                    }
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Fragment newInstance(UUID uuid) {
        NearbyPeopleMinimapFragment nearbyPeopleMinimapFragment = new NearbyPeopleMinimapFragment();
        nearbyPeopleMinimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return nearbyPeopleMinimapFragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onChatterList */
    public void m656xe5efdfa3(ImmutableList<ChatterDisplayData> immutableList) {
        if (this.adapter != null) {
            this.adapter.setChatters(immutableList);
        }
        if (getView() != null) {
            boolean isEmpty = immutableList.isEmpty();
            this.emptyView.setVisibility(isEmpty ? 0 : 8);
            this.userListView.setVisibility(isEmpty ? 8 : 0);
        }
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.minimap_users, viewGroup, false);
        ButterKnife.bind(this, inflate);
        TypedValue typedValue = new TypedValue();
        layoutInflater.getContext().getTheme().resolveAttribute(R.attr.CardViewDetailsBackground, typedValue, true);
        this.cardSelectedColor = typedValue.data;
        this.adapter = new NearbyUserRecyclerAdapter(getContext(), ActivityUtils.getUserManager(getArguments()));
        this.userListView.setAdapter(this.adapter);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            this.chatterList.subscribe(userManager.getChatterList().getChatterList(), ChatterListType.Nearby);
        } else {
            this.chatterList.unsubscribe();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        this.chatterList.unsubscribe();
        super.onStop();
    }

    public void setSelectedUser(UUID uuid) {
        if (this.adapter != null) {
            this.adapter.setSelected(uuid);
        }
    }
}
