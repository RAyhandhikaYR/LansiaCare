package com.example.lansiacare.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey val id: String,
    val userEmail: String,
    val recordType: String, // "blood_pressure", "heart_rate", "blood_sugar", "steps"
    val value: String, // "120/80", "75", "95", "5000"
    val unit: String, // "mmHg", "bpm", "mg/dL", "steps"
    val recordDate: Date,
    val notes: String = "",
    val createdAt: Date = Date()
)