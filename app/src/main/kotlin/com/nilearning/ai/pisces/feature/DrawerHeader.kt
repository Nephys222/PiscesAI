/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nilearning.ai.pisces.R

@Preview(showSystemUi = true)
@Composable
fun DrawerHeader(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f)
            .background(MaterialTheme.colorScheme.tertiary),
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 36.sp,
                color = MaterialTheme.colorScheme.primaryContainer
            )

            Text(modifier = Modifier.padding(top = 16.dp),
                text = stringResource(id = R.string.powered_by),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

    }
}