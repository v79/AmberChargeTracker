package org.liamjd.amber.screens.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
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
import org.liamjd.amber.R
import org.liamjd.amber.currencyToIntOrNull
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.toCurrencyString
import org.liamjd.amber.toLocalString
import org.liamjd.amber.ui.theme.md_theme_light_chargeBarEnd
import org.liamjd.amber.ui.theme.md_theme_light_chargeBarLow
import org.liamjd.amber.ui.theme.md_theme_light_chargeBarStart
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Composable
fun ChargeHistoryItem(
    modifier: Modifier = Modifier,
    event: ChargeEvent,
    updateEvent: (ChargeEvent) -> Unit = {}
) {
    val now = LocalDateTime.now()

    val expanded = remember { mutableStateOf(false) }
    val arrowIcon = if (expanded.value) {
        Icons.AutoMirrored.Filled.ArrowLeft
    } else {
        Icons.Filled.ArrowDropDown
    }

    val duration =
        ChronoUnit.MINUTES.between(event.startDateTime, event.endDateTime ?: LocalDateTime.now())
    val formattedCost = remember { mutableStateOf(event.totalCost?.toCurrencyString()) }
    val dateTime = if (ChronoUnit.DAYS.between(event.startDateTime, now) > 7L) {
        "${event.startDateTime.toLocalString()} (${duration} mins) @${event.kilowatt}kw"
    } else {
        "${event.startDateTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm"))} (${duration} mins) @${event.kilowatt}kw"
    }

    var rowHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val startDrawColour =
        if (event.batteryStartingPct < 20) md_theme_light_chargeBarLow else md_theme_light_chargeBarStart

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded.value = !expanded.value },
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Text(text = dateTime, fontWeight = FontWeight.Bold)
            Text(
                text = formattedCost.value ?: "",
                fontWeight = FontWeight.Bold
            )

            Icon(arrowIcon, contentDescription = "Expand row")

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
            drawRect(color = startDrawColour, size = Size(startWidth, 100f))
            drawRect(
                color = md_theme_light_chargeBarEnd,
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
        if (expanded.value) {
            EventCostRow(
                event = event,
                onSave = {
                    event.totalCost = it.currencyToIntOrNull()
                    updateEvent(event)
                    expanded.value = false
                    formattedCost.value = event.totalCost?.toCurrencyString()
                })
        }
    }
}

@Composable
fun EventCostRow(modifier: Modifier = Modifier, event: ChargeEvent, onSave: (String) -> Unit) {
    val cost = remember { mutableStateOf(event.totalCost?.toCurrencyString() ?: "") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyTextField(
            value = cost.value,
            label = R.string.screen_chargeHistory_totalCost,
            onValueChange = { cost.value = it })
        FilledIconButton(onClick = { onSave(cost.value) }) {
            Icon(Icons.Filled.Save, "Save total cost")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChargeHistoryItemPreview(modifier: Modifier = Modifier) {
    val startDate = LocalDateTime.of(2024, 5, 16, 19, 11, 23)
    val endDate = startDate.plusMinutes(192)
    val event = ChargeEvent(
        odometer = 2344,
        batteryStartingRange = 52,
        batteryEndingRange = 167,
        batteryStartingPct = 24,
        batteryEndingPct = 50,
        vehicleId = 234,
        kilowatt = 22.0f,
        totalCost = 1245,
        startDateTime = startDate,
        endDateTime = endDate
    )
    ChargeHistoryItem(event = event)
}

@Preview(showBackground = true)
@Composable
fun ChargeHistoryItemPreviewLow(modifier: Modifier = Modifier) {
    val startDate = LocalDateTime.of(2024, 5, 16, 19, 11, 23)
    val endDate = startDate.plusMinutes(192)
    val event = ChargeEvent(
        odometer = 2344,
        batteryStartingRange = 52,
        batteryEndingRange = 167,
        batteryStartingPct = 18,
        batteryEndingPct = 50,
        vehicleId = 234,
        kilowatt = 22.0f,
        totalCost = null,
        startDateTime = startDate,
        endDateTime = endDate
    )
    ChargeHistoryItem(event = event)
}