package jwernikowski.pam_lab.ui.songs

import android.text.Editable
import androidx.databinding.BaseObservable
import androidx.lifecycle.*
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.repository.SongRepository
import jwernikowski.pam_lab.utils.Tempo
import jwernikowski.pam_lab.utils.checkValidators
import jwernikowski.pam_lab.utils.validateLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewSongViewModel : ViewModel() {

    private val _dismissView: MutableLiveData<Unit> = MutableLiveData()

    val hasSections: MutableLiveData<Boolean> = MutableLiveData(false)
    val songName: MutableLiveData<String> = MutableLiveData()
    val initialTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_INITIAL)
    val goalTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_GOAL)
    val dismissView: LiveData<Unit> = _dismissView

    val songNameNotEmpty = validateLiveData(songName, ::isSongNameValid)
    val initialTempoInRange = validateLiveData(initialTempo, ::isTempoValid)
    val goalTempoInRange = validateLiveData(goalTempo, ::isTempoValid)
    val initialTempoSmallerThanGoal = validateLiveData(goalTempo, {initialTempo.value == null || initialTempo.value!! <= it})

    private val addSongValidators = arrayOf(songNameNotEmpty, initialTempoInRange, goalTempoInRange, initialTempoInRange)

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var songRepository: SongRepository

    fun onAddSong() {
        if (isDataValid()) {
            val (newSong, sections) = enteredSongData()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    songRepository.addSong(newSong, sections)
                    _dismissView.postValue(null)
                }
            }
        }
    }

    private fun enteredSongData(): Pair<Song, List<Section>> {
        val song = Song(songName.value!!, hasSections.value!!)
        val sections = ArrayList<Section>()
        if (!song.hasSections) {
            sections.add(Section(Section.DEFAULT_SECTION_NAME, initialTempo.value!!, goalTempo.value!!, 1))
        }
        return Pair(song, sections)
    }

    private fun isDataValid(): Boolean {
        return checkValidators(addSongValidators)
    }

    fun onCancel() {
        _dismissView.postValue(null)
    }

    private fun isSongNameValid(songName: String?): Boolean {
        return songName != null && songName.isNotEmpty()
    }

    private fun isTempoValid(tempo: Int): Boolean {
        return tempo >= Tempo.MIN_BPM && tempo <= Tempo.MAX_BPM
    }

}