package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// COLOR PALETTE - GLOOMY DIGITAL POLICE RETRO COMPUTING
val RetroBgDark = Color(0xFF03050C)      // Almost pure dark terminal black
val RetroBgMedium = Color(0xFF090E18)    // Dark gray-blue terminal card
val RetroBgHeader = Color(0xFF101726)    // Shaded header row
val RetroBorderLine = Color(0xFF1D2E4F)  // Low-intensity grid outline
val RetroNeonGreen = Color(0xFF00FF66)   // Classic high-glow phosphorescent green
val RetroNeonCyan = Color(0xFF00E5FF)    // Retro digital cyber teal
val RetroOrange = Color(0xFFFFB300)      // Phosphor orange for warnings
val RetroAlertRed = Color(0xFFFF3366)    // Alert / error red

/**
 * 1. Blinking terminal cursor.
 */
@Composable
fun BlinkingCursor(
    modifier: Modifier = Modifier,
    color: Color = RetroNeonGreen,
    char: String = "▮"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )
    Text(
        text = char,
        color = color.copy(alpha = alpha),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

/**
 * 2. Visual monitor flicker & scanlines effect (CRT effect).
 * Multiplier oscillations deliver a warm, glowing terminal look without rendering lag.
 */
fun Modifier.crtMonitorEffect(glowColor: Color = RetroNeonGreen): Modifier = this.drawBehind {
    val canvasWidth = size.width
    val canvasHeight = size.height

    // Draw scanlines every 4dp (translated to px)
    val scanlineGap = 8f
    var yPos = 0f
    val strokeW = 1.5f
    val scanlineColor = Color.Black.copy(alpha = 0.12f)

    while (yPos < canvasHeight) {
        drawLine(
            color = scanlineColor,
            start = Offset(0f, yPos),
            end = Offset(canvasWidth, yPos),
            strokeWidth = strokeW
        )
        yPos += scanlineGap
    }
}

/**
 * Global Screen flicker and overlay.
 * Adds slight transparency modulations to the screen to simulate retro monitor refresh.
 */
@Composable
fun CrtMonitorOverlay(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flicker")
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.015f,
        targetValue = 0.045f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker_opacity"
    )

    Box(modifier = modifier.fillMaxSize()) {
        content()

        // Scanline and Flicker layer
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .crtMonitorEffect()
        ) {
            // Subtle amber/green phosphorescent vignette glow
            drawRect(
                color = Color(0x000F523E), // Neutral underlying color
                size = size
            )

            // Random flicker layer in sync with animation state
            drawRect(
                color = RetroNeonGreen.copy(alpha = flickerAlpha),
                size = size
            )
        }
    }
}

/**
 * 3. Falling pixel rain effect (Vertical raindrops, pure high-performance Canvas mechanics)
 * Optimized for weak Android runtimes by using static lists and incremental math.
 */
@Composable
fun PixelBackdropRain(modifier: Modifier = Modifier) {
    val dropCount = 45
    val drops = remember {
        List(dropCount) {
            RainDrop(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1.5f, // start above screen
                speed = 0.008f + Random.nextFloat() * 0.014f,
                alpha = 0.08f + Random.nextFloat() * 0.22f,
                length = 15f + Random.nextFloat() * 25f
            )
        }
    }

    // Drive animation update ticking frames
    var tick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { frameTime ->
                tick = frameTime
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drops.forEach { drop ->
            // Scale fractional y and x to canvas dimensions
            val pxX = drop.x * width
            val pxY = drop.y * height

            if (drop.y > 0f && pxY < height) {
                // Draw pixelated drop line
                drawLine(
                    color = RetroNeonGreen.copy(alpha = drop.alpha),
                    start = Offset(pxX, pxY),
                    end = Offset(pxX, pxY + drop.length),
                    strokeWidth = 3f // Thick blocky rain pixel
                )
            }

            // Update drop physics
            drop.y += drop.speed
            if (drop.y > 1.2f) {
                drop.y = -0.1f
                drop.x = Random.nextFloat()
                drop.speed = 0.008f + Random.nextFloat() * 0.014f
                drop.alpha = 0.08f + Random.nextFloat() * 0.22f
            }
        }
    }
}

private class RainDrop(
    var x: Float,
    var y: Float,
    var speed: Float,
    var alpha: Float,
    var length: Float
)

/**
 * 4. Classic Pixelated sharp border outline helper.
 * Hand-drawn sharp, corner notched vintage borders.
 */
