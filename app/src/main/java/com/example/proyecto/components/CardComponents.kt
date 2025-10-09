package com.example.proyecto.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.models.*
import com.example.proyecto.ui.theme.Gotham

// -------------------- Datos de ejemplo --------------------
val scheduleData = HServicesScheduleData(
    created_at = "2025-09-30T16:00:00Z",
    created_by_name = "Admin User",
    day_name = "Monday",
    day_of_week = 1,
    duration_hours = 12,
    end_time = "20:00",
    id = "schedule_001",
    is_available = true,
    start_time = "08:00",
    updated_at = "2025-09-30T16:30:00Z"
)

val hostelService = HostelServices(
    created_at = "2025-09-30T16:30:00Z",
    created_by_name = "Admin User",
    hostel = "hostel_001",
    hostel_location = "loc_001",
    hostel_name = "Montaña Hostel",
    id = "service_001",
    is_active = true,
    schedule = "08:00 - 20:00",
    schedule_data = scheduleData,
    service = "cleaning",
    service_description = "Daily room cleaning and bed making",
    service_max_time = 60,
    service_name = "Room Cleaning",
    service_needs_approval = false,
    service_price = "200 MXN",
    total_reservations = 15,
    updated_at = "2025-09-30T16:45:00Z"
)



// -------------------- Tarjeta de Albergue --------------------
@Composable
fun HostelCard(hostel: Hostel,onClick: (String) -> Unit) {
    // 🔹 Fijar tamaño de fuente sin escalar por accesibilidad
    CompositionLocalProvider(
        LocalDensity provides object : Density by LocalDensity.current {
            override val fontScale: Float
                get() = 1f
        }
    ) {
        val totalCapacity = hostel.men_capacity + hostel.women_capacity
        val freeCapacity =
            (totalCapacity - hostel.current_men_capacity - hostel.current_women_capacity).coerceAtLeast(0)
        val freePercentage =
            if (totalCapacity > 0) freeCapacity.toFloat() / totalCapacity else 0f

        val indicatorColor = when {
            freeCapacity == 0 -> Color.Red
            freePercentage > 0.2 -> Color.Green
            else -> Color.Yellow
        }

        Card(
            modifier = Modifier
                .padding(6.dp)
                .wrapContentSize()
                .clickable { onClick(hostel.id) },
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = hostel.name,
                        fontFamily = Gotham,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }

                Text("${hostel.location_data.city}, ${hostel.location_data.state}", fontSize = 12.sp)
                Text("Tel: ${hostel.phone}", fontSize = 12.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        "Hombres: ${hostel.men_capacity - hostel.current_men_capacity}",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Mujeres: ${hostel.women_capacity - hostel.current_women_capacity}",
                        fontSize = 12.sp
                    )
                }

                Text("Total: $freeCapacity (Total: $totalCapacity)", fontSize = 12.sp)
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewHostelCard() {
    HostelCard(hostel = myHostel)
}*/

// -------------------- Tarjeta de Servicio --------------------
@Composable
fun ServiceCard(service: HostelServices) {
    CompositionLocalProvider(
        LocalDensity provides object : Density by LocalDensity.current {
            override val fontScale: Float
                get() = 1f
        }
    ) {
        val statusColor = if (service.is_active) Color.Green else Color.Red

        Card(
            modifier = Modifier
                .padding(4.dp)
                .wrapContentSize(),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .wrapContentSize()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        service.service_name,
                        fontFamily = Gotham,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(service.service_description, fontSize = 12.sp)
                Text("Albergue: ${service.hostel_name}", fontSize = 12.sp)
                Text(
                    "Requiere aprobación: ${if (service.service_needs_approval) "Sí" else "No"}",
                    fontSize = 12.sp
                )

                Row {
                    Text("Precio: ${service.service_price}", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Duración: ${service.service_max_time} min", fontSize = 12.sp)
                }

                Text("Horario: ${service.schedule}", fontSize = 12.sp)
            }
        }
    }
}

fun CompositionLocalProvider(
    value: ProvidedValue<Density> ,
    content: () -> Unit
) {
}

@Preview(showBackground = true)
@Composable
fun PreviewServiceCard() {
    ServiceCard(service = hostelService)
}
