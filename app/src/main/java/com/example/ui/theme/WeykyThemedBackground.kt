package com.example.ui.theme

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.rendering.visual_state.CoreVisualState
import java.io.File

@Composable
fun WeykyThemedBackground(
  themeStyle: WeykyThemeStyle,
  visualState: CoreVisualState,
  isLightMode: Boolean,
  backgroundColor: Color,
  accentColor: Color,
  minimalConfig: MinimalBackgroundConfig,
  modifier: Modifier = Modifier,
  isPowerSaving: Boolean = false,
  coreCenterFraction: Offset = Offset(0.5f, 0.46f),
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .then(
        when (themeStyle) {
          WeykyThemeStyle.TACTICAL -> {
            Modifier.jarvisTacticalBackground(
              backgroundColor = if (isLightMode) Color(0xFFF2F5F9) else Color(0xFF000000),
              accentColor = accentColor
            )
          }
          WeykyThemeStyle.MODERN -> {
            Modifier.modernCinematicBackground(
              visualState = visualState,
              isLightMode = isLightMode,
              accentColor = accentColor,
              isPowerSaving = isPowerSaving,
              coreCenterFraction = coreCenterFraction
            )
          }
          WeykyThemeStyle.MINIMAL -> {
            Modifier.background(
              if (isLightMode) Color(0xFFF8FAFC) else Color(0xFF0A0D14)
            )
          }
        }
      )
  ) {
    if (themeStyle == WeykyThemeStyle.MINIMAL) {
      MinimalBackgroundLayer(
        minimalConfig = minimalConfig,
        isLightMode = isLightMode,
        modifier = Modifier.fillMaxSize()
      )
    }

    content()
  }
}

/**
 * Modern Cinematic Background:
 * Deep midnight gradient with a smooth, soft radial ambient glow centered behind the core.
 *
 * Requirements:
 * - Color directly derived from actual core state:
 *     Idle: Sky Blue #00D2FF
 *     Listening: Pinkish Purple #D946EF
 *     Speaking (RESPONDING): Amber #F59E0B
 *     Music: Emerald Green #10B981
 *     Charging: Green #22C55E
 * - Smooth color transition via tween(750) without flickering or abrupt steps.
 * - Notifications and temporary cards do NOT alter the background glow color.
 * - Fixed glow intensity and radius; music spectrum visualizer remains localized to core and
 *   does not cause background pulsing.
 * - Center of glow follows adaptive core layout position smoothly.
 * - Battery saver mode reduces rendering load and animation overhead.
 */
