package com.example.proyecto.models



data class MyHostelReservationList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<MyHostelReservations>?
)
