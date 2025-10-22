package lalalala.basicscodelab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

data class CalculatorState(
    val displayText: String = "0",
    val previousValue: Double? = null,
    val currentOperator: String? = null,
    val resetDisplayOnNextInput: Boolean = false,
    val inverseTrig: Boolean = false,
    val isFunctionExpanded: Boolean = false
)

@Composable
fun rememberCalculatorState(): CalculatorStateHolder {
    return remember { CalculatorStateHolder() }
}

@Stable
class CalculatorStateHolder {
    var state by mutableStateOf(CalculatorState())
        private set

    fun onNumberClick(number: String) {
        state = if (state.resetDisplayOnNextInput) {
            state.copy(
                displayText = number,
                resetDisplayOnNextInput = false
            )
        } else {
            state.copy(
                displayText = if (state.displayText == "0") number else state.displayText + number
            )
        }
    }

    fun onOperatorClick(operator: String) {
        val newPreviousValue = if (!state.resetDisplayOnNextInput &&
            state.previousValue != null &&
            state.currentOperator != null) {
            calculateResult()
        } else {
            state.displayText.toDoubleOrNull()
        }

        state = state.copy(
            previousValue = newPreviousValue,
            currentOperator = operator,
            resetDisplayOnNextInput = true,
            displayText = if (!state.resetDisplayOnNextInput &&
                state.previousValue != null &&
                state.currentOperator != null) {
                formatResult(newPreviousValue ?: 0.0)
            } else {
                state.displayText
            }
        )
    }

    fun onEqualsClick() {
        if (state.previousValue == null) return

        val result = calculateResult()
        state = state.copy(
            displayText = formatResult(result),
            previousValue = null,
            currentOperator = null,
            resetDisplayOnNextInput = true
        )
    }

    fun onClearClick() {
        state = CalculatorState()
    }

    fun onBackspaceClick() {
        state = state.copy(
            displayText = if (state.displayText.length > 1) {
                state.displayText.dropLast(1)
            } else {
                "0"
            }
        )
    }

    fun onDecimalClick() {
        if (!state.displayText.contains(".")) {
            state = state.copy(displayText = state.displayText + ".")
        }
    }

    fun onScientificFunctionClick(function: String) {
        val value = state.displayText.toDoubleOrNull() ?: return
        val result = when (function) {
            "sin" -> if (state.inverseTrig) {
                Math.toDegrees(asin(value))
            } else {
                sin(Math.toRadians(value))
            }
            "cos" -> if (state.inverseTrig) {
                Math.toDegrees(acos(value))
            } else {
                cos(Math.toRadians(value))
            }
            "tan" -> if (state.inverseTrig) {
                Math.toDegrees(atan(value))
            } else {
                tan(Math.toRadians(value))
            }
            "ln" -> if (value > 0) ln(value) else Double.NaN
            "log" -> if (value > 0) log10(value) else Double.NaN
            "√" -> if (value >= 0) sqrt(value) else Double.NaN
            "1/x" -> if (value != 0.0) 1 / value else Double.NaN
            "x!" -> calculateFactorial(value)
            else -> value
        }

        state = state.copy(
            displayText = formatResult(result),
            resetDisplayOnNextInput = true
        )
    }

    fun toggleInverseTrig() {
        state = state.copy(inverseTrig = !state.inverseTrig)
    }

    fun toggleFunctionExpanded() {
        state = state.copy(isFunctionExpanded = !state.isFunctionExpanded)
    }

    private fun calculateResult(): Double {
        val curr = state.displayText.toDoubleOrNull() ?: 0.0
        val prev = state.previousValue ?: 0.0
        return when (state.currentOperator) {
            "+" -> prev + curr
            "−" -> prev - curr
            "×" -> prev * curr
            "÷" -> if (curr != 0.0) prev / curr else Double.NaN
            "x^y" -> prev.pow(curr)
            else -> curr
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return "∞"
        val longVal = value.toLong()
        return if (value == longVal.toDouble()) {
            longVal.toString()
        } else {
            "%.8f".format(value).trimEnd('0').trimEnd('.')
        }
    }

    private fun calculateFactorial(n: Double): Double {
        val intVal = n.toInt()
        if (n < 0 || n != intVal.toDouble()) return Double.NaN
        return (1..intVal).fold(1L) { acc, i -> acc * i }.toDouble()
    }
}
