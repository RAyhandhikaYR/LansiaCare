package com.example.lansiacare.ui.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date
import com.example.lansiacare.utils.UserPreferences
import com.example.lansiacare.data.repository.UserRepository
import com.example.lansiacare.data.entities.User

class EmergencyViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userEmail = userPreferences.getUserEmail()
                if (userEmail != null) {
                    val user = userRepository.getUserByEmail(userEmail)
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                _currentUser.value = user
                // Update preferences if name changed
                userPreferences.saveUserSession(user.email, user.name)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun logEmergencyCall(phoneNumber: String) {
        viewModelScope.launch {
            try {
                // In real app, you might want to log emergency calls
                // to a separate table for tracking/analysis
                val logEntry = "Emergency call to $phoneNumber at ${Date()}"
                // Save to emergency log table or send to analytics
            } catch (e: Exception) {
                // Handle error silently (don't interrupt emergency flow)
            }
        }
    }
}