package lalalala.basicscodelab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun ScientificCalculatorScreen(
    modifier: Modifier = Modifier
) {
    val stateHolder = rememberCalculatorState()

    ScientificCalculator(
        state = stateHolder.state,
        onNumberClick = stateHolder::onNumberClick,
        onOperatorClick = stateHolder::onOperatorClick,
        onEqualsClick = stateHolder::onEqualsClick,
        onClearClick = stateHolder::onClearClick,
        onBackspaceClick = stateHolder::onBackspaceClick,
        onDecimalClick = stateHolder::onDecimalClick,
        onScientificFunctionClick = stateHolder::onScientificFunctionClick,
        onToggleInverseTrig = stateHolder::toggleInverseTrig,
        onToggleFunctionExpanded = stateHolder::toggleFunctionExpanded,
        modifier = modifier
    )
}

@Composable
fun ScientificCalculator(
    state: CalculatorState,
    onNumberClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onEqualsClick: () -> Unit,
    onClearClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onDecimalClick: () -> Unit,
    onScientificFunctionClick: (String) -> Unit,
    onToggleInverseTrig: () -> Unit,
    onToggleFunctionExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DisplaySection(
            displayText = state.displayText,
            currentOperator = state.currentOperator,
            previousValue = state.previousValue,
            modifier = Modifier.weight(1f)
        )

        ControlSection(
            isFunctionExpanded = state.isFunctionExpanded,
            onClearClick = onClearClick,
            onToggleFunctionExpanded = onToggleFunctionExpanded
        )

        if (state.isFunctionExpanded) {
            FunctionButtonsGrid(
                onScientificFunctionClick = onScientificFunctionClick,
                onOperatorClick = onOperatorClick,
                onToggleInverseTrig = onToggleInverseTrig,
                currentOperator = state.currentOperator
            )
        }

        NumberOperatorSection(
            onNumberClick = onNumberClick,
            onDecimalClick = onDecimalClick,
            onBackspaceClick = onBackspaceClick,
            onOperatorClick = onOperatorClick,
            currentOperator = state.currentOperator
        )

        EqualsButton(onEqualsClick = onEqualsClick)
    }
}

@Composable
private fun DisplaySection(
    displayText: String,
    currentOperator: String?,
    previousValue: Double?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(5f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = displayText,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End,
                autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = 64.sp, stepSize = 2.sp),
                lineHeight = 1.em,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            if (currentOperator != null && previousValue != null) {
                Text(
                    text = "${formatDisplayValue(previousValue)} $currentOperator",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.End,
                    autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 56.sp, stepSize = 2.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 4.dp)
                )
            }
        }
    }
}

private fun formatDisplayValue(value: Double): String {
    val longVal = value.toLong()
    return if (value == longVal.toDouble()) {
        longVal.toString()
    } else {
        "%.2f".format(value)
    }
}

@Composable
private fun ControlSection(
    isFunctionExpanded: Boolean,
    onClearClick: () -> Unit,
    onToggleFunctionExpanded: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClearClick,
            contentPadding = PaddingValues(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(end = 10.dp)
        ) {
            Text("AC", fontSize = 20.sp)
        }

        Button(
            onClick = onToggleFunctionExpanded,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier
                .weight(3f)
                .height(48.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                if (isFunctionExpanded) "⇋" else "⇌",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun FunctionButtonsGrid(
    onScientificFunctionClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onToggleInverseTrig: () -> Unit,
    currentOperator: String?
) {
    val functionButtons = listOf(
        "1/x", "x!", "√", "sin", "cos",
        "tan", "log", "ln", "Inv", "x^y"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(140.dp)
    ) {
        itemsIndexed(functionButtons) { _, label ->
            val isActive = label == "x^y" && currentOperator == "x^y"

            Button(
                onClick = {
                    when (label) {
                        "x^y" -> onOperatorClick(label)
                        "Inv" -> onToggleInverseTrig()
                        else -> onScientificFunctionClick(label)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
                    contentColor = if (isActive) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    }
                ),
                border = if (isActive) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else null,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.height(64.dp)
            ) {
                Text(label, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun NumberOperatorSection(
    onNumberClick: (String) -> Unit,
    onDecimalClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onOperatorClick: (String) -> Unit,
    currentOperator: String?
) {
    val numberButtons = listOf("7", "8", "9", "4", "5", "6", "1", "2", "3", "0", ".", "⌫")
    val operatorButtons = listOf("÷", "×", "−", "+")

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        NumberGrid(
            buttons = numberButtons,
            onNumberClick = onNumberClick,
            onDecimalClick = onDecimalClick,
            onBackspaceClick = onBackspaceClick,
            modifier = Modifier.weight(3f)
        )

        OperatorColumn(
            operators = operatorButtons,
            onOperatorClick = onOperatorClick,
            currentOperator = currentOperator,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NumberGrid(
    buttons: List<String>,
    onNumberClick: (String) -> Unit,
    onDecimalClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (i in 0 until 4) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (j in 0 until 3) {
                    val index = i * 3 + j
                    val label = buttons[index]
                    Button(
                        onClick = {
                            when (label) {
                                in "0".."9" -> onNumberClick(label)
                                "." -> onDecimalClick()
                                "⌫" -> onBackspaceClick()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text(label, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun OperatorColumn(
    operators: List<String>,
    onOperatorClick: (String) -> Unit,
    currentOperator: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        operators.forEach { op ->
            val isActive = op == currentOperator

            Button(
                onClick = { onOperatorClick(op) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentColor = if (isActive) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                ),
                border = if (isActive) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else null,
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(op, fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun EqualsButton(onEqualsClick: () -> Unit) {
    Button(
        onClick = onEqualsClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(top = 4.dp)
    ) {
        Text("=", fontSize = 24.sp)
    }
}