
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
