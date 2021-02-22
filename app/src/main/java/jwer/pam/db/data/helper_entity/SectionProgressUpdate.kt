package jwer.pam.db.data.helper_entity

import androidx.room.Entity

@Entity
data class SectionProgressUpdate(
    val sectionId: Long,
    val previousProgress: Int
)