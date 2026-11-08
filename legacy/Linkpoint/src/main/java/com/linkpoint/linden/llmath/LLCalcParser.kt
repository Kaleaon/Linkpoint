package com.linkpoint.linden.llmath

class LLCalcParser(private val variables: Map<String, Double> = emptyMap()) {

    private var pos = 0
    private var input = ""

    fun evaluate(expression: String): Double {
        input = expression.trim()
        pos = 0
        val result = parseExpr()
        if (pos < input.length) throw IllegalArgumentException("Unexpected character at pos $pos: '${input[pos]}'")
        return result
    }

    private fun skipWs() { while (pos < input.length && input[pos].isWhitespace()) pos++ }

    private fun parseExpr(): Double {
        var left = parseTerm()
        skipWs()
        while (pos < input.length && (input[pos] == '+' || input[pos] == '-')) {
            val op = input[pos++]
            val right = parseTerm()
            left = if (op == '+') left + right else left - right
            skipWs()
        }
        return left
    }

    private fun parseTerm(): Double {
        var left = parseUnary()
        skipWs()
        while (pos < input.length && (input[pos] == '*' || input[pos] == '/' || input[pos] == '%')) {
            val op = input[pos++]
            val right = parseUnary()
            left = when (op) {
                '*' -> left * right
                '/' -> if (right == 0.0) throw ArithmeticException("Division by zero") else left / right
                else -> left % right
            }
            skipWs()
        }
        return left
    }

    private fun parseUnary(): Double {
        skipWs()
        if (pos < input.length && input[pos] == '-') { pos++; return -parsePower() }
        if (pos < input.length && input[pos] == '+') { pos++; return parsePower() }
        return parsePower()
    }

    private fun parsePower(): Double {
        val base = parsePrimary()
        skipWs()
        if (pos < input.length && input[pos] == '^') {
            pos++
            val exp = parseUnary()
            return Math.pow(base, exp)
        }
        return base
    }

    private fun parsePrimary(): Double {
        skipWs()
        if (pos >= input.length) throw IllegalArgumentException("Unexpected end of expression")
        if (input[pos] == '(') {
            pos++
            val v = parseExpr()
            skipWs()
            if (pos >= input.length || input[pos] != ')') throw IllegalArgumentException("Missing ')'")
            pos++
            return v
        }
        if (input[pos].isDigit() || (input[pos] == '.' && pos + 1 < input.length && input[pos + 1].isDigit())) {
            return parseNumber()
        }
        if (input[pos].isLetter() || input[pos] == '_') {
            return parseNamedValue()
        }
        throw IllegalArgumentException("Unexpected character '${input[pos]}' at pos $pos")
    }

    private fun parseNumber(): Double {
        val start = pos
        while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
        if (pos < input.length && (input[pos] == 'e' || input[pos] == 'E')) {
            pos++
            if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) pos++
            while (pos < input.length && input[pos].isDigit()) pos++
        }
        return input.substring(start, pos).toDouble()
    }

    private fun parseNamedValue(): Double {
        val start = pos
        while (pos < input.length && (input[pos].isLetterOrDigit() || input[pos] == '_')) pos++
        val name = input.substring(start, pos)
        skipWs()
        if (pos < input.length && input[pos] == '(') {
            pos++
            val arg = parseExpr()
            skipWs()
            if (pos >= input.length || input[pos] != ')') throw IllegalArgumentException("Missing ')'")
            pos++
            return when (name.lowercase()) {
                "sqrt" -> Math.sqrt(arg)
                "abs"  -> Math.abs(arg)
                "sin"  -> Math.sin(arg)
                "cos"  -> Math.cos(arg)
                "tan"  -> Math.tan(arg)
                "log"  -> Math.log(arg)
                "log10" -> Math.log10(arg)
                "floor" -> Math.floor(arg)
                "ceil"  -> Math.ceil(arg)
                "round" -> Math.round(arg).toDouble()
                else -> throw IllegalArgumentException("Unknown function: $name")
            }
        }
        return when (name.lowercase()) {
            "pi" -> Math.PI
            "e"  -> Math.E
            else -> variables[name] ?: throw IllegalArgumentException("Unknown variable: $name")
        }
    }
}
