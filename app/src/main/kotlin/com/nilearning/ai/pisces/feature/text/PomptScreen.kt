/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature.text

import androidx.annotation.StringRes
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nilearning.ai.pisces.GenerativeViewModelFactory
import com.nilearning.ai.pisces.ui.theme.GenerativeAISample
import com.nilearning.ai.pisces.ui.theme.Neon80
import com.meetup.twain.MarkdownText
import com.nilearning.ai.pisces.R
import com.nilearning.ai.pisces.util.ClipboardHelper

@Composable
internal fun PromptRoute(
    promptViewModel: PromptViewModel = viewModel(factory = GenerativeViewModelFactory),
    contentType: String,
    @StringRes titleResId: Int
) {
    val promptUiState by promptViewModel.uiState.collectAsState()

    val preText: String = when(contentType) {
        "summary" -> "Summarize the following text for me"
        "speech" -> "Convey a thought effectively about the following subject"
        "story" -> "Generate a story from the following subject"
        "code" -> "Write a simple application code to do the following"
        "slogan" -> "Create an appealing slogan for the following business"
        "promote" -> "Create a promotion text for the following product, service or brand"
        "joke" -> "Write a funny joke to tell my friends about the following subject"
        "free" -> ""
        else -> "What do you think about"
    }

    PromptScreen(promptUiState,
        subject = preText,
        task = titleResId,
        onPromptClicked = { inputText -> promptViewModel.promptStreaming(preText, inputText) },
        onClearClicked = { promptViewModel.clear() })
}

@Composable
fun PromptScreen(
    uiState: PromptUiState = PromptUiState.Loading,
    subject: String = "",
    @StringRes task: Int,
    onPromptClicked: (String) -> Unit = {},
    onClearClicked: () -> Unit = {}
) {
    var textToSummarize by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            OutlinedTextField(
                value = textToSummarize,
                label = { Text(stringResource(task)) }, // { Text(stringResource(R.string.summarize_label)) },
                placeholder = { Text(text = subject.ifBlank { stringResource(R.string.summarize_hint) }, color = MaterialTheme.colorScheme.outline) },
                onValueChange = { textToSummarize = it },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            textToSummarize = ""
                        }
                    )}
            )
            Row(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 16.dp)
                    .align(Alignment.End)
            ) {
                TextButton(
                    onClick = {
                        textToSummarize = ""
                        onClearClicked()
                        focusManager.clearFocus()
                    }
                ) {
                    Text(stringResource(R.string.action_clear))
                }
                TextButton(
                    onClick = {
                        if (textToSummarize.isNotBlank()) {
                            onPromptClicked(textToSummarize)
                            focusManager.clearFocus()
                        }
                    },
                ) {
                    Text(stringResource(R.string.action_go))
                }
            }

        }

        when (uiState) {
            PromptUiState.Initial -> {
                // Nothing is shown
            }

            PromptUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    CircularProgressIndicator()
                }
            }

            is PromptUiState.Success -> {
                Card(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth()
                        .clickable(onClick = {
                            ClipboardHelper.save(context, uiState.outputText)
                        })
                        .indication(interactionSource, LocalIndication.current),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.ic_robot),
                            contentDescription = "Person Icon",
                            tint = Neon80,
                            modifier = Modifier
                                .requiredSize(36.dp)
                                .drawBehind {
                                    drawCircle(color = Color(0xFF584400))
                                }
                        )
                        MarkdownText(
                            markdown = uiState.outputText, // TODO(thatfiredev): Figure out Markdown support
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    ClipboardHelper.save(context, uiState.outputText)
                                })
                        )
                    }
                }
            }

            is PromptUiState.Error -> {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(all = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun SummarizeScreenPreview() {
    GenerativeAISample(darkTheme = true) {
        PromptScreen(task = R.string.summarize_label)
    }
}