@Composable
fun Modifier.modernCinematicBackground(
  visualState: CoreVisualState,
  isLightMode: Boolean,
  accentColor: Color,
  isPowerSaving: Boolean = false,
  coreCenterFraction: Offset = Offset(0.5f, 0.46f)
): Modifier {
  // Retain last active state eligible to affect background ambient glow.
  // Rule: Notifications and temporary cards MUST NOT alter the background glow color.
  var lastEligibleState by remember { mutableStateOf(CoreVisualState.IDLE) }
  if (visualState != CoreVisualState.NOTIFICATION) {
    lastEligibleState = visualState
  }

  // Exact color mapping per specification:
  // - Idle: Sky Blue #00D2FF
  // - Listening: Pinkish Purple #D946EF
  // - Speaking: Amber #F59E0B (RESPONDING)
  // - Music: Emerald Green #10B981
  // - Charging: Green #22C55E
  val targetGlowColor = when (lastEligibleState) {
    CoreVisualState.IDLE -> if (isLightMode) Color(0xFF0284C7) else Color(0xFF00D2FF)
    CoreVisualState.LISTENING -> Color(0xFFD946EF)
    CoreVisualState.PROCESSING -> Color(0xFFD946EF)
    CoreVisualState.RESPONDING -> Color(0xFFF59E0B)
    CoreVisualState.MUSIC -> Color(0xFF10B981)
    CoreVisualState.CHARGING -> Color(0xFF22C55E)
    CoreVisualState.SUCCESS -> Color(0xFF10B981)
    CoreVisualState.CALL -> if (isLightMode) Color(0xFF0284C7) else Color(0xFF00D2FF)
    CoreVisualState.NOTIFICATION -> if (isLightMode) Color(0xFF0284C7) else Color(0xFF00D2FF)
    CoreVisualState.ERROR -> Color(0xFFEF4444)
  }

  val animatedGlowColor by animateColorAsState(
    targetValue = targetGlowColor,
    animationSpec = if (isPowerSaving) snap() else tween(durationMillis = 750),
    label = "modernAmbientGlow"
  )

  val animatedCenterX by animateFloatAsState(
    targetValue = coreCenterFraction.x,
    animationSpec = if (isPowerSaving) snap() else tween(durationMillis = 600),
    label = "glowCenterX"
  )

  val animatedCenterY by animateFloatAsState(
    targetValue = coreCenterFraction.y,
    animationSpec = if (isPowerSaving) snap() else tween(durationMillis = 600),
    label = "glowCenterY"
  )

  val baseGradient = if (isLightMode) {
    listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1), Color(0xFFF1F5F9))
  } else {
    listOf(Color(0xFF030712), Color(0xFF070D1B), Color(0xFF0B132B))
  }

  return this
    .background(Brush.verticalGradient(baseGradient))
    .drawBehind {
      val glowCenter = Offset(size.width * animatedCenterX, size.height * animatedCenterY)
      // Constant glow radius independent of state transitions or music audio levels
      val glowRadius = (minOf(size.width, size.height) * 0.95f).coerceAtLeast(320.dp.toPx())

      // Fixed glow alpha to prevent pulsation or brightness shifts during state transitions
      val glowAlpha = if (isPowerSaving) {
        if (isLightMode) 0.10f else 0.16f
      } else {
        if (isLightMode) 0.14f else 0.22f
      }

      val glowBrush = Brush.radialGradient(
        colors = listOf(
          animatedGlowColor.copy(alpha = glowAlpha),
          animatedGlowColor.copy(alpha = glowAlpha * 0.40f),
          Color.Transparent
        ),
        center = glowCenter,
        radius = glowRadius
      )

      drawRect(brush = glowBrush)
    }
}

@Composable
fun MinimalBackgroundLayer(
  minimalConfig: MinimalBackgroundConfig,
  isLightMode: Boolean,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
    when (minimalConfig.type) {
      MinimalBackgroundType.DEFAULT -> {
        val defaultDark = Color(0xFF0E1117)
        val defaultLight = Color(0xFFF1F5F9)
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(if (isLightMode) defaultLight else defaultDark)
        )
      }
      MinimalBackgroundType.SOLID_COLOR -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(minimalConfig.solidColorHex))
        )
      }
      MinimalBackgroundType.GRADIENT -> {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(minimalConfig.gradientStartHex),
                  Color(minimalConfig.gradientEndHex)
                )
              )
            )
        )
      }
      MinimalBackgroundType.PRESET_WALLPAPER -> {
        val preset = BuiltInWallpapersCatalog.findById(minimalConfig.activePresetId)
          ?: BuiltInWallpapersCatalog.wallpapers.first()

        val startCol = Color(preset.startColorHex)
        val endCol = Color(preset.endColorHex)

        Box(
          modifier = Modifier
            .fillMaxSize()
            .drawBehind {
              if (preset.isRadial) {
                drawRect(
                  brush = Brush.radialGradient(
                    colors = listOf(endCol, startCol),
                    center = Offset(size.width * 0.5f, size.height * 0.45f),
                    radius = size.width.coerceAtLeast(size.height) * 0.75f
                  )
                )
              } else {
                drawRect(
                  brush = Brush.linearGradient(
                    colors = listOf(startCol, endCol),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                  )
                )
              }
            }
        )
      }
      MinimalBackgroundType.CUSTOM_IMAGE -> {
        val customImagePath = minimalConfig.customImagePath
        val bitmap = remember(customImagePath) {
          if (!customImagePath.isNullOrBlank()) {
            val file = File(customImagePath)
            if (file.exists() && file.length() > 0) {
              try {
                BitmapFactory.decodeFile(file.absolutePath)
              } catch (e: Exception) {
                null
              }
            } else null
          } else null
        }

        if (bitmap != null) {
          Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Custom Minimal Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          // Safe fallback to default minimal
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(if (isLightMode) Color(0xFFF1F5F9) else Color(0xFF0E1117))
          )
        }
      }
    }

    // Apply Dimming Overlay to background ONLY
    if (minimalConfig.dimOpacity > 0f) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = minimalConfig.dimOpacity.coerceIn(0f, 0.85f)))
      )
    }
  }
}
