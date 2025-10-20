package com.linkpoint.ui.myava

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterPicView
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.FragmentWithTitle
import com.linkpoint.ui.outfits.OutfitsFragment
import java.util.UUID

class MyAvatarFragment : FragmentWithTitle(), AdapterView.OnItemClickListener, ChatterNameRetriever.OnChatterNameUpdated {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ Int[] f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = null
    @BindView(2131755497)
    TextView myAvatarName
    private ChatterNameRetriever myAvatarNameRetriever = null
    @BindView(2131755499)
    ListView myAvatarOptionsList
    @BindView(2131755498)
    ChatterPicView myAvatarPic
    /* access modifiers changed from: private */
    val SubscriptionData<SubscriptionSingleKey, Integer> myBalance = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$E97LbIKTNF028fQGuPv0gXqIQrc(this))
    private Unbinder unbinder

    private class MyAvatarPagesAdapter : ArrayAdapter()<MyAvatarDetailsPages> {

        /* renamed from: -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
        private const val /* synthetic */ Int[] f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = null
        final /* synthetic */ Int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$ui$myava$MyAvatarDetailsPages

        /* renamed from: -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
        @JvmStatic
private /* synthetic */ Int[] m655getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues() {
            if (f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues != null) {
                return f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues
            }
            Int[] iArr = Int[MyAvatarDetailsPages.values().length]
            try {
                iArr[MyAvatarDetailsPages.pageBalance.ordinal()] = 1
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[MyAvatarDetailsPages.pageProfile.ordinal()] = 4
            } catch (NoSuchFieldError e4) {
            }
            f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = iArr
            return iArr
        }

        public MyAvatarPagesAdapter(Context context) {
            super(context, 17367043, MyAvatarDetailsPages.values())
        }

        public View getView(Int i, View view, ViewGroup viewGroup) {
            String string
            View view2 = super.getView(i, view, viewGroup)
            MyAvatarDetailsPages myAvatarDetailsPages = (MyAvatarDetailsPages) getItem(i)
            if ((view2 instanceof TextView) && myAvatarDetailsPages != null) {
                switch (m655getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues()[myAvatarDetailsPages.ordinal()]) {
                    case 1:
                        Integer num = (Integer) MyAvatarFragment.this.myBalance.getData()
                        if (num != null) {
                            string = MyAvatarFragment.this.getString(R.string.my_ava_balance_title, num)
                        } else {
                            string = MyAvatarFragment.this.getString(R.string.my_ava_balance_unknown)
                        }
                        ((TextView) view2).setText(string)
                        break
                    default:
                        ((TextView) view2).setText(MyAvatarFragment.this.getString(myAvatarDetailsPages.getTitleResource()))
                        break
                }
            }
            return view2
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ Int[] m653getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues() {
        if (f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues != null) {
            return f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues
        }
        Int[] iArr = Int[MyAvatarDetailsPages.values().length]
        try {
            iArr[MyAvatarDetailsPages.pageBalance.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[MyAvatarDetailsPages.pageProfile.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = iArr
        return iArr
    }

    private UUID getAgentUUID() {
        return ActivityUtils.getActiveAgentID(getArguments())
    }

    @JvmStatic
    Bundle makeSelection(UUID uuid) {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        return bundle
    }

    @JvmStatic
    MyAvatarFragment newInstance(UUID uuid) {
        MyAvatarFragment myAvatarFragment = MyAvatarFragment()
        myAvatarFragment.setArguments(makeSelection(uuid))
        return myAvatarFragment
    }

    /* access modifiers changed from: private */
    /* renamed from: onMyBalance */
    fun m654com_lumiyaviewer_lumiya_ui_myava_MyAvatarFragmentmthref0(Integer num) {
        if (this.unbinder != null) {
            ListAdapter adapter = this.myAvatarOptionsList.getAdapter()
            if (adapter instanceof MyAvatarPagesAdapter) {
                ((MyAvatarPagesAdapter) adapter).notifyDataSetChanged()
            }
        }
    }

    fun onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        String resolvedName = chatterNameRetriever.getResolvedName()
        if (this.unbinder != null) {
            this.myAvatarName.setText(resolvedName != null ? resolvedName : getString(R.string.name_loading_title))
            this.myAvatarPic.setChatterID(chatterNameRetriever.chatterID, resolvedName)
        }
        setTitle(resolvedName, (String) null)
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.my_avatar, viewGroup, false)
        this.unbinder = ButterKnife.bind((Object) this, inflate)
        this.myAvatarOptionsList.setAdapter(MyAvatarPagesAdapter(viewGroup.getContext()))
        this.myAvatarOptionsList.setOnItemClickListener(this)
        return inflate
    }

    fun onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind()
            this.unbinder = null
        }
        super.onDestroyView()
    }

    fun onItemClick(AdapterView<?> adapterView, View view, Int i, Long j) {
        UUID agentUUID = getAgentUUID()
        Object itemAtPosition = adapterView.getItemAtPosition(i)
        if ((itemAtPosition instanceof MyAvatarDetailsPages) && agentUUID != null) {
            switch (m653getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues()[((MyAvatarDetailsPages) itemAtPosition).ordinal()]) {
                case 1:
                    DetailsActivity.showEmbeddedDetails(getActivity(), TransactionLogFragment.class, TransactionLogFragment.makeSelection(agentUUID))
                    return
                case 2:
                    DetailsActivity.showEmbeddedDetails(getActivity(), MuteListFragment.class, MuteListFragment.makeSelection(agentUUID))
                    return
                case 3:
                    DetailsActivity.showEmbeddedDetails(getActivity(), OutfitsFragment.class, OutfitsFragment.makeSelection(agentUUID, (UUID) null))
                    return
                case 4:
                    DetailsActivity.showEmbeddedDetails(getActivity(), MyProfileFragment.class, MyProfileFragment.makeSelection(ChatterID.getUserChatterID(agentUUID, agentUUID)))
                    return
                default:
                    return
            }
        }
    }

    fun onStart() {
        super.onStart()
        UUID agentUUID = getAgentUUID()
        UserManager userManager = UserManager.getUserManager(agentUUID)
        if (userManager != null) {
            this.myBalance.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value)
        }
        if (agentUUID != null) {
            this.myAvatarNameRetriever = ChatterNameRetriever(ChatterID.getUserChatterID(agentUUID, agentUUID), this, UIThreadExecutor.getSerialInstance())
        }
    }

    fun onStop() {
        if (this.myAvatarNameRetriever != null) {
            this.myAvatarNameRetriever.dispose()
            this.myAvatarNameRetriever = null
        }
        this.myBalance.unsubscribe()
        super.onStop()
    }
}
