package com.lumiyaviewer.lumiya.ui.objpopup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatLayoutManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ObjectPopupsFragment : Fragment {
    private val AGENT_UUID_KEY: String = "agentUUID"
    private ItemTouchHelper.Callback itemTouchCallback = ItemTouchHelper.SimpleCallback(0, 12) {
        Boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            return false
        }

        Unit onSwiped(RecyclerView.ViewHolder viewHolder, Int i) {
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

    ObjectPopupsFragment create(@Nonnull UUID uuid) {
        ObjectPopupsFragment objectPopupsFragment = ObjectPopupsFragment()
        Bundle bundle = Bundle()
        bundle.putString(AGENT_UUID_KEY, uuid.toString())
        objectPopupsFragment.setArguments(bundle)
        return objectPopupsFragment
    }

    /* access modifiers changed from: private */
    @Nullable
    UserManager getUserManager() {
        Bundle arguments = getArguments()
        if (arguments == null || !arguments.containsKey(AGENT_UUID_KEY)) {
            return null
        }
        return UserManager.getUserManager(UUID.fromString(arguments.getString(AGENT_UUID_KEY)))
    }

    @Nullable
    View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.object_popups_fragment_layout, viewGroup, false)
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.objectPopupsList)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(ChatLayoutManager(layoutInflater.getContext(), 1, false))
        ItemTouchHelper(this.itemTouchCallback).attachToRecyclerView(recyclerView)
        return inflate
    }

    Unit onStart() {
        RecyclerView recyclerView
        super.onStart()
        UserManager userManager = getUserManager()
        View view = getView()
        if (userManager != null && view != null && (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) != null) {
            recyclerView.setAdapter(ObjectPopupsAdapter(getContext(), userManager.getObjectPopupsManager().getObjectPopups(), userManager))
        }
    }

    Unit onStop() {
        RecyclerView recyclerView
        View view = getView()
        if (!(view == null || (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) == null)) {
            recyclerView.setAdapter((RecyclerView.Adapter) null)
        }
        super.onStop()
    }
}
