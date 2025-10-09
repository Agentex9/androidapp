package com.example.proyecto.models

data class Location(
    val address: String,
    val city: String,
    val coordinates: List<Int>,
    val country: String,
    val created_at: String,
    val formatted_address: String,
    val google_maps_url: String,
    val id: String,
    val landmarks: String,
    val latitude: String,
    val longitude: String,
    val state: String,
    val timezone: String,
    val updated_at: String,
    val zip_code: String
)