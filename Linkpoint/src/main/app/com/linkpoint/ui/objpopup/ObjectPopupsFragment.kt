package com.linkpoint.ui.objpopup

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linkpoint.R
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatLayoutManager
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ObjectPopupsFragment : Fragment {
    private val AGENT_UUID_KEY: String = "agentUUID"
    private ItemTouchHelper.Callback itemTouchCallback = ItemTouchHelper.SimpleCallback(0, 12) {
        fun onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2): Boolean {
            return false
        }

        fun onSwiped(RecyclerView.ViewHolder viewHolder, Int i): Unit {
            RecyclerView recyclerView
            RecyclerView.Adapter adapter
            UserManager r1 = ObjectPopupsFragment.this.getUserManager()
            View view = ObjectPopupsFragment.this.getView()
            if (view != null && r1 != null && (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) != null && (adapter = recyclerView.getAdapter()) != null) {
                Int adapterPosition = viewHolder.getAdapterPosition()
                if (adapter instanceof ObjectPopupsAdapter) {
                    r1.getObjectPopupsManager().cancelObjectPopup((SLChatEvent) ((ObjectPopupsAdapter) adapter).getObject(adapterPosition))
                }
            }
        }
    }

    fun create(@NonNull UUID uuid): ObjectPopupsFragment {
        ObjectPopupsFragment objectPopupsFragment = ObjectPopupsFragment()
        Bundle bundle = Bundle()
        bundle.putString(AGENT_UUID_KEY, uuid.toString())
        objectPopupsFragment.setArguments(bundle)
        return objectPopupsFragment
    }

    /* access modifiers changed from: private */
    @Nullable
    fun getUserManager(): UserManager {
        Bundle arguments = getArguments()
        if (arguments == null || !arguments.containsKey(AGENT_UUID_KEY)) {
            return null
        }
        return UserManager.getUserManager(UUID.fromString(arguments.getString(AGENT_UUID_KEY)))
    }

    @Nullable
    fun onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle): View {
        View inflate = layoutInflater.inflate(R.layout.object_popups_fragment_layout, viewGroup, false)
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.objectPopupsList)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(ChatLayoutManager(layoutInflater.getContext(), 1, false))
        ItemTouchHelper(this.itemTouchCallback).attachToRecyclerView(recyclerView)
        return inflate
    }

    fun onStart(): Unit {
        RecyclerView recyclerView
        super.onStart()
        UserManager userManager = getUserManager()
        View view = getView()
        if (userManager != null && view != null && (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) != null) {
            recyclerView.setAdapter(ObjectPopupsAdapter(getContext(), userManager.getObjectPopupsManager().getObjectPopups(), userManager))
        }
    }

    fun onStop(): Unit {
        RecyclerView recyclerView
        View view = getView()
        if (!(view == null || (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) == null)) {
            recyclerView.setAdapter((RecyclerView.Adapter) null)
        }
        super.onStop()
    }
}
