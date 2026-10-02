package com.swimgear.app.ui.legacy

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

internal fun View.aplicarInsets() {
    val left = paddingLeft; val top = paddingTop; val right = paddingRight; val bottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        view.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
        insets
    }
    ViewCompat.requestApplyInsets(this)
}
