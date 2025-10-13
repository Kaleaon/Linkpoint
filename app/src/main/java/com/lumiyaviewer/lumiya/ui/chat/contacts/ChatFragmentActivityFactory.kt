package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity

class ChatFragmentActivityFactory private constructor() : FragmentActivityFactory {

    companion object {
        @JvmStatic
        fun getInstance(): ChatFragmentActivityFactory = INSTANCE

        private val INSTANCE = ChatFragmentActivityFactory()
    }

    override fun createIntent(context: Context, args: Bundle?): Intent {
        val intent = Intent(context, ChatNewActivity::class.java)
        intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, args)
        
        args?.let { bundle ->
            if (bundle.containsKey(ChatterFragment.CHATTER_ID_KEY)) {
                val chatterID = bundle.getParcelable<ChatterID>(ChatterFragment.CHATTER_ID_KEY)
                chatterID?.let {
                    ActivityUtils.setActiveAgentID(intent, it.agentUUID)
                }
            }
        }
        
        return intent
    }

    override fun getFragmentClass(): Class<out Fragment> {
        return ChatFragment::class.java
    }
}
