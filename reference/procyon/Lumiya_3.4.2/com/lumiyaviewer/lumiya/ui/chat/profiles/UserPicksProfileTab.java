// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.widget.TextView;
import android.content.Context;
import android.widget.BaseAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.widget.AdapterView$OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.View$OnClickListener;
import android.os.Bundle;
import javax.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import android.widget.AdapterView;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import android.content.DialogInterface$OnClickListener;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.view.View;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class UserPicksProfileTab extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    private final SubscriptionData<UUID, AvatarPicksReply> avatarPicks;
    private final LoadableMonitor loadableMonitor;
    private PicksAdapter picksAdapter;
    
    public UserPicksProfileTab() {
        this.avatarPicks = new SubscriptionData<UUID, AvatarPicksReply>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.avatarPicks }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
    }
    
    private void onAddNewPick(final View view) {
        ParcelData parcelData = null;
        if (this.userManager != null && this.chatterID instanceof ChatterID.ChatterIDUser) {
            final CurrentLocationInfo currentLocationInfoSnapshot = this.userManager.getCurrentLocationInfoSnapshot();
            if (currentLocationInfoSnapshot != null) {
                parcelData = currentLocationInfoSnapshot.parcelData();
            }
            final SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
            if (parcelData != null && activeAgentCircuit != null) {
                int count;
                if (this.picksAdapter != null) {
                    count = this.picksAdapter.getCount();
                }
                else {
                    count = 0;
                }
                final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
                final String s = Optional.fromNullable(Strings.emptyToNull(parcelData.getName())).or(this.getString(2131296712));
                alertDialog$Builder.setMessage((CharSequence)this.getString(2131296467, s)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A$3(count, this, activeAgentCircuit, s, parcelData)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A());
                alertDialog$Builder.create().show();
            }
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968761, viewGroup, false);
        this.picksAdapter = new PicksAdapter(layoutInflater.getContext(), null);
        inflate.findViewById(2131755726).setOnClickListener((View$OnClickListener)new _$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A$1(this));
        ((ListView)inflate.findViewById(2131755725)).setAdapter((ListAdapter)this.picksAdapter);
        ((ListView)inflate.findViewById(2131755725)).setOnItemClickListener((AdapterView$OnItemClickListener)new _$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A$2(this));
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296753), this.getString(2131297136));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        try {
            if (this.picksAdapter != null) {
                this.picksAdapter.setData(this.avatarPicks.getData());
            }
            this.loadableMonitor.setEmptyMessage(this.avatarPicks.get().Data_Fields.isEmpty(), this.getString(2131296750));
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        int visibility = 0;
        this.loadableMonitor.unsubscribeAll();
        while (true) {
            Label_0096: {
                if (!(chatterID instanceof ChatterID.ChatterIDUser)) {
                    break Label_0096;
                }
                final UserManager userManager = chatterID.getUserManager();
                if (userManager == null) {
                    break Label_0096;
                }
                final int equals = userManager.getUserID().equals(((ChatterID.ChatterIDUser)chatterID).getChatterUUID()) ? 1 : 0;
                this.avatarPicks.subscribe(userManager.getAvatarPicks().getPool(), ((ChatterID.ChatterIDUser)chatterID).getChatterUUID());
                final View view = this.getView();
                if (view != null) {
                    final View viewById = view.findViewById(2131755726);
                    if (equals == 0) {
                        visibility = 8;
                    }
                    viewById.setVisibility(visibility);
                }
                return;
            }
            final int equals = 0;
            continue;
        }
    }
    
    private static class PicksAdapter extends BaseAdapter
    {
        private final LayoutInflater inflater;
        private AvatarPicksReply picksReply;
        
        private PicksAdapter(final Context context) {
            this.picksReply = null;
            this.inflater = LayoutInflater.from(context);
        }
        
        public int getCount() {
            int size;
            if (this.picksReply != null) {
                size = this.picksReply.Data_Fields.size();
            }
            else {
                size = 0;
            }
            return size;
        }
        
        public AvatarPicksReply.Data getItem(final int index) {
            if (this.picksReply != null && index >= 0 && index < this.picksReply.Data_Fields.size()) {
                return (AvatarPicksReply.Data)this.picksReply.Data_Fields.get(index);
            }
            return null;
        }
        
        public long getItemId(final int n) {
            return n;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            View inflate = view;
            if (view == null) {
                inflate = this.inflater.inflate(17367043, viewGroup, false);
            }
            final AvatarPicksReply.Data item = this.getItem(n);
            if (item != null) {
                ((TextView)inflate.findViewById(16908308)).setText((CharSequence)SLMessage.stringFromVariableUTF(item.PickName));
            }
            return inflate;
        }
        
        public boolean hasStableIds() {
            return false;
        }
        
        void setData(final AvatarPicksReply picksReply) {
            this.picksReply = picksReply;
            this.notifyDataSetChanged();
        }
    }
}
