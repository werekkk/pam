package jwernikowski.pam_lab.db.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SectionDao {

    @Query("SELECT * FROM section WHERE songId = :songId ORDER BY `order` ASC")
    fun getBySongId(songId: Long): LiveData<List<Section>>

    @Insert
    fun insertOne(section: Section): Long

    @Delete
    fun delete(section: Section)

}