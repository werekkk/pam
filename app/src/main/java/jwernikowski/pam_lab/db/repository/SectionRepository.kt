package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.dao.SectionDao

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

    fun update(sections: List<Section>) {
        sectionDao.update(sections)
    }

    fun delete(section: Section) {
        sectionDao.delete(section)
    }

}