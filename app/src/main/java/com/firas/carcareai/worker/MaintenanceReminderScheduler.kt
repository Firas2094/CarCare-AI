package com.firas.carcareai.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object MaintenanceReminderScheduler {

    private const val WORK_NAME = "maintenance_reminder_work"

    fun schedule(context: Context) {

        val workRequest =
            PeriodicWorkRequestBuilder<MaintenanceReminderWorker>(
                1,
                TimeUnit.DAYS
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}