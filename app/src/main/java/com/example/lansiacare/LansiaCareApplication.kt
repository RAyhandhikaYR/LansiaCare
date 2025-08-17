package com.example.lansiacare

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.database.AppDatabase
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.repository.HealthRecordRepository



class LansiaCareApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val userRepository by lazy { UserRepository(database.userDao()) }
    val medicationRepository by lazy { MedicationRepository(database.medicationDao()) }
    val healthRecordRepository by lazy { HealthRecordRepository(database.healthRecordDao()) }
    val medicalNoteRepository by lazy { MedicalNoteRepository(database.medicalNoteDao()) }


    val userPreferences by lazy { UserPreferences(this) }

    override fun onCreate() {
        super.onCreate()

    }

    private fun setupMedicationReminders() {
        // Configure WorkManager for medication reminders
        // This will be implemented when notification system is added
    }
}