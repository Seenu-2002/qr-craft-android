package com.seenu.dev.android.qr_craft.presentation.create.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onSurfaceAlt

@Preview(showBackground = true)
@Composable
private fun QrTextFieldPreview() {
    QrCraftTheme {
        Box(modifier = Modifier.padding(12.dp)) {
            var text by remember { mutableStateOf("") }
            QrTextField(text = text, onValueChange = { text = it }, placeholder = "Enter text")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QrTextFieldPreviewError() {
    QrCraftTheme {
        Box(modifier = Modifier.padding(12.dp)) {
            var text by remember { mutableStateOf("sdlghsdhfpd") }
            QrTextField(
                text = text,
                onValueChange = { text = it },
                placeholder = "Enter text",
                errorMessage = "Invalid user input",
                isError = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QrTextFieldPreviewSuggestion() {
    QrCraftTheme {
        Box(modifier = Modifier.padding(12.dp)) {
            var text by remember { mutableStateOf("Akbsdlf") }
            QrTextField(
                text = text,
                onValueChange = { text = it },
                placeholder = "Enter text",
                errorMessage = "Invalid user input",
                suggestionMessage = "Please enter a valid email",
                showSuggestion = true
            )
        }
    }
}

@Composable
fun QrTextField(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    showSuggestion: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    suggestionMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column(modifier = modifier.animateContentSize()) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().let {
                if (isError) {
                    it.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium
                    )
                } else it
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceAlt
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = keyboardOptions,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            isError = isError
        )

        if (isError) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(start = 8.dp, top = 4.dp)
            )
        }

        if (showSuggestion) {
            Text(
                text = suggestionMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceAlt,
                modifier = Modifier
                    .padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}