package com.example.lansiacare.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.lansiacare.data.entities.MedicalNote

@Dao
interface MedicalNoteDao {
    @Query("SELECT * FROM medical_notes WHERE userEmail = :userEmail ORDER BY visitDate DESC")
    fun getMedicalNotes(userEmail: String): LiveData<List<MedicalNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalNote(note: MedicalNote)

    @Update
    suspend fun updateMedicalNote(note: MedicalNote)

    @Delete
    suspend fun deleteMedicalNote(note: MedicalNote)

    @Query("SELECT * FROM medical_notes WHERE id = :id")
    suspend fun getMedicalNoteById(id: String): MedicalNote?
}