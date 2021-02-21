package jwernikowski.pam_lab.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.helper_entity.SectionPreviousBpmAndRhythmUpdate
import jwernikowski.pam_lab.db.data.helper_entity.SectionProgressUpdate

@Dao
interface SectionDao {

    @Query("SELECT * FROM section WHERE songId = :songId ORDER BY `order` ASC")
    fun getBySongId(songId: Long): LiveData<List<Section>>

    @Query("SELECT * FROM section WHERE songId = :songId ORDER BY `order` ASC")
    fun getBySongIdBlocking(songId: Long): List<Section>

    @Query("SELECT * FROM section WHERE sectionId = :sectionId")
    fun getBySectionId(sectionId: Long): LiveData<Section>

    @Insert
    fun insertOne(section: Section): Long

    @Delete
    fun delete(section: Section)

    @Query("DELETE FROM section WHERE songId = :songId")
    fun deleteAllBySongId(songId: Long)

    @Update
    fun update(section: Section)

    @Update
    fun update(sections: List<Section>)

    @Update(entity = Section::class)
    fun progressUpdate(update: SectionProgressUpdate)

    @Update(entity = Section::class)
    fun previousBpmAndRhythmUpdate(update: SectionPreviousBpmAndRhythmUpdate)

}