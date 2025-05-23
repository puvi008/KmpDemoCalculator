package org.example.project.calculator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class CalculatorViewModel {
    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Operator -> enterOperator(action.operator)
            is CalculatorAction.Calculate -> calculate()
            is CalculatorAction.AllClear -> clearAll()
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.ToggleScientific -> toggleScientific()
            is CalculatorAction.ToggleSign -> toggleSign()
            is CalculatorAction.Scientific -> applyScientificOperation(action.operation)
        }
    }

    private fun enterNumber(number: Int) {
        val currentState = _state.value
        if (currentState.waitingForSecondOperand) {
            _state.value = currentState.copy(
                primaryDisplay = number.toString(),
                waitingForSecondOperand = false
            )
        } else {
            _state.value = currentState.copy(
                primaryDisplay = if (currentState.primaryDisplay == "0")
                    number.toString()
                else
                    currentState.primaryDisplay + number
            )
        }
    }

    private fun enterOperator(operator: String) {
        val currentState = _state.value
        val currentNumber = currentState.primaryDisplay.toDoubleOrNull() ?: return

        if (currentState.firstOperand != null && !currentState.waitingForSecondOperand) {
            calculate()
        }

        _state.value = currentState.copy(
            firstOperand = currentNumber,
            currentOperation = operator,
            waitingForSecondOperand = true,
            secondaryDisplay = "${currentNumber} ${operator}"
        )
    }

    private fun calculate() {
        val currentState = _state.value
        val firstOperand = currentState.firstOperand ?: return
        val secondOperand = currentState.primaryDisplay.toDoubleOrNull() ?: return
        val operation = currentState.currentOperation ?: return

        val result = when (operation) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "*" -> firstOperand * secondOperand
            "/" -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.POSITIVE_INFINITY
            "^" -> firstOperand.pow(secondOperand)
            else -> return
        }

        _state.value = currentState.copy(
            primaryDisplay = formatResult(result),
            secondaryDisplay = "${firstOperand} ${operation} ${secondOperand} =",
            firstOperand = null,
            currentOperation = null,
            waitingForSecondOperand = false
        )
    }

    private fun clearAll() {
        _state.value = CalculatorState()
    }

    private fun delete() {
        val currentState = _state.value
        if (currentState.primaryDisplay.length > 1) {
            _state.value = currentState.copy(
                primaryDisplay = currentState.primaryDisplay.dropLast(1)
            )
        } else {
            _state.value = currentState.copy(primaryDisplay = "0")
        }
    }

    private fun enterDecimal() {
        val currentState = _state.value
        if (!currentState.primaryDisplay.contains(".")) {
            _state.value = currentState.copy(
                primaryDisplay = currentState.primaryDisplay + "."
            )
        }
    }

    private fun toggleScientific() {
        _state.value = _state.value.copy(
            isScientificMode = !_state.value.isScientificMode
        )
    }

    private fun toggleSign() {
        val currentState = _state.value
        val currentNumber = currentState.primaryDisplay.toDoubleOrNull() ?: return
        _state.value = currentState.copy(
            primaryDisplay = formatResult(-currentNumber)
        )
    }

    private fun applyScientificOperation(operation: String) {
        val currentState = _state.value
        val currentNumber = currentState.primaryDisplay.toDoubleOrNull() ?: return

        val result = when (operation) {
            "sin" -> sin(currentNumber)
            "cos" -> cos(currentNumber)
            "tan" -> tan(currentNumber)
            "sqrt" -> sqrt(currentNumber)
            "log" -> log10(currentNumber)
            "ln" -> ln(currentNumber)
            "pi" -> PI
            "e" -> E
            else -> return
        }

        _state.value = currentState.copy(
            primaryDisplay = formatResult(result),
            secondaryDisplay = "${operation}(${currentNumber})"
        )
    }

    private fun formatResult(number: Double): String {
        return if (number.toLong().toDouble() == number) {
            number.toLong().toString()
        } else {
            "%.8f$number".trimEnd('0').trimEnd('.')
        }
    }
}
