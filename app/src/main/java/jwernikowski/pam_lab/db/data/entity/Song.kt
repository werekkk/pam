package jwernikowski.pam_lab.db.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Song (
    val name: String,
    val hasSections: Boolean
) : Serializable {
    @PrimaryKey(autoGenerate = true) var songId: Long = 0
}
