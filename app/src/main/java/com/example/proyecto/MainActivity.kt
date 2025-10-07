package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import com.example.proyecto.Navigation.Screen
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.components.BottomNavMenu
import com.example.proyecto.data.ResultState
import com.example.proyecto.screens.*
import com.example.proyecto.ui.theme.ProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
fun App() {
    ProyectoTheme {
        val nav = rememberNavController()
        val generalViewModel: GeneralViewModel = viewModel()

        // 🔹 Ruta actual para sincronizar con BottomNavMenu
        val navBackStackEntry by nav.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // 🔹 Rutas donde NO se muestra el BottomNavMenu
        val hideBottomNav = listOf(
            Screen.Login.route,
            Screen.Register.route,
            Screen.OTP.route
        )

        Scaffold(
            bottomBar = {
                if (currentRoute !in hideBottomNav) {
                    BottomNavMenu(
                        currentRoute = currentRoute,
                        onRouteSelected = { route ->
                            nav.navigate(route) {
                                // Evita apilar pantallas
                                popUpTo(nav.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->

            NavHost(
                navController = nav,
                startDestination = Screen.Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // ------------------ LOGIN ------------------
                composable(Screen.Login.route) {
                    LogIn(
                        OnLogIn = {
                            nav.navigate(Screen.OTP.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        Preregister = { nav.navigate(Screen.Register.route) },
                        VM = generalViewModel
                    )
                }

                // ------------------ REGISTER ------------------
                composable(Screen.Register.route) {
                    PreRegistroScreen(
                        generalViewModel = generalViewModel,
                        onDone = {
                            nav.navigate(Screen.OTP.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        onBack = { nav.popBackStack() }
                    )
                }

                // ------------------ OTP ------------------
                composable(Screen.OTP.route) {
                    val context = LocalContext.current
                    val loginState by generalViewModel.loginState.collectAsState()

                    OtpScreen(
                        onOtpSubmit = { code ->
                            generalViewModel.updateOTP(code)
                            generalViewModel.loginUser(context)
                        },
                        onResend = { generalViewModel.verifyLogin() }
                    )

                    // 🔹 Navega al menú principal cuando el login sea exitoso
                    LaunchedEffect(loginState) {
                        if (loginState is ResultState.Success) {
                            nav.navigate(Screen.Menu.route) {
                                popUpTo(Screen.OTP.route) { inclusive = true }
                            }
                        }
                    }
                }

                // ------------------ MENU PRINCIPAL ------------------
                composable(Screen.Menu.route) {
                    PreviewReservationScreenLayout()
                }

                // ------------------ RESERVATION ------------------
                composable(Screen.Reservation.route) {
                    ReservationScreen(
                        onClickHostel = {
                            nav.navigate(Screen.HostelReservation.createRoute())
                        },
                        onClickService = {
                            nav.navigate(Screen.ServiceReservation.createRoute())
                        },
                        onBack = { nav.popBackStack() }
                    )
                }

                // ------------------ HOSTEL RESERVATION ------------------
                composable(
                    Screen.HostelReservation.route,
                    arguments = listOf(
                        navArgument("hostelId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val hostelId = backStackEntry.arguments?.getString("hostelId")
                    val userId = "1234"
                    HReservationScreen(
                        preselectedHostelId = hostelId,
                        userId = userId,
                        viewModel = generalViewModel
                    )
                }

                // ------------------ SERVICE RESERVATION ------------------
                composable(
                    Screen.ServiceReservation.route,
                    arguments = listOf(
                        navArgument("serviceId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("hostelId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val serviceId = backStackEntry.arguments?.getString("serviceId")
                    val hostelId = backStackEntry.arguments?.getString("hostelId")
                    val userId = "1234"

                    ServiceReservationScreen(
                        preselectedServiceId = serviceId,
                        preselectedHostelId = hostelId,
                        userId = userId,
                        viewModel = generalViewModel,
                        onBack = { nav.popBackStack() }
                    )
                }

                // ------------------ HISTORIAL ------------------
                composable(Screen.Historial.route) { }

                // ------------------ PROFILE ------------------
                composable(Screen.Profile.route) { }
            }
        }
    }
}

//Poder ver cambios en telefono sin iniciar sesion, pre-registro o OTP
/*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.proyecto.Navigation.Screen
import com.example.proyecto.ViewModel.GeneralViewModel
import com.example.proyecto.components.BottomNavMenu
import com.example.proyecto.data.ResultState
import com.example.proyecto.screens.*
import com.example.proyecto.ui.theme.ProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
fun App() {
    ProyectoTheme {
        val nav = rememberNavController()
        val generalViewModel: GeneralViewModel = viewModel()

        // 🔹 Detectar la ruta actual
        val navBackStackEntry by nav.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // 🔹 Pantallas donde NO se muestra el BottomNav
        val hideBottomNav = listOf(
            Screen.Login.route,
            Screen.Register.route,
            Screen.OTP.route
        )

        Scaffold(
            bottomBar = {
                if (currentRoute !in hideBottomNav) {
                    BottomNavMenu(
                        currentRoute = currentRoute,
                        onRouteSelected = { route ->
                            nav.navigate(route) {
                                // Evita acumulación de pantallas
                                popUpTo(nav.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->

            // 🔹 Empezar directamente en la pantalla de MENÚ
            NavHost(
                navController = nav,
                startDestination = Screen.Menu.route,
                modifier = Modifier.padding(innerPadding)
            ) {

                // ------------------ MENÚ PRINCIPAL ------------------
                composable(Screen.Menu.route) {
                    PreviewReservationScreenLayout()
                }

                // ------------------ ALBERGUES ------------------
                composable("albergues") {
                    PreviewReservationScreenLayout()
                }

                // ------------------ RESERVAS ------------------
                composable("reservas") {
                    ReservationScreen(
                        onClickHostel = {
                            nav.navigate(Screen.HostelReservation.createRoute())
                        },
                        onClickService = {
                            nav.navigate(Screen.ServiceReservation.createRoute())
                        },
                        onBack = { nav.popBackStack() }
                    )
                }

                // ------------------ HISTORIAL ------------------
                composable("historial") {
                    // TODO: Pantalla historial real
                    HistorialScreenPlaceholder()
                }

                // ------------------ PERFIL ------------------
                composable("cuenta") {
                    // TODO: Pantalla perfil real
                    ProfileScreenPlaceholder()
                }

                // ------------------ HOSTEL RESERVATION ------------------
                composable(
                    Screen.HostelReservation.route,
                    arguments = listOf(
                        navArgument("hostelId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val hostelId = backStackEntry.arguments?.getString("hostelId")
                    val userId = "1234"
                    HReservationScreen(
                        preselectedHostelId = hostelId,
                        userId = userId,
                        viewModel = generalViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun HistorialScreenPlaceholder() {
    androidx.compose.material3.Text(
        text = "📜 Historial de Reservas",
        modifier = Modifier.padding(20.dp)
    )
}

@Composable
fun ProfileScreenPlaceholder() {
    androidx.compose.material3.Text(
        text = "👤 Mi Perfil",
        modifier = Modifier.padding(20.dp)
    )
}
*/