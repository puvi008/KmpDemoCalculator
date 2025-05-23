package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.calculator.CalculatorAction
import org.example.project.calculator.CalculatorViewModel
import org.example.project.calculator.PlatformDialogController

@Composable
fun App() {
    MaterialTheme {
        val viewModel = remember { CalculatorViewModel() }
        val state by viewModel.state.collectAsState()
        var showDate by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display
            Surface(
                modifier = Modifier.fillMaxWidth().weight(0.4f),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = state.secondaryDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.primaryDisplay,
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Keypad
            Column(
                modifier = Modifier.weight(0.6f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.AllClear) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("AC")
                    }
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.Delete) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⌫")
                    }
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.ToggleScientific) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sci")
                    }
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.Operator("/")) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("÷")
                    }
                }

                // Numbers and basic operators
                (7..9).chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { number ->
                            Button(
                                onClick = { viewModel.onAction(CalculatorAction.Number(number)) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(number.toString())
                            }
                        }
                        Button(
                            onClick = { viewModel.onAction(CalculatorAction.Operator("*")) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("×")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (4..6).forEach { number ->
                        Button(
                            onClick = { viewModel.onAction(CalculatorAction.Number(number)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(number.toString())
                        }
                    }
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.Operator("-")) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("-")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..3).forEach { number ->
                        Button(
                            onClick = { viewModel.onAction(CalculatorAction.Number(number)) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(number.toString())
                        }
                    }
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.Operator("+")) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+")
                    }
                }

                if (state.isScientificMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("sin", "cos", "tan").forEach { function ->
                            Button(
                                onClick = { viewModel.onAction(CalculatorAction.Scientific(function)) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(function)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("√", "^", "ln").forEach { function ->
                            Button(
                                onClick = { viewModel.onAction(CalculatorAction.Scientific(function)) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(function)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.Number(0)) },
                        modifier = Modifier.weight(2f)
                    ) {
                        Text("0")
                    }
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.Decimal) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(".")
                    }
                    Button(
                        onClick = { viewModel.onAction(CalculatorAction.Calculate) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("=")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = { showDate = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("show the dialog")
                    }
                }

                if (showDate) {
                    MyDatePicker(onDismiss = {
                        showDate = false
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(onDismiss: () -> Unit) {
    val dateState = rememberDatePickerState()
    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        Text(
            text = "ok",
        )
    }) {
        DatePicker(
            state = dateState
        )
    }
}

