package com.nilearning.ai.pisces.feature.nano

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nilearning.ai.pisces.R

@Preview()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenAIImageDescriptionScreen(viewModel: GenAIImageDescriptionViewModel = hiltViewModel()) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val imageDescriptionResult = viewModel.resultGenerated.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        uri?.let {
            imageUri = it
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                windowInsets = WindowInsets(top = 0),
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(id = R.string.genai_image_description_title_bar))
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            // Displayed image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(20.dp),
            ) {
                Box {
                    Text(
                        text = stringResource(id = R.string.reason_hint),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

            }

            Row(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp)) {
                // Select image button
                Button(
                    onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.padding(10.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.add_image),
                    )
                }
                // Generate image description button
                Button(
                    onClick = {
                        showBottomSheet = true
                        viewModel.getImageDescription(imageUri, context)
                    }, modifier = Modifier.padding(10.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.genai_image_description_run_inference),
                    )
                }
            }

        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.clearGeneratedText()
                }, sheetState = sheetState,
            ) {
                Text(
                    text = imageDescriptionResult.value,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                )
            }
        }
    }
}