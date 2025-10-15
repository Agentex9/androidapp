package com.example.proyecto.models

data class NewServiceReservation(
    val datetime_reserved: String,
    val men_quantity: Int,
    val service: String,
    val type: String,
    val user: String,
    val women_quantity: Int,
    val status: String
)