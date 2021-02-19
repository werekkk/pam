package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.data.dao.RhythmDao

class RhythmRepository(private val rhythmDao: RhythmDao) {

    fun getAll(): LiveData<List<Rhythm>> {
        return rhythmDao.getAll()
    }

    fun getById(rhythmId: Long): LiveData<Rhythm?> {
        return rhythmDao.getById(rhythmId)
    }

    fun getByIdBlocking(rhythmId: Long): Rhythm? {
        return rhythmDao.getByIdBlocking(rhythmId)
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