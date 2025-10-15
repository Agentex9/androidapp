package com.example.proyecto.models


data class MyServiceReservationList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<MyServiceReservations>?
)
