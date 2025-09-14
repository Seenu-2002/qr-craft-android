package com.seenu.dev.android.qr_craft.presentation.create.components

import android.R.attr.data
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seenu.dev.android.qr_craft.R
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.domain.validator.QrDataValidator
import com.seenu.dev.android.qr_craft.presentation.state.QrType
import com.seenu.dev.android.qr_craft.presentation.ui.theme.QrCraftTheme
import com.seenu.dev.android.qr_craft.presentation.ui.theme.onSurfaceDisabled
import com.seenu.dev.android.qr_craft.presentation.ui.theme.surfaceHigher

@Composable
fun CreateQrForm(modifier: Modifier = Modifier, type: QrType, onGenerateClicked: (QrData) -> Unit) {
    when (type) {
        QrType.TEXT -> QrFormText(modifier = modifier, onGenerateClicked = onGenerateClicked)
        QrType.LINK -> QrFormLink(modifier = modifier, onGenerateClicked = onGenerateClicked)
        QrType.PHONE -> QrFormPhoneNumber(
            modifier = modifier,
            onGenerateClicked = onGenerateClicked
        )

        QrType.CONTACT -> QrFormContact(modifier = modifier, onGenerateClicked = onGenerateClicked)
        QrType.GEO_LOCATION -> QrFormGeoLocation(
            modifier = modifier,
            onGenerateClicked = onGenerateClicked
        )

        QrType.WIFI -> QrFormWifi(modifier = modifier, onGenerateClicked = onGenerateClicked)
    }
}

@Preview
@Composable
private fun QrFormTextPreview() {
    QrCraftTheme {
        QrFormText(
            modifier = Modifier,
            onGenerateClicked = {}
        )
    }
}

@Composable
private fun QrFormText(modifier: Modifier = Modifier, onGenerateClicked: (QrData) -> Unit = {}) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val hasValidData = text.isNotEmpty()
    QrFormBox(
        modifier = modifier,
        hasValidData = hasValidData,
        onGenerateClicked = {
            val data = QrData.Text(text = text)
            onGenerateClicked(data)
        }
    ) {
        QrTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            text = text,
            onValueChange = { text = it },
            placeholder = stringResource(R.string.title_text)
        )
    }
}

@Preview
@Composable
private fun QrFormLinkPreview() {
    QrCraftTheme {
        QrFormLink { }
    }
}

@Composable
fun QrFormLink(modifier: Modifier = Modifier, onGenerateClicked: (QrData) -> Unit = {}) {
    var url by remember { mutableStateOf("") }

    var showUrlSuggestion by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val hasValidData = QrDataValidator.validateUrl(url)
    QrFormBox(
        modifier = modifier,
        hasValidData = hasValidData,
        onGenerateClicked = {
            val data = QrData.Url(url = url)
            onGenerateClicked(data)
        }
    ) {
        QrTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            text = url,
            onValueChange = {
                url = it
                showUrlSuggestion = it.isNotEmpty() && !QrDataValidator.validateUrl(it)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                autoCorrectEnabled = false
            ),
            placeholder = stringResource(R.string.title_url),
            showSuggestion = showUrlSuggestion,
            suggestionMessage = stringResource(R.string.error_url)
        )
    }
}

@Preview
@Composable
private fun QrFormPhoneNumberPreview() {
    QrCraftTheme {
        QrFormPhoneNumber { }
    }
}

@Composable
fun QrFormPhoneNumber(modifier: Modifier = Modifier, onGenerateClicked: (QrData) -> Unit = {}) {
    var phoneNumber by remember {
        mutableStateOf("")
    }

    var showPhoneSuggestion by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val hasValidData = phoneNumber.isNotEmpty()
    QrFormBox(
        modifier = modifier,
        hasValidData = hasValidData,
        onGenerateClicked = {
            val data = QrData.Phone(phoneNumber = phoneNumber)
            onGenerateClicked(data)
        }
    ) {
        QrTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            text = phoneNumber,
            onValueChange = {
                phoneNumber = it
                showPhoneSuggestion = it.isNotEmpty() && !QrDataValidator.validatePhoneNumber(it)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                autoCorrectEnabled = false
            ),
            placeholder = stringResource(R.string.title_url),
            showSuggestion = showPhoneSuggestion,
            suggestionMessage = stringResource(R.string.error_phone)
        )
    }
}

@Preview
@Composable
private fun QrFormContactPreview() {
    QrCraftTheme {
        QrFormContact { }
    }
}

