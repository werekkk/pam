package jwernikowski.pam_lab.ui.fragment.metronome_practice

import androidx.lifecycle.*
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class MetronomePracticeViewModel : ViewModel() {

    companion object {
        const val MIN_DELAY : Int = 400
    }

    var song: MutableLiveData<Song> = MutableLiveData()
    var section: MutableLiveData<Section> = MutableLiveData()

    val lastPracticeEntry: LiveData<PracticeEntry?> =
        Transformations.switchMap(section) {
            practiceEntryRepository.getBySectionId(it.sectionId).map { it.lastOrNull() }
        }

    lateinit var onRatedListener: (PracticeEntry) -> Unit
    private var lastRated = System.currentTimeMillis()

    private val _bpm: MutableLiveData<Int> = MutableLiveData()
    val isOn: MutableLiveData<Boolean> = MutableLiveData()
    val progress: MutableLiveData<Float> = MutableLiveData()

    val maxBpm: MutableLiveData<Int> = MutableLiveData()
    val minBpm: MutableLiveData<Int> = MutableLiveData()

    val bpm: MutableLiveData<Int>
        get() = _bpm

    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository

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


    fun addRating(rating: PracticeEntry.Rating) {
        bpm.value?.let { bpm ->
            section.value?.let {
                if (System.currentTimeMillis() - lastRated > MIN_DELAY) {
                    val newEntry =
                        PracticeEntry(
                            it.sectionId,
                            LocalDateTime.now(),
                            rating,
                            bpm
                        )
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

    fun findLastPractice(section: Section): LiveData<PracticeEntry?> {
        return practiceEntryRepository.getBySectionId(section.sectionId).map { it.lastOrNull() }
    }

}