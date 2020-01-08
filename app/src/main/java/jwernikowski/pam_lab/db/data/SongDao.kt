package jwernikowski.pam_lab.db.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAll(): LiveData<List<Song>>

    @Insert
    fun insertAll(vararg songs: Song)

    @Delete
    fun delete(song: Song)

    @Update
    fun update(song: Song)
}