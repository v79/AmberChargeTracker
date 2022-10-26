package org.liamjd.amber.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Heading(text = R.string.screen_chargeHistory_title)
        // filters
        Row {
            Text("filters go here")
        }
        // table data
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            ChargeHistoryTable(viewModel.allEvents.observeAsState(initial = emptyList()))
        }
    }
}

@Composable
fun ChargeHistoryTable(chargeEvents: State<List<ChargeEvent>>) {
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
            4 -> stringResource(R.string.screen_chargeHistory_costs)
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
                val output = if (ChronoUnit.DAYS.between(item.dateTime, now) > 7L) {
                    item.dateTime.toLocalString()
                } else {
                    item.dateTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm"))
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
                "£${item.totalCost}\n"
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
        data = chargeEvents.value,
        headerCellContent = headerCellTitle,
        cellContent = cellText
    )
}
