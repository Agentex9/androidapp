package com.example.proyecto.data

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.example.proyecto.R
import com.example.proyecto.models.AvailableCapacity
import com.example.proyecto.models.Hostel
import com.example.proyecto.models.Location
import org.json.JSONArray

@DrawableRes
fun drawableId(ctx: Context, name: String?): Int? {
    if (name.isNullOrBlank()) return null
    val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
    return id.takeIf { it != 0 }
}

fun loadHostelsFromRaw(ctx: Context, @RawRes rawId: Int = R.raw.hostels): List<Hostel> {
    val json = ctx.resources.openRawResource(rawId).bufferedReader().use { it.readText() }
    val arr = JSONArray(json)
    val list = mutableListOf<Hostel>()
    for (i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        val lat = o.getDouble("latitude")
        val lon = o.getDouble("longitude")
        val imgId = drawableId(ctx, o.optString("image_key"))
        val state = o.optString("state")
        val city = o.optString("city")
        val country = o.optString("country")
        val neighborhood = o.optString("neighborhood") // colonia

        list += Hostel(
            id = o.getString("id"),
            name = o.getString("name"),
            imageResId = imgId, // usado por painterResource
            location_data = Location(
                address = "",
                city = city,
                state = state,
                country = country,
                zip_code = "",
                landmarks = neighborhood,          // ← colonia aquí
                latitude = lat.toString(),
                longitude = lon.toString(),
                coordinates = listOf(lat.toFloat(), lon.toFloat()),
                created_at = "", updated_at = "",
                formatted_address = "", google_maps_url = "",
                id = "", timezone = ""
            ),
            available_capacity = AvailableCapacity(
                additionalProp1 = "",
                additionalProp2 = "",
                additionalProp3 = ""
            ),
            coordinates = listOf(lat.toFloat(), lon.toFloat()),
            created_at = "",
            created_by_name = "",
            current_capacity = 0,
            current_men_capacity = 0,
            current_women_capacity = 0,
            formatted_address = "",
            is_active = true,
            location = o.optString("city"),
            men_capacity = 0,
            phone = "",
            total_capacity = 0,
            updated_at = "",
            women_capacity = 0
        )
    }
    return list
}
