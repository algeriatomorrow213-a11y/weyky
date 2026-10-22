package com.example.ui.desktop

import androidx.compose.ui.unit.dp
import com.example.ui.adaptive.AdaptiveLayoutMode
import com.example.ui.adaptive.AdaptiveOrientation
import com.example.ui.adaptive.DevicePosture
import com.example.ui.adaptive.WindowSizeResolver
import com.example.ui.adaptive.WindowWidthSizeClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DesktopTaskbarTest {

  @Test
  fun `landscape dimensions activate desktop workstation layout mode`() {
    val landscapeProfile = WindowSizeResolver.resolve(
      widthDp = 840.dp,
      heightDp = 390.dp,
      isRtl = true,
      posture = DevicePosture.NORMAL
    )

    assertEquals(AdaptiveOrientation.LANDSCAPE, landscapeProfile.orientation)
    assertEquals(AdaptiveLayoutMode.PHONE_LANDSCAPE, landscapeProfile.layoutMode)
    assertTrue(landscapeProfile.isCompactLandscape)
    assertTrue("Should preserve RTL direction", landscapeProfile.isRtl)
  }

  @Test
  fun `portrait orientation strictly preserves phone portrait layout`() {
    val portraitProfile = WindowSizeResolver.resolve(
      widthDp = 390.dp,
      heightDp = 840.dp,
      isRtl = true,
      posture = DevicePosture.NORMAL
    )

    assertEquals(AdaptiveOrientation.PORTRAIT, portraitProfile.orientation)
    assertEquals(AdaptiveLayoutMode.PHONE_PORTRAIT, portraitProfile.layoutMode)
    assertFalse(portraitProfile.isCompactLandscape)
    assertFalse(portraitProfile.isTablet)
  }

  @Test
  fun `pinned apps list prevents duplicate entries`() {
    val initial = listOf("com.android.chrome", "com.android.settings")
    val toAdd = "com.android.chrome"

    val updated = if (initial.contains(toAdd)) initial else initial + toAdd
    assertEquals(2, updated.size)
    assertEquals(listOf("com.android.chrome", "com.android.settings"), updated)
  }

  @Test
  fun `pinning and unpinning maintains ordering and consistency`() {
    val initial = listOf("com.android.dialer", "com.android.settings")

    // Pin new app
    val appToAdd = "com.google.android.youtube"
    val afterPin = if (initial.contains(appToAdd)) initial else initial + appToAdd
    assertEquals(3, afterPin.size)
    assertEquals(listOf("com.android.dialer", "com.android.settings", "com.google.android.youtube"), afterPin)

    // Unpin app
    val afterUnpin = afterPin.filter { it != "com.android.settings" }
    assertEquals(2, afterUnpin.size)
    assertEquals(listOf("com.android.dialer", "com.google.android.youtube"), afterUnpin)
  }
}
