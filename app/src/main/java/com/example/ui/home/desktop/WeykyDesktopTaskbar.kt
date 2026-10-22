package com.example.ui.home.desktop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.state.WeykyUiState
import com.example.device.apps.AppMatch
import com.example.device.battery.ChargingState
import com.example.ui.home.AppIconCache
import com.example.ui.theme.JarvisHudColors
import com.example.ui.theme.WeykyThemeStyle

/**
 * Desktop Taskbar for WEYKY Landscape Workstation.
 * Sits permanently at the bottom edge across all 3 themes:
 * - Tactical HUD: Technical cut corners, cyan borders, and glowing telemetry.
 * - Modern: Frosted glass translucent dock with soft glow derived from the living core.
 * - Minimal: Clean, distraction-free matte surface with quiet typography.
 */
@Composable
fun WeykyDesktopTaskbar(
  uiState: WeykyUiState,
  pinnedApps: List<AppMatch>,
  isStartMenuOpen: Boolean,
  borderColor: Color,
  panelBg: Color,
  panelBgElevated: Color,
  textPrimary: Color,
  textSecondary: Color,
  textMuted: Color,
  isLight: Boolean,
  onToggleStartMenu: () -> Unit,
  onLaunchApp: (AppMatch) -> Unit,
  onUnpinApp: (String) -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val themeStyle = uiState.themeStyle

  val taskbarShape = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> CutCornerShape(topStart = 8.dp, topEnd = 8.dp)
    WeykyThemeStyle.MODERN -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    WeykyThemeStyle.MINIMAL -> RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
  }

  val taskbarBackground = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> panelBg.copy(alpha = 0.95f)
    WeykyThemeStyle.MODERN -> if (isLight) Color(0xFFF8FAFC).copy(alpha = 0.92f) else Color(0xFF131A29).copy(alpha = 0.88f)
    WeykyThemeStyle.MINIMAL -> if (isLight) Color(0xFFEEEEEE) else Color(0xFF0F141C)
  }

  val taskbarBorder = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> borderColor.copy(alpha = 0.65f)
    WeykyThemeStyle.MODERN -> borderColor.copy(alpha = 0.40f)
    WeykyThemeStyle.MINIMAL -> if (isLight) Color(0xFFCCCCCC) else Color(0xFF262C36)
  }

  Surface(
    shape = taskbarShape,
    color = taskbarBackground,
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .border(
        width = 1.dp,
        color = taskbarBorder,
        shape = taskbarShape
      )
      .testTag("desktop_taskbar")
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. START SECTION: WEYKY Start Button & Pinned Apps
      Row(
        modifier = Modifier
          .weight(1f, fill = false)
          .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Unified WEYKY Start Button
        WeykyStartButton(
          themeStyle = themeStyle,
          isOpen = isStartMenuOpen,
          borderColor = borderColor,
          panelBgElevated = panelBgElevated,
          textPrimary = textPrimary,
          onClick = onToggleStartMenu
        )

        // Subtle vertical separator
        Box(
          modifier = Modifier
            .width(1.dp)
            .height(26.dp)
            .background(taskbarBorder.copy(alpha = 0.4f))
        )

        // Pinned Applications Row
        Row(
          modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 2.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          pinnedApps.forEach { app ->
            TaskbarPinnedAppItem(
              app = app,
              themeStyle = themeStyle,
              borderColor = borderColor,
              panelBgElevated = panelBgElevated,
              textPrimary = textPrimary,
              onLaunch = { onLaunchApp(app) },
              onUnpin = { onUnpinApp(app.packageName) }
            )
          }
        }
      }

      // 2. END SECTION: System Tray / Quick Status
      WeykyDesktopSystemTray(
        uiState = uiState,
        themeStyle = themeStyle,
        borderColor = borderColor,
        panelBgElevated = panelBgElevated,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textMuted = textMuted,
        onOpenSettings = onOpenSettings
      )
    }
  }
}

/**
 * Unified WEYKY Start Button.
 * Replaces separate App Drawer and Settings buttons with a single Windows-like Start launcher.
 */
@Composable
private fun WeykyStartButton(
  themeStyle: WeykyThemeStyle,
  isOpen: Boolean,
  borderColor: Color,
  panelBgElevated: Color,
  textPrimary: Color,
  onClick: () -> Unit
) {
  val buttonShape = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> CutCornerShape(6.dp)
    WeykyThemeStyle.MODERN -> RoundedCornerShape(12.dp)
    WeykyThemeStyle.MINIMAL -> RoundedCornerShape(8.dp)
  }

  val activeBorder = if (isOpen) borderColor else borderColor.copy(alpha = 0.45f)
  val activeBg = if (isOpen) borderColor.copy(alpha = 0.20f) else panelBgElevated

  Surface(
    onClick = onClick,
    shape = buttonShape,
    color = activeBg,
    modifier = Modifier
      .height(40.dp)
      .border(1.dp, activeBorder, buttonShape)
      .testTag("desktop_start_button")
  ) {
    Row(
      modifier = Modifier
        .padding(horizontal = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
      // WEYKY Orb Glyph Icon
      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(
                borderColor,
                borderColor.copy(alpha = 0.4f),
                Color.Transparent
              )
            )
          )
          .border(1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(Color.White)
        )
      }

      Text(
        text = "WEYKY",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = if (themeStyle == WeykyThemeStyle.TACTICAL) FontFamily.Monospace else FontFamily.Default,
        letterSpacing = 1.sp,
        color = textPrimary
      )
    }
  }
}

