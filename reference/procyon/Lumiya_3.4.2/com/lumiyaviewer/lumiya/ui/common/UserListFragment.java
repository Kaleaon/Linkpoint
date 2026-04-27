// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged;
import java.io.IOException;
import java.io.Closeable;
import com.lumiyaviewer.lumiya.Debug;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View$OnTouchListener;
import android.widget.AdapterView$OnItemClickListener;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import android.widget.AdapterView;
import android.os.Bundle;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.ListAdapter;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v4.app.Fragment;

public abstract class UserListFragment extends Fragment
{
    @Nullable
    protected UserManager userManager;
    
    public UserListFragment() {
        this.userManager = null;
    }
    
    private void updateListViews() {
        final View view = this.getView();
        if (view != null) {
            final ListView listView = (ListView)view.findViewById(2131755338);
            if (listView != null) {
                listView.invalidateViews();
            }
        }
    }
    
    protected abstract ListAdapter createListAdapter(final Context p0, final LoaderManager p1, final UserManager p2);
    
    protected void handleUserDefaultAction(final ChatterID chatterID) {
        if (this.userManager != null) {
            final Bundle selection = ChatFragment.makeSelection(chatterID);
            final Bundle arguments = this.getArguments();
            if (arguments.containsKey("vrMode")) {
                selection.putBoolean("vrMode", arguments.getBoolean("vrMode"));
            }
            DetailsActivity.showDetails(this.getActivity(), ChatFragmentActivityFactory.getInstance(), selection);
        }
    }
    
    protected boolean itemsMayBeDismissed() {
        return false;
    }
    
    @Override
    public void onActivityCreated(final Bundle bundle) {
        super.onActivityCreated(bundle);
        final View view = this.getView();
        if (view != null) {
            final ListView listView = (ListView)view.findViewById(2131755338);
            listView.setOnItemClickListener((AdapterView$OnItemClickListener)new _$Lambda$1wR8wJi1e_GgAIYEhals_u5j3nM(this));
            this.registerForContextMenu((View)listView);
            if (this.itemsMayBeDismissed()) {
                final SwipeDismissListViewTouchListener onTouchListener = new SwipeDismissListViewTouchListener(listView, (SwipeDismissListViewTouchListener.DismissCallbacks)new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(final ListView listView, final int n) {
                        final ListAdapter adapter = listView.getAdapter();
                        return adapter instanceof DismissableAdapter && ((DismissableAdapter)adapter).canDismiss(n);
                    }
                    
                    @Override
                    public void onDismiss(final ListView listView, final int n) {
                        final ListAdapter adapter = listView.getAdapter();
                        if (adapter instanceof DismissableAdapter) {
                            ((DismissableAdapter)adapter).onDismiss(n);
                        }
                    }
                });
                listView.setOnTouchListener((View$OnTouchListener)onTouchListener);
                listView.setOnScrollListener(onTouchListener.makeScrollListener());
            }
        }
    }
    
    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.userManager = ActivityUtils.getUserManager(this.getArguments());
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        return layoutInflater.inflate(2130968622, viewGroup, false);
    }
    
    @Override
    public void onStart() {
        ListAdapter listAdapter = null;
        super.onStart();
        final View view = this.getView();
        Debug.Printf("UserListFragment: onStart, rootView = %s", view);
        if (view != null) {
            final ListView listView = (ListView)view.findViewById(2131755338);
            if (listView != null && listView.getAdapter() == null) {
                final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
                if (userManager != null) {
                    listAdapter = this.createListAdapter((Context)this.getActivity(), this.getLoaderManager(), userManager);
                }
                listView.setAdapter(listAdapter);
            }
        }
    }
    
    @Override
    public void onStop() {
        Object view = this.getView();
        Debug.Printf("UserListFragment: onStop, rootView = %s", view);
        Label_0062: {
            if (view == null) {
                break Label_0062;
            }
            view = ((View)view).findViewById(2131755338);
            if (view == null) {
                break Label_0062;
            }
            final ListAdapter adapter = ((ListView)view).getAdapter();
            while (true) {
                if (!(adapter instanceof Closeable)) {
                    break Label_0057;
                }
                try {
                    ((Closeable)adapter).close();
                    ((ListView)view).setAdapter((ListAdapter)null);
                    super.onStop();
                }
                catch (final IOException ex) {
                    Debug.Warning(ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @EventHandler
    public void onUserInfoChanged(final EventUserInfoChanged eventUserInfoChanged) {
        if (this.userManager != null && this.userManager.getUserID().equals(eventUserInfoChanged.agentUUID) && eventUserInfoChanged.isProfileChanged()) {
            this.updateListViews();
        }
    }
}
