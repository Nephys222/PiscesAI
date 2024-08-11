/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PromptViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<PromptUiState> =
        MutableStateFlow(PromptUiState.Initial)
    val uiState: StateFlow<PromptUiState> =
        _uiState.asStateFlow()

    fun promptStreaming(prefix: String, inputText: String) {
        _uiState.value = PromptUiState.Loading

        val prompt = if (prefix.isBlank()) inputText else "$prefix: $inputText"

        viewModelScope.launch {
            try {
                var outputContent = ""
                generativeModel.generateContentStream(prompt)
                    .collect { response ->
                        outputContent += response.text
                        _uiState.value = PromptUiState.Success(outputContent)
                    }
            } catch (e: Exception) {
                _uiState.value = PromptUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            _uiState.value = PromptUiState.Initial
        }
    }
}
