package com.example.proyecto.models

/**
 * Representa los datos necesarios para crear una nueva reserva de hostel.
 * Esta es la información que el formulario enviará al ViewModel.
 */
data class HostelReservationRequest(
    val hostel_id: String,
    val user_id: String,
    val start_date: String, // Se recomienda formato "YYYY-MM-DD"
    val end_date: String,
    val number_of_guests: Int
)