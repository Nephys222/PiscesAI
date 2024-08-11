/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import com.nilearning.ai.pisces.R

object ClipboardHelper {
    fun save(context: Context, text: String, label: String = context.getString(R.string.copy)) {
        val clip = ClipData.newPlainText(label, text)
        context.getSystemService<ClipboardManager>()?.setPrimaryClip(clip)
    }
}