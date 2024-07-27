package org.liamjd.amber.screens.composables

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.liamjd.amber.R

@Composable
@Preview(showBackground = true)
fun Heading(@StringRes text: Int = R.string.app_name) {
    Text(
        text = stringResource(id = text),
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

/**
 * A number text field is an [OutlinedTextField] refined for just numerical input
 * TODO: does nothing special yet, other than set the keyboard type
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(name = "DarkMode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun NumberTextField(
    modifier: Modifier = Modifier,
    value: String = "123",
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = { },
    @StringRes label: Int? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        isError = false, // needs validator
        enabled = enabled,
        label = label?.let { { Text(stringResource(it)) } },
//        supportingText = { Text(field.message) }
    )
}

/**
 * A currency text field is an [OutlinedTextField] refined for just numerical input
 * TODO: does nothing special yet, other than set the keyboard type
 */
@Composable
@Preview(showBackground = true)
fun CurrencyTextField(
    modifier: Modifier = Modifier,
    value: String = "456",
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {},
    @StringRes label: Int? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        isError = false, // needs validator
        enabled = enabled,
        label = label?.let { { Text(stringResource(it)) } },
//        supportingText = { Text(field.message) }
    )
}

@Composable
@Preview(showBackground = true)
fun LoadingMessage() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Loading")
        }
    }
}