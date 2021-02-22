package jwer.pam.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import java.lang.Exception
import kotlin.math.max

fun View.setMargins(left: Int, right: Int, top: Int, bottom: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = left
        params.rightMargin = right
        params.topMargin = top
        params.bottomMargin = bottom
        requestLayout()
    } else {
        throw Exception("Cannot set margins to a View whose layoutParams are not an instance of ViewGroup.MarginLayoutParams!")
    }
}

fun ViewGroup.getChildMaxEnd(): Int {
    return getChildMax { view -> (view.x + view.layoutParams.width).toInt() }
}


private fun ViewGroup.getChildMax(property: (View) -> Int): Int {
    var max = 0
    this.children.forEach { child -> run {max = max(max, property(child)) } }
    return max
}
