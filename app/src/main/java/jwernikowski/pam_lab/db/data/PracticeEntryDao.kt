package jwernikowski.pam_lab.db.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PracticeEntryDao {

    @Query("SELECT * FROM practiceEntry WHERE songId = :songId ORDER BY date ASC")
    fun getBySongId(songId: Long): LiveData<List<PracticeEntry>>

    @Insert
    fun insertOne(practiceEntry: PracticeEntry): Long

    @Delete
    fun delete(practiceEntry: PracticeEntry)
}