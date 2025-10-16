package com.example.proyecto.models

data class Hostel(
    val available_capacity: AvailableCapacity,
    val coordinates: List<Float>,
    val created_at: String,
    val created_by_name: String,
    val current_capacity: Int,
    val current_men_capacity: Int,
    val current_women_capacity: Int,
    val formatted_address: String,
    val id: String,
    val is_active: Boolean,
    val location: String,
    val location_data: Location,
    val men_capacity: Int,
    val name: String,
    val phone: String,
    val total_capacity: Int,
    val updated_at: String,
    val women_capacity: Int,
    val imageResId: Int? = null
)