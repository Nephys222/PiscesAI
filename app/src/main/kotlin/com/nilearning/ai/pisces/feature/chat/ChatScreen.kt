/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature.chat

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nilearning.ai.pisces.GenerativeViewModelFactory
import com.nilearning.ai.pisces.ui.theme.GenerativeAISample
import com.meetup.twain.MarkdownText
import com.nilearning.ai.pisces.R
import com.nilearning.ai.pisces.util.ClipboardHelper
import kotlinx.coroutines.launch

@Composable
internal fun ChatRoute(
    chatViewModel: ChatViewModel = viewModel(factory = GenerativeViewModelFactory)
) {
    val chatUiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            MessageInput(
                onSendMessage = { inputText ->
                    chatViewModel.sendMessage(inputText)
                },
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                },
                clearChat = {
                    chatViewModel.clear()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Messages List
            ChatList(chatUiState.messages, listState)
        }
    }
}

@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState
) {
    LazyColumn(
        reverseLayout = true,
        state = listState
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}

@Composable
fun ChatBubbleItem(
    chatMessage: ChatMessage
) {
    val isModelMessage = chatMessage.participant == Participant.PISCES ||
            chatMessage.participant == Participant.ERROR

    val backgroundColor = when (chatMessage.participant) {
        Participant.PISCES -> MaterialTheme.colorScheme.tertiaryContainer
        Participant.YOU -> MaterialTheme.colorScheme.secondaryContainer
        Participant.ERROR -> MaterialTheme.colorScheme.errorContainer
    }

    val textColor = when (chatMessage.participant) {
        Participant.PISCES -> MaterialTheme.colorScheme.onSecondaryContainer
        Participant.YOU -> MaterialTheme.colorScheme.secondary
        Participant.ERROR -> MaterialTheme.colorScheme.onErrorContainer
    }

    val bubbleShape = if (isModelMessage) {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    }

    val horizontalAlignment = if (isModelMessage) {
        Alignment.Start
    } else {
        Alignment.End
    }

    val context = LocalContext.current
    val interactionCard = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = chatMessage.participant.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row {
            if (chatMessage.isPending) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(all = 8.dp)
                )
            }
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                        .clickable(onClick = {
                            ClipboardHelper.save(context, chatMessage.text)
                        })
                        .indication(interactionCard, LocalIndication.current),
                ) {
                    MarkdownText(
                        markdown = chatMessage.text, // TODO(thatfiredev): Figure out Markdown support
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(16.dp)
                            .clickable(onClick = {
                                ClipboardHelper.save(context, chatMessage.text)
                            })
                    )
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {},
    clearChat: () -> Unit = {}
) {
    var userMessage by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = userMessage,
                label = { Text(stringResource(R.string.chat_label)) },
                onValueChange = { userMessage = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                interactionSource = interactionSource,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .align(Alignment.End)
            ) {
                IconButton(
                    onClick = {
                        clearChat()
                        userMessage = ""
                        resetScroll()
                        focusManager.clearFocus()
                    },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.action_send),
                        modifier = Modifier
                    )
                }
                IconButton(
                    onClick = {
                        if (userMessage.isNotBlank()) {
                            onSendMessage(userMessage)
                            userMessage = ""
                            resetScroll()
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    enabled = isFocused
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Send,
                        contentDescription = stringResource(R.string.action_send),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ChatRoutePreview() {
    GenerativeAISample(darkTheme = true) {
        ChatRoute()
    }
}
