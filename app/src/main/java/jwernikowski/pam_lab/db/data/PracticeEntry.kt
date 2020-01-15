package jwernikowski.pam_lab.db.data

import android.util.Log
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import lecho.lib.hellocharts.model.PointValue
import java.lang.Integer.max
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

@Entity(foreignKeys = [ForeignKey(entity = Song::class,
    parentColumns = ["songId"],
    childColumns = ["songId"],
    onDelete = ForeignKey.CASCADE)]
)
data class PracticeEntry(
    val songId: Long,
    val date: LocalDateTime,
    val rating: Rating,
    val tempo: Int
) {
    @PrimaryKey(autoGenerate = true) var practiceEntryId = 0L

    companion object {

        fun calculateProgress(orderedEntries: List<PracticeEntry>, initial: Int, goal: Int): Float {
            val entriesByDay : List<List<PracticeEntry>> = getEntriesByDay(orderedEntries)
            val progress : List<Float> = getProgressFromDays(entriesByDay, initial, goal)
            if (progress.isEmpty())
                return 0f
            return progress.last()
        }

        fun calculateDaysPracticed(orderedEntries: List<PracticeEntry>): Int {
            var count = 0
            if (orderedEntries.isNotEmpty()) {
                count++
                val fmt = DateTimeFormatter.ofPattern("yyyyMMdd")
                var last = orderedEntries[0].date.format(fmt)
                var current: String
                orderedEntries.forEach { entry -> run{
                    current = entry.date.format(fmt)
                    if (current.compareTo(last) != 0)
                        count++
                    last = current
                }}
            }
            return count
        }

        fun getPointValues(orderedEntries: List<PracticeEntry>, initial: Int, goal: Int): List<PointValue> {
            val entriesByDay : List<List<PracticeEntry>> = getEntriesByDay(orderedEntries)
            val progress : List<Float> = getProgressFromDays(entriesByDay, initial, goal)

            val points = ArrayList<PointValue>()
            if (entriesByDay.isNotEmpty()) {
                var firstDay = entriesByDay[0][0].date
                firstDay = firstDay
                    .minusHours(firstDay.hour.toLong())
                    .minusMinutes(firstDay.minute.toLong())
                    .minusSeconds(firstDay.second.toLong())
                val days = entriesByDay.map {
                        day -> ChronoUnit.DAYS.between(firstDay, day[0].date).toFloat()
                }
                for (i in progress.indices) {
                    points.add(PointValue(days[i], progress[i]))
                }
            }
            return points
        }

        private fun getEntriesByDay(orderedEntries: List<PracticeEntry>): List<List<PracticeEntry>> {
            val days = ArrayList<List<PracticeEntry>>()
            if (orderedEntries.isNotEmpty()) {
                var currentDay = ArrayList<PracticeEntry>()
                var previousDay = orderedEntries[0].date
                orderedEntries.forEach { entry -> run{
                    if (ChronoUnit.DAYS.between(entry.date.toLocalDate(), previousDay.toLocalDate()) != 0L) {
                        days.add(currentDay)
                        currentDay = ArrayList()
                    }
                    currentDay.add(entry)
                    previousDay = entry.date
                }}
                days.add(currentDay)
            }
            return days
        }

        private fun getProgressFromDays(days: List<List<PracticeEntry>>, initial: Int, goal: Int): List<Float> {
            return days.map { entries -> calculateProgress(initial, goal, getBestBpmFromDay(entries))}
        }

        private fun calculateProgress(initialBpm: Int, goalBpm: Int, tempo: Int): Float {
            var progress = (tempo - initialBpm).toFloat() / (goalBpm - initialBpm).toFloat()
            if (progress < 0f)
                progress = 0f
            if (progress > 1f)
                progress = 1f
            return progress
        }

        private fun getBestBpmFromDay(entries: List<PracticeEntry>): Int {
            return entries.foldRight(0, {entry, max -> max(max, entry.rating.estimateTempo(entry.tempo))})
        }

    }

    enum class Rating {
        VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH;

        fun estimateTempo(bpm: Int): Int {
            return when(this) {
                VERY_LOW -> (bpm * 0.2).toInt()
                LOW -> (bpm * 0.4).toInt()
                MEDIUM -> (bpm * 0.6).toInt()
                HIGH -> (bpm * 0.8).toInt()
                VERY_HIGH -> bpm
            }
        }
    }


}

