
package org.example.project.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIApplication
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerModeDate
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
actual class DatePickerController {
    actual fun showDatePicker(
        onDateSelected: (Long?) -> Unit,
        onDismiss: () -> Unit
    ) {
        val alertController = UIAlertController.alertControllerWithTitle(
            title = "Select Date",
            message = "\n\n\n\n\n\n\n\n",
            preferredStyle = UIAlertControllerStyleActionSheet
        )

        val datePicker = UIDatePicker().apply {
            datePickerMode = UIDatePickerModeDate
            preferredDatePickerStyle = 1u // Compact style
        }

        alertController.setValue(datePicker, "contentViewController")

        val selectAction = UIAlertAction.actionWithTitle(
            title = "Select",
            style = UIAlertActionStyleDefault
        ) { _ ->
            val selectedDate = datePicker.date
            val timestamp = (selectedDate.timeIntervalSince1970 * 1000).toLong()
            onDateSelected(timestamp)
        }

        val cancelAction = UIAlertAction.actionWithTitle(
            title = "Cancel",
            style = UIAlertActionStyleCancel
        ) { _ ->
            onDismiss()
        }

        alertController.addAction(selectAction)
        alertController.addAction(cancelAction)

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(alertController, animated = true, completion = null)
    }
}

@Composable
actual fun rememberDatePickerController(): DatePickerController {
    return remember { DatePickerController() }
}
