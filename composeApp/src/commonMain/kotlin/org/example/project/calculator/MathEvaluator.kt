
package org.example.project.calculator

class MathEvaluator {
    private var pos = -1
    private var ch: Char = ' '
    private lateinit var expr: String

    fun evaluate(expression: String): Double {
        expr = expression.replace(" ", "")
        pos = -1
        nextChar()
        val result = parseExpression()
        if (pos < expr.length) throw RuntimeException("Unexpected: " + ch)
        return result
    }

    private fun nextChar() {
        ch = if (++pos < expr.length) expr[pos] else '\u0000'
    }

    private fun eat(charToEat: Char): Boolean {
        while (ch == ' ') nextChar()
        if (ch == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    private fun parseExpression(): Double {
        var x = parseTerm()
        while (true) {
            if (eat('+')) x += parseTerm()
            else if (eat('-')) x -= parseTerm()
            else return x
        }
    }

    private fun parseTerm(): Double {
        var x = parseFactor()
        while (true) {
            if (eat('*')) x *= parseFactor()
            else if (eat('/')) x /= parseFactor()
            else return x
        }
    }

    private fun parseFactor(): Double {
        if (eat('+')) return parseFactor()
        if (eat('-')) return -parseFactor()

        var x: Double
        val startPos = pos

        if (eat('(')) {
            x = parseExpression()
            eat(')')
        } else if (ch in '0'..'9' || ch == '.') {
            while (ch in '0'..'9' || ch == '.') nextChar()
            x = expr.substring(startPos, pos).toDouble()
        } else if (ch in 'a'..'z') {
            while (ch in 'a'..'z') nextChar()
            val func = expr.substring(startPos, pos)
            x = parseFactor()
            x = when (func) {
                "sin" -> kotlin.math.sin(x)
                "cos" -> kotlin.math.cos(x)
                "tan" -> kotlin.math.tan(x)
                "sqrt" -> kotlin.math.sqrt(x)
                "log" -> kotlin.math.log10(x)
                "ln" -> kotlin.math.ln(x)
                else -> throw RuntimeException("Unknown function: $func")
            }
        } else {
            throw RuntimeException("Unexpected: " + ch)
        }

        if (eat('^')) x = kotlin.math.pow(x, parseFactor())

        return x
    }

    companion object {
        const val PI = kotlin.math.PI
        const val E = kotlin.math.E
    }
}
