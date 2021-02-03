package jwernikowski.pam_lab.db.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(foreignKeys = [ForeignKey(entity = Song::class,
parentColumns = ["songId"],
childColumns = ["songId"],
onDelete = ForeignKey.CASCADE)]
)
data class Section(
    val name: String,
    val initialTempo: Int,
    val goalTempo: Int,
    val order: Int
) : Serializable {
    var songId = 0L
    @PrimaryKey(autoGenerate = true) var sectionId = 0L

    companion object {
        val DEFAULT_SECTION_NAME = "default_section"
    }
}

data class SectionOrder (
    val sectionId: Long,
    val order: Int
)