package jwernikowski.pam_lab.db.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.data.SongDao

class SongRepository (private val songDao: SongDao) {

    fun getAll(): LiveData<List<Song>> {
        return songDao.getAll()
    }

    fun add(song: Song) {
        songDao.insertAll(song)
    }

    fun delete(song: Song) {
        songDao.delete(song)
    }

    fun update(song: Song) {
        songDao.update(song)
    }

}