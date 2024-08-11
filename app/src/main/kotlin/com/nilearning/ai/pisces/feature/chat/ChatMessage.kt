/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature.chat

import java.util.UUID

enum class Participant {
    YOU, PISCES, ERROR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    val participant: Participant = Participant.YOU,
    var isPending: Boolean = false
)
