package com.example.lansiacare.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data
import kotlinx.coroutines.runBlocking
import com.example.lansiacare.notification.NotificationHelper
import com.example.lansiacare.LansiaCareApplication
import com.example.lansiacare.utils.Constants


class MedicationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val medicationId = inputData.getString(Constants.KEY_MEDICATION_ID) ?: return Result.failure()

        return try {
            val application = applicationContext as LansiaCareApplication
            val medication = runBlocking {
                application.medicationRepository.getMedicationById(medicationId)
            }

            if (medication != null && medication.isActive) {
                val notificationHelper = NotificationHelper(applicationContext)
                val notification = notificationHelper.createMedicationReminderNotification(medication)

                val notificationManager = androidx.core.app.NotificationManagerCompat.from(applicationContext)
                notificationManager.notify(medication.id.hashCode(), notification)

                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}