package com.example.ui.home.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.state.WeykyUiState
import com.example.device.apps.AppMatch
import com.example.ui.home.AppIconCache
import com.example.ui.theme.JarvisHudColors
import com.example.ui.theme.WeykyThemeStyle

/**
 * WEYKY Start Menu for Desktop Landscape Workstation.
 * Elevated flyout panel anchored above the Start button:
 * - Search bar with instant filtering.
 * - Installed applications list with clear Pin/Unpin toggles.
 * - Direct Settings shortcut button.
 * - Outside-click dismissal.
 * - Styled consistently across Tactical HUD, Modern, and Minimal themes.
 */
@Composable
fun WeykyStartMenu(
  uiState: WeykyUiState,
  installedApps: List<AppMatch>,
  pinnedPackageNames: List<String>,
  borderColor: Color,
  panelBg: Color,
  panelBgElevated: Color,
  textPrimary: Color,
  textSecondary: Color,
  textMuted: Color,
  isLight: Boolean,
  onLaunchApp: (AppMatch) -> Unit,
  onTogglePinApp: (String) -> Unit,
  onOpenSettings: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val themeStyle = uiState.themeStyle
  var searchQuery by remember { mutableStateOf("") }

  val filteredApps = remember(searchQuery, installedApps) {
    if (searchQuery.isBlank()) {
      installedApps.sortedBy { it.label.lowercase() }
    } else {
      installedApps.filter {
        it.label.contains(searchQuery, ignoreCase = true) ||
          it.packageName.contains(searchQuery, ignoreCase = true)
      }.sortedBy { it.label.lowercase() }
    }
  }

  val menuShape = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> CutCornerShape(12.dp)
    WeykyThemeStyle.MODERN -> RoundedCornerShape(18.dp)
    WeykyThemeStyle.MINIMAL -> RoundedCornerShape(10.dp)
  }

  val menuBackground = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> panelBg.copy(alpha = 0.98f)
    WeykyThemeStyle.MODERN -> if (isLight) Color(0xFFFAFCFF).copy(alpha = 0.96f) else Color(0xFF141C2B).copy(alpha = 0.94f)
    WeykyThemeStyle.MINIMAL -> if (isLight) Color(0xFFFFFFFF) else Color(0xFF111620)
  }

  val menuBorder = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> borderColor
    WeykyThemeStyle.MODERN -> borderColor.copy(alpha = 0.50f)
    WeykyThemeStyle.MINIMAL -> if (isLight) Color(0xFFD0D7DE) else Color(0xFF2C323E)
  }

  // Full-screen click-outside dismisser backdrop
  Box(
    modifier = Modifier
      .fillMaxSize()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onDismiss
      )
  ) {
    // Start Menu Dialog Panel anchored at bottom-start (or bottom-end according to layout)
    Surface(
      shape = menuShape,
      color = menuBackground,
      modifier = modifier
        .width(360.dp)
        .heightIn(max = 380.dp)
        .border(1.dp, menuBorder, menuShape)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = { /* consume inside click */ }
        )
        .testTag("desktop_start_menu")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // 1. TOP HEADER & SEARCH
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Header title & logo
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(borderColor)
              )
              Text(
                text = "قائمة WEYKY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = if (themeStyle == WeykyThemeStyle.TACTICAL) FontFamily.Monospace else FontFamily.Default,
                color = textPrimary
              )
            }

            Text(
              text = "${installedApps.size} تطبيق",
              fontSize = 11.sp,
              color = textMuted
            )
          }

          // Search Field
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
              Text(
                text = "ابحث عن تطبيق...",
                fontSize = 12.sp,
                color = textMuted
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = textSecondary,
                modifier = Modifier.size(18.dp)
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(
                  onClick = { searchQuery = "" },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = textSecondary,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            },
            singleLine = true,
            shape = when (themeStyle) {
              WeykyThemeStyle.TACTICAL -> CutCornerShape(4.dp)
              WeykyThemeStyle.MODERN -> RoundedCornerShape(10.dp)
              WeykyThemeStyle.MINIMAL -> RoundedCornerShape(6.dp)
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = borderColor,
              unfocusedBorderColor = menuBorder.copy(alpha = 0.5f),
              focusedContainerColor = panelBgElevated.copy(alpha = 0.6f),
              unfocusedContainerColor = panelBgElevated.copy(alpha = 0.3f),
              focusedTextColor = textPrimary,
              unfocusedTextColor = textPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("start_menu_search_field")
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. APPLICATIONS LIST
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (filteredApps.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "لا توجد نتائج مطابقة",
                  fontSize = 12.sp,
                  color = textMuted
                )
              }
            }
          } else {
            items(
              items = filteredApps,
              key = { it.packageName }
            ) { app ->
              val isPinned = pinnedPackageNames.contains(app.packageName)
              StartMenuAppRow(
                app = app,
                isPinned = isPinned,
                themeStyle = themeStyle,
                borderColor = borderColor,
                panelBgElevated = panelBgElevated,
                textPrimary = textPrimary,
                textMuted = textMuted,
                onLaunch = {
                  onLaunchApp(app)
                  onDismiss()
                },
                onTogglePin = { onTogglePinApp(app.packageName) }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3. BOTTOM FOOTER & SETTINGS ACTION
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = 1.dp,
              color = menuBorder.copy(alpha = 0.35f),
              shape = when (themeStyle) {
                WeykyThemeStyle.TACTICAL -> CutCornerShape(4.dp)
                WeykyThemeStyle.MODERN -> RoundedCornerShape(10.dp)
                WeykyThemeStyle.MINIMAL -> RoundedCornerShape(6.dp)
              }
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Surface(
            onClick = {
              onOpenSettings()
              onDismiss()
            },
            shape = RoundedCornerShape(6.dp),
            color = Color.Transparent,
            modifier = Modifier.testTag("start_menu_settings_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = borderColor,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "الإعدادات والنظام",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
              )
            }
          }

          Text(
            text = "WEYKY OS",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = textMuted
          )
        }
      }
    }
  }
}

