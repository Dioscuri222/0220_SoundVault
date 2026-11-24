package com.example.proj_soundvault.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proj_soundvault.data.model.AudioBrand
import com.example.proj_soundvault.data.model.ListingConstants
import com.example.proj_soundvault.data.model.ListingFilter
import com.example.proj_soundvault.data.model.ListingItem
import com.example.proj_soundvault.ui.viewmodel.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCatalogScreen(
    listingViewModel: ListingViewModel,
    brands: List<AudioBrand>,
    onItemClick: (Int) -> Unit,
    onCreateClick: () -> Unit,
    onWishlistClick: (ListingItem) -> Unit = {}
) {
    val listings by listingViewModel.listings.collectAsState()
    val filterState by listingViewModel.filterState.collectAsState()
    val isLoading by listingViewModel.isLoading.collectAsState()
    val errorMessage by listingViewModel.errorMessage.collectAsState()
    val successMessage by listingViewModel.successMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage, successMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            listingViewModel.clearMessages()
        }
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            listingViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog SoundVault", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { listingViewModel.loadListings() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Muat Ulang")
                    }
                    BadgedBox(
                        badge = {
                            val activeCount = filterState.activeFilterCount()
                            if (activeCount > 0) {
                                Badge { Text(activeCount.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Multi-Filter Spesifikasi")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = "Pasang Listing Baru")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Kolom pencarian dengan debounce 300 ms di ViewModel
            OutlinedTextField(
                value = filterState.search,
                onValueChange = { listingViewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari IEM, DAC, kabel, atau deskripsi...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (filterState.search.isNotEmpty()) {
                        IconButton(onClick = { listingViewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus Pencarian")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Baris chip filter cepat kategori
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterState.category == null,
                    onClick = { listingViewModel.onCategorySelected(null) },
                    label = { Text("Semua") }
                )
                ListingConstants.CATEGORIES.forEach { category ->
                    FilterChip(
                        selected = filterState.category == category,
                        onClick = {
                            val next = if (filterState.category == category) null else category
                            listingViewModel.onCategorySelected(next)
                        },
                        label = {
                            Text(
                                when (category) {
                                    "DAC_AMP" -> "DAC/Amp"
                                    "CABLE" -> "Kabel"
                                    "ACCESSORY" -> "Aksesori"
                                    else -> category
                                }
                            )
                        }
                    )
                }
                if (filterState.hasActiveFilters()) {
                    AssistChip(
                        onClick = { listingViewModel.resetFilters() },
                        label = { Text("Reset Filter") }
                    )
                }
            }

            // Konten utama: Loading / Empty State / Daftar Kartu
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading && listings.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    listings.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Produk audio tidak ditemukan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Coba ubah kata kunci pencarian atau reset filter spesifikasi.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(onClick = { listingViewModel.resetFilters() }) {
                                Text("Coba Lagi / Reset")
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listings, key = { it.idItem }) { item ->
                                ListingItemCard(
                                    item = item,
                                    onClick = { onItemClick(item.idItem) },
                                    onWishlistClick = { onWishlistClick(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        MultiFilterBottomSheet(
            currentFilter = filterState,
            brands = brands,
            onDismiss = { showFilterSheet = false },
            onReset = {
                listingViewModel.resetFilters()
                showFilterSheet = false
            },
            onApply = { category, brandId, driver, connector, signature, condition ->
                listingViewModel.applyMultiFilter(
                    category = category,
                    brandId = brandId,
                    driverType = driver,
                    connectorType = connector,
                    soundSignature = signature,
                    itemCondition = condition
                )
                showFilterSheet = false
            }
        )
    }
}

@Composable
private fun ListingItemCard(
    item: ListingItem,
    onClick: () -> Unit,
    onWishlistClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.brandName ?: "Merek #${item.brandId}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ListingStatusBadge(status = item.status)
                    IconButton(
                        onClick = onWishlistClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Simpan ke Wishlist",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.formattedPrice(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Badge spesifikasi teknis audio
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecTag(item.formattedCategory())
                SpecTag(item.driverType)
                SpecTag(item.connectorType)
                SpecTag(item.soundSignature)
                SpecTag(item.formattedCondition())
            }

            if (!item.sellerName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Penjual: ${item.sellerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ListingStatusBadge(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "AVAILABLE" -> Color(0xFFD1FAE5) to Color(0xFF065F46) // Emerald
        "RESERVED" -> Color(0xFFFEF3C7) to Color(0xFF92400E)  // Amber
        "SOLD" -> Color(0xFFFEE2E2) to Color(0xFF991B1B)      // Muted Red
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SpecTag(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultiFilterBottomSheet(
    currentFilter: ListingFilter,
    brands: List<AudioBrand>,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    onApply: (
        category: String?,
        brandId: Int?,
        driverType: String?,
        connectorType: String?,
        soundSignature: String?,
        itemCondition: String?
    ) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(currentFilter.category) }
    var selectedBrandId by remember { mutableStateOf(currentFilter.brandId) }
    var driverInput by remember { mutableStateOf(currentFilter.driverType ?: "") }
    var connectorInput by remember { mutableStateOf(currentFilter.connectorType ?: "") }
    var signatureInput by remember { mutableStateOf(currentFilter.soundSignature ?: "") }
    var selectedCondition by remember { mutableStateOf(currentFilter.itemCondition) }
    var brandMenuExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filter Spesifikasi Audio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Dropdown Merek Audio
            val selectedBrandName = brands.find { it.idBrand == selectedBrandId }?.brandName ?: "Semua Merek"
            ExposedDropdownMenuBox(
                expanded = brandMenuExpanded,
                onExpandedChange = { brandMenuExpanded = !brandMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedBrandName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Merek Audio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandMenuExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = brandMenuExpanded,
                    onDismissRequest = { brandMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Semua Merek") },
                        onClick = {
                            selectedBrandId = null
                            brandMenuExpanded = false
                        }
                    )
                    brands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand.brandName) },
                            onClick = {
                                selectedBrandId = brand.idBrand
                                brandMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Kondisi Barang
            Text("Kondisi Barang", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedCondition == null,
                    onClick = { selectedCondition = null },
                    label = { Text("Semua") }
                )
                ListingConstants.CONDITIONS.forEach { cond ->
                    FilterChip(
                        selected = selectedCondition == cond,
                        onClick = { selectedCondition = if (selectedCondition == cond) null else cond },
                        label = { Text(if (cond == "NEW") "Baru (NEW)" else "Bekas (USED)") }
                    )
                }
            }

            // Jenis Driver
            OutlinedTextField(
                value = driverInput,
                onValueChange = { driverInput = it },
                label = { Text("Jenis Driver (misal: 1DD, Hybrid, Planar)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Tipe Konektor
            OutlinedTextField(
                value = connectorInput,
                onValueChange = { connectorInput = it },
                label = { Text("Tipe Konektor (misal: 2-Pin 0.78mm, 4.4mm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Sound Signature
            OutlinedTextField(
                value = signatureInput,
                onValueChange = { signatureInput = it },
                label = { Text("Sound Signature (misal: Harman, Warm, Neutral)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = {
                        onApply(
                            selectedCategory,
                            selectedBrandId,
                            driverInput.trim().takeIf { it.isNotEmpty() },
                            connectorInput.trim().takeIf { it.isNotEmpty() },
                            signatureInput.trim().takeIf { it.isNotEmpty() },
                            selectedCondition
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Terapkan Filter")
                }
            }
        }
    }
}