package cloud.clickfix.stopwatch.utils

import java.util.Locale

object TimeFormatter {
    fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        val millis = (milliseconds % 1000) / 10
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, millis)
    }
}
