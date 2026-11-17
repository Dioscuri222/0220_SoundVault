package com.example.proj_soundvault.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proj_soundvault.data.model.AudioBrand
import com.example.proj_soundvault.data.remote.RetrofitClient
import com.example.proj_soundvault.data.repository.BrandRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BrandUiState(
    val brands: List<AudioBrand> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null
)

class BrandViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BrandRepository(RetrofitClient.getApiService(application))

    private val _uiState = MutableStateFlow(BrandUiState())
    val uiState: StateFlow<BrandUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadBrands()
    }

    fun loadBrands(query: String = _uiState.value.searchQuery) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.getBrands(query)
            _uiState.value = result.fold(
                onSuccess = { list ->
                    _uiState.value.copy(brands = list, isLoading = false, errorMessage = null)
                },
                onFailure = { err ->
                    _uiState.value.copy(isLoading = false, errorMessage = err.message)
                }
            )
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce pencarian agar hemat request API
            loadBrands(newQuery)
        }
    }

    fun createBrand(brandName: String, countryOrigin: String, description: String) {
        if (brandName.isBlank() || countryOrigin.isBlank()) {
            _uiState.value = _uiState.value.copy(snackbarMessage = "Nama merek dan negara asal wajib diisi")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.createBrand(brandName, countryOrigin, description)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = "Merek '${it.brandName}' berhasil ditambahkan"
                    )
                    loadBrands()
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = err.message
                    )
                }
            )
        }
    }

    fun updateBrand(id: Int, brandName: String, countryOrigin: String, description: String) {
        if (brandName.isBlank() || countryOrigin.isBlank()) {
            _uiState.value = _uiState.value.copy(snackbarMessage = "Nama merek dan negara asal wajib diisi")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.updateBrand(id, brandName, countryOrigin, description)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = "Merek '${it.brandName}' berhasil diperbarui"
                    )
                    loadBrands()
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = err.message
                    )
                }
            )
        }
    }

    fun deleteBrand(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.deleteBrand(id)
            result.fold(
                onSuccess = { msg ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = msg
                    )
                    loadBrands()
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        snackbarMessage = err.message
                    )
                }
            )
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}