package jwer.pam.ui.view.metronome

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import jwer.pam.R
import jwer.pam.db.data.Meter
import jwer.pam.db.data.entity.Rhythm
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MetronomeView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint: Paint = Paint()

    private var width: Float = 0f
    private var height: Float = 0f

    private val widthPaddingRatio: Float = 0.7f
    private val heightPaddingRatio: Float = 0.1f
    private val boxLineRatio: Float = 0.2f

    private var rodAlignment: Double = 0.5 // 0.0 max left, 1.0 max right, 0.5 middle
    private var maxAmplitude: Double = 0.25

    private val minAlignment: Double
        get() = 0.5 - maxAmplitude
    private val maxAlignment: Double
        get() = 0.5 + maxAmplitude

    var bpm: Int = 140
    private var lastTime: Long = 0

    private var phi: Double = 0.0
    private var fps: Long = 60
    private var animationDelay = 1000 / fps

    var rhythm: Rhythm = Rhythm.DEFAULT_RHYTHM
    private var bar: Int = 0
    set(value) {
        field = (value % rhythm.meter.measure)
    }

    lateinit var onMetronomeTickListener: (blockIndex: Int) -> Unit

    private var drawingStarted: Boolean = false
    private var isRunning: Boolean = false
    private lateinit var canvas: Canvas

    private lateinit var box: MetronomeBox

    private lateinit var touchHelpAnimation: TouchHelpAnimation

    private var touched = false
    private var isTouchHelpAnimationActive = false

    private var createdTime = System.currentTimeMillis()

    private lateinit var timer: Timer

    companion object {
        val TIME_UNTIL_ANIMATION = 3000
    }

    init {
        paint.color = context.getColor(R.color.metronome_gray)
        paint.strokeWidth = 5f
        startRefreshing()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        width = w + 0f
        height = h + 0f

        box = MetronomeBox(widthPaddingRatio, heightPaddingRatio, width, height, boxLineRatio)
        touchHelpAnimation =
            TouchHelpAnimation(
                context,
                h,
                w
            )
    }

    private fun startRefreshing() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                drawMetronome()
            }
        }, 0, animationDelay)
    }

    fun shutdown() {
        timer.cancel()
    }

    private fun startDrawing() {
        drawingStarted = true
        drawMetronome()
    }

    private fun drawMetronome() {
        if (isRunning) {
            val delta = (System.currentTimeMillis() - lastTime)
            lastTime += delta
            val lastPhi = phi
            phi += PI * (delta.toDouble() / (60000/bpm).toDouble())
            checkTick(lastPhi, phi)
            rodAlignment = sinToAlignment(sin(phi))
        }
        postInvalidateOnAnimation()
    }

    private fun checkTick(prevPhi: Double, currPhi: Double) {
        val prevProgress = getProgressFromPhi(prevPhi)
        val currProgress = getProgressFromPhi(currPhi)
        val prevIndex = blockIndexFromProgress(prevProgress, rhythm.meter)
        val currIndex = blockIndexFromProgress(currProgress, rhythm.meter)
        if (currProgress < prevProgress) {
            bar++
            tick(bar * rhythm.meter.length + currIndex)
            return
        } else if (currIndex != prevIndex) {
            tick(bar * rhythm.meter.length + currIndex)
        }

    }

    private fun blockIndexFromProgress(progress: Double, meter: Meter): Int {
        return (progress / (1 / meter.length.toDouble())).toInt()
    }

    private fun getProgressFromPhi(phi: Double): Double {
        return (phi % PI) / PI
    }

    private fun tick(blockIndex: Int) {
        onMetronomeTickListener(blockIndex)
    }

    private fun sinToAlignment(sin: Double): Double {
        var s = sin + 1
        s /= 2
        s *= (maxAlignment - minAlignment)
        s += minAlignment
        return s
    }

    fun turnOn() {
        lastTime = System.currentTimeMillis()
        touched = true
        isRunning = true
    }

    fun turnOff() {
        isRunning = false
    }

    override fun onDraw(canvas: Canvas?) {
        if (shouldDisplayHelpAnimation()) {
            touchHelpAnimation =
                TouchHelpAnimation(
                    context,
                    height.toInt(),
                    width.toInt()
                )
            touchHelpAnimation.onAnimationFinished = {
                isTouchHelpAnimationActive = false
                createdTime = System.currentTimeMillis()
            }
            isTouchHelpAnimationActive = true
        }
        super.onDraw(canvas)
        canvas?.let {
            this.canvas = it
        }
        drawMetronomeBox(canvas)
        drawRod(canvas)
        if (isTouchHelpAnimationActive)
            touchHelpAnimation.draw(canvas)
        if (!drawingStarted)
            startDrawing()
    }

    private fun shouldDisplayHelpAnimation(): Boolean {
        return !touched && !isTouchHelpAnimationActive && timeToDisplay()
    }

    private fun timeToDisplay(): Boolean {
        return System.currentTimeMillis() - createdTime > TIME_UNTIL_ANIMATION
    }

    private fun drawMetronomeBox(canvas: Canvas?) {
        drawLine(canvas, box.getA(), box.getB())
        drawLine(canvas, box.getB(), box.getC())
        drawLine(canvas, box.getA(), box.getC())
        drawLine(canvas, box.getD(), box.getE())
    }

    private fun drawRod(canvas: Canvas?) {
        val f = box.getF()
        val rodLength = getRodLength()
        cos(rodAlignment * PI)
        val rodEnd = PointF(f.x - rodLength * cos(rodAlignment * PI).toFloat(),
            f.y - rodLength * sin(rodAlignment * PI).toFloat() )
        drawLine(canvas, f, rodEnd)
    }

    private fun drawLine(canvas: Canvas?, a: PointF, b: PointF) {
        canvas?.drawLine(a.x, a.y, b.x, b.y, paint)
    }

    private fun getRodLength(): Float {
        return box.getF().y - (height * heightPaddingRatio / 2)
    }

    inner class MetronomeBox(
        val widthPaddingRatio: Float,
        val heightPaddingRatio: Float,
        val width: Float,
        val height: Float,
        val boxLineRatio: Float
    ) {

        /*
         B


      D  F  E

    A         C
         */

        fun getA(): PointF {
            return PointF(width * widthPaddingRatio / 2,height - (height * heightPaddingRatio / 2))
        }

        fun getB(): PointF {
            return PointF(width / 2, height * heightPaddingRatio / 2)
        }

        fun getC(): PointF {
            return PointF(width - (width * widthPaddingRatio / 2), height - (height * heightPaddingRatio / 2))
        }

        fun getD(): PointF {
            val a = getA()
            val b = getB()
            return PointF(a.x + (b.x - a.x) * boxLineRatio, a.y + (b.y - a.y) * boxLineRatio)
        }

        fun getE(): PointF {
            val c = getC()
            val b = getB()
            return PointF(c.x + (b.x - c.x) * boxLineRatio, c.y + (b.y - c.y) * boxLineRatio)
        }

        fun getF(): PointF {
            val d = getD()
            return PointF(width / 2, d.y)
        }
    }
}