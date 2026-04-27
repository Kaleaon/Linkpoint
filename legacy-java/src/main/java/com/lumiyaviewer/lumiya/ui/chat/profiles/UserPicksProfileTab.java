package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class UserPicksProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private final SubscriptionData<UUID, AvatarPicksReply> avatarPicks = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.avatarPicks).withDataChangedListener(this);
    private PicksAdapter picksAdapter;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PicksAdapter extends BaseAdapter {
        private final LayoutInflater inflater;
        private AvatarPicksReply picksReply;

        private PicksAdapter(Context context) {
            this.picksReply = null;
            this.inflater = LayoutInflater.from(context);
        }

        /* synthetic */ PicksAdapter(Context context, PicksAdapter picksAdapter) {
            this(context);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.picksReply != null) {
                return this.picksReply.Data_Fields.size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public AvatarPicksReply.Data getItem(int i) {
            if (this.picksReply == null || i < 0 || i >= this.picksReply.Data_Fields.size()) {
                return null;
            }
            return this.picksReply.Data_Fields.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.inflater.inflate(R.layout.simple_list_item_1, viewGroup, false);
            }
            AvatarPicksReply.Data item = getItem(i);
            if (item != null) {
                ((TextView) view.findViewById(R.id.text1)).setText(SLMessage.stringFromVariableUTF(item.PickName));
            }
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return false;
        }

        void setData(AvatarPicksReply avatarPicksReply) {
            this.picksReply = avatarPicksReply;
            notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAddNewPick, reason: merged with bridge method [inline-methods] */
    public void m530xb7c87a2b(View view) {
        if (this.userManager == null || !(this.chatterID instanceof ChatterID.ChatterIDUser)) {
            return;
        }
        CurrentLocationInfo currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot();
        final ParcelData parcelData = currentLocationInfoSnapshot != null ? currentLocationInfoSnapshot.parcelData() : null;
        final SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
        if (parcelData == null || activeAgentCircuit == null) {
            return;
        }
        final int count = this.picksAdapter != null ? this.picksAdapter.getCount() : 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String str = (String) Optional.fromNullable(Strings.emptyToNull(parcelData.getName())).or((Optional) getString(com.lumiyaviewer.lumiya.R.string.name_loading_title));
        builder.setMessage(getString(com.lumiyaviewer.lumiya.R.string.create_pick_question, str)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.3
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((UserPicksProfileTab) this).m532xd71354a5((SLAgentCircuit) activeAgentCircuit, (String) str, (ParcelData) parcelData, count, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_2539, reason: not valid java name */
    public /* synthetic */ void m531xd7126bce(AdapterView adapterView, View view, int i, long j) {
        Object item = adapterView.getAdapter().getItem(i);
        if ((item instanceof AvatarPicksReply.Data) && (this.chatterID instanceof ChatterID.ChatterIDUser)) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserPickFragment.class, UserPickFragment.makeSelection(this.chatterID.agentUUID, new AvatarPickKey(((ChatterID.ChatterIDUser) this.chatterID).getChatterUUID(), ((AvatarPicksReply.Data) item).PickID)));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_4543, reason: not valid java name */
    public /* synthetic */ void m532xd71354a5(SLAgentCircuit sLAgentCircuit, String str, ParcelData parcelData, int i, DialogInterface dialogInterface, int i2) {
        UUID randomUUID = UUID.randomUUID();
        sLAgentCircuit.getModules().userProfiles.UpdatePickInfo(randomUUID, this.userManager.getUserID(), UUIDPool.ZeroUUID, str, Strings.nullToEmpty(parcelData.getDescription()), (UUID) Optional.fromNullable(parcelData.getSnapshotUUID()).or((Optional) UUIDPool.ZeroUUID), sLAgentCircuit.getAgentGlobalPosition(), i, true);
        DetailsActivity.showEmbeddedDetails(getActivity(), UserPickFragment.class, UserPickFragment.makeSelection(this.chatterID.agentUUID, new AvatarPickKey(((ChatterID.ChatterIDUser) this.chatterID).getChatterUUID(), randomUUID)));
        dialogInterface.dismiss();
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(com.lumiyaviewer.lumiya.R.layout.user_profile_tab_picks, viewGroup, false);
        this.picksAdapter = new PicksAdapter(layoutInflater.getContext(), null);
        inflate.findViewById(com.lumiyaviewer.lumiya.R.id.add_new_pick_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.1
            private final /* synthetic */ void $m$0(View view) {
                ((UserPicksProfileTab) this).m530xb7c87a2b(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        ((ListView) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.picks_list_view)).setAdapter((ListAdapter) this.picksAdapter);
        ((ListView) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.picks_list_view)).setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A.2
            private final /* synthetic */ void $m$0(AdapterView adapterView, View view, int i, long j) {
                ((UserPicksProfileTab) this).m531xd7126bce(adapterView, view, i, j);
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                $m$0(adapterView, view, i, j);
            }
        });
        ((LoadingLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.loading_layout), getString(com.lumiyaviewer.lumiya.R.string.no_user_selected), getString(com.lumiyaviewer.lumiya.R.string.user_picks_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.swipe_refresh_layout));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        try {
            if (this.picksAdapter != null) {
                this.picksAdapter.setData(this.avatarPicks.getData());
            }
            this.loadableMonitor.setEmptyMessage(this.avatarPicks.get().Data_Fields.isEmpty(), getString(com.lumiyaviewer.lumiya.R.string.no_picks));
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        boolean z;
        UserManager userManager;
        this.loadableMonitor.unsubscribeAll();
        if (!(chatterID instanceof ChatterID.ChatterIDUser) || (userManager = chatterID.getUserManager()) == null) {
            z = false;
        } else {
            z = userManager.getUserID().equals(((ChatterID.ChatterIDUser) chatterID).getChatterUUID());
            this.avatarPicks.subscribe(userManager.getAvatarPicks().getPool(), ((ChatterID.ChatterIDUser) chatterID).getChatterUUID());
        }
        View view = getView();
        if (view != null) {
            view.findViewById(com.lumiyaviewer.lumiya.R.id.add_new_pick_button).setVisibility(z ? 0 : 8);
        }
    }
}
