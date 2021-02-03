package jwernikowski.pam_lab.dto;

import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.db.data.entity.RhythmLine
import jwernikowski.pam_lab.sound.Sound
import jwernikowski.pam_lab.db.data.Converters

class RhythmLineDto(val sound: Sound, var beats: Array<Boolean>) {

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
    }

    override fun toString(): String = "$sound ${Converters.toBoolArrayString(beats)}"
}

