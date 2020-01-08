package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import jwernikowski.pam_lab.db.data.rhythm.Rhythm
import jwernikowski.pam_lab.db.data.rhythm.RhythmDao

class RhythmRepository(private val rhythmDao: RhythmDao) {

    fun getAll(): LiveData<List<Rhythm>> {
        return rhythmDao.getAll()
    }

    fun getById(rhythmId: Long): Rhythm {
        return rhythmDao.getById(rhythmId)
    }

    fun add(rhythm: Rhythm): Long {
        return rhythmDao.insertOne(rhythm)
    }

    fun delete(rhythm: Rhythm) {
        rhythmDao.delete(rhythm)
    }

    fun update(rhythm: Rhythm) {
        rhythmDao.update(rhythm)
    }
}