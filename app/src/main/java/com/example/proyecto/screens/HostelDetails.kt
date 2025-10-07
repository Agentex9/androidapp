package com.example.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.models.Hostel
import com.example.proyecto.models.HostelList
import com.example.proyecto.data.ResultState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelDetailScreen(
    hostelId: String?,
    viewModel: GeneralViewModel,
    onBack: () -> Unit
) {
    // Collect the hostel list state
    val hostelListState by viewModel.hostelListState.collectAsState()

    val hostel: Hostel? = when (hostelListState) {
        is ResultState.Success -> {
            val hostelList = (hostelListState as ResultState.Success<HostelList>).data
            hostelList.hostels.find { it.id == hostelId }
        }
        else -> null
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(hostel?.name ?: "Hostel Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (hostelListState) {
                is ResultState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ResultState.Error -> {
                    Text(
                        text = "Error: ${(hostelListState as ResultState.Error).message}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ResultState.Success -> {
                    if (hostel == null) {
                        Text("Hostel not found", modifier = Modifier.align(Alignment.Center))
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text("Name: ${hostel.name}")
                            Spacer(Modifier.height(8.dp))
                            Text("Phone: ${hostel.phone}")
                            Text("Active: ${hostel.is_active}")
                            Spacer(Modifier.height(8.dp))
                            Text("Men capacity: ${hostel.current_men_capacity}/${hostel.men_capacity}")
                            Text("Women capacity: ${hostel.current_women_capacity}/${hostel.women_capacity}")
                            Spacer(Modifier.height(8.dp))
                            Text("Location:")
                            Text("Address: ${hostel.location.address}")
                            Text("City: ${hostel.location.city}")
                            Text("State: ${hostel.location.state}")
                            Text("Country: ${hostel.location.country}")
                            Text("Zip: ${hostel.location.zip_code}")
                            Text("Landmarks: ${hostel.location.landmarks}")
                        }
                    }
                }
                else -> {
                    Text("No data", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
