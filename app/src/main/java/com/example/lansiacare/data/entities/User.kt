package com.example.lansiacare.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val password: String, //
    val phone: String,
    val emergencyContact: String,
    val emergencyPhone: String,
    val dateOfBirth: Date,
    val createdAt: Date = Date()
)


