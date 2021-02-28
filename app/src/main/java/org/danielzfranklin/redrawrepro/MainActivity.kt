package org.danielzfranklin.redrawrepro

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.IntOffset
import org.danielzfranklin.redrawrepro.ui.theme.RedrawReproTheme
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RedrawReproTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main() {
    Column(Modifier.fillMaxSize()) {
        val offset = remember { mutableStateOf(0) }
        LaunchedEffect(Unit) {
            animate(0f, 1000f, animationSpec = tween(2000)) { value, _ ->
                offset.value = value.roundToInt()
            }
        }

        val painter = remember { Painter(Color.Red) }

        DrawerWithContent(painter, offset.value)
        DrawerWithCache(painter, offset.value)
        DrawerWithTwoBoxes(painter, offset.value)
        DrawerWithCanvas(painter, offset.value)
    }
}

@Immutable
data class Painter(val background: Color) {
    fun paint(canvas: Canvas) {
        canvas.drawRect(Rect(Offset.Zero, Size(500f, 500f)), Paint().apply {
            color = background
        })
    }
}

/** Logs every time offset changes */
@Composable
fun DrawerWithContent(painter: Painter, offset: Int) {
    Box(Modifier
        .offset { IntOffset(offset, 0) }
        .drawWithContent {
            drawIntoCanvas {
                painter.paint(it)
                Log.d(TAG, "DrawerWithContent painting")
            }
        }
    )
}

/** Logs every time offset changes */
@Composable
fun DrawerWithCache(painter: Painter, offset: Int) {
    Box(Modifier
        .offset { IntOffset(offset, 0) }
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawIntoCanvas {
                    painter.paint(it)
                    Log.d(TAG, "DrawerWithCache painting")
                }
            }
        }
    )
}

/** Logs every time offset changes */
@Composable
fun DrawerWithTwoBoxes(painter: Painter, offset: Int) {
    Box(
        Modifier
            .offset { IntOffset(offset, 0) }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawIntoCanvas {
                        painter.paint(it)
                        Log.d(TAG, "DrawerWithTwoBoxes painting")
                    }
                }
        )
    }
}

/** Logs only once */
@Composable
fun DrawerWithCanvas(painter: Painter, offset: Int) {
    Box(
        Modifier
            .offset { IntOffset(offset, 0) }
    ) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            drawIntoCanvas {
                painter.paint(it)
                Log.d(TAG, "DrawerWithCanvas painting")
            }
        }
    }
}

private const val TAG = "redrawrepro"