package jwernikowski.pam_lab.db.repository

import androidx.lifecycle.LiveData
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.SectionDao

class SectionRepository (private val sectionDao: SectionDao) {

    fun getBySongId(songId: Long): LiveData<List<Section>> {
        return sectionDao.getBySongId(songId)
    }

    fun add(section: Section) {
        sectionDao.insertOne(section)
    }

}