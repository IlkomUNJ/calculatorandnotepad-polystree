package lalalala.basicscodelab

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.ViewModel
import java.util.UUID

data class Note(
    val id: UUID = UUID.randomUUID(),
    var textFieldValue: TextFieldValue = TextFieldValue()
)

class TextEditorViewModel : ViewModel() {
    val notes = mutableStateListOf<Note>()
    val selectedTabIndex = mutableIntStateOf(0)
    val isBold = mutableStateOf(false)
    val isItalic = mutableStateOf(false)

    init {
        notes.add(Note())
    }

    fun getCurrentNote(): Note {
        val index = selectedTabIndex.intValue
        if (index >= notes.size) selectedTabIndex.intValue = notes.size - 1
        return notes.getOrElse(selectedTabIndex.intValue) { notes[0] }
    }

    fun addNote() {
        notes.add(Note())
        selectedTabIndex.intValue = notes.size - 1
    }

    fun selectTab(index: Int) {
        if (index in notes.indices) {
            selectedTabIndex.intValue = index
            updateFormattingButtons()
        }
    }

    fun updateText(newValue: TextFieldValue) {
        val currentNote = getCurrentNote()
        val index = notes.indexOf(currentNote)
        if (index < 0) return

        val oldValue = currentNote.textFieldValue
        val finalValue = when {
            newValue.text != oldValue.text && newValue.annotatedString.spanStyles.isEmpty() && oldValue.annotatedString.spanStyles.isNotEmpty() -> {
                val changeOffset = calculateTextChangeOffset(oldValue.text, newValue.text, newValue.selection.start)
                val lengthDiff = newValue.text.length - oldValue.text.length

                val annotatedString = buildAnnotatedString {
                    append(newValue.text)
                    oldValue.annotatedString.spanStyles.forEach { spanStyle ->
                        val adjustedStart = adjustSpanPosition(spanStyle.start, changeOffset, lengthDiff)
                        val adjustedEnd = adjustSpanPosition(spanStyle.end, changeOffset, lengthDiff)
                        if (adjustedStart < adjustedEnd && adjustedStart < newValue.text.length) {
                            val finalEnd = adjustedEnd.coerceAtMost(newValue.text.length)
                            addStyle(spanStyle.item, adjustedStart, finalEnd)
                        }
                    }
                }
                TextFieldValue(annotatedString = annotatedString, selection = newValue.selection)
            }
            newValue.text == oldValue.text && newValue.selection == oldValue.selection &&
            newValue.annotatedString.spanStyles.size < oldValue.annotatedString.spanStyles.size -> return
            else -> newValue
        }

        notes[index] = currentNote.copy(textFieldValue = finalValue)
        if (oldValue.text != finalValue.text || oldValue.selection != finalValue.selection) {
            updateFormattingButtons()
        }
    }

    private fun calculateTextChangeOffset(oldText: String, newText: String, cursorPosition: Int): Int {
        var offset = 0
        val minLength = minOf(oldText.length, newText.length)
        while (offset < minLength && offset < cursorPosition && oldText[offset] == newText[offset]) {
            offset++
        }
        return offset
    }

    private fun adjustSpanPosition(position: Int, changeOffset: Int, lengthDiff: Int): Int {
        return when {
            position < changeOffset -> position
            position >= changeOffset -> (position + lengthDiff).coerceAtLeast(changeOffset)
            else -> position
        }
    }

    fun toggleBold() {
        val selection = getCurrentNote().textFieldValue.selection
        if (selection.collapsed) {
            isBold.value = !isBold.value
        } else {
            val hasBold = hasStyleInSelection(selection) { it.fontWeight == FontWeight.Bold }
            if (hasBold) {
                removeStyle(selection) { it.fontWeight == FontWeight.Bold }
            } else {
                applyStyle(SpanStyle(fontWeight = FontWeight.Bold))
            }
        }
    }

    fun toggleItalic() {
        val selection = getCurrentNote().textFieldValue.selection
        if (selection.collapsed) {
            isItalic.value = !isItalic.value
        } else {
            val hasItalic = hasStyleInSelection(selection) { it.fontStyle == FontStyle.Italic }
            if (hasItalic) {
                removeStyle(selection) { it.fontStyle == FontStyle.Italic }
            } else {
                applyStyle(SpanStyle(fontStyle = FontStyle.Italic))
            }
        }
    }

    fun applyFontSize(size: TextUnit) = applyStyle(SpanStyle(fontSize = size))

    private fun hasStyleInSelection(selection: TextRange, predicate: (SpanStyle) -> Boolean): Boolean {
        val currentNote = getCurrentNote()
        val annotatedString = currentNote.textFieldValue.annotatedString
        return annotatedString.spanStyles.any { range ->
            range.start < selection.end && range.end > selection.start && predicate(range.item)
        }
    }

    private fun removeStyle(selection: TextRange, predicate: (SpanStyle) -> Boolean) {
        val currentNote = getCurrentNote()
        val originalAnnotated = currentNote.textFieldValue.annotatedString
        val annotatedString = buildAnnotatedString {
            append(originalAnnotated.text)
            originalAnnotated.spanStyles.forEach { range ->
                val shouldRemove = predicate(range.item) && range.start < selection.end && range.end > selection.start
                if (!shouldRemove) {
                    addStyle(range.item, range.start, range.end)
                } else {
                    if (range.start < selection.start) addStyle(range.item, range.start, selection.start)
                    if (range.end > selection.end) addStyle(range.item, selection.end, range.end)
                }
            }
        }
        val index = notes.indexOf(currentNote)
        if (index >= 0) {
            notes[index] = currentNote.copy(textFieldValue = TextFieldValue(
                annotatedString = annotatedString,
                selection = TextRange(selection.max)
            ))
        }
    }

    private fun applyStyle(style: SpanStyle) {
        val currentNote = getCurrentNote()
        val originalAnnotated = currentNote.textFieldValue.annotatedString
        val selection = currentNote.textFieldValue.selection
        if (selection.collapsed) return

        val annotatedString = buildAnnotatedString {
            append(originalAnnotated)
            addStyle(style, selection.min, selection.max)
        }
        val index = notes.indexOf(currentNote)
        if (index >= 0) {
            notes[index] = currentNote.copy(textFieldValue = TextFieldValue(
                annotatedString = annotatedString,
                selection = TextRange(selection.max)
            ))
        }
    }

    private fun updateFormattingButtons() {
        val currentNote = getCurrentNote()
        val selection = currentNote.textFieldValue.selection
        if (selection.collapsed) return

        val annotatedString = currentNote.textFieldValue.annotatedString
        if (selection.start >= annotatedString.length) return

        var hasBold = false
        var hasItalic = false
        annotatedString.spanStyles.forEach { range ->
            if (range.start < selection.end && range.end > selection.start) {
                if (range.item.fontWeight == FontWeight.Bold) hasBold = true
                if (range.item.fontStyle == FontStyle.Italic) hasItalic = true
            }
        }
        isBold.value = hasBold
        isItalic.value = hasItalic
    }
}
