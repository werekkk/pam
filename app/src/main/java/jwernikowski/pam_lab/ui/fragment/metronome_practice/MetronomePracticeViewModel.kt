package jwernikowski.pam_lab.ui.fragment.metronome_practice

import android.util.Log
import androidx.lifecycle.*
import jwernikowski.pam_lab.db.data.dao.SectionDao
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.data.helper_entity.SectionPreviousBpmAndRhythmUpdate
import jwernikowski.pam_lab.db.data.helper_entity.SectionProgressUpdate
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.db.repository.SectionRepository
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

    private val practiceEntries = Transformations.switchMap(section) {
        practiceEntryRepository.getBySectionId(it.sectionId)
    }

    lateinit var onRatedListener: (PracticeEntry) -> Unit
    private var lastRated = System.currentTimeMillis()

    val maxBpm = MutableLiveData(Tempo.MAX_BPM)
    val minBpm = MutableLiveData(Tempo.MIN_BPM)

    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository
    @Inject
    lateinit var sectionRepository: SectionRepository

    init {
        MainActivity.component.inject(this)
        initViewModel(null, null)
    }

    fun init(lifecycleOwner: LifecycleOwner) {
        practiceEntries.observe(lifecycleOwner) {}
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
                updateSectionProgress()
                onRatedListener(entry)
            }
        }
    }

    fun removeEntry(entry: PracticeEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                practiceEntryRepository.delete(entry)
                updateSectionProgress()
            }
        }
    }

    fun setSection(newSection: Section) {
        persistPreviousBpmAndRhythm()
        this.section.postValue(newSection)
        setRhythm(newSection.previousRhythmId)
        minBpm.value = newSection.initialTempo
        maxBpm.value = newSection.goalTempo
        bpm.value = newSection.previousBpm
    }

    fun persistPreviousBpmAndRhythm() {
        section.value?.let {
            val bpm = bpm.value!!
            val rhythm = rhythm.value
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val rhythmId = rhythm?.rhythmId.let { if (it == 0L) null else it }
                    sectionRepository.previousBpmAndRhythmUpdate(
                        SectionPreviousBpmAndRhythmUpdate(it.sectionId, bpm, rhythmId)
                    )
                }
            }
        }
    }

    private fun updateSectionProgress() {
        val section = section.value!!
        val entries = practiceEntryRepository.getBySectionIdBlocking(section.sectionId)
        val progress = (PracticeEntry.calculateProgress(entries, section.initialTempo, section.goalTempo)*100).toInt()
        sectionRepository.progressUpdate(SectionProgressUpdate(section.sectionId, progress))
    }

}