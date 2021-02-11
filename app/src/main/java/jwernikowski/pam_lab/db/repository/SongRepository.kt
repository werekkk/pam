package jwernikowski.pam_lab.db.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import jwernikowski.pam_lab.db.AppDatabase
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.dao.SectionDao
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.data.dao.SongDao
import jwernikowski.pam_lab.utils.Tempo

class SongRepository (
    private val songDao: SongDao,
    private val sectionDao: SectionDao,
    private val database: AppDatabase
) {

    fun getById(songId: Long): LiveData<Song> {
        return songDao.getById(songId)
    }

    fun getAll(): LiveData<List<Song>> {
        return songDao.getAll()
    }

    fun addSong(song: Song, sections: List<Section>) {
        database.runInTransaction {
            val savedSongId = songDao.insertOne(song)
            sections.forEach {
                it.songId = savedSongId
                sectionDao.insertOne(it)
            }
        }
    }

    fun delete(song: Song) {
        songDao.delete(song)
    }

    fun update(song: Song,
               oldHasSections: Boolean = song.hasSections,
               initialTempo: Int = Tempo.DEFAULT_INITIAL,
               goalTempo: Int = Tempo.DEFAULT_GOAL
    ) {
        database.runInTransaction {
            if (song.hasSections != oldHasSections) {
                sectionDao.deleteAllBySongId(song.songId)
                if (!song.hasSections) {
                    val defaultSection = Section(
                        Section.DEFAULT_SECTION_NAME,
                        initialTempo,
                        goalTempo,
                        1,
                        song.songId
                    )
                    sectionDao.insertOne(defaultSection)
                }
            }
            songDao.update(song)
        }
    }

}