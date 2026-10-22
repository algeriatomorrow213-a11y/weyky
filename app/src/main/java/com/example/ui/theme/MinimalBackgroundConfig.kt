package com.example.ui.theme

enum class MinimalBackgroundType(val id: String, val titleAr: String) {
  DEFAULT("default", "الافتراضي"),
  SOLID_COLOR("solid_color", "لون ثابت"),
  GRADIENT("gradient", "تدرج لوني"),
  PRESET_WALLPAPER("preset_wallpaper", "مكتبة الخلفيات"),
  CUSTOM_IMAGE("custom_image", "صورة من الهاتف");

  companion object {
    fun fromId(id: String): MinimalBackgroundType =
      entries.firstOrNull { it.id == id } ?: DEFAULT
  }
}

enum class WallpaperCategory(val id: String, val titleAr: String, val titleEn: String) {
  ALL("all", "الكل", "All"),
  SPACE("space", "فضائي", "Space"),
  ABSTRACT("abstract", "تجريدي", "Abstract"),
  NATURE("nature", "طبيعي", "Nature"),
  ARCHITECTURE("architecture", "معماري", "Architecture"),
  DARK("dark", "داكن", "Dark Minimal");

  companion object {
    fun fromId(id: String): WallpaperCategory =
      entries.firstOrNull { it.id == id } ?: ALL
  }
}

data class BuiltInWallpaper(
  val id: String,
  val category: WallpaperCategory,
  val titleAr: String,
  val titleEn: String,
  val startColorHex: Long,
  val endColorHex: Long,
  val accentColorHex: Long,
  val isRadial: Boolean = false
)

