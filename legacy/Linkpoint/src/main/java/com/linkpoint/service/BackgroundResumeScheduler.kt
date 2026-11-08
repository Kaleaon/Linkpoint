package com.linkpoint.service

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object BackgroundResumeScheduler {
    private const val TAG = "BackgroundResumeScheduler"
    private const val PERIODIC_WORK_NAME = "linkpoint_background_resume_periodic"
    private const val ONE_SHOT_WORK_NAME = "linkpoint_background_resume_oneshot"

    fun schedule(context: Context, immediate: Boolean = false) {
        val profile = BackgroundRuntimeConfig.getIntensityProfile(context)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<BackgroundResumeWorker>(
            profile.reconnectBackoffMs,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )

        if (immediate) {
            val oneShotRequest = OneTimeWorkRequestBuilder<BackgroundResumeWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_SHOT_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                oneShotRequest
            )
        }

        Log.d(TAG, "Background resume scheduled with profile=${profile.name}")
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ONE_SHOT_WORK_NAME)
    }
}
