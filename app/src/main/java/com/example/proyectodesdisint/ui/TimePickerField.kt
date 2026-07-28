package com.example.proyectodesdisint.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Locale

@Composable
fun TimePickerField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val initialTime = remember(value) { parseTime(value) }
    val openPicker = {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onValueChange(formatTime(hourOfDay, minute))
            },
            initialTime.first,
            initialTime.second,
            true
        ).show()
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { openPicker() }
        )
    }
}

private fun parseTime(value: String): Pair<Int, Int> {
    val parts = value.split(":")
    if (parts.size == 2) {
        val hour = parts[0].toIntOrNull()
        val minute = parts[1].toIntOrNull()
        if (hour != null && minute != null && hour in 0..23 && minute in 0..59) {
            return hour to minute
        }
    }

    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.HOUR_OF_DAY) to calendar.get(Calendar.MINUTE)
}

private fun formatTime(hour: Int, minute: Int): String {
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}
