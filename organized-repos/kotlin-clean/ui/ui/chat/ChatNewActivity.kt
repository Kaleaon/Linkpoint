package com.linkpoint.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

class ChatNewActivity : MasterDetailsActivity() {

    private val chatDetailsFragmentFactory = object : FragmentActivityFactory {
        override fun createIntent(context: Context, bundle: Bundle?): Intent {
            val intent = Intent(context, ChatNewActivity::class.java)
            if (bundle != null) {
                intent.putExtra(INTENT_SELECTION_KEY, bundle)
            }
            return intent
        }

        override fun getFragmentClass(): Class<out Fragment> {
            return ChatFragment::class.java
        }
    }

    override fun getDetailsFragmentFactory(): FragmentActivityFactory {
        return chatDetailsFragmentFactory
    }
}
