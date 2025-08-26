package com.nilearning.ai.pisces.feature.nano

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.genai.rewriting.RewriterOptions
import com.nilearning.ai.pisces.R

@Preview()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenAIWritingAssistanceScreen(viewModel: GenAIWritingAssistanceViewModel = hiltViewModel()) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showRewriteOptionsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val resultGenerated = viewModel.resultGenerated.collectAsState()

    var textInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                windowInsets = WindowInsets(top = 0),
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(id = R.string.genai_writing_assistance_title_bar))
                }
            )
        },
    ) { innerPadding ->

        Column(
            Modifier
                .padding(12.dp)
                .padding(innerPadding),
        ) {
            // Text input box
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text(stringResource(id = R.string.genai_writing_assistance_text_input_label)) },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(.8f),
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.Center) {
                // Proofread button
                Button(
                    onClick = {
                        showBottomSheet = true
                        viewModel.proofread(textInput, context)
                    },
                    Modifier.padding(10.dp),
                ) {
                    Text(
                        stringResource(id = R.string.genai_writing_assistance_proofread_btn),
                    )
                }
                Button(
                    onClick = {
                        showRewriteOptionsDialog = true
                    },
                    Modifier.padding(10.dp),
                ) {
                    Text(
                        stringResource(id = R.string.genai_writing_assistance_rewrite_btn),
                    )
                }
                OutlinedButton(
                    onClick = { textInput = "" },
                    Modifier.weight(1f).padding(10.dp),
                ) {
                    Text(
                        stringResource(id = R.string.genai_reset_btn),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.clearGeneratedText()
                },
                sheetState = sheetState,
            ) {
                Text(
                    text = resultGenerated.value,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 24.dp,
                        start = 24.dp,
                        end = 24.dp,
                    ),
                )
            }
        }

        if (showRewriteOptionsDialog) {
            RewriteOptionsDialog(
                onConfirm = { rewriteStyleSelected ->
                    showRewriteOptionsDialog = false
                    showBottomSheet = true
                    viewModel.rewrite(
                        textInput,
                        rewriteStyleSelected.rewriteStyle,
                        context,
                    )
                },
                onDismissRequest = {
                    showRewriteOptionsDialog = false
                },
            )
        }
    }
}

@Composable
fun RewriteOptionsDialog(onConfirm: (rewriteStyle: RewriteStyle) -> Unit, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            val radioOptions = RewriteStyle.entries
            val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
            Column(modifier.selectableGroup()) {
                radioOptions.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (option == selectedOption),
                                onClick = { onOptionSelected(option) },
                                role = Role.RadioButton,
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = null, // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(id = R.string.genai_writing_assistance_dismiss_btn))
                    }
                    TextButton(
                        onClick = {
                            onConfirm(selectedOption)
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(id = R.string.genai_writing_assistance_confirm_btn))
                    }
                }
            }
        }
    }
}

enum class RewriteStyle(
    val rewriteStyle: Int,
    val displayName: String,
) {
    ELABORATE(RewriterOptions.OutputType.ELABORATE, "Elaborate"),
    EMOJIFY(RewriterOptions.OutputType.EMOJIFY, "Emojify"),
    SHORTEN(RewriterOptions.OutputType.SHORTEN, "Shorten"),
    FRIENDLY(RewriterOptions.OutputType.FRIENDLY, "Friendly"),
    PROFESSIONAL(RewriterOptions.OutputType.PROFESSIONAL, "Professional"),
    REPHRASE(RewriterOptions.OutputType.REPHRASE, "Rephrase"),
}
