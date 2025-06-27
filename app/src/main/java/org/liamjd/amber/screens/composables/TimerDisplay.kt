package org.liamjd.amber.screens.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.liamjd.amber.screens.leadingZero
import org.liamjd.amber.ui.theme.md_theme_dark_onPrimaryContainer
import org.liamjd.amber.ui.theme.md_theme_dark_primaryContainer
import org.liamjd.amber.ui.theme.md_theme_light_onPrimaryContainer
import org.liamjd.amber.ui.theme.md_theme_light_primaryContainer
import org.liamjd.amber.viewModels.TimerViewModel


@Preview(showBackground = true)
@Composable
fun TimerDisplayPreview() {
    // Use a fake timer value for preview
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = formatTime(7653L),
            textAlign = TextAlign.Center,
            color = if (isSystemInDarkTheme()) {
                md_theme_dark_onPrimaryContainer
            } else {
                md_theme_light_onPrimaryContainer
            },
            fontSize = TextUnit(48f, TextUnitType.Sp)
        )
    }
}

@Composable
fun TimerDisplay(
    isActive: Boolean = false,
    startingSeconds: Long = 7653L,
    viewModel: TimerViewModel
) {
    val timerValue by viewModel.timer.collectAsState()

    LaunchedEffect(key1 = isActive, key2 = startingSeconds) {
        if (isActive) {
            viewModel.startTimer(startingSeconds)
        } else {
            viewModel.pauseTimer()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .border(
                    border = BorderStroke(
                        2.dp, color = if (isSystemInDarkTheme()) {
                            md_theme_dark_primaryContainer
                        } else {
                            md_theme_light_primaryContainer
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatTime(timerValue),
                textAlign = TextAlign.Center,
                color = if (isSystemInDarkTheme()) {
                    md_theme_dark_onPrimaryContainer
                } else {
                    md_theme_light_onPrimaryContainer
                },
                fontSize = TextUnit(
                    48f,
                    TextUnitType.Sp
                )
            )
        }
    }
}

fun formatTime(time: Long): String {
    val hours = (time / 3600).toInt()
    val minutes = ((time % 3600) / 60).toInt()
    val seconds = (time % 60).toInt()
    return "${hours.leadingZero()}h${minutes.leadingZero()}m:${seconds.leadingZero()}s"
}