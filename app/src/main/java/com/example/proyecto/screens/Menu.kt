package com.example.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.components.HostelCard
import com.example.proyecto.components.ServiceCard
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.Hostel
import com.example.proyecto.models.HostelList
import com.example.proyecto.models.HostelServices
import com.example.proyecto.models.HostelServicesList
import com.example.proyecto.ui.theme.Gotham
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.proyecto.components.HostelsMapView



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewReservationScreenLayout(vm: GeneralViewModel = viewModel(),onClick: (String) -> Unit, onClick2: (String) -> Unit) {
    val context = LocalContext.current

    // 🔹 Collect the states
    val hostelListState by vm.hostelListState.collectAsState()
    val serviceListState by vm.hostelServicesState.collectAsState()

    // 🔹 Fetch data when the screen loads
    LaunchedEffect(Unit) {
        vm.fetchHostelsOnce()
        vm.fetchHostelServicesOnce()
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Menú Principal",
                        fontFamily = Gotham,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        vm.fetchHostels()
                        vm.fetchHostelServices()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp , vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🔹 HOSTELS SECTION
                Text(
                    text = "Albergues disponibles",
                    fontFamily = Gotham,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                when (hostelListState) {
                    is ResultState.Loading -> CircularProgressIndicator()
                    is ResultState.Error -> Text(
                        text = "Error: ${(hostelListState as ResultState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )

                    is ResultState.Success<*> -> {
                        val hostels =
                            (hostelListState as ResultState.Success<HostelList>).data.results
                        if (hostels.isNullOrEmpty()) {
                            Text("No hay albergues disponibles.")
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(hostels) { hostel ->
                                    HostelCard(hostel, onClick)
                                }
                            }
                        }
                    }

                    else -> {}
                }

                Spacer(Modifier.height(20.dp))

                // 🔹 SERVICES SECTION
                Text(
                    text = "Servicios disponibles",
                    fontFamily = Gotham,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                when (serviceListState) {
                    is ResultState.Loading -> CircularProgressIndicator()
                    is ResultState.Error -> Text(
                        text = "Error: ${(serviceListState as ResultState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )

                    is ResultState.Success<*> -> {
                        val services =
                            (serviceListState as ResultState.Success<HostelServicesList>).data.results
                        if (services.isNullOrEmpty()) {
                            Text("No hay servicios disponibles.")
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(services) { service ->
                                    ServiceCard(service, onClick2)
                                }
                            }
                        }
                    }

                    else -> {}
                }
                Spacer(Modifier.height(24.dp))

                // Map Section
                HostelsMapView()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservationScreenLayoutPreview() {
    //PreviewReservationScreenLayout()
}

