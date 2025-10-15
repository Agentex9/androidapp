package com.example.proyecto.components

// IMPORTACIONES NECESARIAS PARA GOOGLE MAPS
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

// IMPORTACIÓN PARA TU FUENTE PERSONALIZADA (asegúrate de que la ruta sea correcta)
import com.example.proyecto.ui.theme.Gotham

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HostelsMapView() {
    // Ahora LatLng viene de la librería de Google Maps, no de tu función
    val posada = LatLng(25.683345486074938, -100.34547424967124)
    val divina = LatLng(25.66838634821009, -100.303116273627)
    val apodaca = LatLng(25.791556345387292, -100.13872669275699)

    // MarkerState y CameraPosition ahora se reconocen gracias a los imports
    val posadaState = remember { MarkerState(position = posada) }
    val divinaState = remember { MarkerState(position = divina) }
    val apodacaState = remember { MarkerState(position = apodaca) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posada, 10f)
    }

    Text(
        text = "Ubicación de Albergues",
        fontFamily = Gotham, // 'Gotham' se reconoce por el import
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground,
        // Añadimos padding aquí ya que quitamos el Column exterior
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(Modifier.height(12.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // También añadimos padding aquí
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp) ,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        // Este Column interno de la Card está bien, lo dejamos
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(state = posadaState, title = "Posada del Peregrino")
                    Marker(state = divinaState, title = "Divina Providencia")
                    Marker(state = apodacaState, title = "Albergue Contigo")
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewHostelsMapView() {
    // Para que el preview se vea bien, lo envolvemos en un Column
    Column {
        HostelsMapView()
    }
}