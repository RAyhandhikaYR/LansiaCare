package com.example.lansiacare.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.data.entities.Medication
import com.example.lansiacare.utils.DateUtils

class MedicationViewModel(
    private val medicationRepository: MedicationRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val userEmail = userPreferences.getUserEmail() ?: ""

    val activeMedications = medicationRepository.getActiveMedications(userEmail)
    val allMedications = medicationRepository.getAllMedications(userEmail)

    private val _addResult = MutableLiveData<Result<Boolean>>()
    val addResult: LiveData<Result<Boolean>> = _addResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Tambah obat baru
    fun addMedication(
        name: String,
        dosage: String,
        frequency: String,
        timeSchedule: List<String>,
        startDate: Date,
        endDate: Date?,
        notes: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val medication = Medication(
                    id = DateUtils.generateId(),
                    userEmail = userEmail,
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    timeSchedule = timeSchedule,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes
                )

                medicationRepository.insertMedication(medication)
                _addResult.value = Result.success(true)

                // Schedule notification (akan kita buat nanti)
                scheduleMedicationNotification(medication)

            } catch (e: Exception) {
                _addResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update obat
    fun updateMedication(medication: Medication) {
        viewModelScope.launch {
            try {
                medicationRepository.updateMedication(medication)
                scheduleMedicationNotification(medication)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Hapus obat
    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            try {
                medicationRepository.deleteMedication(medication)
                cancelMedicationNotification(medication.id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Schedule notification (placeholder - akan diimplementasi nanti)
    private fun scheduleMedicationNotification(medication: Medication) {
        // Implementation untuk WorkManager notification
    }

    // Cancel notification (placeholder)
    private fun cancelMedicationNotification(medicationId: String) {
        // Implementation untuk cancel WorkManager
    }
}