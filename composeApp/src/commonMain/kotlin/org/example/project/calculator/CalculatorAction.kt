
package org.example.project.calculator

sealed interface CalculatorAction {
    data class Number(val number: Int) : CalculatorAction
    data class Operator(val operator: String) : CalculatorAction
    object Calculate : CalculatorAction
    object AllClear : CalculatorAction
    object Delete : CalculatorAction
    object Decimal : CalculatorAction
    object ToggleScientific : CalculatorAction
    object ToggleSign : CalculatorAction
    data class Scientific(val operation: String) : CalculatorAction
}
