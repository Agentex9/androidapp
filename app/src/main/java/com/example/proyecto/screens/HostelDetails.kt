package com.example.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.models.Hostel
import com.example.proyecto.models.HostelList
import com.example.proyecto.data.ResultState
import com.example.proyecto.ui.theme.Gotham
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapUiSettings
import com.example.proyecto.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelDetailScreen(
    hostelId: String?,
    viewModel: GeneralViewModel,
    onBack: () -> Unit,
    onReserveClick: (String) -> Unit
) {
    val hostelListState by viewModel.hostelListState.collectAsState()
    val hostel: Hostel? = when (hostelListState) {
        is ResultState.Success -> {
            val hostelList = (hostelListState as ResultState.Success<HostelList>).data
            hostelList.results?.find { it.id == hostelId }
        }
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Albergue") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack , contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
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
                        Text("Albergue no encontrado", modifier = Modifier.align(Alignment.Center))
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header Section
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = hostel.name,
                                    fontFamily = Gotham,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                )

                                val statusColor = if (hostel.is_active) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                            }

                            Text(
                                "Teléfono: ${hostel.phone}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp) ,
                                thickness = DividerDefaults.Thickness , color = Color.Gray.copy(alpha = 0.3f)
                            )

                            // Occupancy Section
                            Text(
                                "Ocupación",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            val totalRatio =
                                (hostel.current_capacity.toFloat() / hostel.total_capacity).coerceIn(0f, 1f)
                            val menRatio =
                                (hostel.current_men_capacity.toFloat() / hostel.men_capacity).coerceIn(0f, 1f)
                            val womenRatio =
                                (hostel.current_women_capacity.toFloat() / hostel.women_capacity).coerceIn(0f, 1f)

                            OccupancyBar(
                                label = "Total",
                                ratio = totalRatio,
                                color = when {
                                    totalRatio >= 1f -> Color(0xFFD32F2F)
                                    totalRatio >= 0.8f -> Color(0xFFFFA000)
                                    else -> Color(0xFF2E7D32)
                                },
                                current = hostel.current_capacity,
                                total = hostel.total_capacity
                            )

                            OccupancyBar(
                                label = "Hombres",
                                ratio = menRatio,
                                color = Color(0xFF1565C0),
                                current = hostel.current_men_capacity,
                                total = hostel.men_capacity
                            )

                            OccupancyBar(
                                label = "Mujeres",
                                ratio = womenRatio,
                                color = Color(0xFFAD1457),
                                current = hostel.current_women_capacity,
                                total = hostel.women_capacity
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp) ,
                                thickness = DividerDefaults.Thickness , color = Color.Gray.copy(alpha = 0.3f)
                            )

                            Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                            // --- Dentro de tu bloque ResultState.Success ---
                            if (!hostel.location_data.latitude.isNullOrBlank() &&
                                !hostel.location_data.longitude.isNullOrBlank()
                            ) {
                                val latitude = hostel.location_data.latitude.toDoubleOrNull()
                                val longitude = hostel.location_data.longitude.toDoubleOrNull()

                                if (latitude != null && longitude != null) {
                                    val hostelLocation = LatLng(latitude, longitude)
                                    val cameraPositionState = rememberCameraPositionState()

                                    // 🔹 Actualiza la cámara cuando cambien las coordenadas del backend
                                    LaunchedEffect(hostelLocation) {
                                        cameraPositionState.position = CameraPosition.fromLatLngZoom(hostelLocation, 14f)
                                    }

                                    // 🔹 Recordar el MarkerState para evitar advertencias y recomposiciones
                                    val markerState = remember { MarkerState(position = hostelLocation) }

                                    GoogleMap(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        cameraPositionState = cameraPositionState,
                                        uiSettings = MapUiSettings(
                                            scrollGesturesEnabled = true,
                                            zoomGesturesEnabled = true,
                                            rotationGesturesEnabled = true,
                                            tiltGesturesEnabled = false,
                                            compassEnabled = true,
                                            mapToolbarEnabled = true
                                        )
                                    ) {
                                        Marker(
                                            state = markerState,
                                            title = hostel.name,
                                            snippet = hostel.location_data.address
                                        )
                                    }
                                } else {
                                    Text(
                                        "Coordenadas inválidas o no disponibles.",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                Text(
                                    "Ubicación no disponible desde el backend.",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }




                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Dirección: ${hostel.location_data.address}", fontSize = 14.sp)
                                Text("Ciudad: ${hostel.location_data.city}", fontSize = 14.sp)
                                Text("Estado: ${hostel.location_data.state}", fontSize = 14.sp)
                                Text("País: ${hostel.location_data.country}", fontSize = 14.sp)
                                Text("Código Postal: ${hostel.location_data.zip_code}", fontSize = 14.sp)
                                Text("Puntos de referencia: ${hostel.location_data.landmarks}", fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Reserve Button
                            Button(
                                onClick = { onReserveClick(hostel.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Pantone320,
                                    contentColor = White
                                )

                            ) {
                                Text("Reservar Albergue",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White )
                            }
                        }
                    }
                }
                else -> {
                    Text("Sin datos", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun OccupancyBar(
    label: String,
    ratio: Float,
    color: Color,
    current: Int,
    total: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, fontWeight = FontWeight.Medium)
            Text("$current / $total", color = color, fontWeight = FontWeight.SemiBold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}

