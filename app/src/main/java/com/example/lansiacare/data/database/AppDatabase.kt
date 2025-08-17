package com.example.lansiacare.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

import com.example.lansiacare.data.entities.User
import com.example.lansiacare.data.entities.Medication
import com.example.lansiacare.data.entities.HealthRecord
import com.example.lansiacare.data.entities.MedicalNote
import com.example.lansiacare.data.database.dao.UserDao
import com.example.lansiacare.data.database.dao.MedicationDao
import com.example.lansiacare.data.database.dao.HealthRecordDao
import com.example.lansiacare.data.database.dao.MedicalNoteDao
import com.example.lansiacare.data.database.Converters

@Database(
    entities = [User::class, Medication::class, HealthRecord::class, MedicalNote::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun medicalNoteDao(): MedicalNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lansia_care_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
