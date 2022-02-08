package github.neroweneed.colorsign.palette

import kotlin.math.*

/*
Color stored in ARGB format
 */
/*
Credit to: https://sighack.com/post/procedural-color-algorithms-monochromatic-color-scheme

 */
@JvmInline
value class Color internal constructor(val value: UInt) {
    companion object {
        fun create(color: Int) = Color(color.toUInt())
        fun create(red: Int, green: Int, blue: Int, alpha: Int = 255) = Color(
            (alpha.coerceIn(0..255).shl(24) or
                    red.coerceIn(0..255).shl(16) or
                    green.coerceIn(0..255).shl(8) or
                    blue.coerceIn(0..255)).toUInt()
        )

        fun create(color: UInt) = Color(color)
        fun create(
            hue: Float,
            saturation: Float,
            value: Float,
            alpha: Float = 1f
        ): github.neroweneed.colorsign.palette.Color {
            val c = value * saturation
            val x = (c * (1 - abs((hue / 60f) % 2 - 1)))
            val m = (value - c)
            val (r2, g2, b2) = when (hue.toInt()) {
                in (0 until 60) -> Triple(c, x, 0f)
                in (60 until 120) -> Triple(x, c, 0f)
                in (120 until 180) -> Triple(0f, c, x)
                in (180 until 240) -> Triple(0f, x, c)
                in (240 until 300) -> Triple(x, 0f, c)
                in (300 until 360) -> Triple(c, 0f, x)
                else -> Triple(c, x, 0f)
            }

            val (r, g, b) = Triple(
                (r2 + m) * 255,
                (g2 + m) * 255,
                (b2 + m) * 255
            )
            val a = (alpha.coerceIn(0f, 1f) * 255)
            return Color(
                (a.toUInt() shl 24) or
                        (r.toUInt() shl 16) or
                        (g.toUInt() shl 8) or
                        (b.toUInt())
            )


        }
    }

    val red: UByte
        get() = ((value and 0x00FF0000u) shr 16).toUByte()
    val green: UByte
        get() = ((value and 0x0000FF00u) shr 8).toUByte()
    val blue: UByte
        get() = (value and 0x000000FFu).toUByte()
    val alpha: UByte
        get() = ((value and 0xFF000000u) shr 24).toUByte()
    val relativeLuminance: Float
        get() {
            val rf = red.toInt() / 255f
            val gf = green.toInt() / 255f
            val bf = blue.toInt() / 255f
            val r = if (rf <= 0.03928f) rf / 12.92f else ((rf + 0.055f) / 1.055f).pow(2.4f)
            val g = if (gf <= 0.03928f) gf / 12.92f else ((gf + 0.055f) / 1.055f).pow(2.4f)
            val b = if (bf <= 0.03928f) bf / 12.92f else ((bf + 0.055f) / 1.055f).pow(2.4f)
            return 0.2126f * r + 0.7152f * g * 0.0722f * b
        }
    val intensity: Float
        get() = red.toInt() * 0.299f + green.toInt() * 0.587f + blue.toInt() * 0.114f

    fun toRGB(): Triple<UByte, UByte, UByte> = Triple(red, green, blue)
    fun toHSB(): Triple<Float, Float, Float> = toHSV()
    fun toHSV(): Triple<Float, Float, Float> {

        val (r, g, b) = toRGB().let { (r, g, b) ->
            Triple(r.toFloat() / 255, g.toFloat() / 255, b.toFloat() / 255)
        }
        val cMax = max(max(r, g), b)
        val cMin = min(min(r, g), b)
        val delta = cMax - cMin

        val hue = 60 * when {
            delta == 0F -> 0F
            cMax == r -> (((g - b) / delta) % 6)
            cMax == g -> (((b - r) / delta) + 2)
            cMax == b -> (((r - g) / delta) + 4)
            else -> 0F
        }
        val saturation = when (cMax) {
            0f -> 0F
            else -> delta / cMax
        }
        return Triple(hue, saturation, cMax)
    }
    fun adjust(red: Int = -1,green: Int = -1,blue: Int = -1,alpha: Int = -1) : Color {
        return create(if (red < 0) this.red.toInt() else red.coerceIn(0..255),
            if (green < 0) this.green.toInt() else green.coerceIn(0..255),
            if (blue < 0) this.blue.toInt() else blue.coerceIn(0..255),
            if (alpha < 0) this.alpha.toInt() else alpha.coerceIn(0..255),
        )
    }
    fun adjust(red: Int = -1,green: Int = -1,blue: Int = -1,alpha: Float = -1f) : Color {
        return create(if (red < 0) this.red.toInt() else red.coerceIn(0..255),
            if (green < 0) this.green.toInt() else green.coerceIn(0..255),
            if (blue < 0) this.blue.toInt() else blue.coerceIn(0..255),
            if (alpha < 0f) this.alpha.toInt() else (alpha*255).toInt().coerceIn(0..255),
        )
    }

    fun toHSL(): Triple<Float, Float, Float> {
        val (r, g, b) = toRGB().let { (r, g, b) ->
            Triple(r.toFloat() / 255, g.toFloat() / 255, b.toFloat() / 255)
        }
        val cMax = max(max(r, g), b)
        val cMin = min(min(r, g), b)
        val delta = cMax - cMin
        val hue = 60 * when {
            delta == 0F -> 0F
            cMax == r -> (((g - b) / delta) % 6)
            cMax == g -> (((b - r) / delta) + 2)
            cMax == b -> (((r - g) / delta) + 4)
            else -> 0F
        }
        val lightness = (cMax + cMax) / 2f
        val saturation = when (cMax) {
            0f -> 0F
            else -> delta / (1 - abs(2 * lightness - 1))
        }
        return Triple(hue, saturation, lightness)
    }

    fun toCMYK(): CMYK {
        val (r, g, b) = toRGB().let { (r, g, b) ->
            Triple(r.toFloat() / 255, g.toFloat() / 255, b.toFloat() / 255)
        }
        val k = 1 - max(max(r, g), b)
        val c = (1 - r - k) / (1 - k)
        val m = (1 - g - k) / (1 - k)
        val y = (1 - b - k) / (1 - k)

        return CMYK(c, m, y, k)
    }

    fun foregroundColor(): Color =
        if (relativeLuminance > 0.179f) Color(0xFF000000u) else Color(0xFFFFFFFFu)


    override

    fun toString(): String = toString(true)
    fun toString(alpha: Boolean): String {
        return if (alpha)
            "#${value.toString(16).uppercase().padStart(8, '0')}"
        else
            "#${value.and(0x00FFFFFFu).toString(16).uppercase().padStart(6, '0')}"
    }


}


