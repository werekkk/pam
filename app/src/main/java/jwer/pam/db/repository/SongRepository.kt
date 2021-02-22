package jwer.pam.db.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jwer.pam.db.AppDatabase
import jwer.pam.db.data.entity.Section
import jwer.pam.db.data.dao.SectionDao
import jwer.pam.db.data.entity.Song
import jwer.pam.db.data.dao.SongDao
import jwer.pam.db.data.helper_entity.SongProgressUpdate
import jwer.pam.utils.Tempo

class SongRepository (
    private val songDao: SongDao,
    private val sectionDao: SectionDao,
    private val database: AppDatabase
) {

    fun getById(songId: Long): LiveData<Song?> {
        return songDao.getById(songId)
    }

    fun getAll(): LiveData<List<Song>> {
        return songDao.getAll()
    }

    fun addSong(song: Song, sections: List<Section>): LiveData<Song> {
        val result = MutableLiveData<Song>()
        database.runInTransaction {
            val savedSongId = songDao.insertOne(song)
            sections.forEach {
                it.songId = savedSongId
                sectionDao.insertOne(it)
            }
            result.postValue(song.copy(songId = savedSongId))
        }
        return result
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
            } else if (!song.hasSections) {
                val existingSection = sectionDao.getBySongIdBlocking(song.songId)[0]
                sectionDao.update(existingSection.copy(
                    initialTempo = initialTempo,
                    goalTempo = goalTempo
                ))
            }
            songDao.update(song)
        }
    }

    fun progressUpdate(progressUpdate: SongProgressUpdate) {
        songDao.progressUpdate(progressUpdate)
    }

}