package jwer.pam.ui.fragment.rhythms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jwer.pam.ui.activity.MainActivity
import jwer.pam.db.data.entity.Rhythm
import jwer.pam.db.repository.RhythmRepository
import javax.inject.Inject

class RhythmsViewModel : ViewModel() {

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var rhythmRepository: RhythmRepository

    val rhythmsLoaded = MutableLiveData(false)
    val allRhythms: LiveData<List<Rhythm>> =
        Transformations.map(rhythmRepository.getAll()) {
            rhythmsLoaded.postValue(true)
            it
        }

    fun delete(rhythm: Rhythm) = rhythmRepository.delete(rhythm)

}