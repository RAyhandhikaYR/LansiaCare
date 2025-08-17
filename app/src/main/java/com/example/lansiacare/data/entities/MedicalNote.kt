package com.example.lansiacare.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "medical_notes")
data class MedicalNote(
    @PrimaryKey val id: String,
    val userEmail: String,
    val title: String,
    val description: String,
    val doctorName: String = "",
    val hospital: String = "",
    val visitDate: Date,
    val attachments: List<String> = emptyList(), // Path file jika ada
    val createdAt: Date = Date()
)
