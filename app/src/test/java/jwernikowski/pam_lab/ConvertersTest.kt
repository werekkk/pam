package jwernikowski.pam_lab

import com.google.common.truth.Truth.assertThat
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.db.data.Converters
import org.junit.Test

class ConvertersTest {
    @Test
    fun toMeterString_isCorrect() {
        assertThat(Converters.toMeter("1/2")).isEqualTo(Meter(1, 2))
        assertThat(Converters.toMeter("5/3")).isEqualTo(Meter(5, 3))
        assertThat(Converters.toMeter("8/8")).isEqualTo(Meter(8, 8))
    }

    @Test
    fun toMeter_isCorrect() {
        val m1 = Meter(1, 6)
        val m2 = Meter(3, 3)
        assertThat(Converters.toMeterString(m1)).isEqualTo(m1.toString())
        assertThat(Converters.toMeterString(m2)).isEqualTo(m2.toString())
    }

    @Test
    fun toBoolArrayString_isCorrect() {
        val a1 = Array(0) {true}
        val a2 = arrayOf(false)
        val a3 = arrayOf(true, true, false, true, false, false)
        assertThat(Converters.toBoolArrayString(a1)).isEqualTo("")
        assertThat(Converters.toBoolArrayString(a2)).isEqualTo("0")
        assertThat(Converters.toBoolArrayString(a3)).isEqualTo("110100")
    }

    @Test
    fun toBoolArray_isCorrect() {
        val s1 = ""
        val s2 = "0"
        val s3 = "110100"
        assertThat(Converters.toBoolArray(s1)).isEqualTo(Array(0) {true})
        assertThat(Converters.toBoolArray(s2)).isEqualTo(arrayOf(false))
        assertThat(Converters.toBoolArray(s3)).isEqualTo(arrayOf(true, true, false, true, false, false))
    }
}