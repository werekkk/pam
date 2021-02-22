package jwer.pam.db.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(foreignKeys = [
    ForeignKey(entity = Song::class,
        parentColumns = ["songId"],
        childColumns = ["songId"],
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Rhythm::class,
        parentColumns = ["rhythmId"],
        childColumns = ["previousRhythmId"],
        onDelete = ForeignKey.SET_NULL)
]
)
data class Section(
    val name: String,
    val initialTempo: Int,
    val goalTempo: Int,
    val order: Int,
    var songId: Long = 0L,
    @PrimaryKey(autoGenerate = true) var sectionId: Long = 0L,
    var previousProgress: Int = 0,
    var previousBpm: Int = initialTempo,
    var previousRhythmId: Long? = null
) : Serializable {

    companion object {
        val DEFAULT_SECTION_NAME = "default_section"

        fun isSectionNameValid(sectionName: String?): Boolean {
            return sectionName != null && sectionName.isNotEmpty()
        }
    }
}