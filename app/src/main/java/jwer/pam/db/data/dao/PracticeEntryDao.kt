package jwer.pam.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import jwer.pam.db.data.entity.PracticeEntry

@Dao
interface PracticeEntryDao {

    @Query("SELECT * FROM practiceEntry pe JOIN section s on pe.sectionId = s.sectionId WHERE s.songId = :songId ORDER BY date ASC")
    fun getBySongId(songId: Long): LiveData<List<PracticeEntry>>

    @Query("SELECT * FROM practiceEntry pe WHERE pe.sectionId = :sectionId ORDER BY date ASC")
    fun getBySectionId(sectionId: Long): LiveData<List<PracticeEntry>>

    @Query("SELECT * FROM practiceEntry pe WHERE pe.sectionId = :sectionId ORDER BY date ASC")
    fun getBySectionIdBlocking(sectionId: Long): List<PracticeEntry>

    @Insert
    fun insertOne(practiceEntry: PracticeEntry): Long

    @Delete
    fun delete(practiceEntry: PracticeEntry)
}