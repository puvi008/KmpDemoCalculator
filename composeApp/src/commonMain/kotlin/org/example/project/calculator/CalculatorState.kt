
package org.example.project.calculator

data class CalculatorState(
    val primaryDisplay: String = "0",
    val secondaryDisplay: String = "",
    val isScientificMode: Boolean = false,
    val isError: Boolean = false
)
