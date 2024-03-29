package jwer.pam.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Song (
    val name: String,
    val hasSections: Boolean,
    @PrimaryKey(autoGenerate = true) var songId: Long = 0,
    var previousProgress: Int = 0
) : Serializable {

    companion object {

        fun isSectionNameValid(sectionName: String?): Boolean {
            return sectionName != null && sectionName.isNotEmpty()
        }

    }

}