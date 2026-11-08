package com.linkpoint.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class BackgroundResumeWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "BackgroundResumeWorker"
    }

    override suspend fun doWork(): Result {
        if (!BackgroundRuntimeConfig.isBackgroundResumeEnabled(applicationContext)) {
            Log.d(TAG, "Background resume disabled; skipping worker run")
            return Result.success()
        }

        return try {
            LinkpointConnectionService.start(applicationContext)
            Log.i(TAG, "Background resume worker started connection service")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start connection service from worker", e)
            Result.retry()
        }
    }
}
