package jwernikowski.pam_lab.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import jwernikowski.pam_lab.db.data.Meter
import java.io.Serializable

@Entity
data class Rhythm(
    val name: String,
    val meter: Meter,
    val defaultBpm: Int = 140
) : Serializable {

    companion object {
        val DEFAULT_RHYTHM =
            Rhythm("Default", Meter(1, 1))
    }

    @PrimaryKey(autoGenerate = true) var rhythmId: Long = 0
}