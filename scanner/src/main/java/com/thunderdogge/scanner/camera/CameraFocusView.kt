package com.thunderdogge.scanner.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Handler
import android.util.AttributeSet
import android.view.View

/**
 * Camera focus rect with variable size and format
 */
class CameraFocusView : View {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val displayDensity: Float

    private val rectPath = Path()

    private val rectEdgeLength: Float

    private val rectEdgeThickness: Float

    private val rectErrorPaint: Paint

    private val rectSuccessPaint: Paint

    private val rectDefaultPaint: Paint

    private val toggleHandler = Handler()

    private val reusableSize = Rectangle()

    init {
        val density = resources.displayMetrics.density
        displayDensity = density
        rectEdgeLength = 70 * density
        rectEdgeThickness = 3 * density
        rectErrorPaint = createRectPaint(rectEdgeThickness, 192, 0xFF, 0x00, 0x00)
        rectSuccessPaint = createRectPaint(rectEdgeThickness, 192, 0x57, 0xAC, 0x68)
        rectDefaultPaint = createRectPaint(rectEdgeThickness, 128, 0xFF, 0xFF, 0xFF)
    }

    val size = Rectangle()

    var zoom = 0.7f
        set(value) {
            if (field != value) {
                if (value < 0f || value > 1f) {
                    throw IllegalArgumentException("Zoom must be in range 0..1")
                }

                field = value
                invalidate()
            }
        }

    var format = Format.Square
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var offsetX = 0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var offsetY = 0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private var isErrorState = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private var isSuccessState = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    fun toggle(flag: Boolean) {
        if (flag) {
            toggleSuccess()
        } else {
            toggleError()
        }
    }

    fun toggleError() {
        isErrorState = true
        toggleHandler.postDelayed({ isErrorState = false }, 500)
    }

    fun toggleSuccess() {
        isSuccessState = true
        toggleHandler.postDelayed({ isSuccessState = false }, 500)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Rect dimensions
        val rectSize = patchRectSize(reusableSize, width, height, zoom, format)
        val rectWidth = rectSize.width.toFloat()
        val rectHeight = rectSize.height.toFloat()
        val rectMarginX = rectSize.x + offsetX * displayDensity
        val rectMarginY = rectSize.y + offsetY * displayDensity

        // Store size
        size.x = rectMarginX.toInt()
        size.y = rectMarginY.toInt()
        size.width = rectSize.width
        size.height = rectSize.height

        // Reset path
        rectPath.reset()

        // Create top left corner
        rectPath.moveTo(rectMarginX + rectEdgeLength, rectMarginY)
        rectPath.lineTo(rectMarginX, rectMarginY)
        rectPath.lineTo(rectMarginX, rectMarginY + rectEdgeLength)

        // Create top right corner
        rectPath.moveTo(rectMarginX + rectWidth - rectEdgeLength, rectMarginY)
        rectPath.lineTo(rectMarginX + rectWidth, rectMarginY)
        rectPath.lineTo(rectMarginX + rectWidth, rectMarginY + rectEdgeLength)

        // Create bottom right corner
        rectPath.moveTo(rectMarginX + rectWidth - rectEdgeLength, rectMarginY + rectHeight)
        rectPath.lineTo(rectMarginX + rectWidth, rectMarginY + rectHeight)
        rectPath.lineTo(rectMarginX + rectWidth, rectMarginY + rectHeight - rectEdgeLength)

        // Create bottom left corner
        rectPath.moveTo(rectMarginX + rectEdgeLength, rectMarginY + rectHeight)
        rectPath.lineTo(rectMarginX, rectMarginY + rectHeight)
        rectPath.lineTo(rectMarginX, rectMarginY + rectHeight - rectEdgeLength)

        // Draw path
        val rectPaint = when {
            isSuccessState -> rectSuccessPaint
            isErrorState -> rectErrorPaint
            else -> rectDefaultPaint
        }

        canvas?.drawPath(rectPath, rectPaint)
    }

    private fun createRectPaint(strokeWidth: Float, alpha: Int, red: Int, green: Int, blue: Int): Paint {
        return Paint().also {
            it.color = Color.argb(alpha, red, green, blue)
            it.style = Paint.Style.STROKE
            it.isAntiAlias = false
            it.strokeWidth = strokeWidth
        }
    }

    private fun patchRectSize(source: Rectangle, originalWidth: Int, originalHeight: Int, zoom: Float, format: Format): Rectangle {
        // Zoomed out size
        val zoomedWidth = (originalWidth * zoom).toInt()
        val zoomedHeight = (originalHeight * zoom).toInt()

        // Target frame size
        var targetWidth = zoomedWidth
        var targetHeight = (zoomedWidth * format.ratioY / format.ratioX).toInt()
        if (targetHeight > zoomedHeight) {
            // Recalculate target width, if result height is bigger than zoomed
            targetHeight = zoomedHeight
            targetWidth = (zoomedHeight * (format.ratioX / format.ratioY)).toInt()
        }

        // Target frame indents
        val targetIndentX = (originalWidth - targetWidth) / 2
        val targetIndentY = (originalHeight - targetHeight) / 2

        source.x = targetIndentX
        source.y = targetIndentY
        source.width = targetWidth
        source.height = targetHeight

        return source
    }

    data class Rectangle(
        var x: Int = 0,
        var y: Int = 0,
        var width: Int = 0,
        var height: Int = 0
    )

    enum class Format(val ratioX: Float, val ratioY: Float) {
        A4(8.27f, 11.7f),
        Square(1f, 1f)
    }
}