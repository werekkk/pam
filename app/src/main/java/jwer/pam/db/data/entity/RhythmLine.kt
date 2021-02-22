package jwer.pam.db.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import jwer.pam.sound.Sound
import java.io.Serializable

@Entity (foreignKeys = [ForeignKey(entity = Rhythm::class,
    parentColumns = ["rhythmId"],
    childColumns = ["rhythmId"],
    onDelete = ForeignKey.CASCADE)]
)
data class RhythmLine(
    val rhythmId: Long,
    val sound: Sound,
    val beats: Array<Boolean>): Serializable {
    @PrimaryKey(autoGenerate = true) var rhythmLineId: Long = 0
}