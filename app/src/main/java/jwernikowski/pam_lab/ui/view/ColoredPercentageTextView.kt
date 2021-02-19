package jwernikowski.pam_lab.ui.view

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet


class ColoredPercentageTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : androidx.appcompat.widget.AppCompatTextView(context, attributeSet, defStyleAttr) {

    private val evaluator = ArgbEvaluator()

    fun setPercentage(percentage: Int) {
        text = "$percentage%"
        updateColor(percentage)
    }

    private fun updateColor(percentage: Int) {
        setTextColor((evaluator.evaluate((percentage.toFloat() / 100), Color.rgb(200,0,0), Color.rgb(0,200,0)) as Int))
    }

}