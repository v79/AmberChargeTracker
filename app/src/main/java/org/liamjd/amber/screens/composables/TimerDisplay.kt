package org.liamjd.amber.screens.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.liamjd.amber.screens.leadingZero
import org.liamjd.amber.ui.theme.md_theme_dark_onPrimaryContainer
import org.liamjd.amber.ui.theme.md_theme_dark_primaryContainer
import org.liamjd.amber.ui.theme.md_theme_light_onPrimaryContainer
import org.liamjd.amber.ui.theme.md_theme_light_primaryContainer

@OptIn(ExperimentalUnitApi::class)
@Preview(showBackground = true)
@Composable
fun TimerDisplay(isActive: Boolean = false, startingSeconds: Long = 7653L) {
    var timeTakenSeconds by remember {
        mutableStateOf(startingSeconds)
    }
    // timer function
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .border(border = BorderStroke(2.dp, color = if (isSystemInDarkTheme()) { md_theme_dark_primaryContainer} else { md_theme_light_primaryContainer})),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatTime(timeTakenSeconds),
                textAlign = TextAlign.Center,
                color = if (isSystemInDarkTheme()) { md_theme_dark_onPrimaryContainer} else { md_theme_light_onPrimaryContainer},
                fontSize = TextUnit(
                    12f,
                    TextUnitType.Em
                )
            )
        }

        LaunchedEffect(key1 = isActive) {
            while (isActive) {
                timeTakenSeconds++
                delay(1_000)
            }
        }
    }
}

/**
 * Format count of seconds as an hours/minutes/seconds string
 * @param time number of seconds
 * @return string in format "hh:mm:ss"
 */
fun formatTime(time: Long): String {
    val hours: Double = time / 3600.0
    val wholeHours: Int = hours.toInt()
    val minutes: Double = (hours - wholeHours) * 60
    val wholeMinutes = minutes.toInt()
    val seconds: Int = ((minutes - wholeMinutes) * 60).toInt()
    return "${wholeHours.leadingZero()}h${wholeMinutes.leadingZero()}m:${seconds.leadingZero()}s"
}