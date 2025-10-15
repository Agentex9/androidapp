package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.proyecto.components.HostelsMapView
import com.example.proyecto.data.NewHostelReservationState
import com.example.proyecto.data.ResultState
import com.example.proyecto.screens.*
import com.example.proyecto.ui.theme.ProyectoTheme
import com.example.proyecto.utilities.TokenManager

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
        val context = LocalContext.current
        val tokenManager = TokenManager(context)
        val user = tokenManager.getUser()
        val userId = user?.id ?: ""


        // 🔹 Rutas donde NO se muestra el BottomNavMenu
        val hideBottomNav = listOf(
            Screen.Login.route,
            Screen.Register.route,
            Screen.OTP.route,
            Screen.Launcher.route
            )

        Scaffold(
            topBar = {},
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

            // Change starting Route to Menu to see functionality
            NavHost(
                navController = nav,
                startDestination = Screen.Launcher.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // ------------------ LAUNCHER ------------------
                composable(Screen.Launcher.route) {
                    LauncherScreen(
                        generalViewModel = generalViewModel,
                        onAuthorized = {
                            nav.navigate(Screen.Menu.route) {
                                popUpTo(Screen.Launcher.route) { inclusive = true }
                            }
                        },
                        onUnauthorized = {
                            nav.navigate(Screen.Login.route) {
                                popUpTo(Screen.Launcher.route) { inclusive = true }
                            }
                        }
                    )
                }
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
                    PreviewReservationScreenLayout(
                        vm = generalViewModel,
                        onClick = { hostelId ->
                            nav.navigate(Screen.HostelInfo.createRoute(hostelId))
                                  },
                        onClick2 = { serviceId ->
                            nav.navigate(Screen.ServiceInfo.createRoute(serviceId))
                        }
                    )
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
                    HReservationScreen(
                        preselectedHostelId = hostelId,
                        userId = userId,
                        viewModel = generalViewModel,
                        onBack = { nav.popBackStack() },
                        onSuccessNavigate = {
                            nav.navigate(Screen.Menu.route) {
                                popUpTo(Screen.Menu.route) { inclusive = true }
                            }
                        }
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

                    ServiceReservationScreen(
                        preselectedServiceId = serviceId,
                        preselectedHostelId = hostelId,
                        userId = userId,
                        viewModel = generalViewModel,
                        onBack = { nav.popBackStack() },
                        onSuccessNavigate = {
                            nav.navigate(Screen.Menu.route) {
                                popUpTo(Screen.Menu.route) { inclusive = true }
                            }
                        }
                    )
                }

                // ------------------ HISTORIAL ------------------
                composable(Screen.Historial.route) {
                    historyScreen(generalViewModel)
                }

                // ------------------ PROFILE ------------------
                composable(Screen.Profile.route) {
                    PerfilScreen(navigate = {
                        // Clear session first
                        generalViewModel.resetsession()

                        // Navigate to Launcher
                        nav.navigate(Screen.Launcher.route) {
                            popUpTo(nav.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    })
                }

                // ------------------ HOSTEL INFO ------------------
                composable(
                    Screen.HostelInfo.route,
                    arguments = listOf(navArgument("hostelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val hostelId = backStackEntry.arguments?.getString("hostelId") ?: ""
                    HostelDetailScreen(
                        hostelId = hostelId,
                        viewModel = generalViewModel,
                        onBack = { nav.popBackStack() },
                        onReserveClick = { id ->
                            nav.navigate(Screen.HostelReservation.createRoute(id))
                        }
                    )

                }

                // ------------------ Service INFO ------------------

                composable(
                    Screen.ServiceInfo.route,
                    arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val serviceid = backStackEntry.arguments?.getString("serviceId") ?: ""
                    ServiceDetailScreen(
                        serviceId = serviceid,
                        viewModel = generalViewModel,
                        onBack = { nav.popBackStack() },
                        onReserveClick = { serviceId, hostelId ->
                            nav.navigate(Screen.ServiceReservation.createRoute(serviceId, hostelId))
                        }
                    )

                }
            }
        }
    }
}
