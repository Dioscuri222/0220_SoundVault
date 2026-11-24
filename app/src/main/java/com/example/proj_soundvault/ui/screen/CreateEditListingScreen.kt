package com.example.proj_soundvault.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proj_soundvault.data.model.AudioBrand
import com.example.proj_soundvault.data.model.ListingConstants
import com.example.proj_soundvault.ui.viewmodel.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditListingScreen(
    editItemId: Int? = null,
    brands: List<AudioBrand>,
    listingViewModel: ListingViewModel,
    onNavigateBack: () -> Unit
) {
    val isEditMode = editItemId != null
    val selectedListing by listingViewModel.selectedListing.collectAsState()
    val isSubmitting by listingViewModel.isSubmitting.collectAsState()
    val errorMessage by listingViewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var selectedBrandId by remember { mutableStateOf<Int?>(null) }
    var itemName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("IEM") }
    var driverType by remember { mutableStateOf("") }
    var connectorType by remember { mutableStateOf("") }
    var soundSignature by remember { mutableStateOf("") }
    var itemCondition by remember { mutableStateOf("USED") }
    var priceText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    var brandExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    // Muat data awal jika dalam mode Edit
    LaunchedEffect(editItemId) {
        if (editItemId != null && selectedListing?.idItem != editItemId) {
            listingViewModel.loadListingDetail(editItemId)
        }
    }

    LaunchedEffect(selectedListing, isEditMode) {
        if (isEditMode && selectedListing != null && selectedListing?.idItem == editItemId) {
            val data = selectedListing!!
            selectedBrandId = data.brandId
            itemName = data.itemName
            category = data.category
            driverType = data.driverType
            connectorType = data.connectorType
            soundSignature = data.soundSignature
            itemCondition = data.itemCondition
            priceText = data.priceAsDouble().toLong().toString()
            description = data.description ?: ""
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            listingViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) "Edit Listing Audio" else "Pasang Listing Baru")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            validationError?.let { msg ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Dropdown Merek Audio (AudioBrand)
            val currentBrandLabel = brands.find { it.idBrand == selectedBrandId }?.brandName
                ?: "Pilih Merek Audio"
            ExposedDropdownMenuBox(
                expanded = brandExpanded,
                onExpandedChange = { brandExpanded = !brandExpanded }
            ) {
                OutlinedTextField(
                    value = currentBrandLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Merek Audio *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = brandExpanded,
                    onDismissRequest = { brandExpanded = false }
                ) {
                    brands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand.brandName) },
                            onClick = {
                                selectedBrandId = brand.idBrand
                                brandExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Nama Seri / Model Produk *") },
                placeholder = { Text("Contoh: Tangzu Xuan NV") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Dropdown Kategori
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    ListingConstants.CATEGORIES.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = driverType,
                onValueChange = { driverType = it },
                label = { Text("Jenis Driver *") },
                placeholder = { Text("Contoh: 1DD, Hybrid 1DD+2BA, Planar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = connectorType,
                onValueChange = { connectorType = it },
                label = { Text("Tipe Konektor *") },
                placeholder = { Text("Contoh: 2-Pin 0.78mm, MMCX, 4.4mm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = soundSignature,
                onValueChange = { soundSignature = it },
                label = { Text("Sound Signature *") },
                placeholder = { Text("Contoh: Harman, Warm, Neutral, V-Shaped") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Kondisi Barang (NEW / USED)
            Text("Kondisi Barang *", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ListingConstants.CONDITIONS.forEach { cond ->
                    FilterChip(
                        selected = itemCondition == cond,
                        onClick = { itemCondition = cond },
                        label = { Text(if (cond == "NEW") "Baru (NEW)" else "Bekas (USED)") }
                    )
                }
            }

            OutlinedTextField(
                value = priceText,
                onValueChange = { input -> priceText = input.filter { it.isDigit() } },
                label = { Text("Harga (IDR) *") },
                placeholder = { Text("Contoh: 1150000") },
                prefix = { Text("Rp ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi & Kelengkapan (Opsional)") },
                placeholder = { Text("Kelengkapan box, kabel, eartips, dan kondisi fisik...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
                    if (selectedBrandId == null ||
                        itemName.isBlank() ||
                        driverType.isBlank() ||
                        connectorType.isBlank() ||
                        soundSignature.isBlank() ||
                        parsedPrice <= 0.0
                    ) {
                        validationError = "Input formulir tidak valid. Pastikan seluruh kolom wajib terisi dan harga > 0."
                        return@Button
                    }

                    validationError = null
                    if (isEditMode && editItemId != null) {
                        listingViewModel.updateListing(
                            id = editItemId,
                            brandId = selectedBrandId!!,
                            itemName = itemName,
                            category = category,
                            driverType = driverType,
                            connectorType = connectorType,
                            soundSignature = soundSignature,
                            itemCondition = itemCondition,
                            price = parsedPrice,
                            description = description,
                            onSuccess = onNavigateBack
                        )
                    } else {
                        listingViewModel.createListing(
                            brandId = selectedBrandId!!,
                            itemName = itemName,
                            category = category,
                            driverType = driverType,
                            connectorType = connectorType,
                            soundSignature = soundSignature,
                            itemCondition = itemCondition,
                            price = parsedPrice,
                            description = description,
                            onSuccess = onNavigateBack
                        )
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (isEditMode) "Simpan Perubahan" else "Pasang Listing",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}