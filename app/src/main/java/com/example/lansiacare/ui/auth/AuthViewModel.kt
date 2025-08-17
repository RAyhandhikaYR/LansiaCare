package com.example.lansiacare.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.data.entities.User

class AuthViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Login function
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.loginUser(email, password)
                if (user != null) {
                    userPreferences.saveUserSession(user.email, user.name)
                    _loginResult.value = Result.success(user)
                } else {
                    _loginResult.value = Result.failure(Exception("Email atau password salah"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Register function
    fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        emergencyContact: String,
        emergencyPhone: String,
        dateOfBirth: Date
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = User(
                    email = email,
                    name = name,
                    password = password,
                    phone = phone,
                    emergencyContact = emergencyContact,
                    emergencyPhone = emergencyPhone,
                    dateOfBirth = dateOfBirth
                )

                val success = userRepository.registerUser(user)
                if (success) {
                    userPreferences.saveUserSession(user.email, user.name)
                    _registerResult.value = Result.success(true)
                } else {
                    _registerResult.value = Result.failure(Exception("Email sudah terdaftar"))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Logout function
    fun logout() {
        userPreferences.clearSession()
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return userPreferences.isLoggedIn()
    }

    // Get current user email
    fun getCurrentUserEmail(): String? {
        return userPreferences.getUserEmail()
    }
}
