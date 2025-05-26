
package org.example.project.platform

import androidx.compose.runtime.Composable

expect class DatePickerController() {
    fun showDatePicker(
        onDateSelected: (Long?) -> Unit,
        onDismiss: () -> Unit = {}
    )
}

@Composable
expect fun rememberDatePickerController(): DatePickerController
