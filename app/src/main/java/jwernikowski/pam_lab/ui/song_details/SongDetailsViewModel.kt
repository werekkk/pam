package jwernikowski.pam_lab.ui.song_details

import androidx.lifecycle.*
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.db.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongDetailsViewModel : ViewModel() {

    val song: MutableLiveData<Song> = MutableLiveData()

    val practiceEntries: LiveData<List<PracticeEntry>> =
        Transformations.switchMap(song) { newSong ->
            practiceEntryRepository.getBySongId(newSong.songId)
        }

//    val progress: LiveData<Float> =
//        Transformations.switchMap(practiceEntries) { newPracticeEntries -> let {
//            song.value!!.let {
//                MutableLiveData<Float>().apply { postValue(PracticeEntry.calculateProgress(newPracticeEntries, it.initialTempo, it.goalTempo)) }
//            }
//        }
//        }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var songsRepository: SongRepository
    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository

    fun deleteSong() {
        song.value?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    songsRepository.delete(it)
                }
            }
        }
    }

    fun updateSong(newSong: Song) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                songsRepository.update(newSong)
            }
        }
        song.value = newSong
    }

    fun deletePracticeEntry(entry: PracticeEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                practiceEntryRepository.delete(entry)
            }
        }
    }
}
