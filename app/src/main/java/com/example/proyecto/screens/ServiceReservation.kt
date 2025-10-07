package com.example.proyecto.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.components.ServiceReservationForm
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.HostelList
import com.example.proyecto.models.HostelServices
import com.example.proyecto.models.NewServiceReservation
import com.example.proyecto.ui.theme.Gotham

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceReservationScreen(
    viewModel: GeneralViewModel,
    userId: String,
    preselectedHostelId: String? = null,
    preselectedServiceId: String? = null,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Nueva Reserva",
                        fontFamily = Gotham,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(Unit) {
                viewModel.setServiceReservationState()
            }

            val hostelListState by viewModel.hostelListState.collectAsState()
            val serviceListState by viewModel.hostelServicesState.collectAsState()
            val reservationState by viewModel.newServiceReservationState.collectAsState()


            when (hostelListState) {
                is ResultState.Loading -> Text("Loading hostels...")
                is ResultState.Error -> Text("Error: ${(hostelListState as ResultState.Error).message}")
                is ResultState.Success -> {
                    val hostels = (hostelListState as ResultState.Success<HostelList>).data.hostels
                    val hostelNames = hostels.map { it.name }
                    val hostelIdMap = hostels.associateBy({ it.name }, { it.id })

                    when (serviceListState) {
                        is ResultState.Loading -> Text("Loading services...")
                        is ResultState.Error -> Text("Error: ${(serviceListState as ResultState.Error).message}")
                        is ResultState.Success -> {
                            val services =
                                (serviceListState as ResultState.Success<List<HostelServices>>).data

                            // Preselected hostel/service (if coming from detail)
                            val preselectedHostelName = preselectedHostelId?.let { id ->
                                hostels.find { it.id == id }?.name
                            }
                            val preselectedService = preselectedServiceId?.let { id ->
                                services.find { it.id == id }
                            }

                            ServiceReservationForm(
                                hostels = hostelNames,
                                services = services,
                                preselectedHostel = preselectedHostelName,
                                preselectedService = preselectedService,
                                hostelIdMap = hostelIdMap,
                                userId = userId,
                                onSubmitReservation = { request ->
                                    viewModel.createServiceReservation(request)
                                }
                            )
                        }

                        else -> {}
                    }
                }

                else -> {}
            }

            // Feedback for reservation state
            when (reservationState) {
                is ResultState.Loading -> Text("Submitting service reservation...")
                is ResultState.Success -> Text("Service reservation created!")
                is ResultState.Error -> Text("Error: ${(reservationState as ResultState.Error).message}")
                else -> {}
            }
        }
    }
}



/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServiceReservationScreenPreview() {
    // Mock Locations
    val locationA = com.example.proyecto.models.Location(
        address = "123 Main St",
        city = "Monterrey",
        country = "Mexico",
        id = "loc1",
        landmarks = "Near Central Park",
        latitude = 256866,
        longitude = -1003161,
        state = "NL",
        zip_code = "64000"
    )

    val locationB = com.example.proyecto.models.Location(
        address = "456 Second Ave",
        city = "Monterrey",
        country = "Mexico",
        id = "loc2",
        landmarks = "Next to Museum",
        latitude = 256870,
        longitude = -1003170,
        state = "NL",
        zip_code = "64010"
    )

    // Mock Hostels
    val mockHostels = listOf(
        com.example.proyecto.models.Hostel(
            created_at = "2025-10-01",
            id = "1",
            is_active = true,
            location = locationA,
            men_capacity = 20,
            current_men_capacity = 5,
            name = "Hostel A",
            phone = "123-456-7890",
            women_capacity = 15,
            current_women_capacity = 3
        ),
        com.example.proyecto.models.Hostel(
            created_at = "2025-10-01",
            id = "2",
            is_active = true,
            location = locationB,
            men_capacity = 25,
            current_men_capacity = 10,
            name = "Hostel B",
            phone = "987-654-3210",
            women_capacity = 20,
            current_women_capacity = 5
        )
    )
    val hostelNames = mockHostels.map { it.name }
    val hostelIdMap = mockHostels.associateBy({ it.name }, { it.id })

    // Mock Schedule
    val mockSchedule = com.example.proyecto.models.HServicesScheduleData(
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

    // Mock Services
    val mockServices = listOf(
        com.example.proyecto.models.HostelServices(
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
        com.example.proyecto.models.HostelServices(
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

    // Directly show ServiceReservationForm in preview
    com.example.proyecto.components.ServiceReservationForm(
        hostels = hostelNames,
        services = mockServices,
        preselectedHostel = null,       // simulate empty selection
        preselectedService = null,
        hostelIdMap = hostelIdMap,
        userId = "12345",
        onSubmitReservation = { reservation ->
            println("Preview Service Reservation Submitted: $reservation")
        }
    )
}

*/