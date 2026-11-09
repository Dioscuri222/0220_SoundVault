package com.example.proj_soundvault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proj_soundvault.data.model.AudioBrand
import com.example.proj_soundvault.data.repository.BrandRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BrandUiState {
    object Loading : BrandUiState
    data class Success(val brands: List<AudioBrand>) : BrandUiState
    data class Error(val message: String) : BrandUiState
}

class BrandViewModel(
    private val repository: BrandRepository = BrandRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<BrandUiState>(BrandUiState.Loading)
    val uiState: StateFlow<BrandUiState> = _uiState.asStateFlow()

    init {
        fetchBrands()
    }

    fun fetchBrands() {
        viewModelScope.launch {
            _uiState.value = BrandUiState.Loading
            try {
                val brands = repository.getBrands()
                _uiState.value = BrandUiState.Success(brands)
            } catch (e: Exception) {
                _uiState.value = BrandUiState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    fun addBrand(name: String, country: String, desc: String) {
        viewModelScope.launch {
            repository.addBrand(AudioBrand(brandName = name, countryOrigin = country, description = desc))
            fetchBrands()
        }
    }

    fun deleteBrand(id: Int) {
        viewModelScope.launch {
            repository.deleteBrand(id)
            fetchBrands()
        }
    }
}