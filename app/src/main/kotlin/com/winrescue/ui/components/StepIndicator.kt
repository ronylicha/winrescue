package com.winrescue.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.winrescue.ui.theme.StepActive
import com.winrescue.ui.theme.StepCompleted
import com.winrescue.ui.theme.StepPending
import com.winrescue.ui.theme.WinRescueTheme

@Composable
fun StepIndicator(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (stepIndex in 0 until totalSteps) {
            StepCircle(
                stepNumber = stepIndex + 1,
                state = when {
                    stepIndex < currentStep -> StepState.COMPLETED
                    stepIndex == currentStep -> StepState.ACTIVE
                    else -> StepState.PENDING
                }
            )

            if (stepIndex < totalSteps - 1) {
                StepLine(
                    completed = stepIndex < currentStep,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private enum class StepState {
    COMPLETED, ACTIVE, PENDING
}

@Composable
private fun StepCircle(
    stepNumber: Int,
    state: StepState
) {
    val circleColor = when (state) {
        StepState.COMPLETED -> StepCompleted
        StepState.ACTIVE -> StepActive
        StepState.PENDING -> StepPending
    }

    val circleSize = 32.dp

    Box(
        modifier = Modifier.size(circleSize),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            StepState.ACTIVE -> {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )

                Canvas(modifier = Modifier.size(circleSize)) {
                    drawCircle(
                        color = circleColor.copy(alpha = pulseAlpha * 0.3f),
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = circleColor,
                        radius = size.minDimension / 2 - 4.dp.toPx()
                    )
                }

                Text(
                    text = "$stepNumber",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            StepState.COMPLETED -> {
                Canvas(modifier = Modifier.size(circleSize)) {
                    drawCircle(color = circleColor)
                }

                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Compl\u00e9t\u00e9",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            StepState.PENDING -> {
                Canvas(modifier = Modifier.size(circleSize)) {
                    drawCircle(
                        color = circleColor,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                Text(
                    text = "$stepNumber",
                    color = circleColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StepLine(
    completed: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (completed) StepCompleted else StepPending

    Canvas(
        modifier = modifier
            .padding(horizontal = 4.dp)
    ) {
        drawLine(
            color = lineColor,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun StepIndicatorStartPreview() {
    WinRescueTheme {
        StepIndicator(
            totalSteps = 4,
            currentStep = 0,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun StepIndicatorMiddlePreview() {
    WinRescueTheme {
        StepIndicator(
            totalSteps = 4,
            currentStep = 2,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun StepIndicatorEndPreview() {
    WinRescueTheme {
        StepIndicator(
            totalSteps = 4,
            currentStep = 3,
            modifier = Modifier.padding(16.dp)
        )
    }
}
