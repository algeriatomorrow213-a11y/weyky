package com.example.ui.theme

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
              accentColor = accentColor
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
 * Color shifts smoothly based on CoreVisualState without abrupt brightness jumps or music flashing.
 */
@Composable
fun Modifier.modernCinematicBackground(
  visualState: CoreVisualState,
  isLightMode: Boolean,
  accentColor: Color
): Modifier {
  val targetGlowColor = when (visualState) {
    CoreVisualState.IDLE -> if (isLightMode) Color(0xFF0284C7) else Color(0xFF00B4D8)
    CoreVisualState.LISTENING -> JarvisHudColors.MagentaPulse
    CoreVisualState.PROCESSING -> Color(0xFF7209B7)
    CoreVisualState.RESPONDING -> JarvisHudColors.HologramCyan
    CoreVisualState.SUCCESS -> Color(0xFF06D6A0)
    CoreVisualState.MUSIC -> JarvisHudColors.NeonGreen
    CoreVisualState.CHARGING -> JarvisHudColors.TacticalAmber
    CoreVisualState.CALL -> Color(0xFF2A9D8F)
    CoreVisualState.NOTIFICATION -> Color(0xFFFB8500)
    CoreVisualState.ERROR -> JarvisHudColors.CrimsonAlert
  }

  val animatedGlowColor by animateColorAsState(
    targetValue = targetGlowColor,
    animationSpec = tween(durationMillis = 600),
    label = "modernAmbientGlow"
  )

  val baseGradient = if (isLightMode) {
    listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1), Color(0xFFF1F5F9))
  } else {
    listOf(Color(0xFF040711), Color(0xFF080E1C), Color(0xFF0C1428))
  }

  return this
    .background(Brush.verticalGradient(baseGradient))
    .drawBehind {
      val glowCenter = Offset(size.width * 0.5f, size.height * 0.46f)
      val glowRadius = (size.width * 0.75f).coerceAtLeast(320.dp.toPx())

      val glowAlpha = if (isLightMode) 0.12f else 0.22f
      val glowBrush = Brush.radialGradient(
        colors = listOf(
          animatedGlowColor.copy(alpha = glowAlpha),
          animatedGlowColor.copy(alpha = glowAlpha * 0.45f),
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
