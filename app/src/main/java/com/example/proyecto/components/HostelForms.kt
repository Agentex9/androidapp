package com.example.proyecto.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto.models.NewHostelReservation
import java.time.LocalDate

// -------------------- MAIN FORM --------------------
@Composable
fun ReservationForm(
    hostels: List<String>?,
    preselectedHostel: String? = null,
    hostelIdMap: Map<String, String>? = emptyMap(),
    userId: String,
    onSubmitReservation: (NewHostelReservation) -> Unit
) {
    var selectedHostel by remember { mutableStateOf(preselectedHostel ?: "") }
    var expanded by remember { mutableStateOf(false) }

    var reservationType by remember { mutableStateOf("individual") }
    var menCount by remember { mutableStateOf(0) }
    var womenCount by remember { mutableStateOf(0) }
    val totalCount = menCount + womenCount

    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HostelSelector(
            hostels = hostels,
            selectedHostel = selectedHostel,
            expanded = expanded,
            onExpandChange = { expanded = it },
            onHostelSelected = { selectedHostel = it }
        )

        ReservationDetails(
            reservationType = reservationType,
            onTypeChange = { reservationType = it },
            menCount = menCount,
            womenCount = womenCount,
            onMenChange = { menCount = it },
            onWomenChange = { womenCount = it },
            onDateChange = { arrivalDate = it },
            totalCount = totalCount
        )

        SubmitReservationButton(
            selectedHostel = selectedHostel,
            reservationType = reservationType,
            arrivalDate = arrivalDate,
            menCount = menCount,
            womenCount = womenCount,
            hostelIdMap = hostelIdMap,
            userId = userId,
            onSubmit = onSubmitReservation // ✅ bubble up
        )
    }
}


// -------------------- COMPONENT 1 --------------------
@Composable
fun HostelSelector(
    hostels: List<String>?,
    selectedHostel: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onHostelSelected: (String) -> Unit
) {
    Column {
        Text(text = "Seleccionar Albergue")
        Box {
            OutlinedTextField(
                value = selectedHostel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Escoge un Albergue") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { onExpandChange(true) }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandChange(false) }
            ) {
                hostels?.forEach { hostel ->
                    DropdownMenuItem(
                        text = { Text(hostel) },
                        onClick = {
                            onHostelSelected(hostel)
                            onExpandChange(false)
                        }
                    )
                }
            }
        }
    }
}

// -------------------- COMPONENT 2 --------------------
@Composable
fun ReservationDetails(
    reservationType: String,
    onTypeChange: (String) -> Unit,
    menCount: Int,
    womenCount: Int,
    onMenChange: (Int) -> Unit,
    onWomenChange: (Int) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    totalCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Reservation Type
        Text("Tipo de Reservación")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = reservationType == "individual",
                onClick = { onTypeChange("individual") }
            )
            Text("Individual", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = reservationType == "group",
                onClick = { onTypeChange("group")}
            )
            Text("Grupal")
        }

        // Men counter
        CounterField(
            label = "Hombre",
            value = menCount,
            onIncrement = {
                if (reservationType == "group") {
                    onMenChange(menCount + 1)
                } else if (reservationType == "individual" && totalCount < 1) {
                    onMenChange(menCount + 1)
                }
            },
            onDecrement = {
                if (menCount > 0) onMenChange(menCount - 1)
            }
        )

        // Women counter
        CounterField(
            label = "Mujer",
            value = womenCount,
            onIncrement = {
                if (reservationType == "group") {
                    onWomenChange(womenCount + 1)
                } else if (reservationType == "individual" && totalCount < 1) {
                    onWomenChange(womenCount + 1)
                }
            },
            onDecrement = {
                if (womenCount > 0) onWomenChange(womenCount - 1)
            }
        )

        // Total
        Text("Total Personas: $totalCount")

        DatePickerField("Fecha de llegada", onDateSelected = {onDateChange(it)})
    }
}


// -------------------- COMPONENT 3 --------------------
@Composable
fun SubmitReservationButton(
    selectedHostel: String,
    reservationType: String,
    arrivalDate: LocalDate?,
    menCount: Int,
    womenCount: Int,
    hostelIdMap: Map<String, String>?,
    userId: String,
    onSubmit: (NewHostelReservation) -> Unit
) {
    val hostelId = hostelIdMap?.get(selectedHostel) ?: ""
    val formattedDate = arrivalDate?.let {
        "%04d-%02d-%02d".format(it.year, it.monthValue, it.dayOfMonth)
    } ?: ""


    Button(
        onClick = {
            val request = NewHostelReservation(
                arrival_date = formattedDate,
                hostel = hostelId,
                men_quantity = menCount,
                type = reservationType,
                user = userId,
                women_quantity = womenCount,
                status = "pending"
            )
            onSubmit(request)  // ✅ pass back up
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedHostel.isNotEmpty() && arrivalDate != null && (menCount + womenCount) > 0
                && (reservationType != "individual" || (menCount + womenCount) == 1)
    ) {
        Text("Enviar Reservación")
    }
}


// -------------------- REUSABLE COUNTER --------------------
@Composable
fun CounterField(label: String, value: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease $label")
            }
            Text(value.toString(), modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = onIncrement) {
                Icon(Icons.Default.Add, contentDescription = "Increase $label")
            }
        }
    }
}
