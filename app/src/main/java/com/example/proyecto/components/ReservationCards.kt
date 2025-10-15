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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.models.MyHostelReservations
import com.example.proyecto.models.MyServiceReservations
import com.example.proyecto.ui.theme.Gotham
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import androidx.compose.ui.res.stringResource
import com.example.proyecto.R

val exampleReservation = MyHostelReservations(
    arrival_date = "2025-10-15",
    created_at = "2025-10-09T14:30:00Z",
    created_by_name = "Juan Pérez",
    hostel = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    hostel_location = "Monterrey, Nuevo León",
    hostel_name = "Albergue San José",
    id = "a12b34c5-d67e-89f0-1234-56789abcdef0",
    men_quantity = 2,
    status = "checked_out",
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
    CompositionLocalProvider(
        LocalDensity provides Density(
            LocalDensity.current.density,
            fontScale = 1f
        )
    ) {
        val backgroundColor = when (reservation.status.lowercase()) {
            "pending" -> Color(0xFFFFF3E0)
            "confirmed" -> Color(0xFFE8F5E9)
            "cancelled" -> Color(0xFFFFEBEE)
            "rejected" -> Color(0xFFFFEAEA)
            "checked_in" -> Color(0xCEE0F7FA)
            "checked_out" -> Color(0xFFF0F0F0)
            else -> Color(0xFFB0B0B0)
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
                    .fillMaxWidth()
            ) {
                Text(
                    text = reservation.hostel_name,
                    fontFamily = Gotham,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Approval,
                        contentDescription = stringResource(R.string.cd_status), // <-- CAMBIO
                        tint = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reservation.status_display,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = when (reservation.status.lowercase()) {
                                "pending" -> Color(0xFFFFA726)
                                "confirmed" -> Color(0xFF136C1B)
                                "cancelled" -> Color(0xFFE53935)
                                "rejected" -> Color(0xFFB71C1C)
                                "checked_in" -> Color(0xFF006F75)
                                "checked_out" -> Color(0xFF757575)
                                else -> Color.Gray
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.cd_location), // <-- CAMBIO
                        tint = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reservation.hostel_location,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = stringResource(R.string.cd_arrival_date), // <-- CAMBIO
                        tint = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stringResource(R.string.label_arrival)} ${reservation.arrival_date}", // <-- CAMBIO
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.cd_reservation_type), // <-- CAMBIO
                        tint = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stringResource(R.string.label_type)} ${reservation.type_display}", // <-- CAMBIO
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = stringResource(R.string.cd_people), // <-- CAMBIO
                        tint = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        // Usando placeholders para los números
                        text = stringResource(R.string.label_men_women, reservation.men_quantity, reservation.women_quantity), // <-- CAMBIO
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    // Usando placeholder para el total
                    text = stringResource(R.string.label_total_people, reservation.total_people), // <-- CAMBIO
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
fun ServiceReservationCard(reservation: MyServiceReservations) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
    val localDatetime = try {
        OffsetDateTime.parse(reservation.datetime_reserved)
            .atZoneSameInstant(ZoneId.systemDefault())
            .format(formatter)
    } catch (e: Exception) { reservation.datetime_reserved }

    val backgroundColor = when (reservation.status.lowercase()) {
        "pending" -> Color(0xFFFFF3E0)
        "confirmed" -> Color(0xFFE8F5E9)
        "cancelled" -> Color(0xFFFFEBEE)
        "rejected" -> Color(0xFFFFEAEA)
        "in_progress" -> Color(0xFFE3F2FD)
        "completed" -> Color(0xFFF3E5F5)
        else -> Color(0xFFF5F5F5)
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
            Text(
                text = reservation.service_name,
                fontFamily = Gotham,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.cd_hostel), // <-- CAMBIO
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reservation.hostel_name,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusColor = when (reservation.status.lowercase()) {
                    "pending" -> Color(0xFFFF9800)
                    "confirmed" -> Color(0xFF2E7D32)
                    "cancelled" -> Color(0xFFD32F2F)
                    "rejected" -> Color(0xFFC62828)
                    "in_progress" -> Color(0xFF1565C0)
                    "completed" -> Color(0xFF6A1B9A)
                    else -> Color.Gray
                }
                Icon(
                    imageVector = Icons.Filled.Approval,
                    contentDescription = stringResource(R.string.cd_status), // <-- CAMBIO
                    tint = Color(0xFF757575)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.cd_date_time), // <-- CAMBIO
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = stringResource(R.string.label_from_datetime, localDatetime), // <-- CAMBIO
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.label_duration_minutes, reservation.duration_minutes), // <-- CAMBIO
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = stringResource(R.string.cd_people), // <-- CAMBIO
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.label_men_women, reservation.men_quantity, reservation.women_quantity), // <-- CAMBIO
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.label_total_people, reservation.total_people), // <-- CAMBIO
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = stringResource(R.string.cd_price), // <-- CAMBIO
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.label_price, reservation.service_price), // <-- CAMBIO
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
    status = "completed",
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


