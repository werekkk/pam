package jwer.pam.db.repository

import androidx.lifecycle.LiveData
import jwer.pam.db.data.entity.Rhythm
import jwer.pam.db.data.dao.RhythmDao

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