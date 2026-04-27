package com.lumiyaviewer.lumiya.ui.myava;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import java.util.UUID;

/* loaded from: classes.dex */
public class MuteListFragment extends FragmentWithTitle {
    private MuteListAdapter adapter;

    @BindView(R.id.muteList)
    ListView muteList;
    private Unbinder unbinder;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableList<MuteListEntry>> muteListData = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.myava.-$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4.2
        private final /* synthetic */ void $m$0(Object obj) {
            ((MuteListFragment) this).m659com_lumiyaviewer_lumiya_ui_myava_MuteListFragmentmthref0((ImmutableList) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());

    private void askForUnblock(final MuteListEntry muteListEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(R.string.unblock_confirm_message), muteListEntry.name)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.myava.-$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4.3
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((MuteListFragment) this).m661lambda$com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_5870((MuteListEntry) muteListEntry, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.myava.-$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doUnblock(MuteListEntry muteListEntry) {
        try {
            this.agentCircuit.get().getModules().muteList.Unblock(muteListEntry);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    public static Bundle makeSelection(UUID uuid) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onMuteList, reason: merged with bridge method [inline-methods] */
    public void m659com_lumiyaviewer_lumiya_ui_myava_MuteListFragmentmthref0(ImmutableList<MuteListEntry> immutableList) {
        if (this.adapter != null) {
            this.adapter.setData(immutableList);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_3737, reason: not valid java name */
    public /* synthetic */ void m660lambda$com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_3737(AdapterView adapterView, View view, int i, long j) {
        Toast.makeText(getContext(), getString(R.string.mute_list_dismiss_hint), 0).show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_5870, reason: not valid java name */
    public /* synthetic */ void m661lambda$com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_5870(MuteListEntry muteListEntry, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        doUnblock(muteListEntry);
    }

    @OnClick({R.id.add_mute_list_button})
    public void onAddMuteListButtonClick() {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        FragmentActivity activity = getActivity();
        if (activity == null || userManager == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(activity, AvatarPickerForMute.class, AvatarPickerForMute.makeArguments(userManager.getUserID()));
    }

    @Override // android.support.v4.app.Fragment
    public boolean onContextItemSelected(MenuItem menuItem) {
        MuteListEntry item;
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        if (adapterContextMenuInfo == null || this.adapter == null || (item = this.adapter.getItem(adapterContextMenuInfo.position)) == null) {
            return false;
        }
        switch (menuItem.getItemId()) {
            case R.id.item_mute_list_delete /* 2131755825 */:
                askForUnblock(item);
                return true;
            default:
                return false;
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.mute_list, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.adapter = new MuteListAdapter(layoutInflater.getContext());
        this.muteList.setAdapter((ListAdapter) this.adapter);
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(this.muteList, new SwipeDismissListViewTouchListener.DismissCallbacks() { // from class: com.lumiyaviewer.lumiya.ui.myava.MuteListFragment.1
            @Override // com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks
            public boolean canDismiss(ListView listView, int i) {
                return true;
            }

            @Override // com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks
            public void onDismiss(ListView listView, int i) {
                MuteListEntry item;
                ListAdapter adapter = listView.getAdapter();
                if (!(adapter instanceof MuteListAdapter) || (item = ((MuteListAdapter) adapter).getItem(i)) == null) {
                    return;
                }
                MuteListFragment.this.doUnblock(item);
            }
        });
        this.muteList.setOnTouchListener(swipeDismissListViewTouchListener);
        this.muteList.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener());
        this.muteList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.myava.-$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4.1
            private final /* synthetic */ void $m$0(AdapterView adapterView, View view, int i, long j) {
                ((MuteListFragment) this).m660lambda$com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_3737(adapterView, view, i, j);
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                $m$0(adapterView, view, i, j);
            }
        });
        registerForContextMenu(this.muteList);
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

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        setTitle(getString(R.string.mute_list_fragment_title), null);
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            this.muteListData.subscribe(userManager.muteListPool(), SubscriptionSingleKey.Value);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.StateAwareFragment, android.support.v4.app.Fragment
    public void onStop() {
        this.muteListData.unsubscribe();
        this.agentCircuit.unsubscribe();
        super.onStop();
    }
}
