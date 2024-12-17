package eu.kanade.presentation.theme.colorscheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Colors for Midnight Dusk theme
 * Original color scheme by CrepeTF
 * M3 color scheme generated by Material Theme Builder (https://goo.gle/material-theme-builder-web)
 *
 * Key colors:
 * Primary #F02475
 * Secondary #F02475
 * Tertiary #7A5733
 * Neutral #16151D
 */
internal object MidnightDuskColorScheme : BaseColorScheme() {

    override val darkScheme = darkColorScheme(
        primary = Color(0xFFF02475),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFBD1C5C),
        onPrimaryContainer = Color(0xFFFFFFFF),
        inversePrimary = Color(0xFFF02475),
        secondary = Color(0xFFF02475), // Unread badge
        onSecondary = Color(0xFF16151D), // Unread badge text
        secondaryContainer = Color(0xFF66183C), // Navigation bar selector pill & progress indicator (remaining)
        onSecondaryContainer = Color(0xFFF02475), // Navigation bar selector icon
        tertiary = Color(0xFF55971C), // Downloaded badge
        onTertiary = Color(0xFF16151D), // Downloaded badge text
        tertiaryContainer = Color(0xFF386412),
        onTertiaryContainer = Color(0xFFE5E1E5),
        background = Color(0xFF16151D),
        onBackground = Color(0xFFE5E1E5),
        surface = Color(0xFF16151D),
        onSurface = Color(0xFFE5E1E5),
        surfaceVariant = Color(0xFF281624), // Navigation bar background (ThemePrefWidget)
        onSurfaceVariant = Color(0xFFD6C1C4),
        surfaceTint = Color(0xFFF02475),
        inverseSurface = Color(0xFF333043),
        inverseOnSurface = Color(0xFFFFFFFF),
        outline = Color(0xFF9F8C8F),
        surfaceContainerLowest = Color(0xFF221320),
        surfaceContainerLow = Color(0xFF251522),
        surfaceContainer = Color(0xFF281624), // Navigation bar background
        surfaceContainerHigh = Color(0xFF2D1C2A),
        surfaceContainerHighest = Color(0xFF2F1F2C),
    )

    override val lightScheme = lightColorScheme(
        primary = Color(0xFFBB0054),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFD9E1),
        onPrimaryContainer = Color(0xFF3F0017),
        inversePrimary = Color(0xFFFFB1C4),
        secondary = Color(0xFFBB0054), // Unread badge
        onSecondary = Color(0xFFFFFFFF), // Unread badge text
        secondaryContainer = Color(0xFFEFBAD4), // Navigation bar selector pill & progress indicator (remaining)
        onSecondaryContainer = Color(0xFFD1377C), // Navigation bar selector icon
        tertiary = Color(0xFF006638), // Downloaded badge
        onTertiary = Color(0xFFFFFFFF), // Downloaded badge text
        tertiaryContainer = Color(0xFF00894b),
        onTertiaryContainer = Color(0xFF2D1600),
        background = Color(0xFFFFFBFF),
        onBackground = Color(0xFF1C1B1F),
        surface = Color(0xFFFFFBFF),
        onSurface = Color(0xFF1C1B1F),
        surfaceVariant = Color(0xFFF9E6F1), // Navigation bar background (ThemePrefWidget)
        onSurfaceVariant = Color(0xFF524346),
        surfaceTint = Color(0xFFBB0054),
        inverseSurface = Color(0xFF313033),
        inverseOnSurface = Color(0xFFF4F0F4),
        outline = Color(0xFF847376),
        surfaceContainerLowest = Color(0xFFDAC0CD),
        surfaceContainerLow = Color(0xFFE8D1DD),
        surfaceContainer = Color(0xFFF9E6F1), // Navigation bar background
        surfaceContainerHigh = Color(0xFFFCF3F8),
        surfaceContainerHighest = Color(0xFFFEF9FC),
    )
}
