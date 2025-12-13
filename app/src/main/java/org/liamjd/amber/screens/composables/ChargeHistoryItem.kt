package org.liamjd.amber.screens.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.liamjd.amber.R
import org.liamjd.amber.currencyToIntOrNull
import org.liamjd.amber.parseToPenceOrNull
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.format
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
    previousEvent: ChargeEvent? = null,
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
        "${event.startDateTime.toLocalString()} (${duration} mins) @${event.kilowatt?.format()}kw"
    } else {
        "${event.startDateTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm"))} (${duration} mins) @${event.kilowatt?.format()}kw"
    }

    var rowHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val startDrawColour =
        if (event.batteryStartingPct < 20) md_theme_light_chargeBarLow else md_theme_light_chargeBarStart
    val rectangleHeight = 150f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(bottom = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded.value = !expanded.value },
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            // Date, time, duration, kw
            Text(text = dateTime, fontWeight = FontWeight.Bold)
            // Cost
            Text(
                text = formattedCost.value ?: "", fontWeight = FontWeight.Bold
            )

            Icon(arrowIcon, contentDescription = "Expand row")

        }
        /** ======== Bar chart, showing starting charge level, and finished charged level, coloured by percentage ==== **/
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
            drawRect(color = startDrawColour, size = Size(startWidth, rectangleHeight))
            drawRect(
                color = md_theme_light_chargeBarEnd,
                topLeft = Offset(startWidth, 0f),
                size = Size(endWidth, rectangleHeight)
            )
        }

        /** ======== Charge differences as percentage and range change ====== */
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
            /** ============ Start and ending values ============ **/
            Column {
                Row(modifier = Modifier.padding(end = 2.dp)) {
                    Text(
                        text = "${event.batteryStartingPct}% ➡ ${event.batteryEndingPct}%",
                        color = Color.Black,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
            Column {
                Text(
                    text = "${event.batteryStartingRange}mi ➡ ${event.batteryEndingRange}mi",
                    color = Color.LightGray,

                    fontStyle = FontStyle.Italic
                )
            }
        }
        /** Distance travelled since last charge **/
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            previousEvent?.let {
                val miles = event.milesSince(previousEvent = it)
                // val milesPerPercent = event.milesPerPercent(previousEvent = it)
                //  (${String.format(locale = Locale.getDefault(), format = "%.2f", milesPerPercent)}mi/%)
                Text(
                    modifier = Modifier.background(color = Color(1.0f, 1.0f, 1.0f, 0.5f))
                        .padding(2.dp),
                    text = "Travelled ${miles}mi over ${it.batteryEndingPct?.minus(event.batteryStartingPct)}% ",
                    color = Color.Black,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }
        }
        /** ============ Cost Row ============ **/
        if (expanded.value) {
            EventCostRow(
                event = event, onSave = { costPerKwHText, totalCostText ->
                    // parse total cost (currency) and costPerKwH (pounds) into pence
                    event.totalCost = totalCostText.currencyToIntOrNull()
                    // Use BigDecimal-based precise parser for cost-per-kWh
                    event.costPerKwHPence = costPerKwHText.parseToPenceOrNull()
                    updateEvent(event)
                    expanded.value = false
                    formattedCost.value = event.totalCost?.toCurrencyString()
                })
        }
    }
}

@Composable
fun EventCostRow(modifier: Modifier = Modifier, event: ChargeEvent, onSave: (String, String) -> Unit) {
    val costPerKwHText = remember { mutableStateOf(event.costPerKwHPence?.let { (it.toFloat() / 100f).format(2) } ?: "") }
    val cost = remember { mutableStateOf(event.totalCost?.toCurrencyString() ?: "") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, top = 4.dp, end = 2.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyTextField(
            modifier = Modifier.weight(0.2f),
            value = costPerKwHText.value,
            label = R.string.screen_chargeHistory_costPerKwH,
            onValueChange = { costPerKwHText.value = it }
        )
        CurrencyTextField(
            modifier = Modifier.weight(0.4f),
            value = cost.value,
            label = R.string.screen_chargeHistory_totalCost,
            onValueChange = { cost.value = it })
        FilledIconButton(onClick = { onSave(costPerKwHText.value, cost.value) }) {
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
        startDateTime = startDate,
        endDateTime = endDate,
        batteryStartingRange = 52,
        batteryEndingRange = 167,
        batteryStartingPct = 24,
        batteryEndingPct = 50,
        vehicleId = 234,
        kilowatt = 22.0f,
        costPerKwHPence = null,
        totalCost = 1245
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
        startDateTime = startDate,
        endDateTime = endDate,
        batteryStartingRange = 52,
        batteryEndingRange = 167,
        batteryStartingPct = 18,
        batteryEndingPct = 50,
        vehicleId = 234,
        kilowatt = 22.0f,
        costPerKwHPence = null,
        totalCost = null
    )
    ChargeHistoryItem(event = event)
}

@Preview(showBackground = true)
@Composable
fun EventCostRowPreview() {
    val startDate = LocalDateTime.of(2024, 5, 16, 19, 11, 23)
    val endDate = startDate.plusMinutes(192)
    val event = ChargeEvent(
        odometer = 2344,
        startDateTime = startDate,
        endDateTime = endDate,
        batteryStartingRange = 52,
        batteryEndingRange = 167,
        batteryStartingPct = 24,
        batteryEndingPct = 50,
        vehicleId = 234,
        kilowatt = 22.0f,
        costPerKwHPence = null,
        totalCost = 1245
    )
    EventCostRow(event = event, onSave = { _, _ -> })
}