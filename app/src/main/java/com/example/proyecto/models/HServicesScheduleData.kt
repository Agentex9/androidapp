package com.example.proyecto.models

data class HServicesScheduleData(
    val created_at: String,
    val created_by_name: String,
    val day_name: String,
    val day_of_week: Int,
    val duration_hours: Float,
    val end_time: String,
    val id: String,
    val is_available: Boolean,
    val start_time: String,
    val updated_at: String
)