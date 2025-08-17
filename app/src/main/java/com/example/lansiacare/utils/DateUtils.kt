package com.example.lansiacare.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun formatTime(date: Date): String {
        return timeFormat.format(date)
    }

    fun formatDateTime(date: Date): String {
        return dateTimeFormat.format(date)
    }

    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun parseTime(timeString: String): Date? {
        return try {
            timeFormat.parse(timeString)
        } catch (e: Exception) {
            null
        }
    }

    // Generate ID unik
    fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}
