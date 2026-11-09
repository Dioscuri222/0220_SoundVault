package com.example.proj_soundvault.data.repository

import com.example.proj_soundvault.data.model.AudioBrand

class BrandRepository {
    // Mock data awal untuk testing UI
    private val mockBrands = mutableListOf(
        AudioBrand(1, "Tangzu", "China", "Terkenal dengan IEM Wan'er S.G dan Tang Sancai eartips."),
        AudioBrand(2, "Moondrop", "China", "IEM populer dengan tuning profil Harman Target."),
        AudioBrand(3, "FiiO", "China", "Spesialis DAC/AMP portable dan audio gear berkinerja tinggi.")
    )

    suspend fun getBrands(): List<AudioBrand> {
        // Nanti jika API siap: return RetrofitClient.instance.getBrands()
        return mockBrands
    }

    suspend fun addBrand(brand: AudioBrand) {
        val newId = (mockBrands.maxOfOrNull { it.id } ?: 0) + 1
        mockBrands.add(brand.copy(id = newId))
    }

    suspend fun deleteBrand(id: Int) {
        mockBrands.removeAll { it.id == id }
    }
}