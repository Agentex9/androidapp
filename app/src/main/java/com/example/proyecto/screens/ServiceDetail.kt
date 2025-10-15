package com.example.proyecto.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.models.HostelServices
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.HostelServicesList
import com.example.proyecto.ui.theme.Gotham

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String?,
    viewModel: GeneralViewModel,
    onBack: () -> Unit,
    onReserveClick: (String, String) -> Unit
) {
    val serviceListState by viewModel.hostelServicesState.collectAsState()
    val service: HostelServices? = when (serviceListState) {
        is ResultState.Success -> {
            val serviceList = (serviceListState as ResultState.Success<HostelServicesList>).data.results
            serviceList?.find { it.id == serviceId }
        }
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Servicio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
            when (serviceListState) {
                is ResultState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ResultState.Error -> {
                    Text(
                        text = "Error: ${(serviceListState as ResultState.Error).message}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ResultState.Success -> {
                    if (service == null) {
                        Text("Servicio no encontrado", modifier = Modifier.align(Alignment.Center))
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = service.service_name,
                                    fontFamily = Gotham,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                )

                                val statusColor =
                                    if (service.is_active) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                            }

                            Text(
                                text = service.service_description,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )

                            // Price & Reservations
                            Text("Detalles Generales", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Precio: ${service.service_price}", fontSize = 14.sp)
                                Text("Máximo tiempo: ${service.service_max_time} min", fontSize = 14.sp)
                                Text("Requiere aprobación: ${if (service.service_needs_approval) "Sí" else "No"}", fontSize = 14.sp)
                                Text("Total de reservas: ${service.total_reservations}", fontSize = 14.sp)
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )

                            // Hostel Info
                            Text("Albergue Asociado", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Nombre: ${service.hostel_name}", fontSize = 14.sp)
                                Text("Ubicación: ${service.hostel_location}", fontSize = 14.sp)
                                Text("Hostel ID: ${service.hostel}", fontSize = 14.sp)
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )

                            // Schedule
                            Text("Horario", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            val schedule = service.schedule_data
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Día: ${schedule.day_name}", fontSize = 14.sp)
                                Text("Disponible: ${if (schedule.is_available) "Sí" else "No"}", fontSize = 14.sp)
                                Text("Inicio: ${schedule.start_time}", fontSize = 14.sp)
                                Text("Fin: ${schedule.end_time}", fontSize = 14.sp)
                                Text("Duración: ${schedule.duration_hours} h", fontSize = 14.sp)
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.3f)
                            )

                            // Reserve Button
                            Button(
                                onClick = { onReserveClick(service.id,service.hostel) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Reservar Servicio", fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
