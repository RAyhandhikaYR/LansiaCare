package com.example.lansiacare.ui.medicalnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.repository.MedicalNoteRepository
import com.example.lansiacare.data.entities.MedicalNote
import com.example.lansiacare.utils.DateUtils

class MedicalNotesViewModel(
    private val medicalNoteRepository: MedicalNoteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val userEmail = userPreferences.getUserEmail() ?: ""

    val medicalNotes = medicalNoteRepository.getMedicalNotes(userEmail)

    private val _addResult = MutableLiveData<Result<Boolean>>()
    val addResult: LiveData<Result<Boolean>> = _addResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Tambah catatan medis baru
    fun addMedicalNote(
        title: String,
        description: String,
        doctorName: String = "",
        hospital: String = "",
        visitDate: Date
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val note = MedicalNote(
                    id = DateUtils.generateId(),
                    userEmail = userEmail,
                    title = title,
                    description = description,
                    doctorName = doctorName,
                    hospital = hospital,
                    visitDate = visitDate
                )

                medicalNoteRepository.insertMedicalNote(note)
                _addResult.value = Result.success(true)
            } catch (e: Exception) {
                _addResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update catatan medis
    fun updateMedicalNote(note: MedicalNote) {
        viewModelScope.launch {
            try {
                medicalNoteRepository.updateMedicalNote(note)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Hapus catatan medis
    fun deleteMedicalNote(note: MedicalNote) {
        viewModelScope.launch {
            try {
                medicalNoteRepository.deleteMedicalNote(note)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}