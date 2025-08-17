package com.example.lansiacare.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.lansiacare.MainActivity
import com.example.lansiacare.R
import com.example.lansiacare.data.entities.Medication


class NotificationHelper(private val context: Context) {

    companion object {
        const val MEDICATION_CHANNEL_ID = "medication_reminders"
        const val EMERGENCY_CHANNEL_ID = "emergency_notifications"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Medication reminder channel
            val medicationChannel = NotificationChannel(
                MEDICATION_CHANNEL_ID,
                "Pengingat Obat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk mengingatkan waktu minum obat"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            // Emergency notification channel
            val emergencyChannel = NotificationChannel(
                EMERGENCY_CHANNEL_ID,
                "Notifikasi Darurat",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk situasi darurat"
                enableVibration(true)
                setVibrationPattern(longArrayOf(0, 1000, 500, 1000))
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannels(listOf(medicationChannel, emergencyChannel))
        }
    }

    fun createMedicationReminderNotification(medication: Medication): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, MEDICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle("Waktunya Minum Obat")
            .setContentText("${medication.name} - ${medication.dosage}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Waktunya minum ${medication.name} dengan dosis ${medication.dosage}. ${medication.notes}"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_check,
                "Sudah Diminum",
                createMedicationTakenPendingIntent(medication.id)
            )
            .addAction(
                R.drawable.ic_time,
                "Ingatkan 15 Menit Lagi",
                createSnoozeReminderPendingIntent(medication.id)
            )
            .build()
    }

    private fun createMedicationTakenPendingIntent(medicationId: String): PendingIntent {
        val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
            action = "MEDICATION_TAKEN"
            putExtra("medication_id", medicationId)
        }
        return PendingIntent.getBroadcast(
            context, medicationId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createSnoozeReminderPendingIntent(medicationId: String): PendingIntent {
        val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
            action = "SNOOZE_REMINDER"
            putExtra("medication_id", medicationId)
        }
        return PendingIntent.getBroadcast(
            context, medicationId.hashCode() + 1000, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}