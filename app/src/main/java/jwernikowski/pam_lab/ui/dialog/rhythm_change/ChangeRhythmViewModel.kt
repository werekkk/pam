package jwernikowski.pam_lab.ui.dialog.rhythm_change

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.ui.activity.MainActivity
import javax.inject.Inject

class ChangeRhythmViewModel : ViewModel() {

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var rhythmRepository: RhythmRepository

    val allRhythms: LiveData<List<Rhythm>> = rhythmRepository.getAll()

}