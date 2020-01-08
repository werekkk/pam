package jwernikowski.pam_lab.ui.rhythms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.db.data.rhythm.Rhythm
import jwernikowski.pam_lab.db.repository.RhythmRepository
import javax.inject.Inject

class RhythmsViewModel : ViewModel() {

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var rhythmRepository: RhythmRepository

    val allRhythms: LiveData<List<Rhythm>> = rhythmRepository.getAll()

    fun delete(rhythm: Rhythm) = rhythmRepository.delete(rhythm)

}