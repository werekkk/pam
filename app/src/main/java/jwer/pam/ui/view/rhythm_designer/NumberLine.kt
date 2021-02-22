package jwer.pam.ui.view.rhythm_designer

import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import jwer.pam.db.data.Meter

class NumberLine(context: Context, meter: Meter, blockWidth: Int): RelativeLayout(context) {

    companion object {
        const val TEXT_SIZE = 20f
    }

    init {
        for (i in 1..meter.measure) {
            val numberView = TextView(context)
            numberView.text = i.toString()
            numberView.textSize =
                TEXT_SIZE
            numberView.x = (BlockLine.ICON_GAP
                    + BlockLine.ICON_WIDTH
                    + (i - 1) * meter.length * (BlockLine.BLOCK_GAP + blockWidth)
                    + blockWidth / 2
                    + BlockLine.BLOCK_GAP).toFloat()
            addView(numberView)
        }
    }

}