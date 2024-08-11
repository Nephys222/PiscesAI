/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.util

import android.net.Uri
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope

/**
 * Saves a list of Uris across configuration changes
 */
class UriSaver : Saver<MutableList<Uri>, List<String>> {
    override fun restore(value: List<String>): MutableList<Uri> = value.map {
        Uri.parse(it)
    }.toMutableList()

    override fun SaverScope.save(value: MutableList<Uri>): List<String> = value.map { it.toString() }
}
