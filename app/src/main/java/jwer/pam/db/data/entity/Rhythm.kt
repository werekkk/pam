package jwer.pam.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jwer.pam.db.data.Meter
import java.io.Serializable

@Entity
data class Rhythm(
    val name: String,
    val meter: Meter,
    val defaultBpm: Int = 140,
    @PrimaryKey(autoGenerate = true) var rhythmId: Long = 0
) : Serializable {

    companion object {
        val DEFAULT_RHYTHM =
            Rhythm("Default", Meter(1, 1))
    }

}