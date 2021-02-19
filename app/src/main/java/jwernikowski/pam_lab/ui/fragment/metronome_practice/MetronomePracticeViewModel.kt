package jwernikowski.pam_lab.ui.fragment.metronome_practice

import androidx.lifecycle.*
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.ui.fragment.shared.MetronomeViewViewModel
import jwernikowski.pam_lab.utils.Tempo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class MetronomePracticeViewModel : MetronomeViewViewModel() {

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

    val maxBpm = MutableLiveData(Tempo.MAX_BPM)
    val minBpm = MutableLiveData(Tempo.MIN_BPM)

    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository

    init {
        MainActivity.component.inject(this)
        initViewModel(null, null)
    }

    override fun coerceBpm(newBpm: Int): Int {
        return newBpm.coerceIn(minBpm.value!!, maxBpm.value!!)
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

}