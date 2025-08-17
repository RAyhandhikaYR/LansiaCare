package com.example.lansiacare.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.repository.HealthRecordRepository
import com.example.lansiacare.data.repository.MedicationRepository
import com.example.lansiacare.utils.Constants
import com.example.lansiacare.data.entities.HealthRecord
import com.example.lansiacare.utils.DateUtils

class DashboardViewModel(
    private val healthRecordRepository: HealthRecordRepository,
    private val medicationRepository: MedicationRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val userEmail = userPreferences.getUserEmail() ?: ""

    // LiveData untuk data kesehatan terbaru
    val bloodPressureRecords = healthRecordRepository.getRecentRecordsByType(userEmail, Constants.RECORD_TYPE_BLOOD_PRESSURE)
    val heartRateRecords = healthRecordRepository.getRecentRecordsByType(userEmail, Constants.RECORD_TYPE_HEART_RATE)
    val bloodSugarRecords = healthRecordRepository.getRecentRecordsByType(userEmail, Constants.RECORD_TYPE_BLOOD_SUGAR)
    val stepsRecords = healthRecordRepository.getRecentRecordsByType(userEmail, Constants.RECORD_TYPE_STEPS)

    // LiveData untuk obat aktif
    val activeMedications = medicationRepository.getActiveMedications(userEmail)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Function untuk menambah data kesehatan
    fun addHealthRecord(type: String, value: String, unit: String, notes: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val record = HealthRecord(
                    id = DateUtils.generateId(),
                    userEmail = userEmail,
                    recordType = type,
                    value = value,
                    unit = unit,
                    recordDate = Date(),
                    notes = notes
                )
                healthRecordRepository.insertHealthRecord(record)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get user name for greeting
    fun getUserName(): String {
        return userPreferences.getUserName() ?: "User"
    }
}