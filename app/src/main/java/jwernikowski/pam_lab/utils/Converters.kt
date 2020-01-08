package jwernikowski.pam_lab.utils

import android.icu.util.Measure
import androidx.room.TypeConverter
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.sound.Sound
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


object Converters {
    @JvmStatic
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }


    @JvmStatic
    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @JvmStatic
    @TypeConverter
    fun fromNumeric(value: Int?): PracticeEntry.Rating? {
        when (value) {
            1 -> return PracticeEntry.Rating.VERY_LOW
            2 -> return PracticeEntry.Rating.LOW
            3 -> return PracticeEntry.Rating.MEDIUM
            4 -> return PracticeEntry.Rating.HIGH
            5 -> return PracticeEntry.Rating.VERY_HIGH
        }
        return null
    }

    @JvmStatic
    @TypeConverter
    fun ratingToNumeric(rating: PracticeEntry.Rating?): Int? {
        when (rating) {
            PracticeEntry.Rating.VERY_LOW -> return 1
            PracticeEntry.Rating.LOW -> return 2
            PracticeEntry.Rating.MEDIUM -> return 3
            PracticeEntry.Rating.HIGH -> return 4
            PracticeEntry.Rating.VERY_HIGH -> return 5
        }
        return 0
    }

    @JvmStatic
    @TypeConverter
    fun toMeterString(meter: Meter): String {
        return meter.measure.toString() + "/" + meter.length.toString()
    }

    @JvmStatic
    @TypeConverter
    fun toMeter(meterStr: String): Meter {
        val parts = meterStr.split("/")
        return Meter(parts[0].toInt(), parts[1].toInt())
    }

    @JvmStatic
    @TypeConverter
    fun toSoundInt(sound: Sound): Int {
        return when(sound) {
            Sound.WOOD -> 1
            Sound.TRIANGLE -> 2
        }
        return 0
    }

    @JvmStatic
    @TypeConverter
    fun toSound(soundNumber: Int): Sound? {
        when(soundNumber) {
            1 -> return Sound.WOOD
            2 -> return Sound.TRIANGLE
        }
        return null
    }

    @JvmStatic
    @TypeConverter
    // [[false, true], [true, false]] -> "01,10"
    fun toTwoDimBoolArrayString(arrays: Array<Array<Boolean>>): String {
        val strings = arrays.map { array -> toBoolArrayString(array) }
        return strings.joinToString(separator = ",")
    }

    @JvmStatic
    @TypeConverter
    // "01,10" -> [[false, true], [true, false]]
    fun toTwoDimBoolArray(arrayStr: String): Array<Array<Boolean>> {
        val lines = arrayStr.split(',')
        return (lines.map { str -> toBoolArray(str) }).toTypedArray()
    }

    @JvmStatic
    @TypeConverter
    // [false, true, false] -> "010"
    fun toBoolArrayString(array: Array<Boolean>): String {
        val bld = StringBuilder()
        array.forEach { value -> if (value) bld.append('1') else bld.append('0') }
        return bld.toString()
    }

    @JvmStatic
    @TypeConverter
    //  "010" -> [false, true, false]
    fun toBoolArray(arrayStr: String): Array<Boolean> {
        return Array(arrayStr.length) {i -> arrayStr[i] == '1' }
    }
}