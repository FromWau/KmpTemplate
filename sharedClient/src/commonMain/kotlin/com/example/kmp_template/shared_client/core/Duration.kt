package com.example.kmp_template.shared_client.core

import kotlin.time.Duration

fun formatDuration(duration: Duration): String {
    return duration.toComponents { hours, minutes, seconds, _ ->
        val hours = if (hours < 1) null else hours.toString().padStart(2, '0')
        val minutes = minutes.toString().padStart(2, '0')
        val seconds = seconds.toString().padStart(2, '0')

        listOfNotNull(hours, minutes, seconds).joinToString(":")
    }
}
