package jwer.pam.dto

import jwer.pam.db.data.Meter
import jwer.pam.db.data.entity.Rhythm

class RhythmDto(
    var rhythmId: Long?,
    var name: String,
    var meter: Meter,
    var defaultBpm: Int) {
    constructor(): this(null,"", Meter.default(), 140)
    constructor(rhythm: Rhythm):
            this(rhythm.rhythmId, rhythm.name, rhythm.meter, rhythm.defaultBpm)
}