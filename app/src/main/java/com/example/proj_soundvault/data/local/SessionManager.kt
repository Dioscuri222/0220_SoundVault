package com.example.proj_soundvault.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.proj_soundvault.data.model.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val isLoggedIn: Boolean = false,
    val accessToken: String = "",
    val refreshToken: String = "",
    val idUser: Int = 0,
    val namaLengkap: String = "",
    val email: String = "",
    val role: String = "USER"
) {
    val isAdmin: Boolean get() = role == "ADMIN"
}

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("soundvault_session", Context.MODE_PRIVATE)

    private val _sessionFlow = MutableStateFlow(loadSessionFromPrefs())
    val sessionFlow: StateFlow<UserSession> = _sessionFlow.asStateFlow()

    private fun loadSessionFromPrefs(): UserSession {
        val access = prefs.getString(KEY_ACCESS_TOKEN, null)
        val refresh = prefs.getString(KEY_REFRESH_TOKEN, null)
        return if (!access.isNullOrEmpty() && !refresh.isNullOrEmpty()) {
            UserSession(
                isLoggedIn = true,
                accessToken = access,
                refreshToken = refresh,
                idUser = prefs.getInt(KEY_USER_ID, 0),
                namaLengkap = prefs.getString(KEY_USER_NAME, "") ?: "",
                email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
                role = prefs.getString(KEY_USER_ROLE, "USER") ?: "USER"
            )
        } else {
            UserSession(isLoggedIn = false)
        }
    }

    fun saveSession(accessToken: String, refreshToken: String, user: UserDto) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putInt(KEY_USER_ID, user.idUser)
            .putString(KEY_USER_NAME, user.namaLengkap)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_ROLE, user.role)
            .apply()
        _sessionFlow.value = loadSessionFromPrefs()
    }

    fun updateAccessToken(newAccessToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, newAccessToken).apply()
        _sessionFlow.value = loadSessionFromPrefs()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun clearSession() {
        prefs.edit().clear().apply()
        _sessionFlow.value = UserSession(isLoggedIn = false)
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "id_user"
        private const val KEY_USER_NAME = "nama_lengkap"
        private const val KEY_USER_EMAIL = "email"
        private const val KEY_USER_ROLE = "role"
    }
}