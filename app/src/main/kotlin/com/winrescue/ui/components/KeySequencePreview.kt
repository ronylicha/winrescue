package com.winrescue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.winrescue.data.model.KeyAction
import com.winrescue.ui.theme.DifficultyEasy
import com.winrescue.ui.theme.JetBrainsMono
import com.winrescue.ui.theme.StepActive
import com.winrescue.ui.theme.Warning
import com.winrescue.ui.theme.WinRescueTheme

private val TerminalBackground = Color(0xFF0A0F1A)
private val TerminalGreen = Color(0xFF22C55E)
private val TerminalPrompt = Color(0xFF3B82F6)
private val TerminalComment = Color(0xFF6B7280)

@Composable
fun KeySequencePreview(
    actions: List<KeyAction>,
    inputs: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TerminalBackground)
            .heightIn(min = 48.dp, max = 300.dp)
            .verticalScroll(verticalScrollState)
            .padding(12.dp)
    ) {
        actions.forEachIndexed { index, action ->
            TerminalLine(
                lineNumber = index + 1,
                action = action,
                inputs = inputs
            )
        }
    }
}

@Composable
private fun TerminalLine(
    lineNumber: Int,
    action: KeyAction,
    inputs: Map<String, String>
) {
    val horizontalScrollState = rememberScrollState()

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = TerminalComment)) {
                append("%02d ".format(lineNumber))
            }

            withStyle(SpanStyle(color = TerminalPrompt)) {
                append("> ")
            }

            when (action) {
                is KeyAction.TypeString -> {
                    withStyle(SpanStyle(color = TerminalGreen)) {
                        append(action.value)
                    }
                }

                is KeyAction.PressKey -> {
                    val keyDisplay = if (action.modifier != null) {
                        "${action.modifier}+${action.key}"
                    } else {
                        action.key
                    }
                    withStyle(SpanStyle(color = StepActive)) {
                        append("[KEY] ")
                    }
                    withStyle(SpanStyle(color = TerminalGreen)) {
                        append(keyDisplay)
                    }
                }

                is KeyAction.KeyCombination -> {
                    withStyle(SpanStyle(color = StepActive)) {
                        append("[COMBO] ")
                    }
                    withStyle(SpanStyle(color = TerminalGreen)) {
                        append(action.keys.joinToString("+"))
                    }
                }

                is KeyAction.Wait -> {
                    withStyle(SpanStyle(color = Warning)) {
                        append("[WAIT] ")
                    }
                    withStyle(SpanStyle(color = TerminalComment)) {
                        append("Attente ${action.ms}ms")
                    }
                }

                is KeyAction.RepeatKey -> {
                    withStyle(SpanStyle(color = StepActive)) {
                        append("[REPEAT] ")
                    }
                    withStyle(SpanStyle(color = TerminalGreen)) {
                        append("${action.key} x ${action.count}")
                    }
                    withStyle(SpanStyle(color = TerminalComment)) {
                        append(" (${action.delayBetweenMs}ms entre)")
                    }
                }

                is KeyAction.TemplateString -> {
                    val resolvedText = resolveTemplate(action.template, inputs)
                    withStyle(SpanStyle(color = DifficultyEasy)) {
                        append(resolvedText)
                    }
                }
            }
        },
        fontFamily = JetBrainsMono,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(horizontalScrollState)
            .padding(vertical = 2.dp)
    )
}

private fun resolveTemplate(template: String, inputs: Map<String, String>): String {
    var resolved = template
    inputs.forEach { (key, value) ->
        resolved = resolved.replace("{{$key}}", value)
    }
    return resolved
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun KeySequencePreviewDemo() {
    WinRescueTheme {
        KeySequencePreview(
            actions = listOf(
                KeyAction.PressKey(key = "F8", modifier = null),
                KeyAction.Wait(ms = 2000),
                KeyAction.RepeatKey(key = "F8", count = 15, delayBetweenMs = 200),
                KeyAction.Wait(ms = 5000),
                KeyAction.TypeString(value = "cmd.exe"),
                KeyAction.PressKey(key = "Enter", modifier = null),
                KeyAction.Wait(ms = 1000),
                KeyAction.TemplateString(template = "net user {{username}} {{password}}"),
                KeyAction.PressKey(key = "Enter", modifier = null),
                KeyAction.KeyCombination(keys = listOf("Ctrl", "Alt", "Delete"))
            ),
            inputs = mapOf(
                "username" to "Administrateur",
                "password" to "NouveauMDP123"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun KeySequencePreviewEmptyPreview() {
    WinRescueTheme {
        KeySequencePreview(
            actions = listOf(
                KeyAction.TypeString(value = "regedit"),
                KeyAction.PressKey(key = "Enter", modifier = null)
            ),
            inputs = emptyMap(),
            modifier = Modifier.padding(16.dp)
        )
    }
}
