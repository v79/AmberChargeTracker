package org.liamjd.amber.screens.composables

import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.ui.theme.md_theme_dark_onSecondary
import org.liamjd.amber.ui.theme.md_theme_dark_secondary
import org.liamjd.amber.ui.theme.md_theme_light_onSecondary
import org.liamjd.amber.ui.theme.md_theme_light_secondary
import java.io.File
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun VehicleCard(
    vehicle: Vehicle = Vehicle(
        "Rolls",
        "Royce",
        54123,
        "MN66BGS",
        LocalDateTime.now(),
        "4-River-1671355081185.jpg"
    ),
    isSelected: Boolean = true,
    isEditable: Boolean = false,
    onClickAction: (Long) -> Unit = { },
    onLongClickAction: (Long) -> Unit = { }
) {


    Card(
        shape = MaterialTheme.shapes.medium, modifier = Modifier
            .padding(4.dp)
            .size(width = 150.dp, height = 150.dp)
            .combinedClickable(onClick = { onClickAction.invoke(vehicle.id) },
                onLongClick = { onLongClickAction.invoke(vehicle.id) }),
        elevation = CardDefaults.cardElevation(3.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, Color.Green)
        } else {
            BorderStroke(0.dp, Color.Gray)
        }
    ) {
        val textBackgroundColour = if (isSystemInDarkTheme()) {
            md_theme_dark_onSecondary
        } else {
            md_theme_light_onSecondary
        }
        // TODO: use the google Palette library to be clever with choosing colours here
        val backgroundGradient =
            Brush.verticalGradient(listOf(textBackgroundColour, Color.Transparent), startY = 0.6f)
        val bottomBackgroundGradient =
            Brush.verticalGradient(listOf(Color.Transparent, textBackgroundColour), startY = 0.3f)
        Box {
            val photoPath = vehicle.photoPath
            if (photoPath != null) {
                val bitmap =
                    getImageBitmapFromPath(LocalContext.current.filesDir, photoPath)
                bitmap?.let {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "my pic",
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .background(brush = backgroundGradient)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = vehicle.manufacturer,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(text = vehicle.model)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    Modifier
                        .background(brush = bottomBackgroundGradient)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "(${vehicle.id}) ${vehicle.registration}")
                }
            }
        }
    }
}

/**
 * Given the local storage file path, and the filename of the image, return a compose ImageBitmap graphic, or null
 */
private fun getImageBitmapFromPath(appFiles: File, fileName: String): ImageBitmap? {
    val photoFile: File =
        File(appFiles.path, fileName)
    val bitmap = if (photoFile.exists()) {
        BitmapFactory.decodeFile(photoFile.absolutePath).asImageBitmap()
    } else {
        null
    }
    return bitmap
}