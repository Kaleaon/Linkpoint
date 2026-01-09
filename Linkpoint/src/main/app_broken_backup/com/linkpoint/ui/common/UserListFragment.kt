package com.linkpoint.ui.common
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
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.events.EventUserInfoChanged
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.chat.ChatterDisplayInfo
import com.linkpoint.ui.chat.contacts.ChatFragmentActivityFactory
import com.linkpoint.ui.common.SwipeDismissListViewTouchListener
import com.linkpoint.ui.render.CardboardActivity
import java.io.Closeable
import java.io.IOException
import androidx.annotation.Nullable

abstract class UserListFragment : Fragment {
    @Nullable
    protected UserManager userManager = null

    private Unit updateListViews() {
        ListView listView
        View view = getView()
        if (view != null && (listView = (view as ListView).findViewById(R.id.contactList)) != null) {
            listView.invalidateViews()
        }
    }

    /* access modifiers changed from: protected */
    abstract ListAdapter createListAdapter(Context context, LoaderManager loaderManager, UserManager userManager2)

    /* access modifiers changed from: protected */
    fun handleUserDefaultAction(ChatterID chatterID)  {
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
    fun itemsMayBeDismissed(): Boolean {
        return false
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689  reason: not valid java name */
    /* synthetic */ Unit m578lambda$com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689(AdapterView adapterView, View view, Int i, Long j) {
        ChatterID chatterID
        Any itemAtPosition = adapterView.getItemAtPosition(i)
        if ((itemAtPosition is ChatterDisplayInfo) && this.userManager != null && (chatterID = ((ChatterDisplayInfo) itemAtPosition).getChatterID(this.userManager)) != null) {
            handleUserDefaultAction(chatterID)
        }
    }

    fun onActivityCreated(Bundle bundle)  {
        super.onActivityCreated(bundle)
        View view = getView()
        if (view != null) {
            ListView listView = (view as ListView).findViewById(R.id.contactList)
            listView.setOnItemClickListener($Lambda$1wR8wJi1eGgAIYEhals_u5j3nM(this))
            registerForContextMenu(listView)
            if (itemsMayBeDismissed()) {
                SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(listView, SwipeDismissListViewTouchListener.DismissCallbacks() {
                    fun canDismiss(ListView listView, Int i): Boolean {
                        ListAdapter adapter = listView.getAdapter()
                        if (adapter is DismissableAdapter) {
                            return ((DismissableAdapter) adapter).canDismiss(i)
                        }
                        return false
                    }

                    fun onDismiss(ListView listView, Int i)  {
                        ListAdapter adapter = listView.getAdapter()
                        if (adapter is DismissableAdapter) {
                            ((DismissableAdapter) adapter).onDismiss(i)
                        }
                    }
                listView.setOnTouchListener(swipeDismissListViewTouchListener)
                listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
            }
        }
    }

    fun onCreate(Bundle bundle)  {
        super.onCreate(bundle)
        this.userManager = ActivityUtils.getUserManager(getArguments())
    }

    fun onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle): View {
        return layoutInflater.inflate(R.layout.contacts_group, viewGroup, false)
    }

    fun onStart()  {
        ListView listView
        ListAdapter listAdapter = null
        super.onStart()
        View view = getView()
        Debug.Printf("UserListFragment: onStart, rootView = %s", view)
        if (view != null && (listView = (view as ListView).findViewById(R.id.contactList)) != null && listView.getAdapter() == null) {
            UserManager userManager2 = ActivityUtils.getUserManager(getArguments())
            if (userManager2 != null) {
                listAdapter = createListAdapter(getActivity(), getLoaderManager(), userManager2)
            }
            listView.setAdapter(listAdapter)
        }
    }

    fun onStop()  {
        ListView listView
        View view = getView()
        Debug.Printf("UserListFragment: onStop, rootView = %s", view)
        if (!(view == null || (listView = (view as ListView).findViewById(R.id.contactList)) == null)) {
            ListAdapter adapter = listView.getAdapter()
            if (adapter is Closeable) {
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
    fun onUserInfoChanged(EventUserInfoChanged eventUserInfoChanged)  {
        if (this.userManager != null && this.userManager.getUserID().equals(eventUserInfoChanged.agentUUID) && eventUserInfoChanged.isProfileChanged()) {
            updateListViews()
        }
    }
}
