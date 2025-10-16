package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.R
import com.example.proyecto.models.Hostel
import com.example.proyecto.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    onBack: () -> Unit = {},
    onClickHostel: () -> Unit = {},
    onClickService: () -> Unit = {},
    selectedHostel: Hostel? = null
) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {

        val cPrimary = colorResource(R.color.pantone_320)
        val cPrimaryDk = colorResource(R.color.pantone_302)
        val cWhite = colorResource(R.color.white)

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Nueva Reserva",
                            fontFamily = Gotham,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Regresar"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Selecciona el tipo de Reserva",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = Gotham,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(24.dp))

                // --- CARD COMPACTA ---
                selectedHostel?.let { hostel ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .shadow(3.dp, shape = RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFF9F9FB)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Imagen principal
                            if (hostel.imageResId != null) {
                                Image(
                                    painter = painterResource(id = hostel.imageResId),
                                    contentDescription = "Imagen de ${hostel.name}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Sin imagen",
                                        color = Color.White,
                                        fontFamily = Gotham,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Texto debajo de la imagen
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = hostel.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = Gotham,
                                    fontWeight = FontWeight.Bold,
                                    color = Pantone302,
                                    fontSize = 17.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${hostel.location_data.city}, ${hostel.location_data.country}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = Gotham,
                                    color = Color.Gray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }

                // --- BOTONES ---
                Button(
                    onClick = onClickHostel,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cPrimary,
                        contentColor = cWhite
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        "Reserva de Albergue",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onClickService,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cPrimaryDk,
                        contentColor = cWhite
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        "Reserva de Servicio",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun ReservationScreenPreview() {
    val albergueConImagen = Hostel(
        id = "1",
        name = "Albergue Contigo",
        imageResId = R.drawable.albergue_contigo, // usa tu imagen real
        location_data = com.example.proyecto.models.Location(
            address = "Calle de la Amistad 10",
            city = "Santiago",
            state = "RM",
            country = "Chile",
            zip_code = "8320000",
            landmarks = "Cerca de la plaza",
            latitude = "-33.4489",
            longitude = "-70.6693",
            coordinates = listOf(-33.4489f, -70.6693f),
            created_at = "2025-10-13",
            updated_at = "2025-10-13",
            formatted_address = "Calle de la Amistad 10, Santiago, RM, Chile",
            google_maps_url = "",
            id = "loc_001",
            timezone = "America/Santiago"
        ),
        available_capacity = com.example.proyecto.models.AvailableCapacity(
            additionalProp1 = "10 camas disponibles",
            additionalProp2 = "5 hombres",
            additionalProp3 = "5 mujeres"
        ),
        coordinates = listOf(-33.4489f, -70.6693f),
        created_at = "2025-10-13",
        created_by_name = "Admin",
        current_capacity = 10,
        current_men_capacity = 5,
        current_women_capacity = 5,
        formatted_address = "Calle de la Amistad 10, Santiago, RM, Chile",
        is_active = true,
        location = "Santiago",
        men_capacity = 10,
        phone = "1234567890",
        total_capacity = 20,
        updated_at = "2025-10-13",
        women_capacity = 10
    )

    ReservationScreen(selectedHostel = albergueConImagen)
}
