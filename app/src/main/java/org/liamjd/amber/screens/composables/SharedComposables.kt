package org.liamjd.amber.screens.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.liamjd.amber.R
import org.liamjd.amber.screens.state.Field
import org.liamjd.amber.ui.theme.md_theme_light_onSurface

@Composable
@Preview
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
fun NumberTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    @StringRes label: Int
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
        colors = TextFieldDefaults.outlinedTextFieldColors(textColor = md_theme_light_onSurface),
        enabled = enabled,
        label = { Text(stringResource(label)) },
    )
}

/**
 * A currency text field is an [OutlinedTextField] refined for just numerical input
 * TODO: does nothing special yet, other than set the keyboard type
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyTextField(
    modifier: Modifier = Modifier,
    field: Field,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    @StringRes label: Int
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
        isError = !field.valid,
        colors = TextFieldDefaults.outlinedTextFieldColors(textColor = md_theme_light_onSurface),
        enabled = enabled,
        label = { Text(stringResource(label)) },
        supportingText = { Text(field.message) }
    )
}
