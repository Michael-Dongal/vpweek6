package Soal1

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object CampusColors {
    val InProgressBackground = Color(0xFFFEF3C7)
    val InProgressForeground = Color(0xFFB45309)
    val CompletedBackground = Color(0xFFDCFCE7)
    val CompletedForeground = Color(0xFF15803D)
    val TagBackground = Color(0xFFF1F5F9)
    val TagForeground = Color(0xFF334155)
}

private val CampusLightColors = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E3A8A),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B)
)

@Composable
fun CampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CampusLightColors,
        content = content
    )
}