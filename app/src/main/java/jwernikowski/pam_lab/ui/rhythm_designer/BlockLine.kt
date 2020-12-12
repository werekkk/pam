package jwernikowski.pam_lab.ui.rhythm_designer

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.models.RhythmLineDto
import jwernikowski.pam_lab.utils.getChildMaxEnd

class BlockLine(context: Context, private val meter: Meter, private val rhythmLine: RhythmLineDto, private val blockWidth: Int, private val tag: String) : RelativeLayout(context) {

    companion object {
        const val BLOCK_GAP: Int = 10
        val BLOCK_HEIGHT: Int = RhythmDesigner.BLOCK_HEIGHT
        val ICON_WIDTH = BLOCK_HEIGHT
        val ICON_GAP = BLOCK_GAP * 2
    }

    private var blocks: Array<View> = arrayOf()

    init {
        updateViews()
    }

    private fun updateViews() {
        removeAllViews()
        addIcon()
        createBlocks()
        minimumWidth = getChildMaxEnd()
    }

    private fun addIcon() {
        val icon = ImageView(context)
        icon.setImageResource(rhythmLine.sound.getDrawable())
        icon.layoutParams = LayoutParams(ICON_WIDTH, ICON_WIDTH)
        icon.x = 0f
        addView(icon)
    }

    private fun createBlocks() {
        blocks = Array(rhythmLine.beats.size) { i ->
            let {
                val newBlock = Block(context, rhythmLine.beats, i, "${tag}_$i")
                newBlock.layoutParams = LayoutParams(blockWidth, BLOCK_HEIGHT)
                newBlock.x = i * (blockWidth + BLOCK_GAP).toFloat() + ICON_WIDTH + ICON_GAP
                newBlock
            }
        }
        blocks.forEach { block -> this.addView(block) }
    }
}