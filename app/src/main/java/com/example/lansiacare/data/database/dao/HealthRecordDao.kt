package com.example.lansiacare.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.lansiacare.data.entities.HealthRecord

@Dao
interface HealthRecordDao {
    @Query("SELECT * FROM health_records WHERE userEmail = :userEmail ORDER BY recordDate DESC")
    fun getHealthRecords(userEmail: String): LiveData<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE userEmail = :userEmail AND recordType = :type ORDER BY recordDate DESC LIMIT 7")
    fun getRecentRecordsByType(userEmail: String, type: String): LiveData<List<HealthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecord)

    @Update
    suspend fun updateHealthRecord(record: HealthRecord)

    @Delete
    suspend fun deleteHealthRecord(record: HealthRecord)
}
