package com.nilearning.ai.pisces.feature.nano

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.imagedescription.ImageDescriber
import com.google.mlkit.genai.imagedescription.ImageDescriberOptions
import com.google.mlkit.genai.imagedescription.ImageDescription
import com.google.mlkit.genai.imagedescription.ImageDescriptionRequest
import com.nilearning.ai.pisces.R
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

class GenAIImageDescriptionViewModel @Inject constructor() : ViewModel() {
    private val _resultGenerated = MutableStateFlow("")
    val resultGenerated: StateFlow<String> = _resultGenerated

    private var imageDescriber: ImageDescriber? = null

    fun getImageDescription(imageUri: Uri?, context: Context) {
        if (imageUri == null) {
            _resultGenerated.value =
                context.getString(R.string.genai_image_description_no_image_selected)
            return
        }

        val imageDescriberOptions = ImageDescriberOptions.builder(context).build()
        imageDescriber = ImageDescription.getClient(imageDescriberOptions)

        viewModelScope.launch {
            imageDescriber?.let { imageDescriber ->
                var featureStatus = FeatureStatus.UNAVAILABLE

                try {
                    featureStatus = imageDescriber.checkFeatureStatus().await()
                } catch (error: Exception) {
                    Log.e("GenAIImageDesc", "Error checking feature status", error)
                }

                if (featureStatus == FeatureStatus.UNAVAILABLE) {
                    _resultGenerated.value =
                        context.getString(R.string.genai_not_available)
                    return@launch
                }

                // If feature is downloadable, making an inference call will automatically start
                // the downloading process.
                // If feature is downloading, the inference request will automatically execute after
                // the feature has been downloaded.
                // Alternatively, you can call imageDescriber.downloadFeature() to monitor the
                // progress of the download.
                if (featureStatus == FeatureStatus.DOWNLOADABLE ||
                    featureStatus == FeatureStatus.DOWNLOADING
                ) {
                    _resultGenerated.value =
                        context.getString(R.string.genai_downloading)
                }

                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
                val request = ImageDescriptionRequest.builder(bitmap).build()
                imageDescriber.runInference(request) { newText ->
                    if (_resultGenerated.value ==
                        context.getString(R.string.genai_downloading)
                    ) {
                        clearGeneratedText()
                    }
                    _resultGenerated.value += newText
                }
                return@launch
            }
        }
    }

    fun clearGeneratedText() {
        _resultGenerated.value = ""
    }

    override fun onCleared() {
        imageDescriber?.close()
    }
}
