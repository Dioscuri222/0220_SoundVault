package com.example.proj_soundvault.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proj_soundvault.ui.viewmodel.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: Int,
    currentUserId: Int,
    currentUserRole: String,
    listingViewModel: ListingViewModel,
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    onOfferClick: (Int) -> Unit = {}
) {
    val item by listingViewModel.selectedListing.collectAsState()
    val isLoading by listingViewModel.isLoading.collectAsState()
    val isSubmitting by listingViewModel.isSubmitting.collectAsState()
    val errorMessage by listingViewModel.errorMessage.collectAsState()
    val successMessage by listingViewModel.successMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        listingViewModel.loadListingDetail(itemId)
    }

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

    val isOwnerOrAdmin = item != null &&
            (item!!.sellerId == currentUserId || currentUserRole.equals("ADMIN", ignoreCase = true))
    val isSold = item?.status.equals("SOLD", ignoreCase = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk Audio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (isOwnerOrAdmin) {
                        IconButton(
                            onClick = { onEditClick(itemId) },
                            enabled = !isSold && !isSubmitting
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Listing")
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !isSubmitting
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus Listing",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading && item == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                item == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Detail produk tidak ditemukan")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { listingViewModel.loadListingDetail(itemId) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                else -> {
                    val listing = item!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = listing.brandName ?: "Merek #${listing.brandId}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            ListingStatusBadge(status = listing.status)
                        }

                        Text(
                            text = listing.itemName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = listing.formattedPrice(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )

                        HorizontalDivider()

                        // Kartu Spesifikasi Teknis
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Spesifikasi Teknis Audio",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                SpecDetailRow("Kategori", listing.formattedCategory())
                                SpecDetailRow("Jenis Driver", listing.driverType)
                                SpecDetailRow("Tipe Konektor", listing.connectorType)
                                SpecDetailRow("Sound Signature", listing.soundSignature)
                                SpecDetailRow("Kondisi", listing.formattedCondition())
                            }
                        }

                        // Deskripsi & Kelengkapan
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Deskripsi & Kelengkapan",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = listing.description?.takeIf { it.isNotBlank() }
                                        ?: "Tidak ada catatan tambahan dari penjual.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Informasi Penjual
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Informasi Penjual",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                SpecDetailRow("Nama", listing.sellerName ?: "User #${listing.sellerId}")
                                if (!listing.sellerPhone.isNullOrBlank()) {
                                    SpecDetailRow("Kontak", listing.sellerPhone)
                                }
                            }
                        }

                        // Tombol Ajukan Penawaran / Beli (hanya untuk USER non-pemilik & status AVAILABLE)
                        val canMakeOffer = listing.sellerId != currentUserId &&
                                !currentUserRole.equals("ADMIN", ignoreCase = true) &&
                                listing.status.equals("AVAILABLE", ignoreCase = true)

                        if (canMakeOffer) {
                            Button(
                                onClick = { onOfferClick(listing.idItem) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Ajukan Penawaran / Beli Langsung", fontWeight = FontWeight.Bold)
                            }
                        } else if (isSold) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Barang ini sudah terjual (SOLD) dan tidak dapat diubah atau ditawar.",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus (REQ-21 & NFR-6)
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Listing Audio?") },
            text = { Text("Apakah Anda yakin ingin menghapus produk ini? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        listingViewModel.deleteListing(
                            id = itemId,
                            onSuccess = onNavigateBack
                        )
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SpecDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}