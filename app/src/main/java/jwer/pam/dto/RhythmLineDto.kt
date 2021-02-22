package jwer.pam.dto;

import jwer.pam.db.data.Meter
import jwer.pam.db.data.entity.RhythmLine
import jwer.pam.sound.Sound
import jwer.pam.db.data.Converters

data class RhythmLineDto(val sound: Sound, var beats: Array<Boolean>) {

    constructor(rhythmLine: RhythmLine) : this(rhythmLine.sound, rhythmLine.beats.clone())

    companion object {

        val DEFAULT_RHYTHM_LINES: List<RhythmLineDto> = listOf(RhythmLineDto(Sound.WOOD, arrayOf(true)))

        fun default(meter: Meter): List<RhythmLineDto> {
            return Sound.values().map { sound ->
                RhythmLineDto(sound, Array(meter.blockCount) {false})
            }
        }

        fun fromRhythmLines(rhythmLines: List<RhythmLine>): List<RhythmLineDto> {
            return rhythmLines.map { line -> RhythmLineDto(line) }
        }

        fun copyOf(rhythmLines: List<RhythmLineDto>): List<RhythmLineDto> {
            val list = ArrayList<RhythmLineDto>(rhythmLines.size)
            rhythmLines.forEach { list.add(it.copy(beats = it.beats.copyOf())) }
            return list
        }
    }

    override fun toString(): String = "$sound ${Converters.toBoolArrayString(beats)}"

    override fun equals(other: Any?): Boolean {
        return if (other is RhythmLineDto) {
            sound == other.sound && beats.contentDeepEquals(other.beats)
        } else false
    }
}

