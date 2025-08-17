package com.example.lansiacare.utils

object Constants {

    // Health Record Types
    const val RECORD_TYPE_BLOOD_PRESSURE = "blood_pressure"
    const val RECORD_TYPE_HEART_RATE = "heart_rate"
    const val RECORD_TYPE_BLOOD_SUGAR = "blood_sugar"
    const val RECORD_TYPE_STEPS = "steps"
    const val RECORD_TYPE_WEIGHT = "weight"

    // Medication Frequency
    const val FREQUENCY_DAILY = "daily"
    const val FREQUENCY_TWICE_DAILY = "twice_daily"
    const val FREQUENCY_THREE_TIMES = "three_times"
    const val FREQUENCY_WEEKLY = "weekly"

    // Emergency Contact
    const val EMERGENCY_AMBULANCE = "119"
    const val EMERGENCY_POLICE = "110"

    // Notification IDs
    const val NOTIFICATION_ID_MEDICATION = 1001
    const val NOTIFICATION_CHANNEL_MEDICATION = "medication_channel"

    // Request Codes
    const val REQUEST_CODE_EMERGENCY_CALL = 2001

    const val KEY_MEDICATION_ID = "key_medication_id"
}