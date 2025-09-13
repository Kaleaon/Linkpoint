package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import java.util.UUID;

public class MyAvatarFragment extends FragmentWithTitle implements AdapterView.OnItemClickListener, ChatterNameRetriever.OnChatterNameUpdated {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = null;
    @BindView(2131755497)
    TextView myAvatarName;
    private ChatterNameRetriever myAvatarNameRetriever = null;
    @BindView(2131755499)
    ListView myAvatarOptionsList;
    @BindView(2131755498)
    ChatterPicView myAvatarPic;
    /* access modifiers changed from: private */
    public final SubscriptionData<SubscriptionSingleKey, Integer> myBalance = new SubscriptionData<>(UIThreadExecutor.getInstance(), new $Lambda$E97LbIKTNF028fQGuPv0gXqIQrc(this));
    private Unbinder unbinder;

    private class MyAvatarPagesAdapter extends ArrayAdapter<MyAvatarDetailsPages> {

        /* renamed from: -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
        private static final /* synthetic */ int[] f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$ui$myava$MyAvatarDetailsPages;

        /* renamed from: -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
        private static /* synthetic */ int[] m655getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues() {
            if (f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues != null) {
                return f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues;
            }
            int[] iArr = new int[MyAvatarDetailsPages.values().length];
            try {
                iArr[MyAvatarDetailsPages.pageBalance.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[MyAvatarDetailsPages.pageProfile.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            f463comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = iArr;
            return iArr;
        }

        public MyAvatarPagesAdapter(Context context) {
            super(context, 17367043, MyAvatarDetailsPages.values());
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            String string;
            View view2 = super.getView(i, view, viewGroup);
            MyAvatarDetailsPages myAvatarDetailsPages = (MyAvatarDetailsPages) getItem(i);
            if ((view2 instanceof TextView) && myAvatarDetailsPages != null) {
                switch (m655getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues()[myAvatarDetailsPages.ordinal()]) {
                    case 1:
                        Integer num = (Integer) MyAvatarFragment.this.myBalance.getData();
                        if (num != null) {
                            string = MyAvatarFragment.this.getString(R.string.my_ava_balance_title, num);
                        } else {
                            string = MyAvatarFragment.this.getString(R.string.my_ava_balance_unknown);
                        }
                        ((TextView) view2).setText(string);
                        break;
                    default:
                        ((TextView) view2).setText(MyAvatarFragment.this.getString(myAvatarDetailsPages.getTitleResource()));
                        break;
                }
            }
            return view2;
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m653getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues() {
        if (f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues != null) {
            return f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues;
        }
        int[] iArr = new int[MyAvatarDetailsPages.values().length];
        try {
            iArr[MyAvatarDetailsPages.pageBalance.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[MyAvatarDetailsPages.pageBlockList.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[MyAvatarDetailsPages.pageOutfits.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[MyAvatarDetailsPages.pageProfile.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        f462comlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues = iArr;
        return iArr;
    }

    private UUID getAgentUUID() {
        return ActivityUtils.getActiveAgentID(getArguments());
    }

    public static Bundle makeSelection(UUID uuid) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    public static MyAvatarFragment newInstance(UUID uuid) {
        MyAvatarFragment myAvatarFragment = new MyAvatarFragment();
        myAvatarFragment.setArguments(makeSelection(uuid));
        return myAvatarFragment;
    }

    /* access modifiers changed from: private */
    /* renamed from: onMyBalance */
    public void m654com_lumiyaviewer_lumiya_ui_myava_MyAvatarFragmentmthref0(Integer num) {
        if (this.unbinder != null) {
            ListAdapter adapter = this.myAvatarOptionsList.getAdapter();
            if (adapter instanceof MyAvatarPagesAdapter) {
                ((MyAvatarPagesAdapter) adapter).notifyDataSetChanged();
            }
        }
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        String resolvedName = chatterNameRetriever.getResolvedName();
        if (this.unbinder != null) {
            this.myAvatarName.setText(resolvedName != null ? resolvedName : getString(R.string.name_loading_title));
            this.myAvatarPic.setChatterID(chatterNameRetriever.chatterID, resolvedName);
        }
        setTitle(resolvedName, (String) null);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.my_avatar, viewGroup, false);
        this.unbinder = ButterKnife.bind((Object) this, inflate);
        this.myAvatarOptionsList.setAdapter(new MyAvatarPagesAdapter(viewGroup.getContext()));
        this.myAvatarOptionsList.setOnItemClickListener(this);
        return inflate;
    }

    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        UUID agentUUID = getAgentUUID();
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if ((itemAtPosition instanceof MyAvatarDetailsPages) && agentUUID != null) {
            switch (m653getcomlumiyaviewerlumiyauimyavaMyAvatarDetailsPagesSwitchesValues()[((MyAvatarDetailsPages) itemAtPosition).ordinal()]) {
                case 1:
                    DetailsActivity.showEmbeddedDetails(getActivity(), TransactionLogFragment.class, TransactionLogFragment.makeSelection(agentUUID));
                    return;
                case 2:
                    DetailsActivity.showEmbeddedDetails(getActivity(), MuteListFragment.class, MuteListFragment.makeSelection(agentUUID));
                    return;
                case 3:
                    DetailsActivity.showEmbeddedDetails(getActivity(), OutfitsFragment.class, OutfitsFragment.makeSelection(agentUUID, (UUID) null));
                    return;
                case 4:
                    DetailsActivity.showEmbeddedDetails(getActivity(), MyProfileFragment.class, MyProfileFragment.makeSelection(ChatterID.getUserChatterID(agentUUID, agentUUID)));
                    return;
                default:
                    return;
            }
        }
    }

    public void onStart() {
        super.onStart();
        UUID agentUUID = getAgentUUID();
        UserManager userManager = UserManager.getUserManager(agentUUID);
        if (userManager != null) {
            this.myBalance.subscribe(userManager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
        if (agentUUID != null) {
            this.myAvatarNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(agentUUID, agentUUID), this, UIThreadExecutor.getSerialInstance());
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
}
