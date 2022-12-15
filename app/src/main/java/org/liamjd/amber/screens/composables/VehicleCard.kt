package org.liamjd.amber.screens.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.liamjd.amber.db.entities.Vehicle

@Preview(showBackground = true)
@Composable
fun VehicleCard(
    vehicle: Vehicle = Vehicle("Rolls", "Royce", 54123, "MN66BGS"),
    isSelected: Boolean = true,
    onClickAction: (Long) -> Unit = { }
) {
    Card(
        shape = MaterialTheme.shapes.medium, modifier = Modifier
            .padding(4.dp)
            .size(width = 150.dp, height = 150.dp)
            .clickable { onClickAction.invoke(vehicle.id) },
        elevation = CardDefaults.cardElevation(3.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, Color.Green)
        } else {
            BorderStroke(0.dp, Color.Gray)
        }
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = vehicle.manufacturer, fontWeight = FontWeight.Bold)
                Text(text = vehicle.model)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "(${vehicle.id}) ${vehicle.registration}")
            }
        }
    }
}