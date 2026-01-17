package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class UserListFragment extends Fragment {
    @Nullable
    protected UserManager userManager = null;

    private void updateListViews() {
        ListView listView;
        View view = getView();
        if (view == null || (listView = (ListView) view.findViewById(R.id.contactList)) == null) {
            return;
        }
        listView.invalidateViews();
    }

    protected abstract ListAdapter createListAdapter(Context context, LoaderManager loaderManager, UserManager userManager);

    protected void handleUserDefaultAction(ChatterID chatterID) {
        if (this.userManager != null) {
            Bundle makeSelection = ChatFragment.makeSelection(chatterID);
            Bundle arguments = getArguments();
            if (arguments.containsKey(CardboardActivity.VR_MODE_TAG)) {
                makeSelection.putBoolean(CardboardActivity.VR_MODE_TAG, arguments.getBoolean(CardboardActivity.VR_MODE_TAG));
            }
            DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), makeSelection);
        }
    }

    protected boolean itemsMayBeDismissed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689  reason: not valid java name */
    public /* synthetic */ void m589lambda$com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689(AdapterView adapterView, View view, int i, long j) {
        ChatterID chatterID;
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if (!(itemAtPosition instanceof ChatterDisplayInfo) || this.userManager == null || (chatterID = ((ChatterDisplayInfo) itemAtPosition).getChatterID(this.userManager)) == null) {
            return;
        }
        handleUserDefaultAction(chatterID);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        View view = getView();
        if (view != null) {
            ListView listView = (ListView) view.findViewById(R.id.contactList);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.common.-$Lambda$1wR8wJi1e-GgAIYEhals_u5j3nM
                private final /* synthetic */ void $m$0(AdapterView adapterView, View view2, int i, long j) {
                    ((UserListFragment) this).m589lambda$com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689(adapterView, view2, i, j);
                }

                @Override // android.widget.AdapterView.OnItemClickListener
                public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                    $m$0(adapterView, view2, i, j);
                }
            });
            registerForContextMenu(listView);
            if (itemsMayBeDismissed()) {
                SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.DismissCallbacks() { // from class: com.lumiyaviewer.lumiya.ui.common.UserListFragment.1
                    @Override // com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks
                    public boolean canDismiss(ListView listView2, int i) {
                        ListAdapter adapter = listView2.getAdapter();
                        if (adapter instanceof DismissableAdapter) {
                            return ((DismissableAdapter) adapter).canDismiss(i);
                        }
                        return false;
                    }

                    @Override // com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks
                    public void onDismiss(ListView listView2, int i) {
                        ListAdapter adapter = listView2.getAdapter();
                        if (adapter instanceof DismissableAdapter) {
                            ((DismissableAdapter) adapter).onDismiss(i);
                        }
                    }
                });
                listView.setOnTouchListener(swipeDismissListViewTouchListener);
                listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener());
            }
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.userManager = ActivityUtils.getUserManager(getArguments());
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.contacts_group, viewGroup, false);
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        ListView listView;
        super.onStart();
        View view = getView();
        Debug.Printf("UserListFragment: onStart, rootView = %s", view);
        if (view == null || (listView = (ListView) view.findViewById(R.id.contactList)) == null || listView.getAdapter() != null) {
            return;
        }
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        listView.setAdapter(userManager != null ? createListAdapter(getActivity(), getLoaderManager(), userManager) : null);
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        ListView listView;
        View view = getView();
        Debug.Printf("UserListFragment: onStop, rootView = %s", view);
        if (view != null && (listView = (ListView) view.findViewById(R.id.contactList)) != null) {
            ListAdapter adapter = listView.getAdapter();
            if (adapter instanceof Closeable) {
                try {
                    ((Closeable) adapter).close();
                } catch (IOException e) {
                    Debug.Warning(e);
                }
            }
            listView.setAdapter((ListAdapter) null);
        }
        super.onStop();
    }

    @EventHandler
    public void onUserInfoChanged(EventUserInfoChanged eventUserInfoChanged) {
        if (this.userManager != null && this.userManager.getUserID().equals(eventUserInfoChanged.agentUUID) && eventUserInfoChanged.isProfileChanged()) {
            updateListViews();
        }
    }
}
