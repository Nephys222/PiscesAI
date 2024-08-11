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
import com.nilearning.ai.pisces.MenuScreen
import com.nilearning.ai.pisces.R
import com.nilearning.ai.pisces.feature.text.PromptRoute

@Composable
fun PromptsRoute(
    navController: NavHostController
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "menu") {
            composable("menu") {
                MenuScreen(onItemClicked = { routeId ->
                    navController.navigate(routeId)
                })
            }
            composable("summary") {
                PromptRoute(contentType = "summary", titleResId = R.string.menu_summarize_title)
            }
            composable("speech") {
                PromptRoute(contentType = "speech", titleResId = R.string.menu_speech_title)
            }
            composable("story") {
                PromptRoute(contentType = "story", titleResId = R.string.menu_story_title)
            }
            composable("code") {
                PromptRoute(contentType = "code", titleResId = R.string.menu_code_title)
            }
            composable("slogan") {
                PromptRoute(contentType = "slogan", titleResId = R.string.menu_slogan_title)
            }
            composable("promote") {
                PromptRoute(contentType = "promote", titleResId = R.string.menu_promote_title)
            }
            composable("joke") {
                PromptRoute(contentType = "joke", titleResId = R.string.menu_joke_title)
            }
            composable("free") {
                PromptRoute(contentType = "free", titleResId = R.string.summarize_label)
            }
        }
    }
}
