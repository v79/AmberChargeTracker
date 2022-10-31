package org.liamjd.amber.screens.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.liamjd.amber.screens.validators.FieldValidator

class Field(
    var value: MutableState<String> = mutableStateOf(""),
    var valid: Boolean = true,
    val validator: FieldValidator? = null
) {
    var message: String = ""
    fun onFieldUpdate(newValue: String) {
        valid = validator?.validate(newValue) ?: true
        value.value = newValue
        message = if (!valid) {
            "Invalid"
        } else {
            ""
        }
    }
}