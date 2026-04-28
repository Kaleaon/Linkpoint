package com.linkpoint.linden.llmath

import kotlin.math.*

class Calc {

    companion object {
        const val X_POS:          String = "PX"
        const val Y_POS:          String = "PY"
        const val Z_POS:          String = "PZ"
        const val X_SCALE:        String = "SX"
        const val Y_SCALE:        String = "SY"
        const val Z_SCALE:        String = "SZ"
        const val X_ROT:          String = "RX"
        const val Y_ROT:          String = "RY"
        const val Z_ROT:          String = "RZ"
        const val HOLLOW:         String = "HLW"
        const val CUT_BEGIN:      String = "CB"
        const val CUT_END:        String = "CE"
        const val PATH_BEGIN:     String = "PB"
        const val PATH_END:       String = "PE"
        const val TWIST_BEGIN:    String = "TB"
        const val TWIST_END:      String = "TE"
        const val X_SHEAR:        String = "SHX"
        const val Y_SHEAR:        String = "SHY"
        const val X_TAPER:        String = "TPX"
        const val Y_TAPER:        String = "TPY"
        const val RADIUS_OFFSET:  String = "ROF"
        const val REVOLUTIONS:    String = "REV"
        const val SKEW:           String = "SKW"
        const val X_HOLE:         String = "HLX"
        const val Y_HOLE:         String = "HLY"
        const val TEX_U_SCALE:    String = "TSU"
        const val TEX_V_SCALE:    String = "TSV"
        const val TEX_U_OFFSET:   String = "TOU"
        const val TEX_V_OFFSET:   String = "TOV"
        const val TEX_ROTATION:   String = "TROT"
        const val TEX_TRANSPARENCY: String = "TRNS"
        const val TEX_GLOW:       String = "GLOW"

        private var instance: Calc? = null

        fun getInstance(): Calc {
            if (instance == null) instance = Calc()
            return instance!!
        }

        fun cleanUp() {
            instance = null
        }
    }

    var lastErrorPos: Int = 0
        private set

    private val constants: MutableMap<String, Float> = mutableMapOf(
        "PI"           to F_PI,
        "TWO_PI"       to F_TWO_PI,
        "PI_BY_TWO"    to F_PI_BY_TWO,
        "SQRT_TWO_PI"  to F_SQRT_TWO_PI,
        "SQRT2"        to F_SQRT2,
        "SQRT3"        to F_SQRT3,
        "DEG_TO_RAD"   to DEG_TO_RAD,
        "RAD_TO_DEG"   to RAD_TO_DEG,
        "GRAVITY"      to GRAVITY,
    )

    private val variables: MutableMap<String, Float> = mutableMapOf()

    fun setVar(name: String, value: Float)  { variables[name] = value }
    fun clearVar(name: String)              { variables.remove(name) }
    fun clearAllVariables()                 { variables.clear() }

    fun evalString(expression: String): Pair<Boolean, Float> {
        lastErrorPos = 0
        val expr = expression.uppercase().trim()
        val parser = ExprParser(expr, constants, variables)
        return try {
            val result = parser.parse()
            Pair(true, result)
        } catch (e: CalcParseException) {
            lastErrorPos = e.position
            Pair(false, 0f)
        }
    }

    private class CalcParseException(val position: Int, message: String) : Exception(message)

