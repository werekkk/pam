package jwernikowski.pam_lab.ui.dialog.song_edit

import androidx.lifecycle.*
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.repository.SectionRepository
import jwernikowski.pam_lab.db.repository.SongRepository
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.utils.*
import kotlinx.coroutines.*
import javax.inject.Inject

class EditSongViewModel : ViewModel() {

    val song: MutableLiveData<Song> = MutableLiveData()

    val sections: LiveData<List<Section>> =
        Transformations.switchMap(song) {
            songLoaded.postValue(false)
            val sections = sectionRepository.getBySongId(it.songId)
            songLoaded.postValue(true)
            sections
        }

    val songName: MutableLiveData<String> = MutableLiveData()
    val hasSections: MutableLiveData<Boolean> = MutableLiveData(false)
    val initialTempo: MutableLiveData<Int> = MutableLiveDataWithDelayedInitialValue(Tempo.DEFAULT_INITIAL,
        Transformations.map(sections) {
            if (!song.value!!.hasSections) {
                it[0].initialTempo
            } else {
                Tempo.DEFAULT_INITIAL
            }
        }
    )
    val goalTempo: MutableLiveData<Int> = MutableLiveDataWithDelayedInitialValue(Tempo.DEFAULT_GOAL,
        Transformations.map(sections) {
            if (!song.value!!.hasSections) {
                it[0].goalTempo
            } else {
                Tempo.DEFAULT_GOAL
            }
        }
    )

    val songLoaded: MutableLiveData<Boolean> = MutableLiveData(false)

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
    @Inject
    lateinit var sectionRepository: SectionRepository

    fun setSong(newSong: Song) {
        songLoaded.postValue(false)
        song.postValue(newSong)
        songName.postValue(newSong.name)
        hasSections.postValue(newSong.hasSections)
    }

    fun handleSaveSong() {
        if (isDataValid()) {
            val updatedSong = enteredSongData()
            val oldHasSections = song.value!!.hasSections
            val updatedInitialTempo = initialTempo.value!!
            val updatedGoalTempo = goalTempo.value!!
            (if (viewModelScope.isActive) viewModelScope else GlobalScope).launch {
                withContext(Dispatchers.IO) {
                    if (updatedSong.hasSections) {
                        songRepository.update(updatedSong, oldHasSections)
                    } else {
                        songRepository.update(updatedSong, oldHasSections, updatedInitialTempo, updatedGoalTempo)
                    }
                }
            }
        }
    }

    private fun enteredSongData(): Song {
        return song.value!!.copy(
            name = songName.value!!,
            hasSections = hasSections.value!!
        )
    }

    fun isDataValid(): Boolean {
        return checkValidators(addSongValidators)
    }

}