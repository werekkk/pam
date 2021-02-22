package jwer.pam.db.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.lang.Integer.max
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.collections.ArrayList

@Entity(foreignKeys = [ForeignKey(entity = Section::class,
    parentColumns = ["sectionId"],
    childColumns = ["sectionId"],
    onDelete = ForeignKey.CASCADE)]
)
data class PracticeEntry(
    val sectionId: Long,
    val date: LocalDateTime,
    val rating: Rating,
    val tempo: Int
) {
    @PrimaryKey(autoGenerate = true) var practiceEntryId = 0L

    companion object {

        fun calculateProgress(orderedEntries: List<PracticeEntry>, initial: Int, goal: Int): Float {
            if (initial == goal) return 1f
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
            return days.map { entries ->
                calculateProgress(
                    initial,
                    goal,
                    getBestBpmFromDay(
                        entries,
                        initial,
                        goal
                    )
                )
            }
        }

        private fun calculateProgress(initialBpm: Int, goalBpm: Int, tempo: Int): Float {
            var progress = (tempo - initialBpm).toFloat() / (goalBpm - initialBpm).toFloat()
            if (progress < 0f)
                progress = 0f
            if (progress > 1f)
                progress = 1f
            return progress
        }

        private fun getBestBpmFromDay(entries: List<PracticeEntry>, initial: Int, goal: Int): Int {
            return entries.foldRight(0, {entry, max -> max(max, entry.rating.estimateTempo(entry.tempo, initial, goal))})
        }

    }

    enum class Rating {
        VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH;

        fun estimateTempo(bpm: Int, initial: Int, goal: Int): Int {
            val diff = bpm - initial
            return when(this) {
                VERY_LOW -> (initial + diff * 0.2).toInt()
                LOW -> (initial + diff * 0.4).toInt()
                MEDIUM -> (initial + diff * 0.6).toInt()
                HIGH -> (initial + diff * 0.8).toInt()
                VERY_HIGH -> bpm
            }
        }
    }


}

