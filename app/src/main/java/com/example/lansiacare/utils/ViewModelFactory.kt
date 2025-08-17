package com.example.lansiacare.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lansiacare.data.repository.HealthRecordRepository
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.ui.auth.AuthViewModel
import com.example.lansiacare.ui.dashboard.DashboardViewModel
import com.example.lansiacare.ui.medication.MedicationViewModel
import com.example.lansiacare.ui.medicalnotes.MedicalNotesViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val medicationRepository: MedicationRepository,
    private val healthRecordRepository: HealthRecordRepository,
    private val medicalNoteRepository: MedicalNoteRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> {
                AuthViewModel(userRepository, userPreferences) as T
            }
            DashboardViewModel::class.java -> {
                DashboardViewModel(healthRecordRepository, medicationRepository, userPreferences) as T
            }
            MedicationViewModel::class.java -> {
                MedicationViewModel(medicationRepository, userPreferences) as T
            }
            MedicalNotesViewModel::class.java -> {
                MedicalNotesViewModel(medicalNoteRepository, userPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}