package com.habitquest.audio

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.habitquest.R

@Composable
fun AmbientSoundPlayer(enabled: Boolean) {
    val context = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    var isForeground by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> isForeground = true
                Lifecycle.Event.ON_STOP -> isForeground = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        isForeground = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(enabled) {
        if (enabled) {
            player = MediaPlayer.create(context, R.raw.ambient_loop).apply {
                isLooping = true
                setVolume(0.18f, 0.18f)
            }
        }

        onDispose {
            player?.release()
            player = null
        }
    }

    LaunchedEffect(enabled, isForeground, player) {
        val currentPlayer = player ?: return@LaunchedEffect
        if (enabled && isForeground) {
            if (!currentPlayer.isPlaying) currentPlayer.start()
        } else if (currentPlayer.isPlaying) {
            currentPlayer.pause()
        }
    }
}
