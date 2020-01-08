package jwernikowski.pam_lab.ui.metronome

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import jwernikowski.pam_lab.R

class TouchHelpAnimation(context: Context, val viewHeight: Int, val viewWidth: Int) {

    private val startTime = System.currentTimeMillis()

    private val picture: Drawable = context.resources.getDrawable(R.drawable.ic_touch_black, null)

    private var x: Int = (viewWidth * 0.75).toInt()
    private var y: Int = viewHeight / 2
    private var size: Int = ICON_SIZE

    var onAnimationFinished: (() -> Unit)? = null

    init {
    }

    companion object {
        val APPEAR_DURATION = 500
        val MOVE_DURATION = 400
        val ZOOM_IN_1_DURATION = 200
        val ZOOM_OUT_DURATION = 250
        val ZOOM_IN_2_DURATION = 150
        val DISAPPEAR_DURATION = 200

        val APPEAR_END = APPEAR_DURATION
        val MOVE_END = APPEAR_END + MOVE_DURATION
        val ZOOM_IN_1_END = MOVE_END + ZOOM_IN_1_DURATION
        val ZOOM_OUT_END = ZOOM_IN_1_END + ZOOM_OUT_DURATION
        val ZOOM_IN_2_END = ZOOM_OUT_END + ZOOM_IN_2_DURATION
        val DISAPPEAR_END = ZOOM_IN_2_END + DISAPPEAR_DURATION

        val ICON_SIZE = 128
        val ZOOM_IN_SIZE = 64
        val ZOOM_OUT_SIZE = 160
    }

    fun draw(canvas: Canvas?) {
        val diff = System.currentTimeMillis() - startTime
        if (diff < APPEAR_END)
            drawAppear(canvas, diff)
        else if (diff < MOVE_END)
            drawMove(canvas, diff - APPEAR_END)
        else if (diff < ZOOM_IN_1_END)
            drawZoomIn1(canvas, diff - MOVE_END)
        else if (diff < ZOOM_OUT_END)
            drawZoomOut(canvas, diff - ZOOM_IN_1_END)
        else if (diff < ZOOM_IN_2_END)
            drawZoomIn2(canvas, diff - ZOOM_OUT_END)
        else if (diff < DISAPPEAR_END)
            drawDisappear(canvas, diff - ZOOM_IN_2_END)
        else if (onAnimationFinished != null)
            onAnimationFinished!!()
    }

    private fun drawAppear(canvas: Canvas?, time: Long) {
        picture.alpha = ((time.toFloat() / APPEAR_DURATION) * 255).toInt()
        setBounds(picture)
        picture.draw(canvas!!)
    }

    private fun drawMove(canvas: Canvas?, time: Long) {
        x = (viewWidth * 0.25 * ((MOVE_DURATION - time.toFloat()) / MOVE_DURATION) + viewWidth * 0.5).toInt()
        setBounds(picture)
        picture.draw(canvas!!)
    }

    private fun drawZoomIn1(canvas: Canvas?, time: Long) {
        size = (ZOOM_IN_SIZE + ((ICON_SIZE - ZOOM_IN_SIZE) * ((ZOOM_IN_1_DURATION - time).toFloat() / ZOOM_IN_1_DURATION))).toInt()
        setBounds(picture)
        picture.draw(canvas!!)
    }

    private fun drawZoomOut(canvas: Canvas?, time: Long) {
        size = (ZOOM_IN_SIZE + (ZOOM_OUT_SIZE - ZOOM_IN_SIZE) * (time.toFloat() / ZOOM_OUT_DURATION)).toInt()
        setBounds(picture)
        picture.draw(canvas!!)
    }

    private fun drawZoomIn2(canvas: Canvas?, time: Long) {
        size = (ICON_SIZE + (ZOOM_OUT_SIZE - ICON_SIZE) * ((ZOOM_IN_2_DURATION - time).toFloat() / ZOOM_IN_2_DURATION)).toInt()
        setBounds(picture)
        picture.draw(canvas!!)
    }

    private fun drawDisappear(canvas: Canvas?, time: Long) {
        picture.alpha = 255 - ((time.toFloat() / DISAPPEAR_DURATION) * 255).toInt()
        setBounds(picture)
        picture.draw(canvas!!)
    }

    private fun setBounds(pic: Drawable) {
        val halfSize = size / 2
        pic.setBounds(x - halfSize, y - halfSize, x + halfSize, y + halfSize)
    }
}