data class CMYK(val c: Float, val m: Float, val y: Float, val k: Float)
typealias Palette = List<github.neroweneed.colorsign.palette.Color>


private fun hsvValueScale(
    hue: Float,
    steps: Int
): List<github.neroweneed.colorsign.palette.Color> {
    return (0 until steps).map { step ->
        val value = map3(step.toFloat(), 0f, steps - 1f, 1f, 0f, 1.6f, Easing.EASE_IN)
        val saturation = if (step < steps / 2)
            map3(step.toFloat(), 0f, (steps / 2) - 1f, 0f, 1f, 1.6f, Easing.EASE_IN)
        else
            1f
        github.neroweneed.colorsign.palette.Color.Companion.create(
            hue,
            saturation,
            value
        )
    }
}

private fun hsvValueScale(steps: Int): List<github.neroweneed.colorsign.palette.Color> {
    return (0 until steps).map { i ->
        val value = map(i.toFloat(), 0f, steps - 1f, 100f, 0f)

        github.neroweneed.colorsign.palette.Color.Companion.create(
            0f,
            0f,
            value
        )
    }
}

private fun map(value: Float, start1: Float, stop1: Float, start2: Float, stop2: Float) =
    start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))

private fun map3(
    value: Float,
    start1: Float,
    stop1: Float,
    start2: Float,
    stop2: Float,
    v: Float,
    easing: Easing
): Float {
    val c = stop2 - start2
    val d = stop1 - start1
    return when (easing) {
        Easing.EASE_IN -> {
            val t = (value - start1) / d
            c * t.pow(v) + start2
        }
        Easing.EASE_OUT -> {
            val t = (value - start1) / d
            c * (1 - (1 - t).pow(v)) + start2
        }
        Easing.EASE_IN_OUT -> {
            val t = (value - start1) / (d / 2f)
            if (t < 1)
                (c / 2) * (t.pow(v)) + start2
            else
                (c / 2) * (2 - (2 - t).pow(v)) + start2
        }
    }
}

enum class Easing {
    EASE_IN, EASE_OUT, EASE_IN_OUT
}

fun github.neroweneed.colorsign.palette.Color.complement(): github.neroweneed.colorsign.palette.Color {
    val (h, s, v) = this.toHSV()
    val h2 = (h + 180) % 360
    return github.neroweneed.colorsign.palette.Color.create(h2, s, v)
}

fun github.neroweneed.colorsign.palette.Color.triadic(): Pair<github.neroweneed.colorsign.palette.Color, github.neroweneed.colorsign.palette.Color> {
    val (h, s, v) = this.toHSV()
    val h2 = (h + 60) % 360
    val h3 = (h + 120) % 360
    return github.neroweneed.colorsign.palette.Color.create(
        h2,
        s,
        v
    ) to github.neroweneed.colorsign.palette.Color.create(h3, s, v)
}

fun github.neroweneed.colorsign.palette.Color.tetradic(): Triple<github.neroweneed.colorsign.palette.Color, github.neroweneed.colorsign.palette.Color, github.neroweneed.colorsign.palette.Color> {
    val (h, s, v) = this.toHSV()
    val h2 = (h + 90) % 360
    val h3 = (h + 180) % 360
    val h4 = (h + 270) % 360
    return Triple(
        github.neroweneed.colorsign.palette.Color.create(h2, s, v),
        github.neroweneed.colorsign.palette.Color.create(h3, s, v),
        github.neroweneed.colorsign.palette.Color.create(h4, s, v)
    )
}

fun github.neroweneed.colorsign.palette.Color.monochromatic(steps: Int): Palette {
    return if (steps <= 2)
        listOf(this)
    else {
        val (h, _, _) = this.toHSV()
        hsvValueScale(h, steps)
    }

}

fun github.neroweneed.colorsign.palette.Color.invert(): Color = Color.create(255 - red.toInt(), 255 - green.toInt(), 255 - blue.toInt(), alpha.toInt())

fun Palette.lowKey(): Palette {
    val half = this.size / 2
    return this.subList(0, half)
}

fun Palette.highKey(): Palette {
    val half = this.size / 2
    return this.subList(half, this.size)
}

fun Palette.midKey(): Palette {
    val half = this.size / 2
    val start = (half / 2f).roundToInt()
    return this.subList(start, start + half)
}

fun Palette.findKey(color: Color): IndexedValue<Color>? {
    return withIndex().minByOrNull { abs(it.value.value.toLong() - color.value.toLong()) }
}
