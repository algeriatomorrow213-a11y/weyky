package com.example.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Three distinct aesthetic styles supported across WEYKY:
 * - TACTICAL: Black background, sharp cut corners, cybernetic HUD borders, monospace tags.
 * - MODERN: Midnight cinematic background, ambient radial glow, rounded glassmorphism cards, luminous borders.
 * - MINIMAL: Calm understated aesthetic, subtle matte dark cards, customizable background options.
 */
enum class WeykyThemeStyle(
  val id: String,
  val titleAr: String,
  val titleEn: String,
  val descriptionAr: String,
  val badgeTag: String
) {
  TACTICAL(
    id = "tactical",
    titleAr = "تكتيكي (Tactical)",
    titleEn = "Tactical HUD",
    descriptionAr = "خلفية سوداء تكتيكية بهوية HUD تقنية متقدمة، زوايا مشطوفة، وحدود سايبر حادة.",
    badgeTag = "HUD // TACTICAL"
  ),
  MODERN(
    id = "modern",
    titleAr = "عصري (Modern)",
    titleEn = "Modern Glass",
    descriptionAr = "هوية سينمائية فاخرة بخلفية ليلية داكنة، بطاقات زجاجية ناعمة، وإضاءة محيطية ساحرة.",
    badgeTag = "GLASS // MODERN"
  ),
  MINIMAL(
    id = "minimal",
    titleAr = "بسيط (Minimal)",
    titleEn = "Minimal Calm",
    descriptionAr = "هوية هادئة وبسيطة بدون تشتيت، بطاقات داكنة ناعمة، مع حرية تخصيص الخلفية.",
    badgeTag = "CALM // MINIMAL"
  );

  companion object {
    fun fromId(id: String): WeykyThemeStyle = entries.firstOrNull { it.id == id } ?: TACTICAL
  }
}

/**
 * Theme day/night mode preference:
 * - AUTO: Follows system dark theme or time of day.
 * - DARK: Enforces dark mode.
 * - LIGHT: Enforces high-contrast light mode.
 */
enum class ThemeModeOption(
  val id: String,
  val titleAr: String,
  val titleEn: String
) {
  AUTO("auto", "تلقائي (حسب النظام)", "Auto (System)"),
  DARK("dark", "الوضع الداكن", "Dark Mode"),
  LIGHT("light", "الوضع الفاتح", "Light Mode");

  companion object {
    fun fromId(id: String): ThemeModeOption = entries.firstOrNull { it.id == id } ?: DARK
  }
}

val LocalWeykyThemeStyle = compositionLocalOf { WeykyThemeStyle.TACTICAL }
