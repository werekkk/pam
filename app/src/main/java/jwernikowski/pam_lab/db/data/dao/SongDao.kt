package jwernikowski.pam_lab.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import jwernikowski.pam_lab.db.data.entity.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM song WHERE songId = :songId")
    fun getById(songId: Long): LiveData<Song>

    @Query("SELECT * FROM song")
    fun getAll(): LiveData<List<Song>>

    @Insert
    fun insertAll(vararg songs: Song)

    @Insert
    fun insertOne(songs: Song): Long

    @Delete
    fun delete(song: Song)

    @Update
    fun update(song: Song)

}