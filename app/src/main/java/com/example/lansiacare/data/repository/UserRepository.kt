package com.example.lansiacare.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.lansiacare.data.database.dao.UserDao
import com.example.lansiacare.data.entities.User

class UserRepository(private val userDao: UserDao) {

    suspend fun loginUser(email: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.loginUser(email, password)
        }
    }

    suspend fun registerUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Check if user already exists
                val existingUser = userDao.getUserByEmail(user.email)
                if (existingUser != null) {
                    return@withContext false
                }
                userDao.insertUser(user)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    // Get user by email
    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }

    // Update user profile
    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }
}