package org.liamjd.amber.screens.composables

import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.liamjd.amber.R

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