package com.example.proj_soundvault.ui.screen.brand

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proj_soundvault.data.model.AudioBrand
import com.example.proj_soundvault.ui.viewmodel.BrandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandCatalogScreen(
    isAdmin: Boolean = false,
    userName: String = "",
    onLogout: () -> Unit = {},
    brandViewModel: BrandViewModel = viewModel()
) {
    val uiState by brandViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showFormDialog by remember { mutableStateOf(false) }
    var editingBrand by remember { mutableStateOf<AudioBrand?>(null) }
    var deletingBrand by remember { mutableStateOf<AudioBrand?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            brandViewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Katalog Merek Audio", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${userName.ifBlank { "Pengguna" }} • ${if (isAdmin) "ADMIN" else "USER"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showLogoutDialog = true }) {
                        Text("Logout", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = {
                        editingBrand = null
                        showFormDialog = true
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Merek")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { brandViewModel.onSearchQueryChange(it) },
                placeholder = { Text("Cari nama merek atau negara asal...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { brandViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Hapus Pencarian")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            when {
                uiState.isLoading && uiState.brands.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null && uiState.brands.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { brandViewModel.loadBrands() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }

                uiState.brands.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada data merek audio.")
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 84.dp)
                    ) {
                        items(uiState.brands, key = { it.idBrand }) { brand ->
                            BrandItemCard(
                                brand = brand,
                                isAdmin = isAdmin,
                                onEdit = {
                                    editingBrand = brand
                                    showFormDialog = true
                                },
                                onDelete = { deletingBrand = brand }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog Tambah / Edit Brand (Khusus ADMIN)
    if (showFormDialog && isAdmin) {
        BrandFormDialog(
            initialBrand = editingBrand,
            onDismiss = { showFormDialog = false },
            onConfirm = { name, country, desc ->
                if (editingBrand == null) {
                    brandViewModel.createBrand(name, country, desc)
                } else {
                    brandViewModel.updateBrand(editingBrand!!.idBrand, name, country, desc)
                }
                showFormDialog = false
            }
        )
    }

    // Dialog Konfirmasi Hapus Brand (Khusus ADMIN)
    if (deletingBrand != null && isAdmin) {
        AlertDialog(
            onDismissRequest = { deletingBrand = null },
            title = { Text("Hapus Merek Audio") },
            text = {
                Text("Yakin ingin menghapus merek '${deletingBrand!!.brandName}'? Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        brandViewModel.deleteBrand(deletingBrand!!.idBrand)
                        deletingBrand = null
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingBrand = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Konfirmasi Logout (Sesuai Gambar 3.6 SRS)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun SoundVault?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Ya, Keluar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }
}

@Composable
private fun BrandItemCard(
    brand: AudioBrand,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = brand.brandName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Negara Asal: ${brand.countryOrigin}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!brand.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = brand.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isAdmin) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Merek")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus Merek",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandFormDialog(
    initialBrand: AudioBrand?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var brandName by remember { mutableStateOf(initialBrand?.brandName ?: "") }
    var countryOrigin by remember { mutableStateOf(initialBrand?.countryOrigin ?: "") }
    var description by remember { mutableStateOf(initialBrand?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialBrand == null) "Tambah Merek Audio" else "Edit Merek Audio")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = { Text("Nama Merek (contoh: Tangzu)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = countryOrigin,
                    onValueChange = { countryOrigin = it },
                    label = { Text("Negara Asal (contoh: China)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Singkat (Opsional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(brandName, countryOrigin, description) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}