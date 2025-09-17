package com.seenu.dev.android.qr_craft.presentation

sealed interface UiState<T> {

    data class Loading<T>(val data: T? = null) : UiState<T>
    data class Empty<T>(val message: String? = null) : UiState<T>
    data class Success<T>(val data: T) : UiState<T>
    data class Error<T> constructor(val exp: Exception? = null, val data: T? = null) : UiState<T>

}