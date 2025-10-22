package com.example.proyecto.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.models.Hostel
import com.example.proyecto.models.HostelList
import com.example.proyecto.data.ResultState
import com.example.proyecto.ui.theme.Gotham
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.example.proyecto.ui.theme.* // Asegúrate de tener esta importación si usas colores personalizados como Pantone320, White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelDetailScreen(
    hostelId: String?,
    viewModel: GeneralViewModel,
    onBack: () -> Unit,
    onReserveClick: (String) -> Unit
) {
    val context = LocalContext.current
    val hostelListState by viewModel.hostelListState.collectAsState()
    val hostel: Hostel? = when (hostelListState) {
        is ResultState.Success -> {
            val hostelList = (hostelListState as ResultState.Success<HostelList>).data
            hostelList.results?.find { it.id == hostelId }
        }
        else -> null
    }

    // 1. Estados para los permisos y la ubicación del usuario
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient: FusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val coroutineScope = rememberCoroutineScope()


    // 2. Launcher para solicitar permisos de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission =
            (result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        // Si se conceden los permisos, intenta obtener la ubicación
        if (hasLocationPermission) {
            coroutineScope.launch {
                getUserLocation(fusedLocationClient, context) { location ->
                    userLocation = location
                }
            }
        }
    }

    // 3. Lanzar la solicitud de permisos al iniciar la pantalla
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Si ya tiene permisos, intenta obtener la ubicación al inicio
            coroutineScope.launch {
                getUserLocation(fusedLocationClient, context) { location ->
                    userLocation = location
                }
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Albergue") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (hostelListState) {
                is ResultState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ResultState.Error -> {
                    Text(
                        text = "Error: ${(hostelListState as ResultState.Error).message}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ResultState.Success -> {
                    if (hostel == null) {
                        Text("Albergue no encontrado", modifier = Modifier.align(Alignment.Center))
                    } else {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header Section
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = hostel.name,
                                    fontFamily = Gotham,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                )

                                val statusColor = if (hostel.is_active) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                            }

                            Text(
                                "Teléfono: ${hostel.phone}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = DividerDefaults.Thickness, color = Color.Gray.copy(alpha = 0.3f)
                            )

                            // Occupancy Section
                            Text(
                                "Ocupación",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            val totalRatio =
                                (hostel.current_capacity.toFloat() / hostel.total_capacity).coerceIn(0f, 1f)
                            val menRatio =
                                (hostel.current_men_capacity.toFloat() / hostel.men_capacity).coerceIn(0f, 1f)
                            val womenRatio =
                                (hostel.current_women_capacity.toFloat() / hostel.women_capacity).coerceIn(0f, 1f)

                            OccupancyBar(
                                label = "Total",
                                ratio = totalRatio,
                                color = when {
                                    totalRatio >= 1f -> Color(0xFFD32F2F)
                                    totalRatio >= 0.8f -> Color(0xFFFFA000)
                                    else -> Color(0xFF2E7D32)
                                },
                                current = hostel.current_capacity,
                                total = hostel.total_capacity
                            )

                            OccupancyBar(
                                label = "Hombres",
                                ratio = menRatio,
                                color = Color(0xFF1565C0),
                                current = hostel.current_men_capacity,
                                total = hostel.men_capacity
                            )

                            OccupancyBar(
                                label = "Mujeres",
                                ratio = womenRatio,
                                color = Color(0xFFAD1457),
                                current = hostel.current_women_capacity,
                                total = hostel.women_capacity
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = DividerDefaults.Thickness, color = Color.Gray.copy(alpha = 0.3f)
                            )

                            Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                            if (!hostel.location_data.latitude.isNullOrBlank() &&
                                !hostel.location_data.longitude.isNullOrBlank()
                            ) {
                                val latitude = hostel.location_data.latitude.toDoubleOrNull()
                                val longitude = hostel.location_data.longitude.toDoubleOrNull()

                                if (latitude != null && longitude != null) {
                                    val hostelLocation = LatLng(latitude, longitude)
                                    val cameraPositionState = rememberCameraPositionState()

                                    LaunchedEffect(hostelLocation) {
                                        cameraPositionState.position = CameraPosition.fromLatLngZoom(hostelLocation, 14f)
                                    }

                                    val markerState = remember { MarkerState(position = hostelLocation) }

                                    GoogleMap(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        cameraPositionState = cameraPositionState,
                                        uiSettings = MapUiSettings(
                                            scrollGesturesEnabled = true,
                                            zoomGesturesEnabled = true,
                                            rotationGesturesEnabled = true,
                                            tiltGesturesEnabled = false,
                                            compassEnabled = true,
                                            mapToolbarEnabled = true
                                        ),
                                        // 4. Habilitar el punto azul de ubicación del usuario
                                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
                                    ) {
                                        Marker(
                                            state = markerState,
                                            title = hostel.name,
                                            snippet = hostel.location_data.address
                                        )
                                        // Opcional: Si quieres un marcador adicional para el usuario
                                        // userLocation?.let {
                                        //     Marker(
                                        //         state = remember { MarkerState(position = it) },
                                        //         title = "Tu ubicación",
                                        //         icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                        //     )
                                        // }
                                    }
                                } else {
                                    Text(
                                        "Coordenadas inválidas o no disponibles.",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                Text(
                                    "Ubicación no disponible desde el backend.",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }


                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Dirección: ${hostel.location_data.address}", fontSize = 14.sp)
                                Text("Ciudad: ${hostel.location_data.city}", fontSize = 14.sp)
                                Text("Estado: ${hostel.location_data.state}", fontSize = 14.sp)
                                Text("País: ${hostel.location_data.country}", fontSize = 14.sp)
                                Text("Código Postal: ${hostel.location_data.zip_code}", fontSize = 14.sp)
                                Text("Puntos de referencia: ${hostel.location_data.landmarks}", fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Reserve Button
                            Button(
                                onClick = { onReserveClick(hostel.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Pantone320, // Asegúrate de que Pantone320 esté definido en tu tema
                                    contentColor = White // Asegúrate de que White esté definido en tu tema
                                )

                            ) {
                                Text("Reservar Albergue",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = White )
                            }
                        }
                    }
                }
                else -> {
                    Text("Sin datos", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

// Función auxiliar para obtener la ubicación del usuario
@Suppress("MissingPermission") // La verificación de permisos se realiza antes de llamar a esta función
private fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (LatLng?) -> Unit
) {
    // Primero, verifica si los permisos están concedidos
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(LatLng(location.latitude, location.longitude))
            } else {
                // Manejar el caso donde lastLocation es null (ej. ubicación nunca solicitada)
                // Podrías intentar solicitar una ubicación actual si esto sucede
                onLocationReceived(null)
            }
        }.addOnFailureListener { e ->
            // Manejar errores al obtener la ubicación
            e.printStackTrace()
            onLocationReceived(null)
        }
    } else {
        // Los permisos no están concedidos, onLocationReceived ya debería ser null
        onLocationReceived(null)
    }
}


@Composable
fun OccupancyBar(
    label: String,
    ratio: Float,
    color: Color,
    current: Int,
    total: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, fontWeight = FontWeight.Medium)
            Text("$current / $total", color = color, fontWeight = FontWeight.SemiBold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}