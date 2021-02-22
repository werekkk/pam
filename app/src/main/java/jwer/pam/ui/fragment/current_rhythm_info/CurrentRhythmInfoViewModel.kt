package jwer.pam.ui.fragment.current_rhythm_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jwer.pam.db.data.entity.Rhythm

class CurrentRhythmInfoViewModel : ViewModel() {

    val currentRhythm = MutableLiveData<Rhythm?>(null)

    val hasRhythm = Transformations.map(currentRhythm) {
        it?.rhythmId != 0L
    }

}