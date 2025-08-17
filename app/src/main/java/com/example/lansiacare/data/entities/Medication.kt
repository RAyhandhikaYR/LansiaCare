package com.example.lansiacare.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey val id: String,
    val userEmail: String,
    val name: String,
    val dosage: String,
    val frequency: String, // "daily", "twice_daily", "weekly"
    val timeSchedule: List<String>, // ["08:00", "20:00"]
    val startDate: Date,
    val endDate: Date?,
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Date = Date()
)