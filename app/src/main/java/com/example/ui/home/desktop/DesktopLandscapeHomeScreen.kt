package com.example.ui.home.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.state.WeykyUiState
import com.example.device.apps.AppMatch
import com.example.device.battery.ChargingState
import com.example.ui.adaptive.DeviceProfile
import com.example.ui.home.HomeContextAlertCard
import com.example.ui.home.HomeCoreSection
import com.example.ui.home.HomeDateCard
import com.example.ui.home.HomeInputCard
import com.example.ui.home.HomeMusicCard
import com.example.ui.home.HomeResponseCard
import com.example.ui.home.HomeWeatherCard

/**
 * Desktop Landscape Workstation Screen for WEYKY.
 * Complete desktop experience for landscape orientation across all 3 themes:
 *
 * 1. Desktop Workspace (Top/Center):
 *    - Start Side: Weather card and active media player.
 *    - Center Stage: Unobstructed Living Core (scale = 0.44f preserved) and assistant response bubble.
 *    - End Side: Date card and contextual telemetry alerts.
 *
 * 2. Elevated Command Bar:
 *    - Positioned strictly in a dedicated zone ABOVE the taskbar (never overlapping or behind).
 *
 * 3. Bottom Desktop Taskbar:
 *    - Fixed full-width taskbar containing WEYKY Start Button, Pinned Applications, and System Tray.
 *
 * 4. Start Menu Flyout:
 *    - Windows-inspired popup menu with instant app search, pin/unpin toggles, and Settings shortcut.
 */
