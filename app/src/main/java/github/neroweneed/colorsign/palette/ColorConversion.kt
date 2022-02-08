package github.neroweneed.colorsign.palette

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
fun Color.toColorSign(): github.neroweneed.colorsign.palette.Color =
    github.neroweneed.colorsign.palette.Color(this.toArgb().toUInt())

fun github.neroweneed.colorsign.palette.Color.toCompose() = androidx.compose.ui.graphics.Color(
    value.toInt()
)