    private class ExprParser(
        private val input: String,
        private val constants: Map<String, Float>,
        private val variables: Map<String, Float>
    ) {
        private var pos: Int = 0

        fun parse(): Float {
            skipSpaces()
            if (pos < input.length && input[pos] == '=') pos++
            val result = parseExpression()
            skipSpaces()
            if (pos != input.length) {
                throw CalcParseException(pos, "Syntax error at $pos")
            }
            return result
        }

        private fun skipSpaces() {
            while (pos < input.length && input[pos] == ' ') pos++
        }

        private fun parseExpression(): Float {
            skipSpaces()
            var value = parseTerm()
            skipSpaces()
            while (pos < input.length) {
                when (input[pos]) {
                    '+' -> { pos++; value += parseTerm() }
                    '-' -> { pos++; value -= parseTerm() }
                    else -> break
                }
                skipSpaces()
            }
            return value
        }

        private fun parseTerm(): Float {
            var value = parsePower()
            skipSpaces()
            while (pos < input.length) {
                when (input[pos]) {
                    '*' -> { pos++; value *= parsePower() }
                    '/' -> { pos++; value /= parsePower() }
                    '%' -> { pos++; val b = parsePower(); value = value % b }
                    else -> break
                }
                skipSpaces()
            }
            return value
        }

        private fun parsePower(): Float {
            var base = parseUnary()
            skipSpaces()
            while (pos < input.length && input[pos] == '^') {
                pos++
                val exp = parseUnary()
                base = base.pow(exp)
                skipSpaces()
            }
            return base
        }

        private fun parseUnary(): Float {
            skipSpaces()
            return when {
                pos < input.length && input[pos] == '-' -> { pos++; -parseFactor() }
                pos < input.length && input[pos] == '+' -> { pos++; parseFactor() }
                else -> parseFactor()
            }
        }

        private fun parseFactor(): Float {
            skipSpaces()
            if (pos >= input.length) throw CalcParseException(pos, "Unexpected end")

            // grouped expression
            if (input[pos] == '(') {
                pos++
                val v = parseExpression()
                skipSpaces()
                if (pos >= input.length || input[pos] != ')') {
                    throw CalcParseException(pos, "Expected ')'")
                }
                pos++
                return v
            }

            // numeric literal
            if (input[pos].isDigit() || input[pos] == '.') {
                return parseNumber()
            }

            // identifier or function call
            if (input[pos].isLetter() || input[pos] == '_') {
                val start = pos
                while (pos < input.length && (input[pos].isLetterOrDigit() || input[pos] == '_')) pos++
                val name = input.substring(start, pos)
                skipSpaces()

                // unary functions
                val unary: ((Float) -> Float)? = when (name) {
                    "SIN"  -> { a -> sin(DEG_TO_RAD * a) }
                    "COS"  -> { a -> cos(DEG_TO_RAD * a) }
                    "TAN"  -> { a -> tan(DEG_TO_RAD * a) }
                    "ASIN" -> { a -> asin(a) * RAD_TO_DEG }
                    "ACOS" -> { a -> acos(a) * RAD_TO_DEG }
                    "ATAN" -> { a -> atan(a) * RAD_TO_DEG }
                    "SINR" -> { a -> sin(a) }
                    "COSR" -> { a -> cos(a) }
                    "TANR" -> { a -> tan(a) }
                    "ASINR"-> { a -> asin(a) }
                    "ACOSR"-> { a -> acos(a) }
                    "ATANR"-> { a -> atan(a) }
                    "SQRT" -> { a -> sqrt(a) }
                    "LOG"  -> { a -> ln(a) }
                    "EXP"  -> { a -> exp(a) }
                    "ABS"  -> { a -> abs(a) }
                    "FLR"  -> { a -> floor(a) }
                    "CEIL" -> { a -> ceil(a) }
                    else   -> null
                }
                if (unary != null) {
                    expectChar('(')
                    val arg = parseExpression()
                    expectChar(')')
                    val result = unary(arg)
                    if (result.isNaN()) throw CalcParseException(pos, "Domain error")
                    return result
                }

                // binary functions
                when (name) {
                    "ATAN2" -> {
                        expectChar('(')
                        val a = parseExpression(); expectChar(',')
                        val b = parseExpression(); expectChar(')')
                        return atan2(a, b)
                    }
                    "MIN" -> {
                        expectChar('(')
                        val a = parseExpression(); expectChar(',')
                        val b = parseExpression(); expectChar(')')
                        return minOf(a, b)
                    }
                    "MAX" -> {
                        expectChar('(')
                        val a = parseExpression(); expectChar(',')
                        val b = parseExpression(); expectChar(')')
                        return maxOf(a, b)
                    }
                }

                // symbol lookup (constants then variables)
                val v = constants[name] ?: variables[name]
                    ?: throw CalcParseException(start, "Unknown symbol: $name")
                return v
            }

            throw CalcParseException(pos, "Unexpected character '${input[pos]}'")
        }

        private fun parseNumber(): Float {
            val start = pos
            while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
            if (pos < input.length && (input[pos] == 'E' || input[pos] == 'e')) {
                pos++
                if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) pos++
                while (pos < input.length && input[pos].isDigit()) pos++
            }
            return input.substring(start, pos).toFloatOrNull()
                ?: throw CalcParseException(start, "Invalid number")
        }

        private fun expectChar(c: Char) {
            skipSpaces()
            if (pos >= input.length || input[pos] != c) {
                throw CalcParseException(pos, "Expected '$c'")
            }
            pos++
        }
    }
}
