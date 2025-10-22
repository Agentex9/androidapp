package com.example.proyecto.components

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.data.ResultState
import com.example.proyecto.models.HostelList
import com.example.proyecto.ui.theme.Gotham
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch


@Composable
fun HostelsMapView(
    viewModel: GeneralViewModel
) {
    val hostelListState by viewModel.hostelListState.collectAsState()
    val context = LocalContext.current

    // --- Runtime permissions ---
    var hasLocationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION ,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION ,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        permissionLauncher.launch(perms)
    }

    when (hostelListState) {
        is ResultState.Idle -> {
            Box(Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                Text("Esperando datos..." , color = Color.Gray)
            }
        }

        is ResultState.Loading -> {
            Box(Modifier.fillMaxSize() , contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Error -> {
            Text(
                text = "Error al cargar los albergues" ,
                color = Color.Red ,
                modifier = Modifier.padding(16.dp)
            )
        }

        is ResultState.Success -> {
            val hostels =
                (hostelListState as ResultState.Success<HostelList>).data.results ?: emptyList()

            if (hostels.isEmpty()) {
                Text("No hay albergues disponibles." , modifier = Modifier.padding(16.dp))
                return
            }

            val firstHostel = hostels.firstOrNull()
            val initialPosition = remember {
                if (firstHostel != null &&
                    !firstHostel.location_data.latitude.isNullOrBlank() &&
                    !firstHostel.location_data.longitude.isNullOrBlank()
                ) {
                    LatLng(
                        firstHostel.location_data.latitude.toDouble() ,
                        firstHostel.location_data.longitude.toDouble()
                    )
                } else {
                    LatLng(25.67 , -100.31) // Monterrey
                }
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(initialPosition , 10f)
            }

            // Propiedades del mapa: encendemos “mi ubicación” si hay permiso
            var mapProperties by remember {
                mutableStateOf(
                    MapProperties(
                        isMyLocationEnabled = hasLocationPermission
                    )
                )
            }
            var uiSettings by remember {
                mutableStateOf(
                    MapUiSettings(
                        myLocationButtonEnabled = true ,
                        zoomControlsEnabled = true ,     // botones +/− nativos
                        zoomGesturesEnabled = true ,
                        scrollGesturesEnabled = true ,
                        rotationGesturesEnabled = true ,
                        tiltGesturesEnabled = true ,
                        mapToolbarEnabled = true        // toolbar nativa al tocar un marcador
                    )
                )
            }


            // Si ya tenemos permisos, intenta centrar en la última ubicación conocida
            @SuppressLint("MissingPermission") // ya verificas hasLocationPermission antes de usar APIs que requieren permiso
            LaunchedEffect(hasLocationPermission) {
                if (hasLocationPermission) {
                    mapProperties = mapProperties.copy(isMyLocationEnabled = true)

                    val fused = LocationServices.getFusedLocationProviderClient(context)
                    val loc = try {
                        fused.lastLocation.await()   // requiere coroutines-play-services + import kotlinx.coroutines.tasks.await
                    } catch (_: Exception) {
                        null
                    }

                    loc?.let {
                        val here = LatLng(it.latitude , it.longitude) // SIN argumentos nombrados
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(here , 12f)
                            ) ,
                            durationMs = 800
                        )
                    }
                }
            }

            Column {
                Text(
                    text = "Ubicación de Albergues" ,
                    fontFamily = Gotham ,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp ,
                    color = MaterialTheme.colorScheme.onBackground ,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp , vertical = 8.dp),
                    textAlign = TextAlign.Center

                )
                val scope = rememberCoroutineScope()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp , vertical = 8.dp) ,
                    shape = RoundedCornerShape(16.dp) ,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) ,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize() ,
                            cameraPositionState = cameraPositionState ,
                            properties = mapProperties ,
                            uiSettings = uiSettings
                        ) {
                            hostels.forEach { hostel ->
                                val lat = hostel.location_data.latitude.toDoubleOrNull()
                                val lon = hostel.location_data.longitude.toDoubleOrNull()
                                if (lat != null && lon != null) {
                                    Marker(
                                        state = MarkerState(LatLng(lat , lon)) ,
                                        title = hostel.name ,
                                        snippet = hostel.location_data.address
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}