@Composable
private fun QrFormContact(modifier: Modifier = Modifier, onGenerateClicked: (QrData) -> Unit = {}) {
    var name by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var phone by remember {
        mutableStateOf("")
    }

    var showEmailSuggestion by remember {
        mutableStateOf(false)
    }

    var showPhoneSuggestion by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val hasValidData = name.isNotEmpty() || email.isNotEmpty() || phone.isNotEmpty()
    QrFormBox(
        modifier = modifier,
        hasValidData = hasValidData,
        onGenerateClicked = {
            val data = QrData.Contact(
                name = name,
                email = email,
                phone = phone
            )
            onGenerateClicked(data)
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            QrTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                text = name,
                onValueChange = { name = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_name)
            )
            Spacer(modifier = Modifier.height(8.dp))
            QrTextField(
                modifier = Modifier.fillMaxWidth(),
                text = email,
                onValueChange = {
                    email = it
                    showEmailSuggestion = it.isNotEmpty() && !QrDataValidator.validateEmail(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_email),
                showSuggestion = showEmailSuggestion,
                suggestionMessage = stringResource(R.string.error_email)
            )
            Spacer(modifier = Modifier.height(8.dp))
            QrTextField(
                modifier = Modifier.fillMaxWidth(),
                text = phone,
                onValueChange = {
                    phone = it
                    showPhoneSuggestion =
                        it.isNotEmpty() && !QrDataValidator.validatePhoneNumber(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_phone),
                showSuggestion = showPhoneSuggestion,
                suggestionMessage = stringResource(R.string.error_phone)
            )
        }
    }
}


@Preview
@Composable
private fun QrFormGeoLocationPreview() {
    QrCraftTheme {
        QrFormGeoLocation { }
    }
}

@Composable
private fun QrFormGeoLocation(
    modifier: Modifier = Modifier,
    onGenerateClicked: (QrData) -> Unit = {}
) {
    var latitude by remember {
        mutableStateOf("")
    }
    var longitude by remember {
        mutableStateOf("")
    }

    var showLatitudeSuggestion by remember {
        mutableStateOf(false)
    }
    var showLongitudeSuggestion by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val hasValidData =
        QrDataValidator.validateLatitude(latitude) && QrDataValidator.validateLongitude(longitude)
    QrFormBox(
        modifier = modifier.focusRequester(focusRequester),
        hasValidData = hasValidData,
        onGenerateClicked = {
            val data = QrData.GeoLocation(
                latitude = latitude.toDouble(),
                longitude = longitude.toDouble()
            )
            onGenerateClicked(data)
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            QrTextField(
                modifier = Modifier.fillMaxWidth(),
                text = latitude,
                onValueChange = {
                    latitude = it
                    showLatitudeSuggestion =
                        it.isNotEmpty() && !QrDataValidator.validateLatitude(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_latitude),
                showSuggestion = showLatitudeSuggestion,
                suggestionMessage = stringResource(R.string.error_latitude)
            )
            Spacer(modifier = Modifier.height(8.dp))
            QrTextField(
                modifier = Modifier.fillMaxWidth(),
                text = longitude,
                onValueChange = {
                    longitude = it
                    showLongitudeSuggestion =
                        it.isNotEmpty() && !QrDataValidator.validateLongitude(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_longitude),
                showSuggestion = showLongitudeSuggestion,
                suggestionMessage = stringResource(R.string.error_longitude)
            )
        }
    }
}

@Preview
@Composable
private fun QrFormWifiPreview() {
    QrCraftTheme {
        QrFormWifi { }
    }
}

@Composable
private fun QrFormWifi(
    modifier: Modifier = Modifier,
    onGenerateClicked: (QrData) -> Unit = {}
) {
    var ssid by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var encryptionType by remember {
        mutableStateOf("")
    }

    var showEncryptionTypeSuggestion by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val hasValidData = ssid.isNotEmpty() && password.isNotEmpty() && encryptionType.isNotEmpty()
    QrFormBox(
        modifier = modifier.focusRequester(focusRequester),
        hasValidData = hasValidData,
        onGenerateClicked = {
            val encryptionType = when (encryptionType.uppercase()) {
                "WEP" -> "WEP"
                "WPA" -> "WPA"
                "WPA2-EAP" -> "WPA2-EAP"
                "NOPASS" -> "nopass"
                else -> {
                    throw IllegalArgumentException("Invalid encryption type")
                }
            }
            val data = QrData.Wifi(
                ssid = ssid,
                password = password,
                encryptionType = encryptionType
            )
            onGenerateClicked(data)
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            QrTextField(
                modifier = Modifier.fillMaxWidth(),
                text = ssid,
                onValueChange = { ssid = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_ssid)
            )
            Spacer(modifier = Modifier.height(8.dp))
            QrTextField(
                modifier = Modifier.fillMaxWidth(),
                text = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_password)
            )
            Spacer(modifier = Modifier.height(8.dp))
            QrTextField(
                modifier = Modifier.fillMaxWidth(),
                text = encryptionType,
                onValueChange = {
                    encryptionType = it
                    showEncryptionTypeSuggestion =
                        it.isNotEmpty() && !QrDataValidator.validateEncryptionType(it)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrectEnabled = false
                ),
                placeholder = stringResource(R.string.title_encryption_type),
                showSuggestion = showEncryptionTypeSuggestion,
                suggestionMessage = stringResource(R.string.error_encryption_type)
            )
        }
    }
}


@Composable
private fun QrFormBox(
    modifier: Modifier,
    hasValidData: Boolean,
    onGenerateClicked: () -> Unit,
    content: @Composable () -> Unit
) {

    Column(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceHigher,
                shape = MaterialTheme.shapes.small
            )
            .padding(12.dp)
    ) {
        content()
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            enabled = hasValidData,
            onClick = onGenerateClicked,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceDisabled,
                disabledContainerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = stringResource(R.string.generate_qr),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }


}