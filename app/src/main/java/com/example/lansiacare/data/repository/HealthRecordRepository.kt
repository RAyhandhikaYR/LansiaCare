package com.example.lansiacare.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.lansiacare.data.database.dao.HealthRecordDao
import com.example.lansiacare.data.entities.HealthRecord

class HealthRecordRepository(private val healthRecordDao: HealthRecordDao) {

    fun getHealthRecords(userEmail: String): LiveData<List<HealthRecord>> {
        return healthRecordDao.getHealthRecords(userEmail)
    }

    fun getRecentRecordsByType(userEmail: String, type: String): LiveData<List<HealthRecord>> {
        return healthRecordDao.getRecentRecordsByType(userEmail, type)
    }

    suspend fun insertHealthRecord(record: HealthRecord) {
        withContext(Dispatchers.IO) {
            healthRecordDao.insertHealthRecord(record)
        }
    }

    suspend fun updateHealthRecord(record: HealthRecord) {
        withContext(Dispatchers.IO) {
            healthRecordDao.updateHealthRecord(record)
        }
    }

    suspend fun deleteHealthRecord(record: HealthRecord) {
        withContext(Dispatchers.IO) {
            healthRecordDao.deleteHealthRecord(record)
        }
    }
}