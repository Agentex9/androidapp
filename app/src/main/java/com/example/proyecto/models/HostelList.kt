package com.example.proyecto.models


data class HostelList(
    val count: Int,
    val next: String,
    val previous: String,
    val hostels: List<Hostel>
)