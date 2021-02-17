package jwernikowski.pam_lab.ui.dialog.song_new

import androidx.lifecycle.*
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.repository.SongRepository
import jwernikowski.pam_lab.utils.Tempo
import jwernikowski.pam_lab.utils.checkValidators
import jwernikowski.pam_lab.utils.validateLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewSongViewModel : ViewModel() {


    val songName: MutableLiveData<String> = MutableLiveData()
    val hasSections: MutableLiveData<Boolean> = MutableLiveData(false)
    val initialTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_INITIAL)
    val goalTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_GOAL)
    val createdSong: MutableLiveData<LiveData<Song>> = MutableLiveData()

    val songNameNotEmpty = validateLiveData(songName) { Song.isSectionNameValid(it)}
    val initialTempoInRange = validateLiveData(initialTempo) { Tempo.isTempoValid(it) }
    val goalTempoInRange = validateLiveData(goalTempo) { Tempo.isTempoValid(it) }
    val initialTempoSmallerThanGoal = validateLiveData(goalTempo) {initialTempo.value == null || initialTempo.value!! <= it}

    private val addSongValidators = arrayOf(songNameNotEmpty, initialTempoInRange, goalTempoInRange, initialTempoSmallerThanGoal)

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
                    createdSong.postValue(songRepository.addSong(newSong, sections))
                }
            }
        }
    }

    private fun enteredSongData(): Pair<Song, List<Section>> {
        val song = Song(
            songName.value!!,
            hasSections.value!!
        )
        val sections = ArrayList<Section>()
        if (!song.hasSections) {
            sections.add(
                Section(
                    Section.DEFAULT_SECTION_NAME,
                    initialTempo.value!!,
                    goalTempo.value!!,
                    1
                )
            )
        }
        return Pair(song, sections)
    }

    private fun isDataValid(): Boolean {
        return checkValidators(addSongValidators)
    }

}