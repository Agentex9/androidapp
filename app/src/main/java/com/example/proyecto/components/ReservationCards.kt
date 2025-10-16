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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.data.ResultState

@Composable
fun ReservationCard(reservation: MyHostelReservations, viewModel: GeneralViewModel, snackbarHostState: SnackbarHostState) {
    CompositionLocalProvider(
        LocalDensity provides Density(
            LocalDensity.current.density,
            fontScale = 1f
        )
    ) {
        var showActionDialog by remember { mutableStateOf(false) }
        var showConfirmDialog by remember { mutableStateOf(false) }

        val cancelState = viewModel.cancelReservationState.collectAsState().value

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
                .padding(8.dp)
                .clickable { showActionDialog = true },
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

        if (showActionDialog) {
            AlertDialog(
                onDismissRequest = { showActionDialog = false },
                title = { Text("Opciones de reservación") },
                text = { Text("¿Qué deseas hacer con esta reservación?") },
                confirmButton = {
                    if (reservation.status.lowercase() in listOf("pending", "confirmed")) {
                        TextButton(onClick = {
                            showActionDialog = false
                            showConfirmDialog = true
                        }) {
                            Text("Cancelar reservación", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showActionDialog = false }) {
                        Text("Regresar")
                    }
                }
            )
        }

        // Second dialog: confirmation for cancellation
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Confirmar cancelación") },
                text = { Text("¿Estás seguro de que deseas cancelar esta reservación?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmDialog = false
                        viewModel.cancelHostelReservation(reservation.id)
                    }) {
                        Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        // Feedback handling (loading, success, error)
        LaunchedEffect(cancelState) {
            when (cancelState) {
                is ResultState.Success -> {
                    snackbarHostState.showSnackbar(
                        message = "Reservación cancelada con éxito",
                        withDismissAction = true
                    )
                    viewModel.resetCancelState()
                }

                is ResultState.Error -> {
                    snackbarHostState.showSnackbar(
                        message = "Error: ${cancelState.message}",
                        withDismissAction = true
                    )
                    viewModel.resetCancelState()
                }

                else -> {}
            }
        }

        // Optional loading indicator (tiny one)
        if (cancelState is ResultState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ServiceReservationCard(reservation: MyServiceReservations, viewModel: GeneralViewModel, snackbarHostState: SnackbarHostState) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
    val localDatetime = try {
        OffsetDateTime.parse(reservation.datetime_reserved)
            .atZoneSameInstant(ZoneId.systemDefault())
            .format(formatter)
    } catch (e: Exception) { reservation.datetime_reserved }

    var showActionDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val cancelState = viewModel.cancelReservationState.collectAsState().value

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
            .padding(8.dp)
            .clickable { showActionDialog = true },
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


    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Opciones de reservación") },
            text = { Text("¿Qué deseas hacer con esta reservación?") },
            confirmButton = {
                if (reservation.status.lowercase() in listOf("pending", "confirmed")) {
                    TextButton(onClick = {
                        showActionDialog = false
                        showConfirmDialog = true
                    }) {
                        Text("Cancelar reservación", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("Regresar")
                }
            }
        )
    }

    // Second dialog: confirmation for cancellation
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar cancelación") },
            text = { Text("¿Estás seguro de que deseas cancelar esta reservación?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    viewModel.cancelServiceReservation(reservation.id)
                }) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Feedback handling (loading, success, error)
    LaunchedEffect(cancelState) {
        when (cancelState) {
            is ResultState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Reservación cancelada con éxito",
                    withDismissAction = true
                )
                viewModel.resetCancelState()
            }

            is ResultState.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error: ${cancelState.message}",
                    withDismissAction = true
                )
                viewModel.resetCancelState()
            }

            else -> {}
        }
    }

    // Optional loading indicator (tiny one)
    if (cancelState is ResultState.Loading) {
        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
    }
}


