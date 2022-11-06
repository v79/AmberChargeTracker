package org.liamjd.amber.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.screens.composables.Heading
import org.liamjd.amber.screens.composables.Table
import org.liamjd.amber.toLocalString
import org.liamjd.amber.viewModels.ChargeHistoryViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun ChargeHistoryScreen(navController: NavController, viewModel: ChargeHistoryViewModel) {
    val context = LocalContext.current
    var timePeriod by remember {
        mutableStateOf(0)
    }

    val filter = viewModel.getEventsWithin(timePeriod).observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Heading(text = R.string.screen_chargeHistory_title)
        // filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            TimeFilterMenu(timePeriod, onSelection = { timePeriod = it })
        }
        // table data
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            ChargeHistoryTable(filter)
        }
    }
}

@Preview
@Composable
fun TimeFilterMenu(timePeriod: Int = 0, onSelection: (Int) -> Unit = {}) {
    var timeFilterExpanded by remember { mutableStateOf(false) }
    val label = if (timePeriod in 1..90) {
        stringResource(id = R.string.screen_chargeHistory_timeFilterDays, timePeriod)
    } else {
        stringResource(R.string.screen_chargeHistory_filterAllTime)
    }
    OutlinedButton(
        modifier = Modifier
            .width(170.dp)
            .padding(8.dp),
        onClick = { timeFilterExpanded = true }) {

        Text(text = label)
        Icon(imageVector = Icons.Default.Menu, contentDescription = "Choose time period")
    }
    DropdownMenu(expanded = timeFilterExpanded, onDismissRequest = { timeFilterExpanded = false }) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.screen_chargeHistory_filterAllTime)) },
            onClick = { timeFilterExpanded = false; onSelection.invoke(0) })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.screen_chargeHistory_timeFilterDays, 7)) },
            onClick = { timeFilterExpanded = false; onSelection.invoke(7) })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.screen_chargeHistory_timeFilterDays, 30)) },
            onClick = { timeFilterExpanded = false; onSelection.invoke(30) })
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.screen_chargeHistory_timeFilterDays, 90)) },
            onClick = { timeFilterExpanded = false; onSelection.invoke(90) })
    }
}

@Composable
fun ChargeHistoryTable(chargeEvents: State<List<ChargeEvent>?>) {
    val now = LocalDateTime.now()
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            // use specific index to vary column width
            0 -> 100.dp
            1 -> 75.dp
            2 -> 75.dp
            else -> 100.dp
        }
    }

    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> stringResource(R.string.screen_chargeHistory_dateTime)
            1 -> stringResource(R.string.screen_chargeHistory_from)
            2 -> stringResource(R.string.screen_chargeHistory_to)
            3 -> stringResource(R.string.screen_chargeHistory_costs)
            else -> ""
        }
        Text(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(8.dp),
            text = value,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }

    val cellText: @Composable (Int, ChargeEvent) -> Unit = { index, item ->
        val value = when (index) {
            0 -> {
                val output = if (ChronoUnit.DAYS.between(item.startDateTime, now) > 7L) {
                    item.startDateTime.toLocalString()
                } else {
                    item.startDateTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm"))
                }
                output.replace(" ", "\n")
            }
            1 -> {
                "${item.batteryStartingPct}%\n${item.batteryStartingRange}mi"
            }
            2 -> {
                "${item.batteryEndingPct}%\n${item.batteryEndingRange}mi"
            }
            3 -> {
                "£${item.totalCost}\n@ ${item.kilowatt}kw"
            }
            else -> ""
        }
        Text(
            text = value.toString(),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            maxLines = 2
        )
    }

    Table(
        columnCount = 4,
        cellWidth = cellWidth,
        data = chargeEvents.value ?: emptyList<ChargeEvent>(),
        headerCellContent = headerCellTitle,
        cellContent = cellText
    )
}
