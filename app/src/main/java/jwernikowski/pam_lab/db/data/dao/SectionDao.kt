package jwernikowski.pam_lab.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.SectionOrder

@Dao
interface SectionDao {

    @Query("SELECT * FROM section WHERE songId = :songId ORDER BY `order` ASC")
    fun getBySongId(songId: Long): LiveData<List<Section>>

    @Insert
    fun insertOne(section: Section): Long

    @Delete
    fun delete(section: Section)

    @Update(entity = Section::class)
    fun update(sections: List<SectionOrder>)

}

