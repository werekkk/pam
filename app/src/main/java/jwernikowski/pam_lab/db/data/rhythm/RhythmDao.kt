package jwernikowski.pam_lab.db.data.rhythm

import androidx.lifecycle.LiveData
import androidx.room.*
import jwernikowski.pam_lab.db.data.rhythm.Rhythm

@Dao
interface RhythmDao {
    @Query("SELECT * FROM rhythm")
    fun getAll(): LiveData<List<Rhythm>>

    @Query("SELECT * FROM rhythm WHERE rhythmId = :rhythmId")
    fun getById(rhythmId: Long): Rhythm

    @Insert
    fun insertOne(rhythm: Rhythm): Long

    @Delete
    fun delete(rhythm: Rhythm)

    @Update
    fun update(rhythm: Rhythm)
}