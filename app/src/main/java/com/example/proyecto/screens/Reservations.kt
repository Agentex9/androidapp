package com.example.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.copy
import com.example.proyecto.R
import com.example.proyecto.ui.theme.Gotham

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    onBack: () -> Unit = {},
    onClickHostel: () -> Unit = {},
    onClickService: () -> Unit = {}
) {
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, fontScale = 1f)
    ) {
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
    ReservationScreen()
}
