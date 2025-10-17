package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.R
import com.example.proyecto.data.loadHostelsFromRaw
import com.example.proyecto.models.Hostel
import com.example.proyecto.ui.theme.Gotham
import com.example.proyecto.ui.theme.Pantone302

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

        val ctx = LocalContext.current
        val localHostels = remember { loadHostelsFromRaw(ctx) }
        val hostelToShow = selectedHostel ?: localHostels.firstOrNull()

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

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp), // o quítalo y baja la imagen a 140.dp
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(localHostels) { h ->
                        ElevatedCard(
                            modifier = Modifier
                                .width(300.dp)
                                .shadow(3.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF9F9FB))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (h.imageResId != null) {
                                    Image(
                                        painter = painterResource(id = h.imageResId),
                                        contentDescription = "Imagen de ${h.name}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp) // 140 para que quepa el texto
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Sin imagen", color = Color.White, fontFamily = Gotham, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        h.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontFamily = Gotham,
                                        fontWeight = FontWeight.Bold,
                                        color = Pantone302,
                                        fontSize = 17.sp,
                                        maxLines = 1
                                    )

                                    val parts = listOf(
                                        h.location_data.landmarks,   // colonia (neighborhood)
                                        h.location_data.city,        // ciudad
                                        h.location_data.country      // país
                                    ).filter { !it.isNullOrBlank() }

                                    Text(
                                        text = parts.joinToString(", "),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = Gotham,
                                        color = Color(0xFF6B7280),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

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
                    Text("Reserva de Albergue", style = MaterialTheme.typography.titleMedium, fontSize = 15.sp)
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
                    Text("Reserva de Servicio", style = MaterialTheme.typography.titleMedium, fontSize = 15.sp)
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun ReservationScreenPreview() {
    ReservationScreen()
}
