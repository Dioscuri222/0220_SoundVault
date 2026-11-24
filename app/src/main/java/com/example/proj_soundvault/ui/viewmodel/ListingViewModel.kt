package com.example.proj_soundvault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proj_soundvault.data.model.ListingFilter
import com.example.proj_soundvault.data.model.ListingItem
import com.example.proj_soundvault.data.model.ListingRequest
import com.example.proj_soundvault.data.repository.ListingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListingViewModel(
    private val repository: ListingRepository
) : ViewModel() {

    private val _listings = MutableStateFlow<List<ListingItem>>(emptyList())
    val listings: StateFlow<List<ListingItem>> = _listings.asStateFlow()

    private val _selectedListing = MutableStateFlow<ListingItem?>(null)
    val selectedListing: StateFlow<ListingItem?> = _selectedListing.asStateFlow()

    private val _filterState = MutableStateFlow(ListingFilter())
    val filterState: StateFlow<ListingFilter> = _filterState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadListings()
    }

    fun loadListings() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getListings(_filterState.value)
                .onSuccess { items -> _listings.value = items }
                .onFailure { err -> _errorMessage.value = err.message }
            _isLoading.value = false
        }
    }

    // Pencarian teks dengan debounce 300 ms (REQ-18 & NFR-2)
    fun onSearchQueryChanged(query: String) {
        _filterState.update { it.copy(search = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            loadListings()
        }
    }

    // Filter cepat per kategori chip pada HomeCatalogScreen
    fun onCategorySelected(category: String?) {
        _filterState.update { it.copy(category = category) }
        loadListings()
    }

    // Terapkan kombinasi multi-filter dari ModalBottomSheet
    fun applyMultiFilter(
        category: String?,
        brandId: Int?,
        driverType: String?,
        connectorType: String?,
        soundSignature: String?,
        itemCondition: String?,
        status: String? = null
    ) {
        _filterState.update { current ->
            current.copy(
                category = category,
                brandId = brandId,
                driverType = driverType,
                connectorType = connectorType,
                soundSignature = soundSignature,
                itemCondition = itemCondition,
                status = status
            )
        }
        loadListings()
    }

    fun resetFilters() {
        searchJob?.cancel()
        _filterState.value = ListingFilter()
        loadListings()
    }

    fun loadListingDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getListingById(id)
                .onSuccess { item -> _selectedListing.value = item }
                .onFailure { err -> _errorMessage.value = err.message }
            _isLoading.value = false
        }
    }

    fun createListing(
        brandId: Int,
        itemName: String,
        category: String,
        driverType: String,
        connectorType: String,
        soundSignature: String,
        itemCondition: String,
        price: Double,
        description: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null
            val request = ListingRequest(
                brandId = brandId,
                itemName = itemName.trim(),
                category = category,
                driverType = driverType.trim(),
                connectorType = connectorType.trim(),
                soundSignature = soundSignature.trim(),
                itemCondition = itemCondition,
                price = price,
                description = description?.trim()?.takeIf { it.isNotEmpty() }
            )
            repository.createListing(request)
                .onSuccess {
                    _successMessage.value = "Listing audio berhasil dipasang"
                    loadListings()
                    onSuccess()
                }
                .onFailure { err ->
                    _errorMessage.value = err.message
                }
            _isSubmitting.value = false
        }
    }

    fun updateListing(
        id: Int,
        brandId: Int,
        itemName: String,
        category: String,
        driverType: String,
        connectorType: String,
        soundSignature: String,
        itemCondition: String,
        price: Double,
        description: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null
            val request = ListingRequest(
                brandId = brandId,
                itemName = itemName.trim(),
                category = category,
                driverType = driverType.trim(),
                connectorType = connectorType.trim(),
                soundSignature = soundSignature.trim(),
                itemCondition = itemCondition,
                price = price,
                description = description?.trim()?.takeIf { it.isNotEmpty() }
            )
            repository.updateListing(id, request)
                .onSuccess { updatedItem ->
                    _selectedListing.value = updatedItem
                    _successMessage.value = "Data listing berhasil diperbarui"
                    loadListings()
                    onSuccess()
                }
                .onFailure { err ->
                    _errorMessage.value = err.message
                }
            _isSubmitting.value = false
        }
    }

    fun deleteListing(
        id: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null
            repository.deleteListing(id)
                .onSuccess { msg ->
                    _selectedListing.value = null
                    _successMessage.value = msg
                    loadListings()
                    onSuccess()
                }
                .onFailure { err ->
                    _errorMessage.value = err.message
                }
            _isSubmitting.value = false
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    class Factory(private val repository: ListingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListingViewModel::class.java)) {
                return ListingViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}