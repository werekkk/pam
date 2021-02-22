package jwer.pam.db.data.helper_entity

import androidx.room.Entity

@Entity
data class SongProgressUpdate(
    val songId: Long,
    val previousProgress: Int
)