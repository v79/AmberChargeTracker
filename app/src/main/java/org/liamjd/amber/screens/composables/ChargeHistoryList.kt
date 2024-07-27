package org.liamjd.amber.screens.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.liamjd.amber.db.entities.ChargeEvent

@Composable
fun ChargeHistoryList(
    modifier: Modifier = Modifier,
    filter: List<ChargeEvent>,
    updateEvent: (ChargeEvent) -> Unit = {}
) {
    if (filter.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            items(
                items = filter,
                key = { event ->
                    // Return a stable + unique key for the item
                    event.id
                }
            ) { event ->
                ChargeHistoryItem(event = event, updateEvent = updateEvent)
            }

        }
    } else {
        Text(text = "No charge events recorded")
    }
}

@Preview(showBackground = true)
@Composable
fun ChargeHistoryLivePreview(modifier: Modifier = Modifier) {

    val eventList: List<ChargeEvent> = listOf(
        ChargeEvent(99, "0", "240", "0", "100", 1L, 22.0f, 174).apply { id = 99 },
        ChargeEvent(123, "85", "125", "43", "56", 1L, 22.0f, null).apply { id = 123 },
        ChargeEvent(195, "94", "178", "48", "89", 1L, 50.0f, 257).apply { id = 234 },
    )

    ChargeHistoryList(filter = eventList)
}