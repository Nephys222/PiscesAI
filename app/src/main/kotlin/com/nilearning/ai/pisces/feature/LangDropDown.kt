/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LangDropDown() {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 5.dp),
        ) {
            Text(modifier = Modifier.weight(1f), text = "Languages", fontSize = 16.sp)
            Icon(
                modifier = Modifier.padding(4.dp),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            modifier = Modifier.wrapContentSize(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            DropdownMenuItem(
                text = { Text(text = "English", color = MaterialTheme.colorScheme.primary) },
                onClick = {
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(text = "Spanish", color = MaterialTheme.colorScheme.primary) },
                onClick = {
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(text = "French", color = MaterialTheme.colorScheme.primary) },
                onClick = {
                    expanded = false
                }
            )
        }
    }
}