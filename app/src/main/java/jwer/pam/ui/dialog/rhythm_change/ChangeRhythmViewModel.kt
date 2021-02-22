package jwer.pam.ui.dialog.rhythm_change

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jwer.pam.db.data.entity.Rhythm
import jwer.pam.db.repository.RhythmRepository
import jwer.pam.ui.activity.MainActivity
import javax.inject.Inject

class ChangeRhythmViewModel : ViewModel() {

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var rhythmRepository: RhythmRepository

    val rhythmsLoaded = MutableLiveData(false)
    val allRhythms: LiveData<List<Rhythm>> = Transformations.map(rhythmRepository.getAll()) {
        rhythmsLoaded.postValue(true)
        it
    }

}