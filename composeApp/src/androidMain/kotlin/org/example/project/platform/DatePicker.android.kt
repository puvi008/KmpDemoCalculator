
package org.example.project.platform

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

actual class DatePickerController {
    private var showDialog by mutableStateOf(false)
    private var onDateSelectedCallback: (Long?) -> Unit = {}
    private var onDismissCallback: () -> Unit = {}

    actual fun showDatePicker(
        onDateSelected: (Long?) -> Unit,
        onDismiss: () -> Unit
    ) {
        this.onDateSelectedCallback = onDateSelected
        this.onDismissCallback = onDismiss
        showDialog = true
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerDialogContent() {
        if (showDialog) {
            val datePickerState = rememberDatePickerState()
            
            DatePickerDialog(
                onDismissRequest = {
                    showDialog = false
                    onDismissCallback()
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            onDateSelectedCallback(datePickerState.selectedDateMillis)
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            onDismissCallback()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
actual fun rememberDatePickerController(): DatePickerController {
    val controller = remember { DatePickerController() }
    controller.DatePickerDialogContent()
    return controller
}
