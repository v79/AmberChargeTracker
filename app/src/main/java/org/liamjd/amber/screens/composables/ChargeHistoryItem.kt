package org.liamjd.amber.screens.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.toLocalString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Composable
fun ChargeHistoryItem(modifier: Modifier = Modifier, event: ChargeEvent) {
    val now = LocalDateTime.now()

    val duration =
        ChronoUnit.MINUTES.between(event.startDateTime, event.endDateTime ?: LocalDateTime.now())

    val dateTime = if (ChronoUnit.DAYS.between(event.startDateTime, now) > 7L) {
        "${event.startDateTime.toLocalString()} (${duration} mins)"
    } else {
        "${event.startDateTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm"))} (${duration} mins)"
    }
    Column(modifier = Modifier.fillMaxWidth()) {

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = dateTime, fontWeight = FontWeight.Bold)
        }
        Canvas(modifier = Modifier.fillMaxWidth()) {
            // total width will be end% - start%
            val startWidth = this.size.width * (event.batteryStartingPct / 100f)
            val endWidth = if (event.batteryEndingPct != null) {
                this.size.width * ((event.batteryEndingPct - event.batteryStartingPct) / 100f)
            } else {
                this.size.width
            }
            drawRect(color = Color.Yellow, size = Size(startWidth, 100f))
            drawRect(
                color = Color.Green,
                topLeft = Offset(startWidth, 0f),
                size = Size(this.size.width - endWidth, 100f)
            )


        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${event.batteryStartingPct}% ➡ ${event.batteryEndingPct}%",
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Preview
@Composable
fun ChargeHistoryItemPreview(modifier: Modifier = Modifier) {
    val startDate = LocalDateTime.of(2024, 5, 16, 19, 11, 23)
    val endDate = startDate.plusMinutes(192)
    val event = ChargeEvent(
        odometer = 2344,
        batteryStartingRange = 52,
        batteryEndingRange = 167,
        batteryStartingPct = 26,
        batteryEndingPct = 72,
        vehicleId = 234,
        kilowatt = 22.0f,
        totalCost = 0,
        startDateTime = startDate,
        endDateTime = endDate
    )
    ChargeHistoryItem(event = event)
}