/**
 * Individual Pinned App Icon in the Taskbar.
 * Launches on tap; provides Unpin option on long press or secondary action.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskbarPinnedAppItem(
  app: AppMatch,
  themeStyle: WeykyThemeStyle,
  borderColor: Color,
  panelBgElevated: Color,
  textPrimary: Color,
  onLaunch: () -> Unit,
  onUnpin: () -> Unit
) {
  val context = LocalContext.current
  val iconBitmap = remember(app.packageName) {
    AppIconCache.getIcon(context, app.packageName)
  }
  var showMenu by remember { mutableStateOf(false) }

  val itemShape = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> CutCornerShape(4.dp)
    WeykyThemeStyle.MODERN -> RoundedCornerShape(10.dp)
    WeykyThemeStyle.MINIMAL -> RoundedCornerShape(6.dp)
  }

  Box {
    Surface(
      shape = itemShape,
      color = panelBgElevated.copy(alpha = 0.5f),
      modifier = Modifier
        .size(40.dp)
        .clip(itemShape)
        .combinedClickable(
          onClick = onLaunch,
          onLongClick = { showMenu = true }
        )
        .testTag("taskbar_app_${app.packageName}")
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        if (iconBitmap != null) {
          Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(28.dp)
          )
        } else {
          // Fallback initial
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(borderColor.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = app.label.take(1).uppercase(),
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = textPrimary
            )
          }
        }
      }
    }

    DropdownMenu(
      expanded = showMenu,
      onDismissRequest = { showMenu = false }
    ) {
      DropdownMenuItem(
        text = {
          Text(
            text = "فتح ${app.label}",
            fontSize = 12.sp,
            color = textPrimary
          )
        },
        onClick = {
          showMenu = false
          onLaunch()
        }
      )
      DropdownMenuItem(
        text = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Unpin",
              tint = Color(0xFFEF4444),
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "إلغاء التثبيت من شريط المهام",
              fontSize = 12.sp,
              color = Color(0xFFEF4444)
            )
          }
        },
        onClick = {
          showMenu = false
          onUnpin()
        }
      )
    }
  }
}

/**
 * Desktop System Tray ( صينية النظام )
 * Displays Clock, Date, Battery, Network, and active Audio indicators.
 */
@Composable
private fun WeykyDesktopSystemTray(
  uiState: WeykyUiState,
  themeStyle: WeykyThemeStyle,
  borderColor: Color,
  panelBgElevated: Color,
  textPrimary: Color,
  textSecondary: Color,
  textMuted: Color,
  onOpenSettings: () -> Unit
) {
  val battery = uiState.batterySnapshot
  val isCharging = battery?.chargingState == ChargingState.CHARGING || battery?.chargingState == ChargingState.FULL
  val batteryPct = battery?.percentage ?: 100
  val isLowBattery = batteryPct <= 20 && !isCharging

  val network = uiState.networkSnapshot
  val isOnline = network?.isConnected == true

  val isMusicPlaying = uiState.isMusicPlaying || uiState.activeMediaMetadata?.isPlaying == true

  val trayShape = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> CutCornerShape(6.dp)
    WeykyThemeStyle.MODERN -> RoundedCornerShape(10.dp)
    WeykyThemeStyle.MINIMAL -> RoundedCornerShape(6.dp)
  }

  Surface(
    onClick = onOpenSettings,
    shape = trayShape,
    color = panelBgElevated.copy(alpha = 0.4f),
    modifier = Modifier
      .height(40.dp)
      .border(1.dp, borderColor.copy(alpha = 0.25f), trayShape)
      .testTag("desktop_system_tray")
  ) {
    Row(
      modifier = Modifier
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Audio indicator if active
      if (isMusicPlaying) {
        Icon(
          imageVector = Icons.Default.GraphicEq,
          contentDescription = "Music Playing",
          tint = if (themeStyle == WeykyThemeStyle.MODERN) Color(0xFF10B981) else borderColor,
          modifier = Modifier.size(16.dp)
        )
      }

      // Network indicator
      Icon(
        imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
        contentDescription = if (isOnline) "Connected" else "Offline",
        tint = if (isOnline) borderColor.copy(alpha = 0.85f) else Color(0xFFEF4444),
        modifier = Modifier.size(16.dp)
      )

      // Battery indicator
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        Icon(
          imageVector = when {
            isCharging -> Icons.Default.BatteryChargingFull
            isLowBattery -> Icons.Default.BatteryAlert
            else -> Icons.Default.BatteryFull
          },
          contentDescription = "Battery $batteryPct%",
          tint = when {
            isCharging -> Color(0xFF22C55E)
            isLowBattery -> Color(0xFFEF4444)
            else -> textSecondary
          },
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "$batteryPct%",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          fontFamily = if (themeStyle == WeykyThemeStyle.TACTICAL) FontFamily.Monospace else FontFamily.Default,
          color = textSecondary
        )
      }

      // Time & Date Clock
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
      ) {
        val timeDisplay = uiState.currentTimeString.ifBlank { "--:--" }
        val dateDisplay = uiState.currentDateString.ifBlank { "" }

        Text(
          text = timeDisplay,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = if (themeStyle == WeykyThemeStyle.TACTICAL) FontFamily.Monospace else FontFamily.Default,
          color = textPrimary
        )

        if (dateDisplay.isNotBlank()) {
          Text(
            text = dateDisplay.take(16),
            fontSize = 9.sp,
            color = textMuted,
            maxLines = 1
          )
        }
      }
    }
  }
}
