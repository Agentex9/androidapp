package com.example.proyecto.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto.models.MyHostelReservations
import com.example.proyecto.models.MyServiceReservations
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val exampleReservation = MyHostelReservations(
    arrival_date = "2025-10-15",
    created_at = "2025-10-09T14:30:00Z",
    created_by_name = "Juan Pérez",
    hostel = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    hostel_location = "Monterrey, Nuevo León",
    hostel_name = "Albergue San José",
    id = "a12b34c5-d67e-89f0-1234-56789abcdef0",
    men_quantity = 2,
    status = "pending",
    status_display = "Pendiente de confirmación",
    total_people = 3,
    type = "group",
    type_display = "Grupo",
    updated_at = "2025-10-09T16:00:00Z",
    user = "123e4567-e89b-12d3-a456-426614174000",
    user_name = "Carlos Ramírez",
    user_phone = "+52811908593",
    women_quantity = 1
)

@Composable
fun ReservationCard(reservation: MyHostelReservations) {
    val backgroundColor = when (reservation.status.lowercase()) {
        "pending" -> Color(0xFFFFF3E0) // light orange
        "confirmed" -> Color(0xFFE8F5E9) // light green
        "cancelled" -> Color(0xFFFFEBEE) // light red
        else -> Color(0xFFF5F5F5) // default gray
    }

    Card(
        modifier = Modifier
            .width(320.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Hostel name and status row

                Text(
                    text = reservation.hostel_name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

            Row(verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Filled.Approval,
                    contentDescription = "Estado",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reservation.status_display,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = when (reservation.status.lowercase()) {
                            "pending" -> Color(0xFFFF9800)
                            "confirmed" -> Color(0xFF2E7D32)
                            "cancelled" -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        },
                        fontWeight = FontWeight.SemiBold
                    )
            )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Hostel location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reservation.hostel_location,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Arrival date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Arrival Date",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Llegada: ${reservation.arrival_date}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reservation type
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Type",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tipo: ${reservation.type_display}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gender distribution
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "People",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hombres: ${reservation.men_quantity}   |   Mujeres: ${reservation.women_quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Total
            Text(
                text = "Total personas: ${reservation.total_people}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun ServiceReservationCard(reservation: MyServiceReservations) {
    // Format datetime in local timezone with AM/PM
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
    val localDatetime = try {
        OffsetDateTime.parse(reservation.datetime_reserved)
            .atZoneSameInstant(ZoneId.systemDefault())
            .format(formatter)
    } catch (e: Exception) { reservation.datetime_reserved } // fallback

    val backgroundColor = when (reservation.status.lowercase()) {
        "pending" -> Color(0xFFFFF3E0) // light orange
        "confirmed" -> Color(0xFFE8F5E9) // light green
        "cancelled" -> Color(0xFFFFEBEE) // light red
        else -> Color(0xFFF5F5F5) // default gray
    }

    Card(
        modifier = Modifier
            .width(320.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 1️⃣ Service Name (Main Title)
            Text(
                text = reservation.service_name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 2️⃣ Hostel Name with House Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Hostel",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reservation.hostel_name,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3️⃣ Status Row with Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusIcon = when (reservation.status.lowercase()) {
                    "confirmed" -> Icons.Default.CheckCircle
                    "pending" -> Icons.Default.Schedule
                    "cancelled" -> Icons.Default.Cancel
                    else -> Icons.Default.Info
                }

                val statusColor = when (reservation.status.lowercase()) {
                    "pending" -> Color(0xFFFF9800)
                    "confirmed" -> Color(0xFF2E7D32)
                    "cancelled" -> Color(0xFFD32F2F)
                    else -> Color.Gray
                }

                Icon(
                    imageVector = statusIcon,
                    contentDescription = "Estado",
                    tint = statusColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reservation.status_display,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4️⃣ Date & Duration
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Date & Time",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Desde: $localDatetime  |  Duración: ${reservation.duration_minutes} min",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5️⃣ Gender distribution
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "People",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hombres: ${reservation.men_quantity}   |   Mujeres: ${reservation.women_quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6️⃣ Total people
            Text(
                text = "Total personas: ${reservation.total_people}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 7️⃣ Price
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Price",
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Precio: ${reservation.service_price}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReservationCard() {
    ReservationCard(reservation = exampleReservation)
}

// Example data
val exampleServiceReservation = MyServiceReservations(
    created_at = "2025-10-09T14:30:00Z",
    created_by_name = "Carlos Ramírez",
    datetime_reserved = "2025-10-15T10:00:00Z",
    duration_minutes = 90,
    end_datetime_reserved = "2025-10-15T11:30:00Z",
    hostel_location = "Monterrey, Nuevo León", // optional, can ignore in card
    hostel_name = "Albergue San José",
    id = "b12c34d5-e67f-89g0-1234-56789abcdef1",
    is_expired = false,
    men_quantity = 2,
    service = "massage",
    service_name = "Relajación muscular",
    service_price = "$50",
    status = "pending",
    status_display = "Pendiente de confirmación",
    total_people = 3,
    type = "individual",
    type_display = "Individual",
    updated_at = "2025-10-09T15:00:00Z",
    user = "123e4567-e89b-12d3-a456-426614174000",
    user_name = "Carlos Ramírez",
    user_phone = "+52811908593",
    women_quantity = 1
)

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewServiceReservationCard() {
    ServiceReservationCard(reservation = exampleServiceReservation)
}


