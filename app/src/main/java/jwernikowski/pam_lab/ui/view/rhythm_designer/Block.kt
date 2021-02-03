package jwernikowski.pam_lab.ui.view.rhythm_designer

import android.content.Context
import android.graphics.Color
import android.view.View

class Block(context: Context, private var targetArray: Array<Boolean>, private var targetIndex: Int, tag: String) : View(context) {

    private var isActive = false
        set(value) {
            field = value
            updateColor()
            targetArray[targetIndex] = value
        }

    init {
        this.tag = tag
        isActive = targetArray[targetIndex]
        updateColor()
        setOnClickListener {run {isActive = !isActive}}
    }

    private fun updateColor() {
        when(isActive) {
            true -> setBackgroundColor(Color.BLACK)
            false -> setBackgroundColor(Color.LTGRAY)
        }
        invalidate()
    }
}