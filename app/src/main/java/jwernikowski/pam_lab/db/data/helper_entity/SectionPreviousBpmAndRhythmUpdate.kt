package jwernikowski.pam_lab.db.data.helper_entity

import androidx.room.Entity

@Entity
data class SectionPreviousBpmAndRhythmUpdate(
    val sectionId: Long,
    val previousBpm: Int,
    val previousRhythmId: Long?
)