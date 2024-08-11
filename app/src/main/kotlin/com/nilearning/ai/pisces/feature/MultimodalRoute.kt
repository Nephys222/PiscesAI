/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nilearning.ai.pisces.ListScreen
import com.nilearning.ai.pisces.R
import com.nilearning.ai.pisces.feature.multimodal.PhotoReasoningRoute

@Composable
fun MultimodalRoute(
    navController: NavHostController
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "enlist") {
            composable("enlist") {
                ListScreen(onItemClicked = { routeId ->
                    navController.navigate(routeId)
                })
            }
            composable("reason") {
                PhotoReasoningRoute(contentType = "reason", titleResId = R.string.image_reason_title)
            }
            composable("caption") {
                PhotoReasoningRoute(contentType = "caption", titleResId = R.string.image_caption_title)
            }
            composable("info") {
                PhotoReasoningRoute(contentType = "info", titleResId = R.string.image_info_title)
            }
            composable("task") {
                PhotoReasoningRoute(contentType = "task", titleResId = R.string.add_image)
            }
        }
    }
}
