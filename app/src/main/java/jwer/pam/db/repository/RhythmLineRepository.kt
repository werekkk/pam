package jwer.pam.db.repository

import androidx.lifecycle.LiveData
import jwer.pam.db.data.entity.RhythmLine
import jwer.pam.db.data.dao.RhythmLineDao

class RhythmLineRepository(private val rhythmLineDao: RhythmLineDao) {
    fun getByRhythmId(rhythmId: Long): LiveData<List<RhythmLine>> {
        return rhythmLineDao.getByRhythmId(rhythmId)
    }

    fun getByRhythmIdBlocking(rhythmId: Long): List<RhythmLine> {
        return rhythmLineDao.getByRhythmIdBlocking(rhythmId)
    }

    fun add(rhythmLine: RhythmLine): Long {
        return rhythmLineDao.insertOne(rhythmLine)
    }

    fun addAll(vararg rhythmLines: RhythmLine) {
        return rhythmLineDao.insertAll(*rhythmLines)
    }

    fun update(rhythmLine: RhythmLine) {
        rhythmLineDao.update(rhythmLine)
    }

    fun deleteByRhythmId(rhythmId: Long) {
        rhythmLineDao.deleteByRhythmId(rhythmId)
    }
}