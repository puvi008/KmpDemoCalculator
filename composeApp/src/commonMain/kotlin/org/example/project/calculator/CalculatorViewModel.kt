
package org.example.project.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.E

class CalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    private var currentExpression = StringBuilder()
    private var lastResult: Double? = null

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> appendNumber(action.number)
            is CalculatorAction.Operator -> appendOperator(action.operator)
            is CalculatorAction.Decimal -> appendDecimal()
            is CalculatorAction.Clear -> clear()
            is CalculatorAction.AllClear -> allClear()
            is CalculatorAction.Calculate -> calculate()
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.ToggleScientific -> toggleScientificMode()
            is CalculatorAction.Constant -> appendConstant(action.value)
            is CalculatorAction.Function -> appendFunction(action.name)
        }
        updateDisplay()
    }

    private fun appendNumber(number: Int) {
        if (_state.value.primaryDisplay == "0") {
            currentExpression.clear()
        }
        currentExpression.append(number)
    }

    private fun appendOperator(operator: String) {
        if (currentExpression.isNotEmpty()) {
            currentExpression.append(operator)
        } else if (lastResult != null) {
            currentExpression.append(lastResult).append(operator)
        }
    }

    private fun appendDecimal() {
        if (!currentExpression.contains('.')) {
            if (currentExpression.isEmpty()) {
                currentExpression.append("0")
            }
            currentExpression.append(".")
        }
    }

    private fun appendConstant(value: Double) {
        currentExpression.append(value)
    }

    private fun appendFunction(name: String) {
        currentExpression.append("$name(")
    }

    private fun clear() {
        if (currentExpression.isNotEmpty()) {
            currentExpression.clear()
            updateDisplay()
        }
    }

    private fun allClear() {
        currentExpression.clear()
        lastResult = null
        _state.value = CalculatorState()
    }

    private fun delete() {
        if (currentExpression.isNotEmpty()) {
            currentExpression.deleteAt(currentExpression.length - 1)
        }
    }

    private fun toggleScientificMode() {
        _state.value = _state.value.copy(isScientificMode = !_state.value.isScientificMode)
    }

    private fun calculate() {
        try {
            // Placeholder for actual calculation logic
            // Will integrate with MathEvaluator later
            _state.value = _state.value.copy(
                secondaryDisplay = currentExpression.toString(),
                isError = false
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                primaryDisplay = "Error",
                isError = true
            )
        }
    }

    private fun updateDisplay() {
        _state.value = _state.value.copy(
            primaryDisplay = if (currentExpression.isEmpty()) "0" 
                           else currentExpression.toString()
        )
    }
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operator(val operator: String) : CalculatorAction()
    data class Function(val name: String) : CalculatorAction()
    data class Constant(val value: Double) : CalculatorAction()
    object Decimal : CalculatorAction()
    object Clear : CalculatorAction()
    object AllClear : CalculatorAction()
    object Calculate : CalculatorAction()
    object Delete : CalculatorAction()
    object ToggleScientific : CalculatorAction()
}
package org.example.project.calculator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan
import kotlin.math.ln
import kotlin.math.log10

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
            "%.8f".format(number).trimEnd('0').trimEnd('.')
        }
    }
}
