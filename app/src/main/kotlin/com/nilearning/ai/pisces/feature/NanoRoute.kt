package com.nilearning.ai.pisces.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nilearning.ai.pisces.R
import com.nilearning.ai.pisces.ui.domain.MLKitItem
import com.nilearning.ai.pisces.ui.domain.mlKitItems
import kotlinx.serialization.Serializable

@Composable
fun NanoRoute(
    navController: NavHostController

) {
    val usageInfo = buildAnnotatedString {
        append(stringResource(R.string.genai_availability_warning))
        withLink(
            LinkAnnotation.Url(
                "https://developers.google.com/ml-kit/genai",
                TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
            )
        ) {
            append(stringResource(R.string.redirect_link))
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeScreen,
        ) {
            composable<HomeScreen> {
                Column {
                    LazyColumn(Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                        items(mlKitItems) {
                            MLKitListItem(mlKitItem = it) {
                                navController.navigate(it.route)
                            }
                        }
                    }

                    Text(
                        text = usageInfo,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                    )
                }
            }

            mlKitItems.forEach {
                val catalogItem = it
                composable(catalogItem.route) {
                    catalogItem.sampleEntryScreen()
                }
            }
        }
    }
}

@Composable
fun MLKitListItem(mlKitItem: MLKitItem, onButtonClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = {
            onButtonClick()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
        ) {
            Row {
                Icon(modifier = Modifier.padding(end = 8.dp),
                    imageVector = ImageVector.vectorResource(mlKitItem.iconResId),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null)
                Text(
                    text = stringResource(mlKitItem.title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = stringResource(mlKitItem.description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Serializable
object HomeScreen

