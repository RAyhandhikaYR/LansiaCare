package com.example.lansiacare.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.lansiacare.data.database.dao.MedicationDao
import com.example.lansiacare.data.entities.Medication

class MedicationRepository(private val medicationDao: MedicationDao) {

    fun getActiveMedications(userEmail: String): LiveData<List<Medication>> {
        return medicationDao.getActiveMedications(userEmail)
    }

    fun getAllMedications(userEmail: String): LiveData<List<Medication>> {
        return medicationDao.getAllMedications(userEmail)
    }

    suspend fun insertMedication(medication: Medication) {
        withContext(Dispatchers.IO) {
            medicationDao.insertMedication(medication)
        }
    }

    suspend fun updateMedication(medication: Medication) {
        withContext(Dispatchers.IO) {
            medicationDao.updateMedication(medication)
        }
    }

    suspend fun deleteMedication(medication: Medication) {
        withContext(Dispatchers.IO) {
            medicationDao.deleteMedication(medication)
        }
    }

    suspend fun getMedicationById(id: String): Medication? {
        return withContext(Dispatchers.IO) {
            medicationDao.getMedicationById(id)
        }
    }
}