@Composable
fun RetroCard(
    modifier: Modifier = Modifier,
    borderColor: Color = RetroBorderLine,
    backgroundColor: Color = RetroBgMedium,
    glow: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val finalBorderColor = if (glow) RetroNeonGreen else borderColor

    Box(
        modifier = modifier
            .background(backgroundColor)
            .drawBehind {
                // Draw 2px sharp outer border
                val w = size.width
                val h = size.height
                val borderPx = 2.dp.toPx()

                // Classic retro double lines or singular thick pixel borders
                drawRect(
                    color = finalBorderColor,
                    topLeft = Offset.Zero,
                    size = size,
                    style = Stroke(width = borderPx)
                )

                if (glow) {
                    // Small neon dots at corner pixels
                    drawRect(
                        color = RetroNeonGreen,
                        topLeft = Offset.Zero,
                        size = Size(borderPx * 3f, borderPx * 3f)
                    )
                    drawRect(
                        color = RetroNeonGreen,
                        topLeft = Offset(w - borderPx * 3f, 0f),
                        size = Size(borderPx * 3f, borderPx * 3f)
                    )
                    drawRect(
                        color = RetroNeonGreen,
                        topLeft = Offset(0f, h - borderPx * 3f),
                        size = Size(borderPx * 3f, borderPx * 3f)
                    )
                    drawRect(
                        color = RetroNeonGreen,
                        topLeft = Offset(w - borderPx * 3f, h - borderPx * 3f),
                        size = Size(borderPx * 3f, borderPx * 3f)
                    )
                }
            }
            .padding(12.dp)
    ) {
        Column {
            content()
        }
    }
}

/**
 * 5. Retro tactile keyboard button.
 * Pressing on it shifts the button's content down 3dp inside a shadowed border frame,
 * producing an extremely satisfying mechanical keyboard aesthetic.
 */
@Composable
fun RetroButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = RetroBgHeader,
    borderColor: Color = RetroNeonGreen,
    textColor: Color = Color.White,
    glow: Boolean = false,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Determine shadow offset
    val shadowOffsetDp = if (isPressed && enabled) 1.dp else 4.dp
    val topPadding = if (isPressed && enabled) 3.dp else 0.dp

    val displayBorderColor = when {
        !enabled -> Color.DarkGray
        isPressed -> RetroNeonCyan
        glow -> RetroNeonGreen
        else -> borderColor
    }

    val displayBgColor = if (enabled) buttonColor else Color(0xFF080D16)

    Box(
        modifier = modifier
            .height(50.dp)
            .clickable(
                onClick = { if (enabled) onClick() },
                interactionSource = interactionSource,
                indication = null // Avoid standard modern round ripples
            )
            .drawBehind {
                val w = size.width
                val h = size.height
                val borderPx = 2.dp.toPx()
                val shadowPx = shadowOffsetDp.toPx()

                // Draw retro shadow at the bottom right
                if (enabled) {
                    drawRect(
                        color = Color.Black.copy(alpha = 0.6f),
                        topLeft = Offset(shadowPx, shadowPx),
                        size = Size(w - shadowPx, h - shadowPx)
                    )
                }

                // Main button background
                drawRect(
                    color = displayBgColor,
                    topLeft = Offset(0f, topPadding.toPx()),
                    size = Size(w - shadowPx, h - shadowPx - topPadding.toPx())
                )

                // High contrast sharp double pixel border
                drawRect(
                    color = displayBorderColor,
                    topLeft = Offset(0f, topPadding.toPx()),
                    size = Size(w - shadowPx, h - shadowPx - topPadding.toPx()),
                    style = Stroke(width = borderPx)
                )
            }
            .padding(
                start = 12.dp,
                end = 12.dp + shadowOffsetDp,
                top = topPadding,
                bottom = shadowOffsetDp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

/**
 * 7. Translucent terminal overlay for notifications of unlocked items.
 * Renders on top of the desk whenever a new clue is unlocked.
 */
@Composable
fun NewClueAlert(
    clueName: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(clueName) {
        if (clueName.isNotEmpty()) {
            visible = true
            // Play for 3.5 seconds then fade out
            delay(3500)
            visible = false
            delay(400) // wait for animation
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .drawBehind {
                    val borderPx = 2.dp.toPx()
                    drawRect(
                        color = RetroAlertRed,
                        topLeft = Offset.Zero,
                        size = size,
                        style = Stroke(width = borderPx)
                    )
                }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(RetroAlertRed.copy(alpha = 0.2f))
                        .border(1.5.dp, RetroAlertRed),
                    contentAlignment = Alignment.Center
                ) {
                    BlinkingCursor(color = RetroAlertRed, char = "⚠")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ВНИМАНИЕ: ДОБАВЛЕН КОСВЕННЫЙ СЛЕД",
                        color = RetroAlertRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = clueName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                RetroButton(
                    onClick = { visible = false },
                    buttonColor = Color(0xFF261014),
                    borderColor = RetroAlertRed,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        "ПОДТВЕРДИТЬ",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
