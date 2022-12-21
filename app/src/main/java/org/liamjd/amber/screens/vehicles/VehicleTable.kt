package org.liamjd.amber.screens.vehicles

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.screens.composables.Table
import org.liamjd.amber.ui.theme.md_theme_dark_background
import org.liamjd.amber.ui.theme.md_theme_light_background
import java.time.LocalDateTime

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun VehicleTable(
    vehicle: Vehicle = Vehicle(
        "Rolls Royce",
        "Silver Cloud",
        5176,
        "MNY 99 BGS",
        LocalDateTime.now(),
        null
    )
) {

    val headingTextColour = if (isSystemInDarkTheme()) {
        md_theme_dark_background
    } else {
        md_theme_light_background
    }
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 125.dp
            1 -> 100.dp
            2 -> 75.dp
            else -> 100.dp
        }
    }

    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Model"
            1 -> "Registration"
            2 -> "Odometer"
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

    val tableContent: @Composable (Int, Vehicle) -> Unit = { index, item ->
        val value: String = when (index) {
            0 -> "${item.manufacturer}\n${item.model}"
            1 -> item.registration
            2 -> item.odometerReading.toString()
            else -> ""
        }
        Text(
            text = value,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            maxLines = 2,
            overflow = TextOverflow.Clip
        )

    }

    Table(
        columnCount = 3, cellWidth = cellWidth, data = listOf(vehicle),
        headerCellContent = headerCellTitle, cellContent = tableContent,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    Log.i("VehicleTable", "Long press detected")
                }
            )
        }
    )
}