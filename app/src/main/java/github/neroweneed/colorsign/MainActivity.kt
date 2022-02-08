package github.neroweneed.colorsign

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import github.neroweneed.colorsign.palette.*
import github.neroweneed.colorsign.ui.theme.ColorSignTheme
import kotlin.random.Random
import kotlin.random.nextUInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ColorSignTheme {
                var baseColorValue by remember {
                    mutableStateOf(0u)
                }
                val baseColorCurrent by animateValueAsState(baseColorValue,TwoWayConverter<UInt,AnimationVector1D>({ AnimationVector1D(it.toFloat()) }, { it.value.toUInt() }),
                    spring(visibilityThreshold = 1u),null
                )
                val baseColor by remember {
                    derivedStateOf {
                        Color.create(baseColorCurrent)
                    }
                }
                val key by remember {
                    derivedStateOf {
                        baseColor.monochromatic(10)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            baseColorValue = 0xFF000000u or Random.nextUInt()
                        }) {
                            Icon(Icons.Rounded.Refresh,"New Color")
                        }
                    }
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Column(Modifier.fillMaxSize(),Arrangement.SpaceEvenly,Alignment.CenterHorizontally) {
                            ColorPaletteRow(
                                Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(4.dp)),
                                key.lowKey()
                            )
                            ColorPaletteRow(
                                Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(4.dp)),
                                key.midKey()
                            )
                            ColorPaletteRow(
                                Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(4.dp)),
                                key.highKey()
                            )
                        }

                    }
                }

            }
        }
    }
}
@Composable
fun ColorPaletteColumn(modifier: Modifier = Modifier, colors: List<Color>) {
    Column(
        modifier
    ) {
        colors.forEach { color ->
            ColorPaletteBox(
                Modifier
                    .weight(1f).aspectRatio(1f,true), color
            )
        }
    }
}

@Composable
fun ColorPaletteRow(modifier: Modifier = Modifier, colors: List<Color>) {
    Row(
        modifier
    ) {
        colors.forEach { color ->
            ColorPaletteBox(
                Modifier
                    .weight(1f).aspectRatio(1f), color
            )
        }
    }
}

@Composable
fun ColorPaletteBox(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier.background(
            color.toCompose()
        )
    ) {
        val fg = color.foregroundColor()
        val ifg = fg.invert().adjust(alpha = 0.1f)
        Text(
            color.toString(false),
            Modifier.align(Alignment.BottomEnd).background(
                ifg.toCompose(), RoundedCornerShape(4.dp,0.dp,0.dp,0.dp)).padding(end = 4.dp,bottom = 2.dp,start = 4.dp,top = 2.dp),
            color = fg.toCompose(),
            style = MaterialTheme.typography.caption
        )
    }
}



@Composable
fun Greeting(name: String) {
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ColorSignTheme {

    }
}