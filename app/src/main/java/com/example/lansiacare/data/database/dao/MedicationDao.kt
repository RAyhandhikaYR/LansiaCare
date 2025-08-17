package com.example.lansiacare.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.lansiacare.data.entities.Medication

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE userEmail = :userEmail AND isActive = 1")
    fun getActiveMedications(userEmail: String): LiveData<List<Medication>>

    @Query("SELECT * FROM medications WHERE userEmail = :userEmail")
    fun getAllMedications(userEmail: String): LiveData<List<Medication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication)

    @Update
    suspend fun updateMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: String): Medication?
}
