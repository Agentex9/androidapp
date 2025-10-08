package com.example.proyecto.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.proyecto.Navigation.Screen
import com.example.proyecto.R
import com.example.proyecto.ui.theme.ProyectoTheme // Asegúrate de importar tu tema

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Muestra la barra de navegación inferior de la aplicación.
 * Este componente es "stateless", lo que significa que no gestiona su propio estado.
 * Recibe el estado actual y notifica los eventos de clic hacia arriba.
 *
 * @param currentRoute La ruta de la pantalla actualmente seleccionada.
 * @param onRouteSelected La función lambda que se invoca cuando el usuario toca un ítem.
 */
@Composable
fun BottomNavMenu(
    currentRoute: String?, // <-- CAMBIO 1: Recibe la ruta actual como un String
    onRouteSelected: (String) -> Unit // <-- CAMBIO 2: Recibe una función para notificar clics
) {
    val items = listOf(
        NavItem("Albergues", Icons.Filled.Home, Screen.Menu.route),
        NavItem("Reservas", Icons.Filled.DateRange, Screen.Reservation.route),
        NavItem("Historial", Icons.Filled.History, Screen.Historial.route),
        NavItem("Mi Cuenta", Icons.Filled.Person, Screen.Profile.route)
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onRouteSelected(item.route) }, // <-- CAMBIO 3: Llama a la función
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 12.sp) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(R.color.white),
                    unselectedIconColor = colorResource(R.color.pantone_cool_gray_8),
                    selectedTextColor = colorResource(R.color.pantone_302),
                    unselectedTextColor = colorResource(R.color.pantone_cool_gray_8),
                    indicatorColor = colorResource(R.color.pantone_320)
                )
            )
        }
    }
}


// ----------------------------------------------------------------------------------
// ¡AQUÍ ESTÁ LA MAGIA! AHORA PODEMOS PREVISUALIZARLO FÁCILMENTE
// ----------------------------------------------------------------------------------

@Preview(showBackground = true, name = "Pestaña Albergues Seleccionada")
@Composable
fun BottomNavMenuAlberguesPreview() {
    ProyectoTheme {
        BottomNavMenu(
            currentRoute = "Reservation",
            onRouteSelected = {} // No hace nada en la preview
        )
    }
}

@Preview(showBackground = true, name = "Pestaña Historial Seleccionada")
@Composable
fun BottomNavMenuHistorialPreview() {
    ProyectoTheme {
        BottomNavMenu(
            currentRoute = "Historial",
            onRouteSelected = { /* No hace nada en la preview */ }
        )
    }
}