/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class MenuItem(
    val routeId: String,
    @DrawableRes val iconResId: Int,
    val titleResId: Int,
    val descriptionResId: Int
)

@Composable
fun MenuScreen(
    onItemClicked: (String) -> Unit = { }
) {
    val menuItems = listOf(
        MenuItem("summary", R.drawable.ic_summarize, R.string.menu_summarize_title, R.string.menu_summarize_description),
        MenuItem("speech", R.drawable.ic_summarize, R.string.menu_speech_title, R.string.menu_speech_description),
        MenuItem("story", R.drawable.ic_summarize, R.string.menu_story_title, R.string.menu_story_description),
        MenuItem("code", R.drawable.ic_summarize, R.string.menu_code_title, R.string.menu_code_description),
        MenuItem("slogan", R.drawable.ic_summarize, R.string.menu_slogan_title, R.string.menu_slogan_description),
        MenuItem("promote", R.drawable.ic_summarize, R.string.menu_promote_title, R.string.menu_promote_description),
        MenuItem("joke", R.drawable.ic_summarize, R.string.menu_joke_title, R.string.menu_joke_description),
        MenuItem("free", R.drawable.ic_summarize, R.string.summarize_label, R.string.menu_free_description)
    )
    LazyColumn(
        Modifier
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        items(menuItems) { menuItem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    onItemClicked(menuItem.routeId)
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(modifier = Modifier.padding(end = 8.dp),
                            imageVector = ImageVector.vectorResource(menuItem.iconResId),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null)
                        Text(
                            text = stringResource(menuItem.titleResId),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Text(
                        text = stringResource(menuItem.descriptionResId),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MenuScreenPreview() {
    MenuScreen()
}