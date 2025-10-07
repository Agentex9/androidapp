package com.example.proyecto.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import androidx.compose.material3.DatePickerDefaults
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationPicker(
    modifier: Modifier = Modifier,
    initialDateMillis: Long? = null,
    initialHour: Int = 11,
    initialMinute: Int = 0,
    onConfirm: (LocalDate, LocalTime) -> Unit
) {
    val zone = ZoneId.systemDefault()

    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: System.currentTimeMillis()
    )
    val timeState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    val selectedDate = Instant.ofEpochMilli(dateState.selectedDateMillis ?: System.currentTimeMillis())
        .atZone(zone).toLocalDate()
    val selectedTime = LocalTime.of(timeState.hour, timeState.minute)

    val dateText = "%02d/%02d/%04d".format(selectedDate.dayOfMonth, selectedDate.monthValue, selectedDate.year)
    val timeText = "%02d:%02d %s".format(
        (if (selectedTime.hour % 12 == 0) 12 else selectedTime.hour % 12),
        selectedTime.minute,
        if (selectedTime.hour < 12) "AM" else "PM"
    )

    val cPrimary    = colorResource(R.color.pantone_320)
    val cPrimaryDk  = colorResource(R.color.pantone_302)
    val cGray       = colorResource(R.color.pantone_cool_gray_8)
    val cWhite      = colorResource(R.color.white)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // FECHA
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de llegada") },
                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cPrimary,
                    unfocusedBorderColor = cGray,
                    focusedLabelColor = cPrimaryDk,
                    unfocusedLabelColor = cGray,
                    focusedLeadingIconColor = cPrimaryDk,
                    unfocusedLeadingIconColor = cGray,
                    focusedTextColor = cPrimaryDk,
                    unfocusedTextColor = cGray,
                    cursorColor = cPrimary
                )
            )
            Box(
                Modifier
                    .matchParentSize()
                    .clickable { showDate = true }
            )
        }

        // HORA
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = timeText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora de llegada") },
                leadingIcon = { Icon(Icons.Outlined.Schedule, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cPrimary,
                    unfocusedBorderColor = cGray,
                    focusedLabelColor = cPrimaryDk,
                    unfocusedLabelColor = cGray,
                    focusedLeadingIconColor = cPrimaryDk,
                    unfocusedLeadingIconColor = cGray,
                    focusedTextColor = cPrimaryDk,
                    unfocusedTextColor = cGray,
                    cursorColor = cPrimary
                )
            )
            Box(
                Modifier
                    .matchParentSize()
                    .clickable { showTime = true }
            )
        }

        // Botones
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = { showDate = true }) { Text("Elegir fecha") }
            OutlinedButton(onClick = { showTime = true }) { Text("Elegir hora") }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { onConfirm(selectedDate, selectedTime) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = cPrimaryDk,
                    contentColor = cWhite
                )
            ) { Text("Confirmar") }
        }
    }

    // Date picker
    if (showDate) {
        DatePickerDialog(
            onDismissRequest = { showDate = false },

            confirmButton = {
                TextButton(
                    onClick = { showDate = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.pantone_320)
                    )
                ) { Text("OK") }
            },

            dismissButton = {
                TextButton(
                    onClick = { showDate = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.pantone_302)
                    )
                ) { Text("Cancelar") }
            }
        )

        {
            val cPrimary   = colorResource(R.color.pantone_320)
            val cPrimaryDk = colorResource(R.color.pantone_302)
            val cGray      = colorResource(R.color.pantone_cool_gray_8)
            val cBg        = colorResource(R.color.white)      // o pantone_621 si quieres fondo verdoso

            DatePicker(
            state = dateState,
            colors = DatePickerDefaults.colors(
                containerColor = cBg,
                titleContentColor = cPrimaryDk,
                headlineContentColor = cPrimaryDk,                 // “1 oct 2025”
                weekdayContentColor = cGray,                       // D L M...
                subheadContentColor = cGray,                       // “Octubre de 2025”
                navigationContentColor = cPrimaryDk,               // flechas
                yearContentColor = cGray,
                currentYearContentColor = cPrimaryDk,
                selectedYearContentColor = cBg,
                selectedYearContainerColor = cPrimaryDk,
                dayContentColor = cGray,
                disabledDayContentColor = cGray.copy(alpha = 0.4f),
                selectedDayContentColor = cBg,                     // número del día seleccionado
                selectedDayContainerColor = cPrimary,              // círculo del día seleccionado
                todayContentColor = cPrimaryDk,
                todayDateBorderColor = cPrimary,                   // borde del “hoy”
                dayInSelectionRangeContainerColor = cPrimary.copy(alpha = 0.18f),
                dayInSelectionRangeContentColor = cPrimaryDk
            )
            )
        }
    }

    // Time picker
    if (showTime) {
        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(
                    onClick = { showTime = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.pantone_320) // Color del texto OK
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTime = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.pantone_302) // Color del texto Cancelar
                    )
                ) {
                    Text("Cancelar")
                }
            },
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = timeState,
                        colors = TimePickerDefaults.colors(
                            containerColor = colorResource(R.color.white),                     // Fondo del diálogo
                            clockDialColor = colorResource(R.color.pantone_621),        // fondo del reloj
                            selectorColor = colorResource(R.color.pantone_320),         // círculo que selecciona hora/minuto
                            timeSelectorSelectedContainerColor = colorResource(R.color.pantone_302), // Fondo detrás de la hora/minuto seleccionados
                            timeSelectorUnselectedContainerColor = colorResource(R.color.pantone_621),
                            timeSelectorSelectedContentColor = colorResource(R.color.white),   // Texto de hora/minuto seleccionados
                            timeSelectorUnselectedContentColor = colorResource(R.color.pantone_cool_gray_8),                            periodSelectorBorderColor = colorResource(R.color.pantone_302),
                            periodSelectorSelectedContainerColor = colorResource(R.color.pantone_320),
                            periodSelectorUnselectedContainerColor = colorResource(R.color.pantone_cool_gray_8),
                            periodSelectorSelectedContentColor = colorResource(R.color.white),
                            periodSelectorUnselectedContentColor = colorResource(R.color.white)
                        )
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
private fun ReservationPickerPreview() {
    MaterialTheme {
        ReservationPicker(
            modifier = Modifier.padding(16.dp),
            onConfirm = { _, _ -> }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String = "Select Date",
    initialDateMillis: Long? = null,
    onDateSelected: (LocalDate) -> Unit
) {
    val zone = ZoneId.systemDefault()
    var showDate by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: System.currentTimeMillis()
    )

    val selectedDate = dateState.selectedDateMillis?.let {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC).toLocalDate()
    } ?: LocalDate.now()
    val dateText = "%02d/%02d/%04d".format(selectedDate.dayOfMonth, selectedDate.monthValue, selectedDate.year)

    Box(Modifier.fillMaxWidth()){
        OutlinedTextField(
        value = dateText,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
    )
        Box(
            Modifier
                .matchParentSize()
                .clickable { showDate = true }
        )
    }

    if (showDate) {
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    showDate = false
                    onDateSelected(selectedDate)
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    label: String = "Select Time",
    initialHour: Int = 11,
    initialMinute: Int = 0,
    onTimeSelected: (LocalTime) -> Unit
) {
    var showTime by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(initialHour, initialMinute)

    val selectedTime = LocalTime.of(timeState.hour, timeState.minute)
    val timeText = "%02d:%02d %s".format(
        (if (selectedTime.hour % 12 == 0) 12 else selectedTime.hour % 12),
        selectedTime.minute,
        if (selectedTime.hour < 12) "AM" else "PM"
    )
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = timeText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { showTime = true }
        )
    }

    if (showTime) {
        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(onClick = {
                    showTime = false
                    onTimeSelected(selectedTime)
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTime = false }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timeState)
            }
        )
    }
}


@Preview(showBackground = true, widthDp = 400)
@Composable
fun DatePickerFieldPreview() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selected date: $selectedDate")
        Spacer(Modifier.height(8.dp))
        DatePickerField(
            label = "Arrival Date",
            onDateSelected = { selectedDate = it }
        )
    }
}


@Preview(showBackground = true, widthDp = 400)
@Composable
fun TimePickerFieldPreview() {
    var selectedTime by remember { mutableStateOf(LocalTime.of(11, 0)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Selected time: $selectedTime")
        Spacer(Modifier.height(8.dp))
        TimePickerField(
            label = "Arrival Time",
            initialHour = 11,
            initialMinute = 0,
            onTimeSelected = { selectedTime = it }
        )
    }
}
