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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.components.ReservationForm
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.HostelList
import com.example.proyecto.ui.theme.Gotham

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HReservationScreen(
    viewModel: GeneralViewModel,
    userId: String,
    preselectedHostelId: String? = null,
    onBack: () -> Unit = {},
    onSuccessNavigate: () -> Unit = {} // 👈 callback to go back to menu
) {
    val snackbarHostState = remember { SnackbarHostState() }

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // 👈 Add this
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Fetch hostels and reset state
            LaunchedEffect(Unit) {
                viewModel.fetchHostels()
                viewModel.setHostelReservationState()
            }

            val hostelListState by viewModel.hostelListState.collectAsState()
            val reservationState by viewModel.newHostelReservationState.collectAsState()

            // 🔹 Reservation feedback
            LaunchedEffect(reservationState) {
                when (reservationState) {
                    is ResultState.Success -> {
                        snackbarHostState.showSnackbar("✅ Reserva creada con éxito")
                        onSuccessNavigate()
                    }
                    is ResultState.Error -> {
                        val msg = (reservationState as ResultState.Error).message
                        snackbarHostState.showSnackbar("❌ Error: $msg")
                    }
                    else -> {}
                }
            }

            when (hostelListState) {
                is ResultState.Loading -> Text("Cargando hostales...")
                is ResultState.Error -> Text("Error: ${(hostelListState as ResultState.Error).message}")
                is ResultState.Success -> {
                    val hostels = (hostelListState as ResultState.Success<HostelList>).data.results
                    val hostelNames = hostels?.map { it.name }
                    val hostelIdMap = hostels?.associateBy({ it.name }, { it.id })
                    var selectedHostelName by remember { mutableStateOf("") }

                    if (preselectedHostelId != null) {
                        val preselectedHostel = hostels?.find { it.id == preselectedHostelId }
                        selectedHostelName = preselectedHostel?.name ?: ""
                    }

                    ReservationForm(
                        hostels = hostelNames,
                        preselectedHostel = selectedHostelName,
                        hostelIdMap = hostelIdMap,
                        userId = userId,
                        onSubmitReservation = { request ->
                            viewModel.createHostelReservation(request)
                        }
                    )
                }

                else -> Text("$hostelListState")
            }

            if (reservationState is ResultState.Loading) {
                Text("Enviando reserva...")
            }
        }
    }
}