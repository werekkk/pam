package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.dao.PracticeEntryDao

class PracticeEntryRepository (private val practiceEntryDao: PracticeEntryDao) {

    fun getBySongId(songId: Long): LiveData<List<PracticeEntry>> {
        return practiceEntryDao.getBySongId(songId)
    }

    fun getBySectionId(sectionId: Long): LiveData<List<PracticeEntry>> {
        return practiceEntryDao.getBySectionId(sectionId)
    }

    fun getBySectionIdBlocking(sectionId: Long): List<PracticeEntry> {
        return practiceEntryDao.getBySectionIdBlocking(sectionId)
    }

    fun add(practiceEntry: PracticeEntry): Long {
        return practiceEntryDao.insertOne(practiceEntry)
    }

    fun delete(practiceEntry: PracticeEntry) {
        practiceEntryDao.delete(practiceEntry)
    }
}