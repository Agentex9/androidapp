package com.example.proyecto.Navigation

sealed class Screen(val route: String) {
    data object Launcher : Screen("Launcher")
    data object Reservation : Screen("Reservation")
    data object Historial : Screen("Historial")
    data object Profile : Screen("Perfil")
    data object Login : Screen("Login")
    data object OTP : Screen("OTP")
    data object Register : Screen("Register")
    data object Menu : Screen("Menu")

    data object HostelReservation : Screen("HostelReservation?hostelId={hostelId}") {
        fun createRoute(hostelId: String? = null): String {
            return if (hostelId != null) {
                "HostelReservation?hostelId=$hostelId"
            } else {
                "HostelReservation"
            }
        }
    }

    data object ServiceReservation : Screen("ServiceReservation?serviceId={serviceId}&hostelId={hostelId}") {
        fun createRoute(serviceId: String? = null, hostelId: String? = null): String {
            return if (serviceId != null && hostelId != null) {
                "ServiceReservation?serviceId=$serviceId&hostelId=$hostelId"
            } else {
                "ServiceReservation" // no id
            }
        }
    }

    // Hostel info screen with hostelId placeholder
    data object HostelInfo : Screen("HostelInfo/{hostelId}") {
        fun createRoute(hostelId: String) = "HostelInfo/$hostelId"
    }

    // Service info screen with serviceId placeholder
    data object ServiceInfo : Screen("ServiceInfo/{serviceId}") {
        fun createRoute(serviceId: String) = "ServiceInfo/$serviceId"
    }
}