/**
 * Individual Application Row in the Start Menu.
 * Shows app icon, label, and a direct Pin/Unpin button.
 */
@Composable
private fun StartMenuAppRow(
  app: AppMatch,
  isPinned: Boolean,
  themeStyle: WeykyThemeStyle,
  borderColor: Color,
  panelBgElevated: Color,
  textPrimary: Color,
  textMuted: Color,
  onLaunch: () -> Unit,
  onTogglePin: () -> Unit
) {
  val context = LocalContext.current
  val iconBitmap = remember(app.packageName) {
    AppIconCache.getIcon(context, app.packageName)
  }

  val rowShape = when (themeStyle) {
    WeykyThemeStyle.TACTICAL -> CutCornerShape(4.dp)
    WeykyThemeStyle.MODERN -> RoundedCornerShape(8.dp)
    WeykyThemeStyle.MINIMAL -> RoundedCornerShape(4.dp)
  }

  Surface(
    onClick = onLaunch,
    shape = rowShape,
    color = panelBgElevated.copy(alpha = 0.35f),
    modifier = Modifier
      .fillMaxWidth()
      .height(44.dp)
      .testTag("start_menu_app_${app.packageName}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // App Icon
        if (iconBitmap != null) {
          Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(26.dp)
          )
        } else {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(borderColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = app.label.take(1).uppercase(),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = textPrimary
            )
          }
        }

        // App Name
        Text(
          text = app.label,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = textPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Pin / Unpin Action Button
      IconButton(
        onClick = onTogglePin,
        modifier = Modifier
          .size(32.dp)
          .testTag("pin_toggle_${app.packageName}")
      ) {
        Icon(
          imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
          contentDescription = if (isPinned) "إلغاء التثبيت" else "تثبيت في شريط المهام",
          tint = if (isPinned) borderColor else textMuted,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
