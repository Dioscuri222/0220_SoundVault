package com.example.proj_soundvault.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proj_soundvault.data.local.SessionManager
import com.example.proj_soundvault.data.local.UserSession
import com.example.proj_soundvault.data.remote.RetrofitClient
import com.example.proj_soundvault.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val sessionManager = SessionManager(application)
    private val repository = AuthRepository(
        apiService = RetrofitClient.getApiService(application),
        sessionManager = sessionManager
    )

    val sessionState: StateFlow<UserSession> = sessionManager.sessionFlow

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Email dan kata sandi wajib diisi")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = repository.login(email.trim(), password)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState(successMessage = "Selamat datang, ${it.namaLengkap}") },
                onFailure = { AuthUiState(errorMessage = it.message) }
            )
        }
    }

    fun register(
        namaLengkap: String,
        email: String,
        password: String,
        noTelepon: String,
        onRegistered: () -> Unit
    ) {
        if (namaLengkap.isBlank() || email.isBlank() || password.isBlank() || noTelepon.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Seluruh kolom pendaftaran wajib diisi")
            return
        }
        if (password.length < 8) {
            _uiState.value = AuthUiState(errorMessage = "Kata sandi minimal 8 karakter")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = repository.register(namaLengkap, email, password, noTelepon)
            result.fold(
                onSuccess = { msg ->
                    _uiState.value = AuthUiState(successMessage = msg)
                    onRegistered()
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState(errorMessage = err.message)
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}