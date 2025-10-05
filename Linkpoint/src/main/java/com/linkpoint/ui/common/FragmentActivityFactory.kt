package com.linkpoint.ui.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment

interface FragmentActivityFactory {
    fun createIntent(context: Context, bundle: Bundle): Intent
    fun getFragmentClass(): Class<out Fragment>
}