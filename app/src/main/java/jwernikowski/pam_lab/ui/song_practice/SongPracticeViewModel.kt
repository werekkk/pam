package jwernikowski.pam_lab.ui.song_practice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class SongPracticeViewModel : ViewModel() {

    companion object {
        const val MIN_DELAY : Int = 1000
    }

    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository

    lateinit var onRatedListener: (PracticeEntry) -> Unit
    private var lastRated = System.currentTimeMillis()

    private val _bpm: MutableLiveData<Int> = MutableLiveData()
    val isOn: MutableLiveData<Boolean> = MutableLiveData()
    val progress: MutableLiveData<Float> = MutableLiveData()


    val maxBpm: MutableLiveData<Int> = MutableLiveData()
    val minBpm: MutableLiveData<Int> = MutableLiveData()

    val bpm: MutableLiveData<Int>
        get() = _bpm

    init {
        MainActivity.component.inject(this)
        _bpm.value = 140
        isOn.value = false
    }

    fun increaseBpm(value: Int) {
        _bpm.value?.let {
            val newBpm = it + value
            if (newBpm < minBpm.value!!)
                _bpm.value = minBpm.value
            else if (newBpm > maxBpm.value!!)
                _bpm.value = maxBpm.value
            else
                _bpm.value = newBpm
        }

    }

    var song : MutableLiveData<Song> = MutableLiveData()

    fun addRating(rating: PracticeEntry.Rating) {
        bpm.value?.let { bpm ->
            song.value?.let {
                if (System.currentTimeMillis() - lastRated > MIN_DELAY) {
                    val newEntry = PracticeEntry(it.songId, LocalDateTime.now(), rating, bpm)
                    saveEntry(newEntry)
                    lastRated = System.currentTimeMillis()
                }
            }
        }
    }

    private fun saveEntry(entry: PracticeEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                entry.practiceEntryId = practiceEntryRepository.add(entry)
                onRatedListener(entry)
            }
        }
    }

    fun removeEntry(entry: PracticeEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                practiceEntryRepository.delete(entry)
            }
        }
    }

}