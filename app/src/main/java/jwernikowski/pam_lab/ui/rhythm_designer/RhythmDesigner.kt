package jwernikowski.pam_lab.ui.rhythm_designer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.models.RhythmLineDto
import jwernikowski.pam_lab.sound.SoundPlayer
import jwernikowski.pam_lab.utils.getChildMaxEnd
import jwernikowski.pam_lab.utils.setMargins
import kotlin.collections.ArrayList
import kotlin.math.max

class RhythmDesigner(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    companion object {
        const val BLOCK_HEIGHT: Int = 120
        const val BLOCK_MIN_WIDTH: Int = 50
        const val MARGIN: Int = 25
    }

    var player: SoundPlayer? = null
    private var meter: Meter = Meter.default()

    var rhythmLines: List<RhythmLineDto> = RhythmLineDto.default(meter)
    set(value) {
        field = value
        createViews()
    }

    private var blockLines: ArrayList<BlockLine> = arrayListOf()

    private var blockWidth = BLOCK_MIN_WIDTH

    private var redLine: RedLine? = null

    var bpm: Int = 140
    set(value) {
        field = value
        redLine?.bpm = bpm
    }

    init {
        orientation = VERTICAL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createViews()
        post { requestLayout() }
    }

    fun setMeter(meter: Meter) {
        if (meter == this.meter)
            return
        this.meter = meter
        rhythmLines = RhythmLineDto.default(meter)
        redLine?.meter = meter
        createViews()
    }

    fun play(soundPlayer: SoundPlayer) {
        player = soundPlayer
        removeView(redLine)
        addView(redLine)
        redLine?.play()
    }

    fun pause() {
        player = null
        redLine?.pause()
    }

    fun reset() {
        player = null
        removeView(redLine)
        redLine?.reset()
    }

    fun isRhythmEmpty(): Boolean {
        var x = false
        rhythmLines.forEach {line -> run{
            line.beats.forEach { value -> run {x = x || value} }
        }}
        return !x
    }

    private fun createViews() {
        blockWidth = calculateBlockWidth()
        removeAllViews()
        initRedLine()
        addView(NumberLine(context, meter, blockWidth))
        blockLines = arrayListOf()
        rhythmLines.forEach { line -> blockLines.add(BlockLine(context, meter, line, blockWidth, line.sound.toString())) }
        blockLines.forEach { line -> run{
            line.layoutParams = RelativeLayout.LayoutParams(line.getChildMaxEnd(), BLOCK_HEIGHT)
            line.setMargins(MARGIN, MARGIN, MARGIN, 0)
            line.x = 0f
            line.y = 0f
            addView(line)
        } }
        minimumWidth = getChildMaxEnd()
    }

    private fun initRedLine() {
        val redLineStart = BlockLine.ICON_GAP + BlockLine.ICON_WIDTH + MARGIN + 0f
        val redLineFinish =  redLineStart + MARGIN + meter.blockCount*(BlockLine.BLOCK_GAP + blockWidth) - BlockLine.BLOCK_GAP
        redLine = RedLine(context, redLineStart, redLineFinish, meter, bpm)
        redLine!!.onTick = { blockIndex: Int ->  run{
            player?.playRhythmLines(rhythmLines, blockIndex)
        }}
    }

    private fun calculateBlockWidth(): Int {
        val parentWidth = (parent as View).width
        val availableWidth = (parentWidth
                - 2*MARGIN
                - (meter.blockCount - 1)*BlockLine.BLOCK_GAP
                - BlockLine.ICON_GAP
                - BlockLine.ICON_WIDTH) / meter.blockCount
        return max(BLOCK_MIN_WIDTH, availableWidth)
    }

}

