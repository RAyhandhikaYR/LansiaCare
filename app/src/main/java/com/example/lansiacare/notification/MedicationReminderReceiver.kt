package com.example.lansiacare.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import java.util.concurrent.TimeUnit
import com.example.lansiacare.utils.Constants

class MedicationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "MEDICATION_TAKEN" -> {
                val medicationId = intent.getStringExtra("medication_id")
                medicationId?.let {
                    handleMedicationTaken(context, it)
                }
            }
            "SNOOZE_REMINDER" -> {
                val medicationId = intent.getStringExtra("medication_id")
                medicationId?.let {
                    handleSnoozeReminder(context, it)
                }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule all medication reminders after device reboot
                rescheduleAllReminders(context)
            }
        }
    }

    private fun handleMedicationTaken(context: Context, medicationId: String) {
        // Mark medication as taken for today
        // This could update a database table to track medication adherence

        // Cancel the current notification
        val notificationManager = androidx.core.app.NotificationManagerCompat.from(context)
        notificationManager.cancel(medicationId.hashCode())
    }

    private fun handleSnoozeReminder(context: Context, medicationId: String) {
        // Cancel current notification
        val notificationManager = androidx.core.app.NotificationManagerCompat.from(context)
        notificationManager.cancel(medicationId.hashCode())

        // Schedule a new reminder after 15 minutes
        val workData = Data.Builder()
            .putString(Constants.KEY_MEDICATION_ID, medicationId)
            .build()

        val snoozeWork = OneTimeWorkRequestBuilder<MedicationReminderWorker>()
            .setInputData(workData)
            .setInitialDelay(15, TimeUnit.MINUTES)
            .addTag("medication_snooze_$medicationId")
            .build()

        WorkManager.getInstance(context).enqueue(snoozeWork)
    }

    private fun rescheduleAllReminders(context: Context) {
        // This would reschedule all active medication reminders
        // Implementation would load all active medications from database
        // and reschedule their reminders using WorkManager
    }
}