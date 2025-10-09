package com.example.proyecto.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto.models.HServicesScheduleData
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
        if (preselectedHostel == null) {
            HostelSelector(
                hostels = hostels,
                selectedHostel = selectedHostel,
                expanded = hostelExpanded,
                onExpandChange = { hostelExpanded = it },
                onHostelSelected = { hostel ->
                    selectedHostel = hostel
                    selectedService = "" // reset service when hostel changes
                }
            )
        } else {
            Text("Hostel: $selectedHostel")
        }

        // Service Selector (only enabled if hostel is chosen)
        ServiceSelector(
            services = filteredServices?.map { it.service_name },
            selectedService = selectedService,
            expanded = serviceExpanded,
            onExpandChange = { serviceExpanded = it },
            onServiceSelected = { selectedService = it }
        )

        // Reservation details (same logic as hostel reservation)
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
            label = "Seleccionar Hora",
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
        Text(text = "Select Service")
        Box {
            OutlinedTextField(
                value = selectedService,
                onValueChange = {},
                readOnly = true,
                label = { Text("Service") },
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
                    type = reservationType,
                    user = userId,
                    women_quantity = womenCount
                )
                onSubmit(request)
            }
        },
        enabled = selectedService != null && datetimeReserved.isNotBlank() && (menCount + womenCount) > 0
                && (reservationType != "individual" || (menCount + womenCount) == 1),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Submit Service Reservation")
    }
}

// -------------------- PREVIEW --------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServiceReservationFormPreview() {
    val mockSchedule = HServicesScheduleData(
        created_at = "2025-10-01T08:00:00",
        created_by_name = "Admin",
        day_name = "Monday",
        day_of_week = 1,
        duration_hours = 2,
        end_time = "10:00",
        id = "sch1",
        is_available = true,
        start_time = "08:00",
        updated_at = "2025-10-01T08:30:00"
    )

    val mockServices = listOf(
        HostelServices(
            created_at = "2025-10-01",
            created_by_name = "Admin",
            hostel = "1",
            hostel_location = "Location A",
            hostel_name = "Hostel A",
            id = "101",
            is_active = true,
            schedule = "Morning",
            schedule_data = mockSchedule,
            service = "S1",
            service_description = "Laundry service for clothes",
            service_max_time = 60,
            service_name = "Laundry",
            service_needs_approval = false,
            service_price = "10",
            total_reservations = 5,
            updated_at = "2025-10-01"
        ),
        HostelServices(
            created_at = "2025-10-01",
            created_by_name = "Admin",
            hostel = "2",
            hostel_location = "Location B",
            hostel_name = "Hostel B",
            id = "102",
            is_active = true,
            schedule = "Afternoon",
            schedule_data = mockSchedule.copy(
                id = "sch2",
                day_name = "Tuesday",
                start_time = "14:00",
                end_time = "16:00"
            ),
            service = "S2",
            service_description = "Shower service with hot water",
            service_max_time = 30,
            service_name = "Shower",
            service_needs_approval = false,
            service_price = "5",
            total_reservations = 2,
            updated_at = "2025-10-01"
        )
    )

    ServiceReservationForm(
        hostels = listOf("Hostel A", "Hostel B"),
        services = mockServices,
        userId = "12345",
        onSubmitReservation = { reservation ->
            println("Preview Service Reservation Submitted: $reservation")
        }
    )
}
