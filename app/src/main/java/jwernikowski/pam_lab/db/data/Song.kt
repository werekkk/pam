package jwernikowski.pam_lab.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Song (
    @ColumnInfo(name = "song_name") val name: String,
    @ColumnInfo(name = "initial_tempo") val initialTempo: Int,
    @ColumnInfo(name = "goal_tempo") val goalTempo: Int
) : Serializable {
    @PrimaryKey(autoGenerate = true) var songId: Long = 0
}
