package org.liamjd.amber.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import org.liamjd.amber.viewModels.ChargeHistoryViewModel

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
        Row(modifier = Modifier.fillMaxWidth()) {
            ChargeHistoryTable(viewModel.allEvents.observeAsState(initial = emptyList()))
        }
    }
}

@Composable
fun ChargeHistoryTable(chargeEvents: State<List<ChargeEvent>>) {

    val cellWidth: (Int) -> Dp = { _ ->
     /*   when (index) {
            // use specific index to vary column width
            2 -> 150.dp
            else -> 100.dp
        }*/
        100.dp
    }

    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Odometer Start"
            1 -> "Start Range"
            2 -> "End Range"
            else -> ""
        }
        Text(
            modifier = Modifier.background(Color.LightGray).padding(8.dp),
            text = value,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    val cellText: @Composable (Int, ChargeEvent) -> Unit = { index, item ->
        val value = when (index) {
            0 -> item.odometer
            1 -> item.batteryStartingRange
            2 -> item.batteryEndingRange
            else -> ""
        }
        Text(
            text = value.toString(),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    Table(
        columnCount = 3,
        cellWidth = cellWidth,
        data = chargeEvents.value,
        headerCellContent = headerCellTitle,
        cellContent = cellText
    )
}
