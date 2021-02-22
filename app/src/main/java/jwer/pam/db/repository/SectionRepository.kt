package jwer.pam.db.repository

import androidx.lifecycle.LiveData
import jwer.pam.db.data.entity.Section
import jwer.pam.db.data.dao.SectionDao
import jwer.pam.db.data.helper_entity.SectionPreviousBpmAndRhythmUpdate
import jwer.pam.db.data.helper_entity.SectionProgressUpdate

class SectionRepository (private val sectionDao: SectionDao) {

    fun getBySongId(songId: Long): LiveData<List<Section>> {
        return sectionDao.getBySongId(songId)
    }

    fun getBySectionId(sectionId: Long): LiveData<Section> {
        return sectionDao.getBySectionId(sectionId)
    }

    fun add(section: Section) {
        sectionDao.insertOne(section)
    }

    fun update(section: Section) {
        sectionDao.update(section)
    }

    fun progressUpdate(update: SectionProgressUpdate) {
        sectionDao.progressUpdate(update)
    }

    fun previousBpmAndRhythmUpdate(update: SectionPreviousBpmAndRhythmUpdate) {
        sectionDao.previousBpmAndRhythmUpdate(update)
    }

    fun update(sections: List<Section>) {
        sectionDao.update(sections)
    }

    fun delete(section: Section) {
        sectionDao.delete(section)
    }

}