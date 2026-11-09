package com.example.proj_soundvault.data.remote

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class UserRole { USER, ADMIN }

object MockSessionManager {
    private val _currentRole = MutableStateFlow(UserRole.USER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun switchRole() {
        _currentRole.value = if (_currentRole.value == UserRole.USER) UserRole.ADMIN else UserRole.USER
    }
}