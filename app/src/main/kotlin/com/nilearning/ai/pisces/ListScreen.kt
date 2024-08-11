/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces

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

@Composable
fun ListScreen(
    onItemClicked: (String) -> Unit = { }
) {
    val menuItems = listOf(
        MenuItem("reason", R.drawable.ic_images, R.string.image_reason_title, R.string.image_reason_description),
        MenuItem("caption", R.drawable.ic_images, R.string.image_caption_title, R.string.image_caption_description),
        MenuItem("info", R.drawable.ic_images, R.string.image_info_title, R.string.image_info_description),
        MenuItem("task", R.drawable.ic_images, R.string.add_image, R.string.menu_free_description)
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
fun ListScreenPreview() {
    ListScreen()
}