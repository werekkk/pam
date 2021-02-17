package jwernikowski.pam_lab.ui.activity.song_practice

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.repository.SectionRepository
import jwernikowski.pam_lab.ui.activity.MainActivity
import javax.inject.Inject

class SongPracticeViewModel : ViewModel() {

    val song = MutableLiveData<Song>()
    val currentSection = MutableLiveData<Section>()

    val sections: LiveData<List<Section>> =
        Transformations.switchMap(song) {
            sectionRepository.getBySongId(it.songId)
        }

    val hasNextSection: LiveData<Boolean> =
        Transformations.switchMap(currentSection) { cur ->
            Transformations.map(sections) {
                it.last().sectionId != cur.sectionId
            }
        }

    val hasPreviousSection: LiveData<Boolean> =
        Transformations.switchMap(currentSection) { cur ->
            Transformations.map(sections) {
                it.first().sectionId != cur.sectionId
            }
        }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var sectionRepository: SectionRepository

    fun setState(newSong: Song, newSection: Section) {
        song.postValue(newSong)
        currentSection.postValue(newSection)
    }

    fun handleNextSection() {
        if (hasNextSection.value ?: false) {
            currentSection.value?.let { current ->
                sections.value?.let {
                    currentSection.postValue(it[it.indexOfFirst { it.sectionId == current.sectionId } + 1])
                }
            }
        }
    }

    fun handlePreviousSection() {
        if (hasPreviousSection.value ?: false) {
            currentSection.value?.let { current ->
                sections.value?.let {
                    currentSection.postValue(it[it.indexOfFirst { it.sectionId == current.sectionId } - 1])
                }
            }
        }
    }

}