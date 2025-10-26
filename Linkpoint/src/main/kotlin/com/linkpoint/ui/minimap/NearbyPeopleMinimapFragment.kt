package com.linkpoint.ui.minimap

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.google.common.base.Objects
import com.google.common.collect.ImmutableList
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.manager.ChatterDisplayData
import com.linkpoint.slproto.users.manager.ChatterListType
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.chat.contacts.ChatFragmentActivityFactory
import com.linkpoint.ui.chat.contacts.ChatterItemViewBuilder
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.minimap.MinimapView
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class NearbyPeopleMinimapFragment : Fragment() {
    private NearbyUserRecyclerAdapter adapter = null
    /* access modifiers changed from: private */
    public Int cardSelectedColor = 0
    private val SubscriptionData<ChatterListType, ImmutableList<ChatterDisplayData>> chatterList = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$0SrW7eOJ5Pm_SVTDQOmxGjUXtco(this))
    @BindView(16908292)
    View emptyView
    @BindView(2131755490)
    RecyclerView userListView

    private class NearbyUserRecyclerAdapter : RecyclerView().Adapter<NearbyUserViewHolder> {
        private ImmutableList<ChatterDisplayData> chatters = ImmutableList.of()
        private val Context context
        private val LayoutInflater layoutInflater
        private Long nextStableId = 1
        private Int selectedPosition = -1
        private UUID selectedUUID
        private val Map<UUID, Long> stableIds = HashMap()
        private val UserManager userManager

        NearbyUserRecyclerAdapter(Context context2, UserManager userManager2) {
            this.context = context2
            this.userManager = userManager2
            this.layoutInflater = LayoutInflater.from(context2)
            setHasStableIds(true)
        }

         public fun getItemCount(): Int {
            return this.chatters.size()
        }

         public fun getItemId(i: Int): Long {
            UUID optionalChatterUUID
            Long l
            if (i < 0 || i >= this.chatters.size() || (optionalChatterUUID = ((ChatterDisplayData) this.chatters.get(i)).chatterID.getOptionalChatterUUID()) == null || (l = this.stableIds.get(optionalChatterUUID)) == null) {
                return -1
            }
            return l.longValue()
        }

        fun onBindViewHolder(nearbyUserViewHolder: NearbyUserViewHolder, i: Int) {
            val z: Boolean = false
            if (i >= 0 && i < this.chatters.size()) {
                val context2: Context = this.context
                val layoutInflater2: LayoutInflater = this.layoutInflater
                val userManager2: UserManager = this.userManager
                val chatterDisplayData: ChatterDisplayData = (ChatterDisplayData) this.chatters.get(i)
                if (i == this.selectedPosition) {
                    z = true
                }
                nearbyUserViewHolder.bindToData(context2, layoutInflater2, userManager2, chatterDisplayData, z)
            }
        }

         public fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NearbyUserViewHolder {
            return NearbyUserViewHolder(this.layoutInflater.inflate(R.layout.minimap_user_item, viewGroup, false))
        }

        fun setChatters(immutableList: ImmutableList<ChatterDisplayData>) {
            if (immutableList == null) {
                immutableList = ImmutableList.of()
            }
            this.chatters = immutableList
            this.selectedPosition = -1
            val hashSet: HashSet = HashSet()
            val i: Int = 0
            while (true) {
                val i2: Int = i
                if (i2 < this.chatters.size()) {
                    val optionalChatterUUID: UUID = ((ChatterDisplayData) this.chatters.get(i2)).chatterID.getOptionalChatterUUID()
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

        fun setSelected(uuid: UUID) {
            this.selectedUUID = uuid
            if (uuid != null) {
                val i2: Int = 0
                while (true) {
                    i = i2
                    if (i >= this.chatters.size()) {
                        break
                    }
                    val optionalChatterUUID: UUID = ((ChatterDisplayData) this.chatters.get(i)).chatterID.getOptionalChatterUUID()
                    if (optionalChatterUUID != null && Objects.equal(uuid, optionalChatterUUID)) {
                        break
                    }
                    i2 = i + 1
                }
            }
            i = -1
            if (i != this.selectedPosition) {
                val i3: Int = this.selectedPosition
                this.selectedPosition = i
                notifyItemChanged(this.selectedPosition)
                notifyItemChanged(i3)
            }
        }
    }

    private class NearbyUserViewHolder : RecyclerView().ViewHolder : View.OnClickListener {
        private val Float cardSelectedElevation
        private val CardView cardView
        private ChatterDisplayData chatterDisplayData = null
        private val View selectedLayout
        private val FrameLayout userItemViewHolder
        private val ChatterItemViewBuilder viewBuilder = ChatterItemViewBuilder()

        public NearbyUserViewHolder(View view) {
            super(view)
            this.userItemViewHolder = (FrameLayout) view.findViewById(R.id.user_item_view_holder)
            this.cardView = (CardView) view.findViewById(R.id.user_card_view)
            this.cardSelectedElevation = this.cardView.getCardElevation()
            this.selectedLayout = view.findViewById(R.id.user_item_selected_layout)
            this.userItemViewHolder.setOnClickListener(this)
            view.findViewById(R.id.user_item_chat_button).setOnClickListener(this)
        }

        fun bindToData(context: Context, layoutInflater: LayoutInflater, userManager: UserManager, chatterDisplayData2: ChatterDisplayData, z: Boolean) {
            this.viewBuilder.reset()
            chatterDisplayData2.buildView(context, this.viewBuilder, userManager)
            val childAt: View = this.userItemViewHolder.getChildAt(0)
            val view: View = this.viewBuilder.getView(layoutInflater, childAt, this.userItemViewHolder, true)
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

        override fun onClick(view: View) {
            switch (view.getId()) {
                case R.id.user_item_view_holder:
                    val fragmentManager: FragmentManager = NearbyPeopleMinimapFragment.this.getFragmentManager()
                    if (fragmentManager != null) {
                        val findFragmentById: Fragment = fragmentManager.findFragmentById(R.id.selector)
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

    static Fragment newInstance(UUID uuid) {
        val nearbyPeopleMinimapFragment: NearbyPeopleMinimapFragment = NearbyPeopleMinimapFragment()
        nearbyPeopleMinimapFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, (Bundle) null))
        return nearbyPeopleMinimapFragment
    }

    /* access modifiers changed from: private */
    /* renamed from: onChatterList */
    fun m645com_lumiyaviewer_lumiya_ui_minimap_NearbyPeopleMinimapFragmentmthref0(immutableList: ImmutableList<ChatterDisplayData>) {
        val i: Int = 8
        if (this.adapter != null) {
            this.adapter.setChatters(immutableList)
        }
        if (getView() != null) {
            val isEmpty: Boolean = immutableList.isEmpty()
            this.emptyView.setVisibility(isEmpty ? 0 : 8)
            val recyclerView: RecyclerView = this.userListView
            if (!isEmpty) {
                i = 0
            }
            recyclerView.setVisibility(i)
        }
    }

     public override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        val inflate: View = layoutInflater.inflate(R.layout.minimap_users, viewGroup, false)
        ButterKnife.bind((Object) this, inflate)
        val typedValue: TypedValue = TypedValue()
        layoutInflater.getContext().getTheme().resolveAttribute(R.attr.CardViewDetailsBackground, typedValue, true)
        this.cardSelectedColor = typedValue.data
        this.adapter = NearbyUserRecyclerAdapter(getContext(), ActivityUtils.getUserManager(getArguments()))
        this.userListView.setAdapter(this.adapter)
        return inflate
    }

    override fun onStart() {
        super.onStart()
        val userManager: UserManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            this.chatterList.subscribe(userManager.getChatterList().getChatterList(), ChatterListType.Nearby)
        } else {
            this.chatterList.unsubscribe()
        }
    }

    override fun onStop() {
        this.chatterList.unsubscribe()
        super.onStop()
    }

    fun setSelectedUser(uuid: UUID) {
        if (this.adapter != null) {
            this.adapter.setSelected(uuid)
        }
    }
}
