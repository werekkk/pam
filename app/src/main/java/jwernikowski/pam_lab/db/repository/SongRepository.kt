package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import androidx.room.Transaction
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.SectionDao
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.data.SongDao

class SongRepository (private val songDao: SongDao, private val sectionDao: SectionDao) {

    fun getAll(): LiveData<List<Song>> {
        return songDao.getAll()
    }

    @Transaction
    fun addSong(song: Song, sections: List<Section>) {
        val savedSongId = songDao.insertOne(song)
        sections.forEach {
            it.songId = savedSongId
            sectionDao.insertOne(it)
        }
    }

    fun delete(song: Song) {
        songDao.delete(song)
    }

    fun update(song: Song) {
        songDao.update(song)
    }

}