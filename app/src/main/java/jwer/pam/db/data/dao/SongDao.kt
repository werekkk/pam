package jwer.pam.db.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import jwer.pam.db.data.entity.Song
import jwer.pam.db.data.helper_entity.SongProgressUpdate

@Dao
interface SongDao {

    @Query("SELECT * FROM song WHERE songId = :songId")
    fun getById(songId: Long): LiveData<Song?>

    @Query("SELECT * FROM song ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): LiveData<List<Song>>

    @Insert
    fun insertAll(vararg songs: Song)

    @Insert
    fun insertOne(songs: Song): Long

    @Delete
    fun delete(song: Song)

    @Update
    fun update(song: Song)

    @Update(entity = Song::class)
    fun progressUpdate(progressUpdate: SongProgressUpdate)

}