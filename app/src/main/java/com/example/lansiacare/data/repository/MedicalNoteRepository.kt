package com.example.lansiacare.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.lansiacare.data.database.dao.MedicalNoteDao
import com.example.lansiacare.data.entities.MedicalNote

class MedicalNoteRepository(private val medicalNoteDao: MedicalNoteDao) {

    fun getMedicalNotes(userEmail: String): LiveData<List<MedicalNote>> {
        return medicalNoteDao.getMedicalNotes(userEmail)
    }

    suspend fun insertMedicalNote(note: MedicalNote) {
        withContext(Dispatchers.IO) {
            medicalNoteDao.insertMedicalNote(note)
        }
    }

    suspend fun updateMedicalNote(note: MedicalNote) {
        withContext(Dispatchers.IO) {
            medicalNoteDao.updateMedicalNote(note)
        }
    }

    suspend fun deleteMedicalNote(note: MedicalNote) {
        withContext(Dispatchers.IO) {
            medicalNoteDao.deleteMedicalNote(note)
        }
    }

    suspend fun getMedicalNoteById(id: String): MedicalNote? {
        return withContext(Dispatchers.IO) {
            medicalNoteDao.getMedicalNoteById(id)
        }
    }
}