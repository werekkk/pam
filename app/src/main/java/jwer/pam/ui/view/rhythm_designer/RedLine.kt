package jwer.pam.ui.view.rhythm_designer

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import jwer.pam.R
import jwer.pam.db.data.Meter
import java.util.*

class  RedLine(context: Context, private var startX: Float, private var finishX: Float, meter: Meter, bpm: Int): ImageView(context) {

    companion object {
        const val REFRESH_INTERVAL: Long = 25L
    }

    private var timer = Timer()
    private var lastRefresh = System.currentTimeMillis()
    private var velocity = 0.2f
    private var blockWidth: Float = (finishX - startX) / meter.blockCount

    var onTick: (blockIndex: Int) -> Unit = {}

    var meter: Meter = meter
    set(value) {
        field = value
        blockWidth = (finishX - startX) / meter.blockCount
        calculateVelocity()
    }

    var bpm: Int = bpm
    set(value) {
        field = value
        calculateVelocity()
    }

    init {
        setImageResource(R.drawable.line)
        layoutParams = RelativeLayout.LayoutParams(10, 400)
        x = startX
        calculateVelocity()
    }

    private fun calculateVelocity() {
        velocity = (bpm * ((finishX - startX) / meter.measure)) / 60000f
    }

    fun play() {
        lastRefresh = System.currentTimeMillis()
        timer = Timer()
        if (x == startX) {
            onTick(0)
        }
        timer.schedule(object: TimerTask() {
            override fun run() {
                updateLinePosition()
            }
        }, 0,
            REFRESH_INTERVAL
        )
    }

    private fun updateLinePosition() {
        val previousX = x
        move()
        checkIfOutOfBounds()
        checkOnTick(previousX, x)
    }

    private fun move() {
        val diff = System.currentTimeMillis() - lastRefresh
        x += diff * velocity
        lastRefresh += diff
    }

    private fun checkOnTick(previousX: Float, currentX: Float) {
        if (currentX < previousX) {
            onTick(0)
            return
        }
        val prevBlock = getBlockIndex(previousX)
        val currBlock = getBlockIndex(currentX)
        if (prevBlock != currBlock) {
            onTick(currBlock)
        }
    }

    private fun getBlockIndex(x: Float): Int {
        val relX = x - startX
        return (relX / blockWidth).toInt() % meter.blockCount
    }

    private fun checkIfOutOfBounds() {
        if (x > finishX)
            x -= finishX - startX
    }

    fun pause() {
        timer.cancel()
    }

    fun reset() {
        x = startX
    }
}