package com.example.proyecto.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto.R
import com.example.proyecto.models.HServicesScheduleData
import com.example.proyecto.models.Hostel
import com.example.proyecto.models.HostelServices
import com.example.proyecto.models.NewServiceReservation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// -------------------- MAIN FORM --------------------
@Composable
fun ServiceReservationForm(
    hostels: List<String>?,
    services: List<HostelServices>?,
    preselectedHostel: String? = null,
    preselectedService: HostelServices? = null,
    hostelIdMap: Map<String, String>? = emptyMap(),
    userId: String,
    onSubmitReservation: (NewServiceReservation) -> Unit
) {
    var selectedHostel by remember { mutableStateOf(preselectedHostel ?: "") }
    var hostelExpanded by remember { mutableStateOf(false) }

    // Filtered list of services for the selected hostel
    val filteredServices = services?.filter { it.hostel_name == selectedHostel }

    var selectedService by remember { mutableStateOf(preselectedService?.service_name ?: "") }
    var serviceExpanded by remember { mutableStateOf(false) }

    var reservationType by remember { mutableStateOf("individual") }
    var menCount by remember { mutableStateOf(0) }
    var womenCount by remember { mutableStateOf(0) }
    val totalCount = menCount + womenCount

    var datetimeReserved by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

    val isoString: String by remember(datetimeReserved, selectedTime) {
        derivedStateOf {
            datetimeReserved?.let { date ->
                LocalDateTime.of(date, selectedTime)
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT)
            } ?: ""
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Hostel Selector (skip if prefilled)
        HostelSelector(
            hostels = hostels,
            selectedHostel = selectedHostel,
            expanded = hostelExpanded,
            onExpandChange = { hostelExpanded = it },
            onHostelSelected = {
                selectedHostel = it
                selectedService = ""
            }
        )

        // Service Selector (only enabled if hostel is chosen)
        ServiceSelector(
            services = filteredServices?.map { it.service_name },
            selectedService = selectedService,
            expanded = serviceExpanded,
            onExpandChange = { serviceExpanded = it },
            onServiceSelected = { selectedService = it }
        )

        // Reservation details (same logic as hostel reservation)
        // NOTA: El texto dentro de ReservationDetails ("Individual", "Grupal", etc.)
        // debe ser traducido en el archivo donde ese componente está definido.
        ReservationDetails(
            reservationType = reservationType,
            onTypeChange = { reservationType = it },
            menCount = menCount,
            womenCount = womenCount,
            onMenChange = { menCount = it },
            onWomenChange = { womenCount = it },
            onDateChange = { datetimeReserved = it },
            totalCount = totalCount
        )

        TimePickerField(initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            label = stringResource(R.string.label_select_time),
            onTimeSelected = { selectedTime = it })

        SubmitServiceReservationButton(
            selectedService = services?.firstOrNull { it.service_name == selectedService },
            reservationType = reservationType,
            datetimeReserved = isoString,
            menCount = menCount,
            womenCount = womenCount,
            userId = userId,
            onSubmit = onSubmitReservation
        )
    }
}

// -------------------- COMPONENT: Service Selector --------------------
@Composable
fun ServiceSelector(
    services: List<String>?,
    selectedService: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onServiceSelected: (String) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.title_select_service))
        Box {
            OutlinedTextField(
                value = selectedService,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_service)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { onExpandChange(true) }) {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.cd_expand_dropdown)
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandChange(false) }
            ) {
                services?.forEach { service ->
                    DropdownMenuItem(
                        text = { Text(service) },
                        onClick = {
                            onServiceSelected(service)
                            onExpandChange(false)
                        }
                    )
                }
            }
        }
    }
}

// -------------------- SUBMIT BUTTON --------------------
@Composable
fun SubmitServiceReservationButton(
    selectedService: HostelServices?,
    reservationType: String,
    datetimeReserved: String,
    menCount: Int,
    womenCount: Int,
    userId: String,
    onSubmit: (NewServiceReservation) -> Unit
) {

    Button(

        onClick = {
            selectedService?.let {
                val request = NewServiceReservation(
                    datetime_reserved = datetimeReserved,
                    men_quantity = menCount,
                    service = it.id,
                    type = reservationType, // Lógica sin cambios (usa "individual" o "group")
                    user = userId,
                    women_quantity = womenCount,
                    status = if (it.service_needs_approval) "pending" else "confirmed"
                )
                onSubmit(request)
            }
        },
        enabled = selectedService != null && datetimeReserved.isNotBlank() && (menCount + womenCount) > 0
                && (reservationType != "individual" || (menCount + womenCount) == 1),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.action_submit_service_reservation))
    }
}