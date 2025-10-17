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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
fun HostelCard(hostel: Hostel, onClick: (String) -> Unit) {
    // Fix font scaling for consistency
    CompositionLocalProvider(
        LocalDensity provides object : Density by LocalDensity.current {
            override val fontScale: Float get() = 1f
        }
    ) {
        val totalCapacity = hostel.total_capacity
        val currentCapacity = hostel.current_capacity
        val occupancyRatio = if (totalCapacity > 0) currentCapacity.toFloat() / totalCapacity else 0f

        // 🎨 Custom colors
        val pantone302 = Color(0xFF003B5C)     // Deep blue
        val pantone1575 = Color(0xFFFF7F32)    // Orange
        val wineRed = Color(0xFF8B0000)        // Deep wine red

        val occupancyColor = when {
            occupancyRatio >= 1f -> wineRed
            occupancyRatio >= 0.8f -> pantone1575
            else -> pantone302
        }

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { onClick(hostel.id) },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(hostel.image_url) // <- your image URL
                        .crossfade(true)
                        .build(),
                    contentDescription = "${hostel.name} image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Hostel name
                Text(
                    text = hostel.name,
                    fontFamily = Gotham,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Phone number
                Text(
                    text = "Tel: ${hostel.phone}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Occupancy text
                Text(
                    text = "Ocupación: $currentCapacity / $totalCapacity",
                    fontSize = 13.sp,
                    color = occupancyColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHostelCard() {
    HostelCard(sampleHostel, onClick = {})
}

// -------------------- Tarjeta de Servicio --------------------
@Composable
fun ServiceCard(service: HostelServices, onClick: (String) -> Unit) {
    // Fix font scaling
    CompositionLocalProvider(
        LocalDensity provides object : Density by LocalDensity.current {
            override val fontScale: Float get() = 1f
        }
    ) {
        val statusColor = if (service.is_active) Color(0xFF003B5C) else Color(0xFF8B0000) // active=blue, inactive=wine red

        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { onClick(service.id) },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Service Name + Status dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.service_name,
                        fontFamily = Gotham,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Hostel name
                Text(
                    text = "Albergue: ${service.hostel_name}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // Requires approval
                Text(
                    text = "Requiere aprobación: ${if (service.service_needs_approval) "Sí" else "No"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // Price
                Text(
                    text = "Precio: ${service.service_price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewServiceCard() {
    ServiceCard(service = hostelService, onClick = {})
}

val sampleHostel = Hostel(
    available_capacity = AvailableCapacity(
        additionalProp1 = "10 beds available",
        additionalProp2 = "5 for men",
        additionalProp3 = "5 for women"
    ),
    coordinates = listOf(25.6866f, -100.3161f), // Monterrey example coords
    created_at = "2025-10-13T10:30:00Z",
    created_by_name = "Admin User",
    current_capacity = 0,
    current_men_capacity = 0,
    current_women_capacity = 0,
    formatted_address = "Av. Universidad 123, San Nicolás, Monterrey, NL, México",
    id = "hostel_001",
    is_active = true,
    location = "Monterrey, Nuevo León",
    location_data = Location(
        address = "Av. Universidad 123",
        city = "Monterrey",
        coordinates = listOf(25.6866f, -100.3161f),
        country = "México",
        created_at = "2025-10-10T09:00:00Z",
        formatted_address = "Av. Universidad 123, San Nicolás, Monterrey, NL, México",
        google_maps_url = "https://maps.google.com/?q=25.6866,-100.3161",
        id = "loc_001",
        landmarks = "Cerca de la UANL",
        latitude = "25.6866",
        longitude = "-100.3161",
        state = "Nuevo León",
        timezone = "America/Monterrey",
        updated_at = "2025-10-13T11:00:00Z",
        zip_code = "66450"
    ),
    men_capacity = 25,
    name = "Hostel Monterrey Centro",
    phone = "+52 81 1234 5678",
    total_capacity = 50,
    updated_at = "2025-10-13T12:00:00Z",
    women_capacity = 25,
    image_url = null
)

