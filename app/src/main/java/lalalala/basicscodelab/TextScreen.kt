package lalalala.basicscodelab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun TextScreen(viewModel: TextEditorViewModel = viewModel()) {
    val selectedIndex = viewModel.selectedTabIndex.intValue
    val currentNote = viewModel.notes.getOrNull(selectedIndex.coerceIn(0, viewModel.notes.size - 1)) ?: viewModel.notes.firstOrNull() ?: Note()
    val isBoldActive = viewModel.isBold.value
    val isItalicActive = viewModel.isItalic.value
    val verticalScrollState = rememberScrollState()
    val textSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notepad") },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer),
                actions = {
                    FilledTonalIconButton(
                        onClick = { viewModel.addNote() },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Note")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.imePadding()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StyleControls(
                        isBoldActive = isBoldActive,
                        isItalicActive = isItalicActive,
                        onBoldClick = { viewModel.toggleBold() },
                        onItalicClick = { viewModel.toggleItalic() },
                        onFontSizeSelect = { size -> viewModel.applyFontSize(size) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (viewModel.notes.isNotEmpty()) {
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedIndex.coerceIn(0, viewModel.notes.size - 1),
                    edgePadding = 0.dp
                ) {
                    viewModel.notes.forEachIndexed { index, _ ->
                        Tab(
                            selected = selectedIndex == index,
                            onClick = { viewModel.selectTab(index) },
                            text = { Text("Note ${index + 1}") }
                        )
                    }
                }
            }
            CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                BasicTextField(
                    value = currentNote.textFieldValue,
                    onValueChange = { newValue -> viewModel.updateText(newValue) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .verticalScroll(verticalScrollState),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.padding(12.dp)) {
                            if (currentNote.textFieldValue.text.isEmpty()) {
                                Text(
                                    "Start typing...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun StyleControls(
    isBoldActive: Boolean,
    isItalicActive: Boolean,
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onFontSizeSelect: (TextUnit) -> Unit
) {
    FilledTonalIconButton(
        onClick = onBoldClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (isBoldActive) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text("B", fontWeight = FontWeight.Bold)
    }
    FilledTonalIconButton(
        onClick = onItalicClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (isItalicActive) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text("I", fontStyle = FontStyle.Italic)
    }
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilledTonalIconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.FormatSize, "Font Size")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf(12, 16, 20, 24).forEach { size ->
                DropdownMenuItem(
                    text = { Text("${size}sp") },
                    onClick = {
                        onFontSizeSelect(size.sp)
                        expanded = false
                    }
                )
            }
        }
    }
}
