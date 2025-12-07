package org.liamjd.amber.screens.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.liamjd.amber.db.entities.ChargeEvent

/**
 * A vertical list of charge history items, displayed as bars
 */
@Composable
fun ChargeHistoryList(
    modifier: Modifier = Modifier,
    filter: List<ChargeEvent>,
    updateEventFn: (ChargeEvent) -> Unit = {}
) {
    if (filter.isNotEmpty()) {
        val listSize = filter.size
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            items(
                items = filter,
                key = { event ->
                    // Return a stable + unique key for the item
                    event.id
                }
            ) { event ->
                val prevEvent = if (filter.indexOf(event) < listSize - 1) {
                    filter[filter.indexOf(event) + 1]
                } else {
                    null
                }
                ChargeHistoryItem(
                    event = event,
                    previousEvent = prevEvent,
                    updateEvent = updateEventFn
                )
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
        ChargeEvent(0, "0", "240", "0", "100", 1L, 22.0f, 174).apply { id = 99 },
        ChargeEvent(67, "83", "245", "39", "80", 1L, 22.0f, null).apply { id = 123 },
        ChargeEvent(192, "46", "223", "22", "80", 1L, 50.0f, 257).apply { id = 234 },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
    ) {
        ChargeHistoryList(filter = eventList)
    }
}