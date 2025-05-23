
package org.example.project.calculator

import kotlin.math.*

class MathEvaluator {
    fun evaluate(expression: String): Double {
        return try {
            val tokens = tokenize(expression)
            evaluateTokens(tokens)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid expression")
        }
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var currentNumber = StringBuilder()
        
        expression.forEach { char ->
            when {
                char.isDigit() || char == '.' -> currentNumber.append(char)
                char.isOperator() -> {
                    if (currentNumber.isNotEmpty()) {
                        tokens.add(currentNumber.toString())
                        currentNumber.clear()
                    }
                    tokens.add(char.toString())
                }
            }
        }
        
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber.toString())
        }
        
        return tokens
    }

    private fun evaluateTokens(tokens: List<String>): Double {
        if (tokens.isEmpty()) return 0.0
        
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<Char>()
        
        tokens.forEach { token ->
            when {
                token.isNumber() -> numbers.add(token.toDouble())
                token.isSingleOperator() -> {
                    while (operators.isNotEmpty() && hasPrecedence(token[0], operators.last())) {
                        numbers.add(applyOperation(operators.removeLast(), numbers.removeLast(), numbers.removeLast()))
                    }
                    operators.add(token[0])
                }
            }
        }
        
        while (operators.isNotEmpty()) {
            numbers.add(applyOperation(operators.removeLast(), numbers.removeLast(), numbers.removeLast()))
        }
        
        return numbers.last()
    }

    private fun applyOperation(operator: Char, b: Double, a: Double): Double = when (operator) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> if (b != 0.0) a / b else throw ArithmeticException("Division by zero")
        '^' -> a.pow(b)
        else -> throw IllegalArgumentException("Invalid operator")
    }

    private fun Char.isOperator() = this in "+-*/^()"
    private fun String.isNumber() = toDoubleOrNull() != null
    private fun String.isSingleOperator() = length == 1 && first().isOperator()
    
    private fun hasPrecedence(op1: Char, op2: Char): Boolean {
        if (op2 == '(' || op2 == ')') return false
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false
        if (op1 == '^' && (op2 != '^')) return false
        return true
    }
}
