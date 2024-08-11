/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    generativeModel: GenerativeModel
) : ViewModel() {
    private val chat = generativeModel.startChat(
//        history = listOf(
//            content(role = "user") { text("Hello, I want to break my boundaries of knowledge.") },
//            content(role = "model") { text("Glad to help you. What would you like to know?") }
//        )
    )

//    private val _uiState: MutableStateFlow<ChatUiState> =
//        MutableStateFlow(ChatUiState(chat.history.map { content ->
//            // Map the initial messages
//            ChatMessage(
//                text = content.parts.first().asTextOrNull() ?: "",
//                participant = if (content.role == "user") Participant.YOU else Participant.PISCES,
//                isPending = false
//            )
//        }))
    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(
            ChatUiState(mutableListOf(
            ChatMessage(
                text = "Hi, there. What would you like to know?",
                participant = Participant.PISCES,
                isPending = false
            )
        ))
        )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()


    fun sendMessage(userMessage: String) {
        // Add a pending message
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage,
                participant = Participant.YOU,
                isPending = true
            )
        )

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse,
                            participant = Participant.PISCES,
                            isPending = false
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage,
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            _uiState.value.replaceLastPendingMessage()
            _uiState.value.setDefaultMessage()
            _uiState.value = ChatUiState(
                mutableListOf(
                    ChatMessage(
                        text = "Hi, there. What would you like to know?",
                        participant = Participant.PISCES,
                        isPending = false
                    )
                )
            )
        }
    }
}
