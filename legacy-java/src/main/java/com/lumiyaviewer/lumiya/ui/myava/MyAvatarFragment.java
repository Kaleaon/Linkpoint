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
import com.lumiyaviewer.lumiya.react.Subscription;
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

/* loaded from: classes.dex */
public class MyAvatarFragment extends FragmentWithTitle implements AdapterView.OnItemClickListener, ChatterNameRetriever.OnChatterNameUpdated {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f462x282be546 = null;

    @BindView(R.id.my_avatar_name)
    TextView myAvatarName;

    @BindView(R.id.my_ava_options_list)
    ListView myAvatarOptionsList;

    @BindView(R.id.my_avatar_pic)
    ChatterPicView myAvatarPic;
    private Unbinder unbinder;
    private ChatterNameRetriever myAvatarNameRetriever = null;
    private final SubscriptionData<SubscriptionSingleKey, Integer> myBalance = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.myava.-$Lambda$E97LbIKTNF028fQGuPv0gXqIQrc
        private final /* synthetic */ void $m$0(Object obj) {
            ((MyAvatarFragment) this).m664com_lumiyaviewer_lumiya_ui_myava_MyAvatarFragmentmthref0((Integer) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyAvatarPagesAdapter extends ArrayAdapter<MyAvatarDetailsPages> {

        /* renamed from: -com-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues, reason: not valid java name */
        private static final /* synthetic */ int[] f463x282be546 = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$ui$myava$MyAvatarDetailsPages;

        /* renamed from: -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues, reason: not valid java name */
        private static /* synthetic */ int[] m665x90cfe3ea() {
            if (f463x282be546 != null) {
                return f463x282be546;
            }
            int[] iArr = new int[MyAvatarDetailsPages.valuesCustom().length];
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
            f463x282be546 = iArr;
            return iArr;
        }

        public MyAvatarPagesAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, MyAvatarDetailsPages.valuesCustom());
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            MyAvatarDetailsPages item = getItem(i);
            if ((view2 instanceof TextView) && item != null) {
                switch (m665x90cfe3ea()[item.ordinal()]) {
                    case 1:
                        Integer num = (Integer) MyAvatarFragment.this.myBalance.getData();
                        ((TextView) view2).setText(num != null ? MyAvatarFragment.this.getString(R.string.my_ava_balance_title, num) : MyAvatarFragment.this.getString(R.string.my_ava_balance_unknown));
                        break;
                    default:
                        ((TextView) view2).setText(MyAvatarFragment.this.getString(item.getTitleResource()));
                        break;
                }
            }
            return view2;
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-myava-MyAvatarDetailsPagesSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m663x90cfe3ea() {
        if (f462x282be546 != null) {
            return f462x282be546;
        }
        int[] iArr = new int[MyAvatarDetailsPages.valuesCustom().length];
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
        f462x282be546 = iArr;
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

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onMyBalance, reason: merged with bridge method [inline-methods] */
    public void m664com_lumiyaviewer_lumiya_ui_myava_MyAvatarFragmentmthref0(Integer num) {
        if (this.unbinder != null) {
            ListAdapter adapter = this.myAvatarOptionsList.getAdapter();
            if (adapter instanceof MyAvatarPagesAdapter) {
                ((MyAvatarPagesAdapter) adapter).notifyDataSetChanged();
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        String resolvedName = chatterNameRetriever.getResolvedName();
        if (this.unbinder != null) {
            this.myAvatarName.setText(resolvedName != null ? resolvedName : getString(R.string.name_loading_title));
            this.myAvatarPic.setChatterID(chatterNameRetriever.chatterID, resolvedName);
        }
        setTitle(resolvedName, null);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.my_avatar, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.myAvatarOptionsList.setAdapter((ListAdapter) new MyAvatarPagesAdapter(viewGroup.getContext()));
        this.myAvatarOptionsList.setOnItemClickListener(this);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        UUID agentUUID = getAgentUUID();
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if (!(itemAtPosition instanceof MyAvatarDetailsPages) || agentUUID == null) {
            return;
        }
        switch (m663x90cfe3ea()[((MyAvatarDetailsPages) itemAtPosition).ordinal()]) {
            case 1:
                DetailsActivity.showEmbeddedDetails(getActivity(), TransactionLogFragment.class, TransactionLogFragment.makeSelection(agentUUID));
                return;
            case 2:
                DetailsActivity.showEmbeddedDetails(getActivity(), MuteListFragment.class, MuteListFragment.makeSelection(agentUUID));
                return;
            case 3:
                DetailsActivity.showEmbeddedDetails(getActivity(), OutfitsFragment.class, OutfitsFragment.makeSelection(agentUUID, null));
                return;
            case 4:
                DetailsActivity.showEmbeddedDetails(getActivity(), MyProfileFragment.class, MyProfileFragment.makeSelection(ChatterID.getUserChatterID(agentUUID, agentUUID)));
                return;
            default:
                return;
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
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

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        if (this.myAvatarNameRetriever != null) {
            this.myAvatarNameRetriever.dispose();
            this.myAvatarNameRetriever = null;
        }
        this.myBalance.unsubscribe();
        super.onStop();
    }
}
