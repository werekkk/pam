package jwernikowski.pam_lab.db.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.dao.SectionDao
import jwernikowski.pam_lab.db.data.helper_entity.SectionPreviousBpmAndRhythmUpdate
import jwernikowski.pam_lab.db.data.helper_entity.SectionProgressUpdate

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