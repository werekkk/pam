package jwer.pam.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import jwer.pam.db.data.entity.RhythmLine

@Dao
interface RhythmLineDao {
    @Query("SELECT * FROM rhythmLine WHERE rhythmId = :rhythmId ORDER BY sound ASC")
    fun getByRhythmId(rhythmId: Long): LiveData<List<RhythmLine>>

    @Query("SELECT * FROM rhythmLine WHERE rhythmId = :rhythmId ORDER BY sound ASC")
    fun getByRhythmIdBlocking(rhythmId: Long): List<RhythmLine>

    @Insert
    fun insertOne(rhythmLine: RhythmLine): Long

    @Insert
    fun insertAll(vararg rhythmLines: RhythmLine)

    @Update
    fun update(rhythmLine: RhythmLine)

    @Query("DELETE FROM rhythmLine WHERE rhythmId = :rhythmId")
    fun deleteByRhythmId(rhythmId: Long)
}