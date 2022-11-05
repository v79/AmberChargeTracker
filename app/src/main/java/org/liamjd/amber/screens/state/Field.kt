package org.liamjd.amber.screens.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.liamjd.amber.screens.validators.FieldValidator
import org.liamjd.amber.screens.validators.ValidationResult

class Field(
    var value: MutableState<String> = mutableStateOf(""),
    val validator: FieldValidator? = null
) {
    var valid: Boolean = true
    var message: String = ""
    val computed: String = value.value

    /**
     * Updates the backing mutableState value, and calls the validator
     */
    fun onFieldUpdate(newValue: String, after: () -> Unit = {}) {
        val result = validator?.validate(newValue) ?: ValidationResult.Valid
        valid = result.isValid
        value.value = newValue
        message = when (result) {
            is ValidationResult.Invalid -> result.message
            is ValidationResult.Valid -> ""
            is ValidationResult.Warning -> result.message
        }
        after.invoke()
    }

    /**
     * Convenience function to hide the ugliness of value.value = newvalue
     */
    fun resetValue(newValue: String) {
        value.value = newValue
    }
}

@Composable
fun rememberFieldState(initialValue: String = "", validator: FieldValidator? = null) = remember {
    Field(mutableStateOf(initialValue), validator = validator)
}