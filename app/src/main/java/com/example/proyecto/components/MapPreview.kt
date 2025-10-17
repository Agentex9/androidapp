package com.example.proyecto.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.HostelList
import com.example.proyecto.ui.theme.Gotham
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HostelsMapView(
    viewModel: GeneralViewModel
) {
    val hostelListState by viewModel.hostelListState.collectAsState()

    when (hostelListState) {
        is ResultState.Idle -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Esperando datos...", color = Color.Gray)
            }
        }

        is ResultState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Error -> {
            Text(
                text = "Error al cargar los albergues",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }

        is ResultState.Success -> {
            val hostels = (hostelListState as ResultState.Success<HostelList>).data.results ?: emptyList()

            if (hostels.isEmpty()) {
                Text("No hay albergues disponibles.", modifier = Modifier.padding(16.dp))
            } else {
                // Tomamos la primera ubicación como punto inicial de cámara
                val firstHostel = hostels.firstOrNull()
                val initialPosition = remember {
                    if (firstHostel != null &&
                        !firstHostel.location_data.latitude.isNullOrBlank() &&
                        !firstHostel.location_data.longitude.isNullOrBlank()
                    ) {
                        LatLng(
                            firstHostel.location_data.latitude.toDouble(),
                            firstHostel.location_data.longitude.toDouble()
                        )
                    } else {
                        LatLng(25.67, -100.31) // fallback: Monterrey
                    }
                }

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
                }

                Column {
                    Text(
                        text = "Ubicación de Albergues",
                        fontFamily = Gotham,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                // 🔹 Crea un marcador para cada hostel recibido del backend
                                hostels.forEach { hostel ->
                                    val lat = hostel.location_data.latitude.toDoubleOrNull()
                                    val lon = hostel.location_data.longitude.toDoubleOrNull()
                                    if (lat != null && lon != null) {
                                        val position = LatLng(lat, lon)
                                        val markerState = remember { MarkerState(position = position) }

                                        Marker(
                                            state = markerState,
                                            title = hostel.name,
                                            snippet = hostel.location_data.address
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
