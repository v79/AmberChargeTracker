package org.liamjd.amber.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.screens.composables.Table
import org.liamjd.amber.screens.vehicles.VehicleCard
import org.liamjd.amber.toLocalString
import org.liamjd.amber.ui.theme.*
import org.liamjd.amber.viewModels.ChargeHistoryViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeHistoryScreen(navController: NavController, viewModel: ChargeHistoryViewModel) {
    val context = LocalContext.current
    val timePeriod by remember {
        viewModel.timePeriod
    }
    val currentVehicle = remember {
        viewModel.vehicle
    }
    val filter = remember { viewModel.events }

    AmberChargeTrackerTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.screen_chargeHistory_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.StartScreen.route) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Back to main menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = md_theme_light_surfaceTint
                )
            )
        },
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    currentVehicle.value?.let { vehicle ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                        ) {
                            VehicleCard(
                                vehicle,
                                isSelected = true,
                                onClickAction = {})
                            TimeFilterMenu(
                                timePeriod,
                                onSelection = { timePeriod -> viewModel.changeTimeFilter(timePeriod) })
                        }
                        Row(modifier = Modifier.padding(start = 12.dp)) {
                            Text("${filter.value.size} events")
                        }
                        // table data
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                        ) {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                ChargeHistoryTable(filter)
                            }

                        }
                    }
                }
            },
            bottomBar = {
                BottomAppBar {

                }
            }
        )
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun TimeFilterMenu(timePeriod: Int = 0, onSelection: (Int) -> Unit = {}) {
    var timeFilterExpanded by remember { mutableStateOf(false) }
    val label = if (timePeriod in 1..90) {
        stringResource(id = R.string.screen_chargeHistory_timeFilterDays, timePeriod)
    } else {
        stringResource(R.string.screen_chargeHistory_filterAllTime)
    }
    Box {
        OutlinedButton(
            modifier = Modifier
                .width(170.dp)
                .padding(8.dp),
            onClick = { timeFilterExpanded = true }) {

            Text(text = label)
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Choose time period")
        }
        DropdownMenu(
            expanded = timeFilterExpanded,
            onDismissRequest = { timeFilterExpanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.screen_chargeHistory_filterAllTime)) },
                onClick = { timeFilterExpanded = false; onSelection.invoke(0) })
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            id = R.string.screen_chargeHistory_timeFilterDays,
                            7
                        )
                    )
                },
                onClick = { timeFilterExpanded = false; onSelection.invoke(7) })
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            id = R.string.screen_chargeHistory_timeFilterDays,
                            30
                        )
                    )
                },
                onClick = { timeFilterExpanded = false; onSelection.invoke(30) })
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            id = R.string.screen_chargeHistory_timeFilterDays,
                            90
                        )
                    )
                },
                onClick = { timeFilterExpanded = false; onSelection.invoke(90) })
        }
    }
}

/**
 * Dummy data for previewing the charge history table
 */
class ChargeHistoryPreviewStub : PreviewParameterProvider<State<List<ChargeEvent>?>> {
    private val eventList: List<ChargeEvent> = listOf(
        ChargeEvent(123, "85", "125", "43", "56", 1L, 22.0f, 174),
        ChargeEvent(195, "94", "178", "48", "89", 1L, 50.0f, 257)
    )
    override val values: Sequence<State<List<ChargeEvent>?>>
        get() = sequenceOf(mutableStateOf(eventList))
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun ChargeHistoryTable(@PreviewParameter(ChargeHistoryPreviewStub::class) chargeEvents: MutableState<List<ChargeEvent>>?) {
    Log.i(
        "ChargeHistoryScreen",
        "Rendering ChargeHistoryTable with ${chargeEvents?.value?.size} events"
    )
    val now = LocalDateTime.now()
    val headingTextColour = if (isSystemInDarkTheme()) {
        md_theme_dark_background
    } else {
        md_theme_light_background
    }
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            // use specific index to vary column width
            0 -> 140.dp
            1 -> 70.dp
            2 -> 70.dp
            3 -> 75.dp
            else -> 100.dp
        }
    }

    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> stringResource(R.string.screen_chargeHistory_dateTime)
            1 -> stringResource(R.string.screen_chargeHistory_from)
            2 -> stringResource(R.string.screen_chargeHistory_to)
            3 -> "⬆️"
            else -> ""
        }
        Text(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(8.dp),
            text = value,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = headingTextColour
        )
    }

    val cellText: @Composable (Int, ChargeEvent) -> Unit = { index, item ->
        var txtColor = if (isSystemInDarkTheme()) { md_theme_dark_onPrimaryContainer} else { md_theme_light_onPrimaryContainer }
        val duration = ChronoUnit.MINUTES.between(item.startDateTime,item.endDateTime?: LocalDateTime.now())
        val value = when (index) {
            0 -> {
                val dateTime = if (ChronoUnit.DAYS.between(item.startDateTime, now) > 7L) {
                    "${item.startDateTime.toLocalString()} (${duration}mins)"
                } else {
                    "${item.startDateTime.format(DateTimeFormatter.ofPattern("EEEE HH:mm"))} (${duration}mins)"
                }
                dateTime + " @${item.kilowatt}kwh"
            }
            1 -> {
                "${item.batteryStartingPct}%\n${item.batteryStartingRange}mi"
            }
            2 -> {
                "${item.batteryEndingPct}%\n${item.batteryEndingRange}mi"
            }
            3 -> {
                txtColor = md_theme_light_txtIncrease
                "${item.batteryEndingPct?.minus(item.batteryStartingPct)}%\n" +
                        "${item.batteryEndingRange?.minus(item.batteryStartingRange)}mi"
            }
            else -> ""
        }
        Text(
            text = value,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = txtColor,
            modifier = Modifier.padding(8.dp).height(37.dp),
            maxLines = 2
        )
    }

    Table(
        columnCount = 4,
        cellWidth = cellWidth,
        data = chargeEvents?.value ?: emptyList(),
        headerCellContent = headerCellTitle,
        cellContent = cellText
    )
}
