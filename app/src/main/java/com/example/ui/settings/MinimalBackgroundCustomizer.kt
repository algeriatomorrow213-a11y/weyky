package com.example.ui.settings

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BuiltInWallpaper
import com.example.ui.theme.BuiltInWallpapersCatalog
import com.example.ui.theme.JarvisHudCard
import com.example.ui.theme.MinimalBackgroundConfig
import com.example.ui.theme.MinimalBackgroundType
import com.example.ui.theme.WallpaperCategory
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MinimalBackgroundCustomizer(
  config: MinimalBackgroundConfig,
  borderColor: Color,
  panelBg: Color,
  textPrimary: Color,
  textSecondary: Color,
  onUpdateConfig: (MinimalBackgroundConfig) -> Unit,
  onPickImageUri: (Uri) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onResetMinimal: () -> Unit,
  onResetAllDefaults: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) }
  var selectedCategory by remember { mutableStateOf(WallpaperCategory.ALL) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }

  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      onPickImageUri(uri)
    }
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & Subtitle
    Column {
      Text(
        text = "تخصيص خلفية النمط البسيط (Minimal)",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = textPrimary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = "يقتصر تخصيص الخلفية على النمط البسيط فقط، مما يمنحك تجربة مريحة وخيارات متعددة دون اتصال.",
        fontSize = 11.sp,
        color = textSecondary
      )
    }

    // Tabs for Minimal Background Options
    val tabs = listOf(
      "مكتبة مدمجة",
      "صورة من الهاتف",
      "لون ثابت",
      "تدرج لوني",
      "المفضلة"
    )
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      tabs.forEachIndexed { index, tabTitle ->
        val isSelected = selectedTab == index
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) borderColor.copy(alpha = 0.20f) else panelBg)
            .border(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) borderColor else textSecondary.copy(alpha = 0.25f),
              shape = RoundedCornerShape(8.dp)
            )
            .clickable { selectedTab = index }
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Text(
            text = tabTitle,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) textPrimary else textSecondary
          )
        }
      }
    }

    // TAB 0: OFFLINE BUILT-IN WALLPAPER LIBRARY
    if (selectedTab == 0) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Category Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          WallpaperCategory.entries.forEach { category ->
            val isSelected = selectedCategory == category
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) borderColor.copy(alpha = 0.18f) else Color(0x33FFFFFF))
                .border(
                  width = 1.dp,
                  color = if (isSelected) borderColor else Color.Transparent,
                  shape = RoundedCornerShape(16.dp)
                )
                .clickable { selectedCategory = category }
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = category.titleAr,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) borderColor else textSecondary
              )
            }
          }
        }

        val filteredWallpapers = remember(selectedCategory) {
          if (selectedCategory == WallpaperCategory.ALL) {
            BuiltInWallpapersCatalog.wallpapers
          } else {
            BuiltInWallpapersCatalog.wallpapers.filter { it.category == selectedCategory }
          }
        }

        // Wallpapers Grid / Flow
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          maxItemsInEachRow = 2
        ) {
          filteredWallpapers.forEach { wallpaper ->
            val isCurrent = config.type == MinimalBackgroundType.PRESET_WALLPAPER &&
              config.activePresetId == wallpaper.id
            val isFav = config.favoritePresetIds.contains(wallpaper.id)

            WallpaperItemCard(
              wallpaper = wallpaper,
              isCurrent = isCurrent,
              isFavorite = isFav,
              borderColor = borderColor,
              textPrimary = textPrimary,
              textSecondary = textSecondary,
              onSelect = {
                onUpdateConfig(
                  config.copy(
                    type = MinimalBackgroundType.PRESET_WALLPAPER,
                    activePresetId = wallpaper.id
                  )
                )
              },
              onToggleFavorite = { onToggleFavorite(wallpaper.id) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // TAB 1: CUSTOM IMAGE FROM DEVICE
    if (selectedTab == 1) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
          onClick = { imagePickerLauncher.launch("image/*") },
          colors = ButtonDefaults.buttonColors(containerColor = borderColor.copy(alpha = 0.22f)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().testTag("pick_custom_wallpaper_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = borderColor)
            Text(text = "اختيار صورة من الهاتف", color = textPrimary, fontWeight = FontWeight.Bold)
          }
        }

        // Direct Preview of Chosen Image
        val customPath = config.customImagePath
        val previewBitmap = remember(customPath) {
          if (!customPath.isNullOrBlank()) {
            val file = File(customPath)
            if (file.exists()) {
              try {
                BitmapFactory.decodeFile(file.absolutePath)
              } catch (e: Exception) {
                null
              }
            } else null
          } else null
        }

        if (previewBitmap != null) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "المعاينة الحالية للصورة المختارة:", fontSize = 12.sp, color = textSecondary)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
              Image(
                bitmap = previewBitmap.asImageBitmap(),
                contentDescription = "Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
              // Dim preview overlay
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color.Black.copy(alpha = config.dimOpacity))
              )
              Text(
                text = "معاينة مع تعتيم ${(config.dimOpacity * 100).toInt()}%",
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .background(Color.Black.copy(alpha = 0.6f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(100.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(panelBg)
              .border(1.dp, textSecondary.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "لم يتم اختيار صورة بعد. اضغط الزر أعلاه لاختيار صورة من جهازك.",
              fontSize = 12.sp,
              color = textSecondary
            )
          }
        }
      }
    }

    // TAB 2: SOLID COLOR
    if (selectedTab == 2) {
      val solidColorPresets = listOf(
        "أوبسيديان نقي" to 0xFF060709L,
        "فحم داكن" to 0xFF12141AL,
        "رمادي أردواز" to 0xFF181E29L,
        "غابة ليلية" to 0xFF081812L,
        "برقوقي عميق" to 0xFF1A0C1AL,
        "كحلي ملكي" to 0xFF0A1326L,
        "إسبريسو دافئ" to 0xFF18100AL
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "اختر لوناً ثابتاً للخلفية:", fontSize = 12.sp, color = textSecondary)
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          solidColorPresets.forEach { (name, hex) ->
            val isSelected = config.type == MinimalBackgroundType.SOLID_COLOR &&
              config.solidColorHex == hex
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(hex))
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) borderColor else Color(0x44FFFFFF),
                  shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                  onUpdateConfig(
                    config.copy(
                      type = MinimalBackgroundType.SOLID_COLOR,
                      solidColorHex = hex
                    )
                  )
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(hex))
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                )
                Text(
                  text = name,
                  fontSize = 12.sp,
                  color = Color.White,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                if (isSelected) {
                  Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = borderColor, modifier = Modifier.size(14.dp))
                }
              }
            }
          }
        }
      }
    }

    // TAB 3: GRADIENT
    if (selectedTab == 3) {
      val gradientPresets = listOf(
        "شفق ليل" to (0xFF0B1124L to 0xFF04060DL),
        "زمرد داكن" to (0xFF061A14L to 0xFF020A07L),
        "بنفسجي غامق" to (0xFF1A0A26L to 0xFF08020EL),
        "رمادي هادئ" to (0xFF1E2430L to 0xFF0C0E14L),
        "قهوة المساء" to (0xFF24160CL to 0xFF0A0604L)
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "اختر تدرجاً لونياً مخصصاً:", fontSize = 12.sp, color = textSecondary)
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          gradientPresets.forEach { (name, pair) ->
            val isSelected = config.type == MinimalBackgroundType.GRADIENT &&
              config.gradientStartHex == pair.first && config.gradientEndHex == pair.second

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(
                  Brush.linearGradient(listOf(Color(pair.first), Color(pair.second)))
                )
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) borderColor else Color(0x44FFFFFF),
                  shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                  onUpdateConfig(
                    config.copy(
                      type = MinimalBackgroundType.GRADIENT,
                      gradientStartHex = pair.first,
                      gradientEndHex = pair.second
                    )
                  )
                }
                .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = name,
                  fontSize = 12.sp,
                  color = Color.White,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                if (isSelected) {
                  Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = borderColor, modifier = Modifier.size(14.dp))
                }
              }
            }
          }
        }
      }
    }

    // TAB 4: FAVORITES
    if (selectedTab == 4) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "الخلفيات المفضلة لديك:", fontSize = 12.sp, color = textSecondary)
        if (config.favoritePresetIds.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(90.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(panelBg),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "لا توجد خلفيات في المفضلة بعد. اضغط على رمز النجمة بجانب أي خلفية لإضافتها.",
              fontSize = 12.sp,
              color = textSecondary
            )
          }
        } else {
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
          ) {
            config.favoritePresetIds.mapNotNull { BuiltInWallpapersCatalog.findById(it) }.forEach { wallpaper ->
              val isCurrent = config.type == MinimalBackgroundType.PRESET_WALLPAPER &&
                config.activePresetId == wallpaper.id

              WallpaperItemCard(
                wallpaper = wallpaper,
                isCurrent = isCurrent,
                isFavorite = true,
                borderColor = borderColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                onSelect = {
                  onUpdateConfig(
                    config.copy(
                      type = MinimalBackgroundType.PRESET_WALLPAPER,
                      activePresetId = wallpaper.id
                    )
                  )
                },
                onToggleFavorite = { onToggleFavorite(wallpaper.id) },
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    // OPACITY DIMMING CONTROLS (Applies to background only)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(panelBg)
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "درجة تعتيم الخلفية (Background Dimming): ${(config.dimOpacity * 100).toInt()}%",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = textPrimary
      )
      Text(
        text = "يُطبق التعتيم على الخلفية فقط لضمان قراءة النصوص وتباين النواة والبطاقات.",
        fontSize = 10.sp,
        color = textSecondary
      )

      // Ready Opacity Presets: Light (20%), Medium (40%), Dark (65%)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val opacityPresets = listOf(
          "خفيف (20%)" to 0.20f,
          "متوسط (40%)" to 0.40f,
          "داكن (65%)" to 0.65f
        )
        opacityPresets.forEach { (label, value) ->
          val isSelected = Math.abs(config.dimOpacity - value) < 0.05f
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) borderColor.copy(alpha = 0.20f) else Color(0x22FFFFFF))
              .border(
                width = 1.dp,
                color = if (isSelected) borderColor else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
              )
              .clickable { onUpdateConfig(config.copy(dimOpacity = value)) }
              .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) borderColor else textPrimary
            )
          }
        }
      }

      Slider(
        value = config.dimOpacity,
        onValueChange = { onUpdateConfig(config.copy(dimOpacity = it)) },
        valueRange = 0.0f..0.85f,
        colors = SliderDefaults.colors(
          thumbColor = borderColor,
          activeTrackColor = borderColor,
          inactiveTrackColor = textSecondary.copy(alpha = 0.3f)
        )
      )
    }

    // RESET ACTIONS
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Reset Minimal Background Alone
      OutlinedButton(
        onClick = onResetMinimal,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.weight(1f)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(text = "استعادة خلفية Minimal", fontSize = 11.sp)
        }
      }

      // 2. Full Appearance Reset
      OutlinedButton(
        onClick = { showResetConfirmDialog = true },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
        modifier = Modifier.weight(1f)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
          Text(text = "استعادة المظهر بالكامل", fontSize = 11.sp)
        }
      }
    }

    // Confirmation Dialog before Full Reset
    if (showResetConfirmDialog) {
      AlertDialog(
        onDismissRequest = { showResetConfirmDialog = false },
        title = {
          Text(text = "تأكيد استعادة المظهر بالكامل", fontWeight = FontWeight.Bold)
        },
        text = {
          Text(
            text = "هل أنت متأكد من رغبتك في استعادة جميع إعدادات المظهر إلى الوضع الافتراضي (النمط التكتيكي والخلفية السوداء)؟ لن يتم حذف قائمة المفضلة."
          )
        },
        confirmButton = {
          Button(
            onClick = {
              onResetAllDefaults()
              showResetConfirmDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
          ) {
            Text("نعم، استعادة الافتراضي", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { showResetConfirmDialog = false }) {
            Text("إلغاء")
          }
        }
      )
    }
  }
}

