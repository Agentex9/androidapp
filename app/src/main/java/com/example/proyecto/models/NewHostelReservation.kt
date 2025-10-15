package com.example.proyecto.models

data class NewHostelReservation(
    val arrival_date: String,
    val hostel: String,
    val men_quantity: Int,
    val type: String,
    val user: String,
    val women_quantity: Int,
    val status: String
)