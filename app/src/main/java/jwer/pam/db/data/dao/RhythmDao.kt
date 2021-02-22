package jwer.pam.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import jwer.pam.db.data.entity.Rhythm

@Dao
interface RhythmDao {
    @Query("SELECT * FROM rhythm ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): LiveData<List<Rhythm>>

    @Query("SELECT * FROM rhythm WHERE rhythmId = :rhythmId")
    fun getById(rhythmId: Long): LiveData<Rhythm?>

    @Query("SELECT * FROM rhythm WHERE rhythmId = :rhythmId")
    fun getByIdBlocking(rhythmId: Long): Rhythm?

    @Insert
    fun insertOne(rhythm: Rhythm): Long

    @Delete
    fun delete(rhythm: Rhythm)

    @Update
    fun update(rhythm: Rhythm)
}