object BuiltInWallpapersCatalog {
  val wallpapers: List<BuiltInWallpaper> = listOf(
    // Space
    BuiltInWallpaper(
      id = "space_deep_cosmos",
      category = WallpaperCategory.SPACE,
      titleAr = "سديم كوني عميق",
      titleEn = "Deep Cosmos",
      startColorHex = 0xFF050814L,
      endColorHex = 0xFF101935L,
      accentColorHex = 0xFF3867D6L,
      isRadial = true
    ),
    BuiltInWallpaper(
      id = "space_nebula_pulse",
      category = WallpaperCategory.SPACE,
      titleAr = "نبض المجرة",
      titleEn = "Nebula Pulse",
      startColorHex = 0xFF0B0718L,
      endColorHex = 0xFF220D3AL,
      accentColorHex = 0xFF8854D0L,
      isRadial = true
    ),
    BuiltInWallpaper(
      id = "space_starlight_void",
      category = WallpaperCategory.SPACE,
      titleAr = "أفق النجوم",
      titleEn = "Starlight Void",
      startColorHex = 0xFF03070EL,
      endColorHex = 0xFF0A1828L,
      accentColorHex = 0xFF20BF6BL,
      isRadial = false
    ),

    // Abstract
    BuiltInWallpaper(
      id = "abstract_cyber_wave",
      category = WallpaperCategory.ABSTRACT,
      titleAr = "موجة سايبر",
      titleEn = "Cyber Wave",
      startColorHex = 0xFF090D16L,
      endColorHex = 0xFF142238L,
      accentColorHex = 0xFF0FB9B1L,
      isRadial = false
    ),
    BuiltInWallpaper(
      id = "abstract_dark_prism",
      category = WallpaperCategory.ABSTRACT,
      titleAr = "منشور داكن",
      titleEn = "Dark Prism",
      startColorHex = 0xFF100918L,
      endColorHex = 0xFF1C1328L,
      accentColorHex = 0xFFEB3B5AL,
      isRadial = true
    ),
    BuiltInWallpaper(
      id = "abstract_monolith",
      category = WallpaperCategory.ABSTRACT,
      titleAr = "مونووليث ناعم",
      titleEn = "Monolith Echo",
      startColorHex = 0xFF0A0C10L,
      endColorHex = 0xFF161A22L,
      accentColorHex = 0xFF778CA3L,
      isRadial = false
    ),

    // Nature
    BuiltInWallpaper(
      id = "nature_ocean",
      category = WallpaperCategory.NATURE,
      titleAr = "محيط عميق",
      titleEn = "Ocean Abyss",
      startColorHex = 0xFF040E1AL,
      endColorHex = 0xFF0A2238L,
      accentColorHex = 0xFF2BCBBAL,
      isRadial = false
    ),
    BuiltInWallpaper(
      id = "nature_aurora",
      category = WallpaperCategory.NATURE,
      titleAr = "شفق قطبي ليلي",
      titleEn = "Northern Aurora",
      startColorHex = 0xFF031410L,
      endColorHex = 0xFF0C2B22L,
      accentColorHex = 0xFF26DE81L,
      isRadial = true
    ),
    BuiltInWallpaper(
      id = "nature_dune",
      category = WallpaperCategory.NATURE,
      titleAr = "كثبان المساء",
      titleEn = "Twilight Dune",
      startColorHex = 0xFF160E08L,
      endColorHex = 0xFF2C1C12L,
      accentColorHex = 0xFFFA8231L,
      isRadial = false
    ),

    // Architecture
    BuiltInWallpaper(
      id = "arch_neon_skyline",
      category = WallpaperCategory.ARCHITECTURE,
      titleAr = "أفق نيون",
      titleEn = "Neon Skyline",
      startColorHex = 0xFF090814L,
      endColorHex = 0xFF16152BL,
      accentColorHex = 0xFF45AA2L,
      isRadial = false
    ),
    BuiltInWallpaper(
      id = "arch_brutalist",
      category = WallpaperCategory.ARCHITECTURE,
      titleAr = "هندسة خرسانية",
      titleEn = "Brutalist Grid",
      startColorHex = 0xFF0E1117L,
      endColorHex = 0xFF1A1F2CL,
      accentColorHex = 0xFFA5B1C2L,
      isRadial = false
    ),

    // Dark Minimal
    BuiltInWallpaper(
      id = "dark_obsidian",
      category = WallpaperCategory.DARK,
      titleAr = "أوبسيديان نقي",
      titleEn = "Obsidian Pure",
      startColorHex = 0xFF060709L,
      endColorHex = 0xFF0F1116L,
      accentColorHex = 0xFF4B6584L,
      isRadial = true
    ),
    BuiltInWallpaper(
      id = "dark_carbon",
      category = WallpaperCategory.DARK,
      titleAr = "كاربون مطفي",
      titleEn = "Carbon Matte",
      startColorHex = 0xFF0A0C0FL,
      endColorHex = 0xFF14171DL,
      accentColorHex = 0xFF3867D6L,
      isRadial = false
    ),
    BuiltInWallpaper(
      id = "dark_eclipse",
      category = WallpaperCategory.DARK,
      titleAr = "كسوف رمادي",
      titleEn = "Eclipse Shadow",
      startColorHex = 0xFF08090CL,
      endColorHex = 0xFF12141AL,
      accentColorHex = 0xFF8854D0L,
      isRadial = true
    )
  )

  fun findById(id: String): BuiltInWallpaper? = wallpapers.firstOrNull { it.id == id }
}

data class MinimalBackgroundConfig(
  val type: MinimalBackgroundType = MinimalBackgroundType.DEFAULT,
  val solidColorHex: Long = 0xFF0E121AL,
  val gradientStartHex: Long = 0xFF0D1424L,
  val gradientEndHex: Long = 0xFF050810L,
  val activePresetId: String = "space_deep_cosmos",
  val customImagePath: String? = null,
  val dimOpacity: Float = 0.35f, // 0.0 to 0.85
  val favoritePresetIds: Set<String> = setOf("space_deep_cosmos", "dark_obsidian", "nature_ocean")
)
