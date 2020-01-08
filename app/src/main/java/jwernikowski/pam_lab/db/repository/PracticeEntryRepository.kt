package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.PracticeEntryDao

class PracticeEntryRepository (private val practiceEntryDao: PracticeEntryDao) {

    fun getBySongId(songId: Long): LiveData<List<PracticeEntry>> {
        return practiceEntryDao.getBySongId(songId)
    }

    fun add(practiceEntry: PracticeEntry): Long {
        return practiceEntryDao.insertOne(practiceEntry)
    }

    fun delete(practiceEntry: PracticeEntry) {
        practiceEntryDao.delete(practiceEntry)
    }
}