@Composable
fun DesktopLandscapeHomeScreen(
  uiState: WeykyUiState,
  deviceProfile: DeviceProfile,
  borderColor: Color,
  panelBg: Color,
  panelBgElevated: Color,
  textPrimary: Color,
  textSecondary: Color,
  textMuted: Color,
  isLight: Boolean,
  onQuerySubmit: (String) -> Unit,
  onQueryChange: (String) -> Unit,
  onOpenSettings: () -> Unit,
  onRequestPermission: (String) -> Unit,
  onToggleVoice: () -> Unit,
  onConfirmOption: (String) -> Unit,
  onLaunchApp: (AppMatch) -> Unit,
  onTogglePinApp: (String) -> Unit,
  onToggleMediaPlayback: () -> Unit,
  onNextMediaTrack: () -> Unit,
  onPreviousMediaTrack: () -> Unit,
  keyboardController: SoftwareKeyboardController?,
  modifier: Modifier = Modifier
) {
  var isStartMenuOpen by remember { mutableStateOf(false) }

  // Resolve pinned applications in order
  val pinnedApps = remember(uiState.pinnedAppPackages, uiState.installedApps) {
    if (uiState.pinnedAppPackages.isEmpty()) {
      // Smart default pins from installed apps if user hasn't explicitly unpinned all
      uiState.installedApps.take(4)
    } else {
      val appMap = uiState.installedApps.associateBy { it.packageName }
      uiState.pinnedAppPackages.mapNotNull { appMap[it] }
    }
  }

  val dynamicCoreSize = deviceProfile.calculateAdaptiveCoreSize(uiState.coreScale)

  val isMusicActivelyPlaying = uiState.isMusicPlaying || (uiState.activeMediaMetadata?.isPlaying == true)
  val hasActiveMedia = isMusicActivelyPlaying || (uiState.activeMediaMetadata != null && !uiState.activeMediaMetadata.title.isNullOrBlank())
  val hasResponse = !uiState.lastResponseText.isNullOrBlank()

  val battery = uiState.batterySnapshot
  val isLowBattery = battery != null && battery.isAvailable && battery.percentage <= 20 &&
    battery.chargingState != ChargingState.CHARGING &&
    battery.chargingState != ChargingState.FULL
  val isBatteryFull = battery != null && battery.isAvailable && battery.percentage == 100 &&
    battery.chargingState == ChargingState.FULL
  val hasMissedCall = (uiState.missedCallsSummary?.missedCallsCount ?: 0) > 0
  val hasUrgentNotif = (uiState.notificationSummary?.totalImportantCount ?: 0) > 0
  val hasFallbackEvent = uiState.importantEvents.isNotEmpty()
  val hasAlert = isLowBattery || isBatteryFull || hasMissedCall || hasUrgentNotif || hasFallbackEvent

  // Side columns take 1f each; center column takes dynamic weight to keep core strictly centered
  val centerWeight = when {
    deviceProfile.windowWidthDp >= 900.dp -> 1.50f
    deviceProfile.windowWidthDp >= 700.dp -> 1.35f
    else -> 1.20f
  }

  // Response card max height adapted to available workspace
  val responseMaxHeight = when {
    deviceProfile.windowHeightDp < 400.dp -> (deviceProfile.windowHeightDp * 0.22f).coerceIn(48.dp, 80.dp)
    deviceProfile.windowHeightDp < 600.dp -> (deviceProfile.windowHeightDp * 0.28f).coerceIn(72.dp, 120.dp)
    else -> (deviceProfile.windowHeightDp * 0.32f).coerceIn(100.dp, 180.dp)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("desktop_landscape_container")
  ) {
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // 1. DESKTOP MAIN WORKSPACE (Takes available vertical space above the taskbar)
      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(
            start = deviceProfile.contentPaddingHorizontalDp,
            top = deviceProfile.contentPaddingVerticalDp.coerceAtLeast(6.dp),
            end = deviceProfile.contentPaddingHorizontalDp,
            bottom = 4.dp
          ),
        horizontalArrangement = Arrangement.spacedBy(deviceProfile.cardSpacingDp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // --- START SIDE REGION (Weather & Media) ---
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Weather Widget
          HomeWeatherCard(
            uiState = uiState,
            borderColor = borderColor,
            panelBg = panelBg,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textMuted = textMuted,
            density = deviceProfile.cardDensity,
            onQuerySubmit = onQuerySubmit,
            modifier = Modifier.fillMaxWidth()
          )

          // Media Player (when active)
          Column(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            if (hasActiveMedia) {
              HomeMusicCard(
                uiState = uiState,
                borderColor = borderColor,
                panelBgElevated = panelBgElevated,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onToggleMediaPlayback = onToggleMediaPlayback,
                onNextMediaTrack = onNextMediaTrack,
                onPreviousMediaTrack = onPreviousMediaTrack,
                isLandscape = true
              )
            }
          }
        }

        // --- CENTER REGION (Living Core Stage & Response Card) ---
        Column(
          modifier = Modifier
            .weight(centerWeight)
            .fillMaxHeight(),
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Center Core Stage
          BoxWithConstraints(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            val maxDim = minOf(maxHeight, maxWidth)
            val coreSize = minOf(dynamicCoreSize, maxDim * 0.92f)
              .coerceIn(minOf(52.dp, maxDim * 0.75f), 360.dp)

            HomeCoreSection(
              uiState = uiState,
              coreSizeDp = coreSize,
              onToggleVoice = onToggleVoice
            )
          }

          // Assistant Response Bubble (when active)
          if (hasResponse) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .heightIn(max = responseMaxHeight)
                .verticalScroll(rememberScrollState())
            ) {
              HomeResponseCard(
                uiState = uiState,
                borderColor = borderColor,
                panelBg = panelBg,
                panelBgElevated = panelBgElevated,
                textPrimary = textPrimary,
                onQuerySubmit = onQuerySubmit,
                onConfirmOption = onConfirmOption,
                onRequestPermission = onRequestPermission
              )
            }
          }
        }

        // --- END SIDE REGION (Date & Context Alerts) ---
        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Date / Calendar Widget
          HomeDateCard(
            uiState = uiState,
            borderColor = borderColor,
            panelBg = panelBg,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textMuted = textMuted,
            density = deviceProfile.cardDensity,
            onQuerySubmit = onQuerySubmit,
            modifier = Modifier.fillMaxWidth()
          )

          // Contextual Telemetry Alerts (when active)
          Column(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            if (hasAlert) {
              HomeContextAlertCard(
                uiState = uiState,
                borderColor = borderColor,
                onQuerySubmit = onQuerySubmit
              )
            }
          }
        }
      }

      // 2. ELEVATED INPUT BAR: Positioned strictly ABOVE the taskbar in its own dedicated strip
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            horizontal = deviceProfile.contentPaddingHorizontalDp,
            vertical = 4.dp
          ),
        contentAlignment = Alignment.Center
      ) {
        HomeInputCard(
          uiState = uiState,
          borderColor = borderColor,
          panelBg = panelBg,
          textPrimary = textPrimary,
          textSecondary = textSecondary,
          onQueryChange = onQueryChange,
          onQuerySubmit = onQuerySubmit,
          onToggleVoice = onToggleVoice,
          keyboardController = keyboardController,
          modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxWidth(0.72f)
        )
      }

      // 3. FIXED BOTTOM DESKTOP TASKBAR
      WeykyDesktopTaskbar(
        uiState = uiState,
        pinnedApps = pinnedApps,
        isStartMenuOpen = isStartMenuOpen,
        borderColor = borderColor,
        panelBg = panelBg,
        panelBgElevated = panelBgElevated,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textMuted = textMuted,
        isLight = isLight,
        onToggleStartMenu = { isStartMenuOpen = !isStartMenuOpen },
        onLaunchApp = onLaunchApp,
        onUnpinApp = onTogglePinApp,
        onOpenSettings = onOpenSettings,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // 4. FLOATING WEYKY START MENU POPUP
    if (isStartMenuOpen) {
      WeykyStartMenu(
        uiState = uiState,
        installedApps = uiState.installedApps,
        pinnedPackageNames = if (uiState.pinnedAppPackages.isEmpty()) {
          pinnedApps.map { it.packageName }
        } else {
          uiState.pinnedAppPackages
        },
        borderColor = borderColor,
        panelBg = panelBg,
        panelBgElevated = panelBgElevated,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textMuted = textMuted,
        isLight = isLight,
        onLaunchApp = onLaunchApp,
        onTogglePinApp = onTogglePinApp,
        onOpenSettings = onOpenSettings,
        onDismiss = { isStartMenuOpen = false },
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(
            start = 8.dp,
            bottom = 56.dp
          )
      )
    }
  }
}
