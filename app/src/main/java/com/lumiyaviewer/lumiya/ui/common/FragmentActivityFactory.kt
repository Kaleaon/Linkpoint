package com.lumiyaviewer.lumiya.ui.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

interface FragmentActivityFactory {
    fun createIntent(context: Context, args: Bundle): Intent
    fun getFragmentClass(): Class<out Fragment>
}