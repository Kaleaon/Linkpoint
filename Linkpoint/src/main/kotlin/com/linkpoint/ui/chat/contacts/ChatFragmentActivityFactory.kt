package com.linkpoint.ui.chat.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.chat.ChatNewActivity
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.ChatterFragment
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

class ChatFragmentActivityFactory private constructor() : FragmentActivityFactory {

    companion object {
        @JvmStatic
        private val Instance = ChatFragmentActivityFactory()

        @JvmStatic
        fun getInstance(): ChatFragmentActivityFactory {
            return Instance
        }
    }

    override fun createIntent(context: Context, bundle: Bundle?): Intent {
        val intent = Intent(context, ChatNewActivity::class.java)
        intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
        if (bundle != null && bundle.containsKey(ChatterFragment.CHATTER_ID_KEY)) {
            val chatterID = bundle.getParcelable<ChatterID>(ChatterFragment.CHATTER_ID_KEY)
            if (chatterID != null) {
                ActivityUtils.setActiveAgentID(intent, chatterID.agentUUID)
            }
        }
        return intent
    }

    override fun getFragmentClass(): Class<out Fragment> {
        return ChatFragment::class.java
    }
}
