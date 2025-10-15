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
import androidx.compose.material3.DatePickerDefaults
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
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyecto.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
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
        if (selectedTime.hour % 12 == 0) 12 else selectedTime.hour % 12,
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
                label = { Text(stringResource(R.string.label_arrival_date)) },
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
                label = { Text(stringResource(R.string.label_arrival_time)) },
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
            OutlinedButton(onClick = { showDate = true }) { Text(stringResource(R.string.action_choose_date)) }
            OutlinedButton(onClick = { showTime = true }) { Text(stringResource(R.string.action_choose_time)) }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { onConfirm(selectedDate, selectedTime) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = cPrimaryDk,
                    contentColor = cWhite
                )
            ) { Text(stringResource(R.string.action_confirm)) }
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
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDate = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.pantone_302)
                    )
                ) { Text(stringResource(R.string.action_cancel)) }
            }
        ) {
            DatePicker(state = dateState)
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
                        contentColor = colorResource(R.color.pantone_320)
                    )
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTime = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(R.color.pantone_302)
                    )
                ) { Text(stringResource(R.string.action_cancel)) }
            },
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timeState)
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
    label: String = stringResource(R.string.action_select_date),
    initialDateMillis: Long? = null,
    onDateSelected: (LocalDate) -> Unit
) {
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
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { showDate = true }
        )
    }

    if (showDate) {
        val cPrimary    = colorResource(R.color.pantone_320)
        val cPrimaryDk  = colorResource(R.color.pantone_302)
        val cWhite      = colorResource(R.color.white)
        val cGray       = colorResource(R.color.pantone_cool_gray_8)
        val cBg         = colorResource(R.color.white) // Fondo del calendario

        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDate = false
                        onDateSelected(selectedDate)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = cPrimary
                    )
                ) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDate = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = cPrimaryDk
                    )
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(
                state = dateState,
                colors = DatePickerDefaults.colors(
                    containerColor = cBg,
                    titleContentColor = cPrimaryDk,
                    headlineContentColor = cPrimaryDk,
                    weekdayContentColor = cGray,
                    subheadContentColor = cGray,
                    navigationContentColor = cPrimaryDk,
                    yearContentColor = cGray,
                    currentYearContentColor = cPrimaryDk,
                    selectedYearContentColor = cWhite,
                    selectedYearContainerColor = cPrimaryDk,
                    dayContentColor = cGray,
                    disabledDayContentColor = cGray.copy(alpha = 0.4f),
                    selectedDayContentColor = cWhite,
                    selectedDayContainerColor = cPrimary,
                    todayContentColor = cPrimaryDk,
                    todayDateBorderColor = cPrimary,
                    dayInSelectionRangeContainerColor = cPrimary.copy(alpha = 0.18f),
                    dayInSelectionRangeContentColor = cPrimaryDk
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    label: String = stringResource(R.string.action_select_time),
    initialHour: Int = 11,
    initialMinute: Int = 0,
    onTimeSelected: (LocalTime) -> Unit
) {
    var showTime by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(initialHour, initialMinute, is24Hour = false) // Aseguramos formato AM/PM
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
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { showTime = true }
        )
    }

    if (showTime) {
        val cPrimary    = colorResource(R.color.pantone_320)
        val cPrimaryDk  = colorResource(R.color.pantone_302)
        val cWhite      = colorResource(R.color.white)
        val cGray       = colorResource(R.color.pantone_cool_gray_8)
        val cBg         = colorResource(R.color.white)

        AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTime = false
                        onTimeSelected(selectedTime)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = cPrimary
                    )
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTime = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = cPrimaryDk
                    )
                ) { Text(stringResource(R.string.action_cancel)) }
            },
            text = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = timeState,
                        colors = TimePickerDefaults.colors(
                            containerColor = cBg,
                            clockDialColor = cPrimary.copy(alpha = 0.1f),
                            selectorColor = cPrimary,
                            timeSelectorSelectedContainerColor = cPrimary,
                            timeSelectorUnselectedContainerColor = cGray.copy(alpha = 0.2f),
                            timeSelectorSelectedContentColor = cWhite,
                            timeSelectorUnselectedContentColor = cPrimaryDk,
                            periodSelectorBorderColor = cPrimaryDk,
                            periodSelectorSelectedContainerColor = cPrimary,
                            periodSelectorUnselectedContainerColor = cGray.copy(alpha = 0.2f),
                            periodSelectorSelectedContentColor = cWhite,
                            periodSelectorUnselectedContentColor = cPrimaryDk
                        )
                    )
                }
            }
        )
    }
}


@Preview(showBackground = true, widthDp = 400)
@Composable
fun DatePickerFieldPreview() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.text_selected_date, selectedDate))
        Spacer(Modifier.height(8.dp))
        DatePickerField(
            label = stringResource(R.string.label_arrival_date),
            onDateSelected = { selectedDate = it }
        )
    }
}


@Preview(showBackground = true, widthDp = 400)
@Composable
fun TimePickerFieldPreview() {
    var selectedTime by remember { mutableStateOf(LocalTime.of(11, 0)) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.text_selected_time, selectedTime))
        Spacer(Modifier.height(8.dp))
        TimePickerField(
            label = stringResource(R.string.label_arrival_time),
            initialHour = 11,
            initialMinute = 0,
            onTimeSelected = { selectedTime = it }
        )
    }
}