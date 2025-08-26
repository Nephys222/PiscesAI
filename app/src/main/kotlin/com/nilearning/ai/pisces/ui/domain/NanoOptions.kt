package com.nilearning.ai.pisces.ui.domain

import android.Manifest
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.nilearning.ai.pisces.feature.nano.GenAIImageDescriptionScreen
import com.nilearning.ai.pisces.feature.nano.GenAISummarizationScreen
import com.nilearning.ai.pisces.feature.nano.GenAIWritingAssistanceScreen
import com.nilearning.ai.pisces.R

@RequiresPermission(Manifest.permission.RECORD_AUDIO)
val mlKitItems = listOf(
    MLKitItem(
        title = R.string.genai_summarization_sample_title,
        description = R.string.genai_summarization_sample_description,
        iconResId = R.drawable.ic_experiment,
        route = "GenAISummarizationScreen",
        sampleEntryScreen = { GenAISummarizationScreen() }
    ),
    MLKitItem(
        title = R.string.genai_image_description_sample_title,
        description = R.string.genai_image_description_sample_description,
        iconResId = R.drawable.ic_experiment,
        route = "GenAIImageDescriptionScreen",
        sampleEntryScreen = { GenAIImageDescriptionScreen() }
    ),
    MLKitItem(
        title = R.string.genai_writing_assistance_sample_title,
        description = R.string.genai_writing_assistance_sample_description,
        iconResId = R.drawable.ic_experiment,
        route = "GenAIWritingAssistanceScreen",
        sampleEntryScreen = { GenAIWritingAssistanceScreen() }
    )
)

data class MLKitItem(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val iconResId: Int,
    val route: String,
    val sampleEntryScreen: @Composable () -> Unit
)