package org.liamjd.amber.screens.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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
        "${event.startDateTime.toLocalString()} (${duration} mins) @${event.kilowatt}kw"
    } else {
        "${event.startDateTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm"))} (${duration} mins) @${event.kilowatt}kw"
    }

    var rowHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = dateTime, fontWeight = FontWeight.Bold)
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight / 2)
        ) {
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
                size = Size(endWidth, 100f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp, end = 2.dp)
                .onGloballyPositioned {
                    rowHeight = with(density) {
                        it.size.height.toDp()
                    }
                },
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${event.batteryStartingPct}% ➡ ${event.batteryEndingPct}%",
                    color = Color.Black,
                    fontStyle = FontStyle.Italic
                )
            }
            Column {
                Text(
                    text = "${event.batteryStartingRange}mi ➡ ${event.batteryEndingRange}mi",
                    color = Color.White,
                    fontStyle = FontStyle.Italic
                )
            }
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
        batteryStartingPct = 25,
        batteryEndingPct = 50,
        vehicleId = 234,
        kilowatt = 22.0f,
        totalCost = 0,
        startDateTime = startDate,
        endDateTime = endDate
    )
    ChargeHistoryItem(event = event)
}