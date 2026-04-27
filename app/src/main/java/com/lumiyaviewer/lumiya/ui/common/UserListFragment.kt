package com.lumiyaviewer.lumiya.ui.common
import java.util.*

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.LoaderManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.ListView
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.eventbus.EventHandler
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.events.EventUserInfoChanged
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity
import java.io.Closeable
import java.io.IOException
import androidx.annotation.Nullable

abstract class UserListFragment : Fragment {
    @Nullable
    protected UserManager userManager = null

    private Unit updateListViews() {
        ListView listView
        View view = getView()
        if (view != null && (listView = (ListView) view.findViewById(R.id.contactList)) != null) {
            listView.invalidateViews()
        }
    }

    /* access modifiers changed from: protected */
    abstract ListAdapter createListAdapter(Context context, LoaderManager loaderManager, UserManager userManager2)

    /* access modifiers changed from: protected */
    Unit handleUserDefaultAction(ChatterID chatterID) {
        if (this.userManager != null) {
            Bundle makeSelection = ChatFragment.makeSelection(chatterID)
            Bundle arguments = getArguments()
            if (arguments.containsKey(CardboardActivity.VR_MODE_TAG)) {
                makeSelection.putBoolean(CardboardActivity.VR_MODE_TAG, arguments.getBoolean(CardboardActivity.VR_MODE_TAG))
            }
            DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), makeSelection)
        }
    }

    /* access modifiers changed from: protected */
    Boolean itemsMayBeDismissed() {
        return false
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689  reason: not valid java name */
    /* synthetic */ Unit m578lambda$com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689(AdapterView adapterView, View view, Int i, Long j) {
        ChatterID chatterID
        Any itemAtPosition = adapterView.getItemAtPosition(i)
        if ((itemAtPosition instanceof ChatterDisplayInfo) && this.userManager != null && (chatterID = ((ChatterDisplayInfo) itemAtPosition).getChatterID(this.userManager)) != null) {
            handleUserDefaultAction(chatterID)
        }
    }

    Unit onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle)
        View view = getView()
        if (view != null) {
            ListView listView = (ListView) view.findViewById(R.id.contactList)
            listView.setOnItemClickListener($Lambda$1wR8wJi1eGgAIYEhals_u5j3nM(this))
            registerForContextMenu(listView)
            if (itemsMayBeDismissed()) {
                SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(listView, SwipeDismissListViewTouchListener.DismissCallbacks() {
                    Boolean canDismiss(ListView listView, Int i) {
                        ListAdapter adapter = listView.getAdapter()
                        if (adapter instanceof DismissableAdapter) {
                            return ((DismissableAdapter) adapter).canDismiss(i)
                        }
                        return false
                    }

                    Unit onDismiss(ListView listView, Int i) {
                        ListAdapter adapter = listView.getAdapter()
                        if (adapter instanceof DismissableAdapter) {
                            ((DismissableAdapter) adapter).onDismiss(i)
                        }
                    }
                listView.setOnTouchListener(swipeDismissListViewTouchListener)
                listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
            }
        }
    }

    Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
        this.userManager = ActivityUtils.getUserManager(getArguments())
    }

    View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.contacts_group, viewGroup, false)
    }

    Unit onStart() {
        ListView listView
        ListAdapter listAdapter = null
        super.onStart()
        View view = getView()
        Debug.Printf("UserListFragment: onStart, rootView = %s", view)
        if (view != null && (listView = (ListView) view.findViewById(R.id.contactList)) != null && listView.getAdapter() == null) {
            UserManager userManager2 = ActivityUtils.getUserManager(getArguments())
            if (userManager2 != null) {
                listAdapter = createListAdapter(getActivity(), getLoaderManager(), userManager2)
            }
            listView.setAdapter(listAdapter)
        }
    }

    Unit onStop() {
        ListView listView
        View view = getView()
        Debug.Printf("UserListFragment: onStop, rootView = %s", view)
        if (!(view == null || (listView = (ListView) view.findViewById(R.id.contactList)) == null)) {
            ListAdapter adapter = listView.getAdapter()
            if (adapter instanceof Closeable) {
                try {
                    ((Closeable) adapter).close()
                } catch (IOException e) {
                    Debug.Warning(e)
                }
            }
            listView.setAdapter((ListAdapter) null)
        }
        super.onStop()
    }

    @EventHandler
    Unit onUserInfoChanged(EventUserInfoChanged eventUserInfoChanged) {
        if (this.userManager != null && this.userManager.getUserID().equals(eventUserInfoChanged.agentUUID) && eventUserInfoChanged.isProfileChanged()) {
            updateListViews()
        }
    }
}
