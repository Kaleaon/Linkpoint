package com.linkpoint.ui.common
import java.util.*

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
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
import javax.annotation.Nullable

abstract class UserListFragment : Fragment() {
    protected UserManager userManager = null

     private fun updateListViews() {
        ListView listView
        val view: View = getView()
        if (view != null && (listView = (ListView) view.findViewById(R.id.contactList)) != null) {
            listView.invalidateViews()
        }
    }

    /* access modifiers changed from: protected */
    public abstract ListAdapter createListAdapter(Context context, LoaderManager loaderManager, UserManager userManager2)

    /* access modifiers changed from: protected */
    fun handleUserDefaultAction(chatterID: ChatterID) {
        if (this.userManager != null) {
            val makeSelection: Bundle = ChatFragment.makeSelection(chatterID)
            val arguments: Bundle = getArguments()
            if (arguments.containsKey(CardboardActivity.VR_MODE_TAG)) {
                makeSelection.putBoolean(CardboardActivity.VR_MODE_TAG, arguments.getBoolean(CardboardActivity.VR_MODE_TAG))
            }
            DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), makeSelection)
        }
    }

    /* access modifiers changed from: protected */
     public fun itemsMayBeDismissed(): Boolean {
        return false
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689  reason: not valid java name */
    public /* synthetic */ Unit m578lambda$com_lumiyaviewer_lumiya_ui_common_UserListFragment_1689(AdapterView adapterView, View view, Int i, Long j) {
        ChatterID chatterID
        val itemAtPosition: Object = adapterView.getItemAtPosition(i)
        if ((itemAtPosition instanceof ChatterDisplayInfo) && this.userManager != null && (chatterID = ((ChatterDisplayInfo) itemAtPosition).getChatterID(this.userManager)) != null) {
            handleUserDefaultAction(chatterID)
        }
    }

    override fun onActivityCreated(bundle: Bundle) {
        super.onActivityCreated(bundle)
        val view: View = getView()
        if (view != null) {
            val listView: ListView = (ListView) view.findViewById(R.id.contactList)
            listView.setOnItemClickListener($Lambda$1wR8wJi1eGgAIYEhals_u5j3nM(this))
            registerForContextMenu(listView)
            if (itemsMayBeDismissed()) {
                val swipeDismissListViewTouchListener: SwipeDismissListViewTouchListener = SwipeDismissListViewTouchListener(listView, SwipeDismissListViewTouchListener.DismissCallbacks() {
                     public fun canDismiss(listView: ListView, i: Int): Boolean {
                        val adapter: ListAdapter = listView.getAdapter()
                        if (adapter instanceof DismissableAdapter) {
                            return ((DismissableAdapter) adapter).canDismiss(i)
                        }
                        return false
                    }

                    fun onDismiss(listView: ListView, i: Int) {
                        val adapter: ListAdapter = listView.getAdapter()
                        if (adapter instanceof DismissableAdapter) {
                            ((DismissableAdapter) adapter).onDismiss(i)
                        }
                    }
                listView.setOnTouchListener(swipeDismissListViewTouchListener)
                listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener())
            }
        }
    }

    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        this.userManager = ActivityUtils.getUserManager(getArguments())
    }

     public override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        return layoutInflater.inflate(R.layout.contacts_group, viewGroup, false)
    }

    override fun onStart() {
        ListView listView
        val listAdapter: ListAdapter = null
        super.onStart()
        val view: View = getView()
        Debug.Printf("UserListFragment: onStart, rootView = %s", view)
        if (view != null && (listView = (ListView) view.findViewById(R.id.contactList)) != null && listView.getAdapter() == null) {
            val userManager2: UserManager = ActivityUtils.getUserManager(getArguments())
            if (userManager2 != null) {
                listAdapter = createListAdapter(getActivity(), getLoaderManager(), userManager2)
            }
            listView.setAdapter(listAdapter)
        }
    }

    override fun onStop() {
        ListView listView
        val view: View = getView()
        Debug.Printf("UserListFragment: onStop, rootView = %s", view)
        if (!(view == null || (listView = (ListView) view.findViewById(R.id.contactList)) == null)) {
            val adapter: ListAdapter = listView.getAdapter()
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
    fun onUserInfoChanged(eventUserInfoChanged: EventUserInfoChanged) {
        if (this.userManager != null && this.userManager.getUserID().equals(eventUserInfoChanged.agentUUID) && eventUserInfoChanged.isProfileChanged()) {
            updateListViews()
        }
    }
}
