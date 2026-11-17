package com.example.proj_soundvault.data.remote

import android.content.Context
import com.example.proj_soundvault.data.local.SessionManager
import com.example.proj_soundvault.data.model.RefreshRequest
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 10.0.2.2 mengarah ke localhost laptop dari Android Emulator.
    // Jika memakai HP fisik via USB/Wi-Fi, ganti dengan IPv4 laptop (misal: "http://192.168.1.10:3000/api/")
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    @Volatile
    private var apiServiceInstance: ApiService? = null

    fun getApiService(context: Context): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            val sessionManager = SessionManager(context.applicationContext)

            // Client khusus refresh token tanpa authenticator agar tidak terjadi infinite loop
            val plainRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val refreshApi = plainRetrofit.create(ApiService::class.java)

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor(sessionManager))
                .authenticator(TokenAuthenticator(sessionManager, refreshApi))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(ApiService::class.java).also { apiServiceInstance = it }
        }
    }

    private class AuthInterceptor(
        private val sessionManager: SessionManager
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val path = original.url().encodedPath()

            if (path.contains("auth/login") || path.contains("auth/register") || path.contains("auth/refresh")) {
                return chain.proceed(original)
            }

            val token = sessionManager.getAccessToken()
            val requestBuilder = original.newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            return chain.proceed(requestBuilder.build())
        }
    }

    private class TokenAuthenticator(
        private val sessionManager: SessionManager,
        private val refreshApi: ApiService
    ) : Authenticator {
        override fun authenticate(route: Route?, response: Response): Request? {
            val path = response.request().url().encodedPath()
            if (path.contains("auth/login") || path.contains("auth/refresh") || responseCount(response) >= 2) {
                return null
            }

            synchronized(this) {
                val currentAccessToken = sessionManager.getAccessToken()
                val requestToken = response.request().header("Authorization")?.removePrefix("Bearer ")

                if (!currentAccessToken.isNullOrEmpty() && currentAccessToken != requestToken) {
                    return response.request().newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                val refreshToken = sessionManager.getRefreshToken() ?: run {
                    sessionManager.clearSession()
                    return null
                }

                return try {
                    val refreshRes = refreshApi.refreshToken(RefreshRequest(refreshToken)).execute()
                    val newAccessToken = refreshRes.body()?.data?.accessToken
                    if (refreshRes.isSuccessful && !newAccessToken.isNullOrEmpty()) {
                        sessionManager.updateAccessToken(newAccessToken)
                        response.request().newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    } else {
                        sessionManager.clearSession()
                        null
                    }
                } catch (e: Exception) {
                    sessionManager.clearSession()
                    null
                }
            }
        }

        private fun responseCount(response: Response): Int {
            var count = 1
            var prior = response.priorResponse()
            while (prior != null) {
                count++
                prior = prior.priorResponse()
            }
            return count
        }
    }
}