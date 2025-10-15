package com.example.proyecto.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.proyecto.R
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.data.ResultState

@Composable
fun LauncherScreen(
    generalViewModel: GeneralViewModel,
    onAuthorized: () -> Unit,
    onUnauthorized: () -> Unit
) {
    // 🔹 Run the check once (e.g., to verify session or preload data)
    LaunchedEffect(Unit) {
        generalViewModel.checkSessionOnce()
    }

    // 🔹 Observe the current state
    val hostelsState by generalViewModel.hostelListState.collectAsState()

    when (hostelsState) {
        is ResultState.Success<*> -> {
            // ✅ Navigate once authorized
            LaunchedEffect(Unit) { onAuthorized() }
        }

        is ResultState.Error -> {
            // ✅ Navigate if unauthorized or failed
            LaunchedEffect(Unit) { onUnauthorized() }
        }

        else -> {
            // 🔹 Show centered logo + loading spinner while waiting
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.caritas_bg),
                        contentDescription = "Logo Cáritas",
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .aspectRatio(1f),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CircularProgressIndicator()
                }
            }
        }
    }
}