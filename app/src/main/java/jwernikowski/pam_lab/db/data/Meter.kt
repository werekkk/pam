package jwernikowski.pam_lab.db.data

data class Meter(var measure: Int, var length: Int) {
    companion object {

        const val MIN_MEASURE = 1
        const val MIN_LENGTH = 1
        const val MAX_MEASURE = 8
        const val MAX_LENGTH = 9

        fun default(): Meter {
            return Meter(4, 2)
        }
    }

    init {
        truncate()
    }

    val blockCount: Int
        get() = measure * length

    override fun toString(): String {
        return "$measure/$length"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        val x = other as Meter
        return x.length == this.length && x.measure == this.measure
    }

    fun truncate() {
        length = length.coerceIn(MIN_LENGTH, MAX_LENGTH)
        measure = measure.coerceIn(MIN_MEASURE, MAX_MEASURE)
    }
}