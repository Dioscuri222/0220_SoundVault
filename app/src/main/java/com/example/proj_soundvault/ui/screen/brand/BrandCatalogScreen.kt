package com.example.proj_soundvault.ui.screen.brand

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proj_soundvault.data.model.AudioBrand
import com.example.proj_soundvault.data.remote.MockSessionManager
import com.example.proj_soundvault.data.remote.UserRole
import com.example.proj_soundvault.ui.viewmodel.BrandUiState
import com.example.proj_soundvault.ui.viewmodel.BrandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandCatalogScreen(
    viewModel: BrandViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentRole by MockSessionManager.currentRole.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SoundVault Brands") },
                actions = {
                    // Tombol Switch Role (USER <-> ADMIN)
                    FilterChip(
                        selected = currentRole == UserRole.ADMIN,
                        onClick = { MockSessionManager.switchRole() },
                        label = { Text(if (currentRole == UserRole.ADMIN) "Role: ADMIN" else "Role: USER") }
                    )
                }
            )
        },
        floatingActionButton = {
            // FAB Tambah hanya muncul untuk ADMIN
            if (currentRole == UserRole.ADMIN) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Brand")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is BrandUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is BrandUiState.Error -> Text(state.message, Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                is BrandUiState.Success -> {
                    if (state.brands.isEmpty()) {
                        Text("Belum ada data brand.", Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.brands) { brand ->
                                BrandCard(
                                    brand = brand,
                                    isAdmin = currentRole == UserRole.ADMIN,
                                    onDelete = { viewModel.deleteBrand(brand.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBrandDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, country, desc ->
                viewModel.addBrand(name, country, desc)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BrandCard(brand: AudioBrand, isAdmin: Boolean, onDelete: () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(brand.brandName, style = MaterialTheme.typography.titleMedium)
                Text("Asal: ${brand.countryOrigin}", style = MaterialTheme.typography.bodySmall)
                if (brand.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(brand.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddBrandDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Audio Brand") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Brand") })
                OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Negara Asal") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, country, desc) }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}