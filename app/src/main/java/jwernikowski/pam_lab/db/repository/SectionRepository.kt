package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.dao.SectionDao
import jwernikowski.pam_lab.db.data.entity.SectionOrder

class SectionRepository (private val sectionDao: SectionDao) {

    fun getBySongId(songId: Long): LiveData<List<Section>> {
        return sectionDao.getBySongId(songId)
    }

    fun add(section: Section) {
        sectionDao.insertOne(section)
    }

    fun updateSections(sections: List<SectionOrder>) {
        sectionDao.update(sections)
    }
}