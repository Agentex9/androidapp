package com.example.proyecto.screens

import android.widget.Space
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.components.HostelCard
import com.example.proyecto.components.ReservationCard
import com.example.proyecto.components.ServiceReservationCard
import com.example.proyecto.data.MyReservationsState
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.MyHostelReservationList
import com.example.proyecto.models.MyHostelReservations
import com.example.proyecto.models.MyServiceReservationList
import com.example.proyecto.models.MyServiceReservations
import com.example.proyecto.ui.theme.Gotham

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun historyScreen(vm: GeneralViewModel) {
    LaunchedEffect(Unit) {
        vm.fetchMyServiceReservations()
        vm.fetchMyHostelReservations()
        vm.fetchMyUpcomingServiceReservations()
    }

    val hostelReservations by vm.myHostelReservationsState.collectAsState()
    val upcomingReservations by vm.myUpcomingReservationsState.collectAsState()
    val serviceReservations by vm.myServiceReservationsState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mis Reservas",
                        fontFamily = Gotham,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        vm.fetchMyServiceReservations()
                        vm.fetchMyHostelReservations()
                        vm.fetchMyUpcomingServiceReservations()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalDensity provides Density(
                LocalDensity.current.density,
                fontScale = 1f
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when(hostelReservations){
                    is ResultState.Success<*> -> {
                        val reservations =
                            (hostelReservations as ResultState.Success<MyHostelReservationList>).data.results

                        if(!reservations.isNullOrEmpty()){
                            val checkedInReservations = reservations.filter { it.status.equals("checked_in", ignoreCase = true) }
                            if (checkedInReservations.isNotEmpty()) {
                                Text(
                                    "Reserva Activa",
                                    fontFamily = Gotham,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(checkedInReservations) { reservation ->
                                        ReservationCard(reservation)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    else -> {}
                }

                Text(
                    "Mis proximas Reservas",
                    fontFamily = Gotham,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (upcomingReservations) {
                    is ResultState.Loading -> CircularProgressIndicator()
                    is ResultState.Error -> {Text(
                        text = "Error: ${(upcomingReservations as ResultState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(4.dp))}

                    is ResultState.Success<*> -> {
                        val reservations =
                            (upcomingReservations as ResultState.Success<MyServiceReservationList>).data.results

                        if (reservations.isNullOrEmpty()){
                            Text("No tienes Reservas Proximas")
                            Spacer(modifier = Modifier.height(4.dp))}
                        else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(reservations) { reservation ->
                                    ServiceReservationCard(reservation)
                                }
                            }
                        }


                    }

                    else -> {}

                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Todas mis Reservas de Servicios",
                    fontFamily = Gotham,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (serviceReservations) {
                    is ResultState.Loading -> CircularProgressIndicator()
                    is ResultState.Error -> Text(
                        text = "Error: ${(serviceReservations as ResultState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )

                    is ResultState.Success<*> -> {
                        val reservations =
                            (serviceReservations as ResultState.Success<MyServiceReservationList>).data.results

                        if (reservations.isNullOrEmpty()) {
                            Text("No tienes Reservas Proximas")
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(reservations) { reservation ->
                                    ServiceReservationCard(reservation)
                                }
                            }
                        }
                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Historial de Reservas de Albergues",
                    fontFamily = Gotham,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                when (hostelReservations) {
                    is ResultState.Loading -> CircularProgressIndicator()
                    is ResultState.Error -> Text(
                        text = "Error: ${(hostelReservations as ResultState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )

                    is ResultState.Success<*> -> {
                        val reservations =
                            (hostelReservations as ResultState.Success<MyHostelReservationList>).data.results

                        if (reservations.isNullOrEmpty())
                            Text("No tienes Reservas Proximas")
                        else {
                            val otherReservations = reservations.filterNot {
                                it.status.equals(
                                    "checked_in",
                                    ignoreCase = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (otherReservations.isEmpty()) {
                                Text("No tienes otras Reservas")
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)

                                ) {
                                    items(otherReservations) { reservation ->
                                        ReservationCard(reservation)

                                    }

                                }
                            }
                        }
                    }
                    else -> {}
                }



            }


        }
    }
}
@Preview
@Composable
fun preview(){
    val generalViewModel: GeneralViewModel = viewModel()
    historyScreen(generalViewModel)
}