@Composable
private fun WallpaperItemCard(
  wallpaper: BuiltInWallpaper,
  isCurrent: Boolean,
  isFavorite: Boolean,
  borderColor: Color,
  textPrimary: Color,
  textSecondary: Color,
  onSelect: () -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier
) {
  val startCol = Color(wallpaper.startColorHex)
  val endCol = Color(wallpaper.endColorHex)

  Box(
    modifier = modifier
      .height(96.dp)
      .clip(RoundedCornerShape(10.dp))
      .border(
        width = if (isCurrent) 2.dp else 1.dp,
        color = if (isCurrent) borderColor else Color(0x33FFFFFF),
        shape = RoundedCornerShape(10.dp)
      )
      .background(
        Brush.linearGradient(
          colors = listOf(startCol, endCol),
          start = Offset(0f, 0f),
          end = Offset(120f, 120f)
        )
      )
      .clickable { onSelect() }
      .padding(8.dp)
  ) {
    // Favorite Star Icon Button
    IconButton(
      onClick = onToggleFavorite,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .size(26.dp)
    ) {
      Icon(
        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
        contentDescription = "Favorite",
        tint = if (isFavorite) Color(0xFFFFD700) else Color.White.copy(alpha = 0.7f),
        modifier = Modifier.size(18.dp)
      )
    }

    // Title and Active Badge
    Column(
      modifier = Modifier.align(Alignment.BottomStart)
    ) {
      Text(
        text = wallpaper.titleAr,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
      if (isCurrent) {
        Text(
          text = "النشط حالياً",
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = borderColor
        )
      }
    }
  }
}
