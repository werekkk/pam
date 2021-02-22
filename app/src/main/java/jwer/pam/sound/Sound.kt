package jwer.pam.sound

import jwer.pam.R

enum class Sound {
    WOOD, TRIANGLE;

    fun all(): ArrayList<Sound> {
        val all = ArrayList<Sound>()
        all.add(WOOD)
        all.add(TRIANGLE)
        return all
    }

    fun getDrawable(): Int {
        when(this) {
            WOOD -> return R.drawable.wood
            TRIANGLE -> return R.drawable.triangle
        }
    }
}
