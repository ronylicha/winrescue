package com.winrescue.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.winrescue.data.model.Difficulty
import com.winrescue.ui.theme.DifficultyAdvanced
import com.winrescue.ui.theme.DifficultyEasy
import com.winrescue.ui.theme.DifficultyMedium
import com.winrescue.ui.theme.WinRescueTheme

@Composable
fun DifficultyBadge(
    difficulty: Difficulty,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, label) = when (difficulty) {
        Difficulty.EASY -> DifficultyEasy to "Facile"
        Difficulty.MEDIUM -> DifficultyMedium to "Moyen"
        Difficulty.ADVANCED -> DifficultyAdvanced to "Avanc\u00e9"
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor.copy(alpha = 0.15f)
    ) {
        Text(
            text = label,
            color = backgroundColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun difficultyColor(difficulty: Difficulty): Color = when (difficulty) {
    Difficulty.EASY -> DifficultyEasy
    Difficulty.MEDIUM -> DifficultyMedium
    Difficulty.ADVANCED -> DifficultyAdvanced
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DifficultyBadgeEasyPreview() {
    WinRescueTheme {
        DifficultyBadge(difficulty = Difficulty.EASY)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DifficultyBadgeMediumPreview() {
    WinRescueTheme {
        DifficultyBadge(difficulty = Difficulty.MEDIUM)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DifficultyBadgeAdvancedPreview() {
    WinRescueTheme {
        DifficultyBadge(difficulty = Difficulty.ADVANCED)
    }
}
