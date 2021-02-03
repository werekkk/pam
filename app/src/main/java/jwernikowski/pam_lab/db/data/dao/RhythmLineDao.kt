package jwernikowski.pam_lab.db.data.dao

import androidx.room.*
import jwernikowski.pam_lab.db.data.entity.RhythmLine

@Dao
interface RhythmLineDao {
    @Query("SELECT * FROM rhythmLine WHERE rhythmId = :rhythmId ORDER BY sound ASC")
    fun getByRhythmId(rhythmId: Long): List<RhythmLine>

    @Insert
    fun insertOne(rhythmLine: RhythmLine): Long

    @Insert
    fun insertAll(vararg rhythmLines: RhythmLine)

    @Update
    fun update(rhythmLine: RhythmLine)

    @Query("DELETE FROM rhythmLine WHERE rhythmId = :rhythmId")
    fun deleteByRhythmId(rhythmId: Long)
}