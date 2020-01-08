package jwernikowski.pam_lab.models

import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.db.data.rhythm.Rhythm
import jwernikowski.pam_lab.db.data.rhythm.RhythmLine

class RhythmDto(
    var rhythmId: Long?,
    var name: String,
    var meter: Meter,
    var defaultBpm: Int) {
    constructor(): this(null,"", Meter.default(), 140)
    constructor(rhythm: Rhythm):
            this(rhythm.rhythmId, rhythm.name, rhythm.meter, rhythm.defaultBpm)
}