package com.linkpoint.ui.objpopup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.linkpoint.R
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatLayoutManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ObjectPopupsFragment : Fragment() {
    private const val AGENT_UUID_KEY: String = "agentUUID"
    private val ItemTouchHelper.Callback itemTouchCallback = ItemTouchHelper.SimpleCallback(0, 12) {
         public fun onMove(recyclerView: RecyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2): Boolean {
            return false
        }

        fun onSwiped(RecyclerView.ViewHolder viewHolder, i: Int) {
            RecyclerView recyclerView
            RecyclerView.Adapter adapter
            val r1: UserManager = ObjectPopupsFragment.this.getUserManager()
            val view: View = ObjectPopupsFragment.this.getView()
            if (view != null && r1 != null && (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) != null && (adapter = recyclerView.getAdapter()) != null) {
                val adapterPosition: Int = viewHolder.getAdapterPosition()
                if (adapter instanceof ObjectPopupsAdapter) {
                    r1.getObjectPopupsManager().cancelObjectPopup((SLChatEvent) ((ObjectPopupsAdapter) adapter).getObject(adapterPosition))
                }
            }
        }
    }

    @JvmStatic
     fun create(uuid: UUID): ObjectPopupsFragment {
        val objectPopupsFragment: ObjectPopupsFragment = ObjectPopupsFragment()
        val bundle: Bundle = Bundle()
        bundle.putString(AGENT_UUID_KEY, uuid.toString())
        objectPopupsFragment.setArguments(bundle)
        return objectPopupsFragment
    }

    /* access modifiers changed from: private */
     public fun getUserManager(): UserManager {
        val arguments: Bundle = getArguments()
        if (arguments == null || !arguments.containsKey(AGENT_UUID_KEY)) {
            return null
        }
        return UserManager.getUserManager(UUID.fromString(arguments.getString(AGENT_UUID_KEY)))
    }

     public override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        val inflate: View = layoutInflater.inflate(R.layout.object_popups_fragment_layout, viewGroup, false)
        val recyclerView: RecyclerView = (RecyclerView) inflate.findViewById(R.id.objectPopupsList)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(ChatLayoutManager(layoutInflater.getContext(), 1, false))
        ItemTouchHelper(this.itemTouchCallback).attachToRecyclerView(recyclerView)
        return inflate
    }

    override fun onStart() {
        RecyclerView recyclerView
        super.onStart()
        val userManager: UserManager = getUserManager()
        val view: View = getView()
        if (userManager != null && view != null && (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) != null) {
            recyclerView.setAdapter(ObjectPopupsAdapter(getContext(), userManager.getObjectPopupsManager().getObjectPopups(), userManager))
        }
    }

    override fun onStop() {
        RecyclerView recyclerView
        val view: View = getView()
        if (!(view == null || (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) == null)) {
            recyclerView.setAdapter((RecyclerView.Adapter) null)
        }
        super.onStop()
    }
}
