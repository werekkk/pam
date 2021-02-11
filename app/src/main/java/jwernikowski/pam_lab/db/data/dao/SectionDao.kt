package jwernikowski.pam_lab.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import jwernikowski.pam_lab.db.data.entity.Section

@Dao
interface SectionDao {

    @Query("SELECT * FROM section WHERE songId = :songId ORDER BY `order` ASC")
    fun getBySongId(songId: Long): LiveData<List<Section>>

    @Query("SELECT * FROM section WHERE sectionId = :sectionId")
    fun getBySectionId(sectionId: Long): LiveData<Section>

    @Insert
    fun insertOne(section: Section): Long

    @Delete
    fun delete(section: Section)

    @Update
    fun update(section: Section)

    @Update
    fun update(sections: List<Section>)

}
