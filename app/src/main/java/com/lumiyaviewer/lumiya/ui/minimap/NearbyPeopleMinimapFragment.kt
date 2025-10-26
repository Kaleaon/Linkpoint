package com.lumiyaviewer.lumiya.ui.minimap

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.google.common.base.Objects
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.react.SubscriptionData
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity
import com.lumiyaviewer.lumiya.ui.minimap.MinimapView
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class NearbyPeopleMinimapFragment : Fragment {
    private NearbyUserRecyclerAdapter adapter = null
    /* access modifiers changed from: private */
    Int cardSelectedColor = 0
    private SubscriptionData<ChatterListType, ImmutableList<ChatterDisplayData>> chatterList = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$0SrW7eOJ5Pm_SVTDQOmxGjUXtco(this))
    @BindView(16908292)
    View emptyView
    @BindView(2131755490)
    RecyclerView userListView

    private class NearbyUserRecyclerAdapter : RecyclerView.Adapter<NearbyUserViewHolder> {
        @Nonnull
        private ImmutableList<ChatterDisplayData> chatters = ImmutableList.of()
        private Context context
        private LayoutInflater layoutInflater
        private Long nextStableId = 1
        private Int selectedPosition = -1
        @Nullable
        private UUID selectedUUID
        private Map<UUID, Long> stableIds = HashMap()
        private UserManager userManager

        NearbyUserRecyclerAdapter(Context context2, UserManager userManager2) {
            this.context = context2
            this.userManager = userManager2
            this.layoutInflater = LayoutInflater.from(context2)
            setHasStableIds(true)
        }

        Int getItemCount() {
            return this.chatters.size()
        }

        Long getItemId(Int i) {
            UUID optionalChatterUUID
            Long l
            if (i < 0 || i >= this.chatters.size() || (optionalChatterUUID = ((ChatterDisplayData) this.chatters.get(i)).chatterID.getOptionalChatterUUID()) == null || (l = this.stableIds.get(optionalChatterUUID)) == null) {
                return -1
            }
            return l.longValue()
        }

        Unit onBindViewHolder(NearbyUserViewHolder nearbyUserViewHolder, Int i) {
            Boolean z = false
            if (i >= 0 && i < this.chatters.size()) {
                Context context2 = this.context
                LayoutInflater layoutInflater2 = this.layoutInflater
                UserManager userManager2 = this.userManager
                ChatterDisplayData chatterDisplayData = (ChatterDisplayData) this.chatters.get(i)
                if (i == this.selectedPosition) {
                    z = true
                }
                nearbyUserViewHolder.bindToData(context2, layoutInflater2, userManager2, chatterDisplayData, z)
            }
        }

        NearbyUserViewHolder onCreateViewHolder(ViewGroup viewGroup, Int i) {
            return NearbyUserViewHolder(this.layoutInflater.inflate(R.layout.minimap_user_item, viewGroup, false))
        }

        Unit setChatters(@Nullable ImmutableList<ChatterDisplayData> immutableList) {
            if (immutableList == null) {
                immutableList = ImmutableList.of()
            }
            this.chatters = immutableList
            this.selectedPosition = -1
            HashSet hashSet = HashSet()
            Int i = 0
            while (true) {
                Int i2 = i
                if (i2 < this.chatters.size()) {
                    UUID optionalChatterUUID = ((ChatterDisplayData) this.chatters.get(i2)).chatterID.getOptionalChatterUUID()
                    if (optionalChatterUUID != null) {
                        hashSet.add(optionalChatterUUID)
                        if (!this.stableIds.containsKey(optionalChatterUUID)) {
                            this.stableIds.put(optionalChatterUUID, Long.valueOf(this.nextStableId))
                            this.nextStableId++
                        }
                        if (Objects.equal(optionalChatterUUID, this.selectedUUID)) {
                            this.selectedPosition = i2
                        }
                    }
                    i = i2 + 1
                } else {
                    this.stableIds.keySet().retainAll(hashSet)
                    notifyDataSetChanged()
                    return
                }
            }
        }

        Unit setSelected(UUID uuid) {
            this.selectedUUID = uuid
            if (uuid != null) {
                Int i2 = 0
                while (true) {
                    i = i2
                    if (i >= this.chatters.size()) {
                        break
                    }
                    UUID optionalChatterUUID = ((ChatterDisplayData) this.chatters.get(i)).chatterID.getOptionalChatterUUID()
                    if (optionalChatterUUID != null && Objects.equal(uuid, optionalChatterUUID)) {
                        break
                    }
                    i2 = i + 1
                }
            }
            i = -1
            if (i != this.selectedPosition) {
                Int i3 = this.selectedPosition
                this.selectedPosition = i
                notifyItemChanged(this.selectedPosition)
                notifyItemChanged(i3)
            }
        }
    }

    private class NearbyUserViewHolder : RecyclerView.ViewHolder : View.OnClickListener {
        private Float cardSelectedElevation
        private CardView cardView
        private ChatterDisplayData chatterDisplayData = null
        private View selectedLayout
        private FrameLayout userItemViewHolder
        private ChatterItemViewBuilder viewBuilder = ChatterItemViewBuilder()

        NearbyUserViewHolder(View view) {
            super(view)
            this.userItemViewHolder = (FrameLayout) view.findViewById(R.id.user_item_view_holder)
            this.cardView = (CardView) view.findViewById(R.id.user_card_view)
            this.cardSelectedElevation = this.cardView.getCardElevation()
            this.selectedLayout = view.findViewById(R.id.user_item_selected_layout)
            this.userItemViewHolder.setOnClickListener(this)
            view.findViewById(R.id.user_item_chat_button).setOnClickListener(this)
        }

        Unit bindToData(Context context, LayoutInflater layoutInflater, UserManager userManager, ChatterDisplayData chatterDisplayData2, Boolean z) {
            this.viewBuilder.reset()
            chatterDisplayData2.buildView(context, this.viewBuilder, userManager)
            View childAt = this.userItemViewHolder.getChildAt(0)
            View view = this.viewBuilder.getView(layoutInflater, childAt, this.userItemViewHolder, true)
            if (view != childAt) {
                if (childAt != null) {
                    this.userItemViewHolder.removeView(childAt)
                }
                this.userItemViewHolder.addView(view)
            }
            if (z) {
                this.cardView.setCardElevation(this.cardSelectedElevation)
                this.cardView.setCardBackgroundColor(NearbyPeopleMinimapFragment.this.cardSelectedColor)
                this.selectedLayout.setVisibility(0)
            } else {
                this.cardView.setCardElevation(0.0f)
                this.cardView.setCardBackgroundColor(0)
                this.selectedLayout.setVisibility(8)
            }
            this.chatterDisplayData = chatterDisplayData2
        }

        Unit onClick(View view) {
            switch (view.getId()) {
                case R.id.user_item_view_holder:
                    FragmentManager fragmentManager = NearbyPeopleMinimapFragment.this.getFragmentManager()
                    if (fragmentManager != null) {
                        Fragment findFragmentById = fragmentManager.findFragmentById(R.id.selector)
                        if (findFragmentById instanceof MinimapView.OnUserClickListener) {
                            ((MinimapView.OnUserClickListener) findFragmentById).onUserClick(this.chatterDisplayData.chatterID.getOptionalChatterUUID())
                            return
                        }
                        return
                    }
                    return
                case R.id.user_item_chat_button:
                    if (this.chatterDisplayData != null) {
                        DetailsActivity.showDetails(NearbyPeopleMinimapFragment.this.getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(this.chatterDisplayData.chatterID))
                        return
                    }
                    return
                default:
                    return
            }
        }
    }

    Fragment newInstance(UUID uuid) {
        NearbyPeopleMinimapFragment nearbyPeopleMinimapFragment = NearbyPeopleMinimapFragment()
        nearbyPeopleMinimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, (Bundle) null))
        return nearbyPeopleMinimapFragment
    }

    /* access modifiers changed from: private */
    /* renamed from: onChatterList */
    Unit m645com_lumiyaviewer_lumiya_ui_minimap_NearbyPeopleMinimapFragmentmthref0(ImmutableList<ChatterDisplayData> immutableList) {
        Int i = 8
        if (this.adapter != null) {
            this.adapter.setChatters(immutableList)
        }
        if (getView() != null) {
            Boolean isEmpty = immutableList.isEmpty()
            this.emptyView.setVisibility(isEmpty ? 0 : 8)
            RecyclerView recyclerView = this.userListView
            if (!isEmpty) {
                i = 0
            }
            recyclerView.setVisibility(i)
        }
    }

    @Nullable
    View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.minimap_users, viewGroup, false)
        ButterKnife.bind((Any) this, inflate)
        TypedValue typedValue = TypedValue()
        layoutInflater.getContext().getTheme().resolveAttribute(R.attr.CardViewDetailsBackground, typedValue, true)
        this.cardSelectedColor = typedValue.data
        this.adapter = NearbyUserRecyclerAdapter(getContext(), ActivityUtils.getUserManager(getArguments()))
        this.userListView.setAdapter(this.adapter)
        return inflate
    }

    Unit onStart() {
        super.onStart()
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.chatterList.subscribe(userManager.getChatterList().getChatterList(), ChatterListType.Nearby)
        } else {
            this.chatterList.unsubscribe()
        }
    }

    Unit onStop() {
        this.chatterList.unsubscribe()
        super.onStop()
    }

    Unit setSelectedUser(UUID uuid) {
        if (this.adapter != null) {
            this.adapter.setSelected(uuid)
        }
    }
}
