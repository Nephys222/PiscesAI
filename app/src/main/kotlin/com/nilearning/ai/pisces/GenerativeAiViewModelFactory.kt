/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.nilearning.ai.pisces.Constants.GEMINI_MODEL_NAME
import com.nilearning.ai.pisces.feature.chat.ChatViewModel
import com.nilearning.ai.pisces.feature.multimodal.PhotoReasoningViewModel
import com.nilearning.ai.pisces.feature.text.PromptViewModel

val GenerativeViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        viewModelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val config = generationConfig {
            temperature = 0.7f
        }

        return with(viewModelClass) {
            when {
                isAssignableFrom(PromptViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-flash` AI model
                    // for text generation
                    val generativeModel = GenerativeModel(
                        modelName = GEMINI_MODEL_NAME,
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    PromptViewModel(generativeModel)
                }

                isAssignableFrom(PhotoReasoningViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-flash` AI model
                    // for multimodal text generation
                    val generativeModel = GenerativeModel(
                        modelName = GEMINI_MODEL_NAME,
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    PhotoReasoningViewModel(generativeModel)
                }

                isAssignableFrom(ChatViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-flash` AI model for chat
                    val generativeModel = GenerativeModel(
                        modelName = GEMINI_MODEL_NAME,
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    ChatViewModel(generativeModel)
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${viewModelClass.name}")
            }
        } as T
    }
}
