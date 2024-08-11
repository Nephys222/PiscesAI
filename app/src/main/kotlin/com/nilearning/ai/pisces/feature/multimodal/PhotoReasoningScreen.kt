/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature.multimodal

import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.nilearning.ai.pisces.GenerativeViewModelFactory
import coil.size.Precision
import com.nilearning.ai.pisces.util.UriSaver
import com.meetup.twain.MarkdownText
import com.nilearning.ai.pisces.R
import com.nilearning.ai.pisces.ui.theme.Neon80
import com.nilearning.ai.pisces.util.ClipboardHelper
import kotlinx.coroutines.launch

@Composable
internal fun PhotoReasoningRoute(
    viewModel: PhotoReasoningViewModel = viewModel(factory = GenerativeViewModelFactory),
    contentType: String,
    @StringRes titleResId: Int
) {
    val photoReasoningUiState by viewModel.uiState.collectAsState()

    val preText: String = when(contentType) {
        "reason" -> "Look at the image(s), and then answer the following question"
        "caption" -> "Look at the image(s), and then write a short description with the following task"
        "info" -> "Look at the image(s), and then get information or explanation with the following task"
        "task" -> ""
        else -> "What do you think about"
    }

    val coroutineScope = rememberCoroutineScope()
    val imageRequestBuilder = ImageRequest.Builder(LocalContext.current)
    val imageLoader = ImageLoader.Builder(LocalContext.current).build()

    PhotoReasoningScreen(
        uiState = photoReasoningUiState,
        onReasonClicked = { inputText, selectedItems ->
            coroutineScope.launch {
                val bitmaps = selectedItems.mapNotNull {
                    val imageRequest = imageRequestBuilder
                        .data(it)
                        // Scale the image down to 768px for faster uploads
                        .size(size = 768)
                        .precision(Precision.EXACT)
                        .build()
                    try {
                        val result = imageLoader.execute(imageRequest)
                        if (result is SuccessResult) {
                            return@mapNotNull (result.drawable as BitmapDrawable).bitmap
                        } else {
                            return@mapNotNull null
                        }
                    } catch (e: Exception) {
                        return@mapNotNull null
                    }
                }
                viewModel.reason(preText, inputText, bitmaps)
            }
        },
        onClearClicked = { viewModel.clear() },
        subject = preText,
        task = titleResId
    )
}

@Composable
fun PhotoReasoningScreen(
    uiState: PhotoReasoningUiState = PhotoReasoningUiState.Loading,
    onReasonClicked: (String, List<Uri>) -> Unit = { _, _ -> },
    onClearClicked: () -> Unit = {},
    subject: String = "",
    @StringRes task: Int
) {
    var userQuestion by rememberSaveable { mutableStateOf("") }
    val imageUris = rememberSaveable(saver = UriSaver()) { mutableStateListOf() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let {
            imageUris.add(it)
        }
    }

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    IconButton(
                        onClick = {
                            pickMedia.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .padding(all = 4.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.ic_slideshow),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(R.string.add_image),
                        )
                    }
                    OutlinedTextField(
                        value = userQuestion,
                        label = { Text(stringResource(task)) }, // { Text(stringResource(R.string.reason_label)) },
                        placeholder = { Text(text = subject.ifBlank { stringResource(R.string.reason_hint) }, color = MaterialTheme.colorScheme.outline) },
                        onValueChange = { userQuestion = it },
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    userQuestion = ""
                                }
                            )}
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.End)
                ) {
                    TextButton(
                        onClick = {
                            userQuestion = ""
                            imageUris.clear()
                            onClearClicked()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .padding(all = 4.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(stringResource(R.string.action_clear))
                    }
                    TextButton(
                        onClick = {
                            if (userQuestion.isNotBlank()) {
                                onReasonClicked(userQuestion, imageUris.toList())
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .padding(all = 4.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(stringResource(R.string.action_go))
                    }
                }
                LazyRow(
                    modifier = Modifier.padding(all = 8.dp)
                ) {
                    items(imageUris) { imageUri ->
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .requiredSize(72.dp)
                        )
                    }
                }
            }
        }
        when (uiState) {
            PhotoReasoningUiState.Initial -> {
                // Nothing is shown
            }

            PhotoReasoningUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    CircularProgressIndicator()
                }
            }

            is PhotoReasoningUiState.Success -> {
                Card(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .clickable(onClick = {
                            ClipboardHelper.save(context, uiState.outputText)
                        })
                        .indication(interactionSource, LocalIndication.current),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_robot),
                            contentDescription = "Robot Icon",
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

            is PhotoReasoningUiState.Error -> {
                Card(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
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
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun PhotoReasoningScreenPreview() {
    PhotoReasoningScreen(task = R.string.reason_label)
}
