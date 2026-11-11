package com.linkpoint.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootResumeReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootResumeReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            if (BackgroundRuntimeConfig.isBackgroundResumeEnabled(context)) {
                Log.i(TAG, "Boot/package replace detected, scheduling background resume")
                BackgroundResumeScheduler.schedule(context, immediate = true)
            }
        }
    }
}
