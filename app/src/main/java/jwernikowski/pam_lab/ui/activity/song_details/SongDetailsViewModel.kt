package jwernikowski.pam_lab.ui.activity.song_details

import androidx.lifecycle.*
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.db.repository.SectionRepository
import jwernikowski.pam_lab.db.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongDetailsViewModel : ViewModel() {

    val songId: MutableLiveData<Long> = MutableLiveData()

    val songLoaded = MutableLiveData(false)
    val sectionsLoaded = MutableLiveData(false)
    val practiceEntriesLoaded = MutableLiveData(false)
    val daysPracticedLoaded = MutableLiveData(false)
    val progressLoaded = MutableLiveData(false)

    val song: LiveData<Song?> =
        Transformations.switchMap(songId) {
            songLoaded.postValue(false)
            val loadedSong = songsRepository.getById(it)
            songLoaded.postValue(true)
            loadedSong
        }

    val sections: LiveData<List<Section>> =
        Transformations.switchMap(song) {
            it?.let {
                sectionsLoaded.postValue(false)
                val sections = sectionRepository.getBySongId(it.songId)
                sectionsLoaded.postValue(true)
                sections
            }
        }

    val practiceEntries: LiveData<List<PracticeEntry>> =
        Transformations.switchMap(song) {
            it?.let {
                practiceEntriesLoaded.postValue(false)
                daysPracticedLoaded.postValue(false)
                progressLoaded.postValue(false)
                val entries = practiceEntryRepository.getBySongId(it.songId)
                practiceEntriesLoaded.postValue(true)
                entries
            }
        }

    val daysPracticed: LiveData<Int> =
        Transformations.map(practiceEntries) {
            daysPracticedLoaded.postValue(false)
            val daysPracticed = PracticeEntry.calculateDaysPracticed(it)
            daysPracticedLoaded.postValue(true)
            daysPracticed
        }

    val progress: LiveData<Float> =
        Transformations.switchMap(practiceEntries) { entries ->
            Transformations.map(sections) { sections -> run{
                progressLoaded.postValue(false)
                val sectionBySectionId = sections.associateBy({it.sectionId}, {it})
                val entriesBySectionId = entries.groupBy { it.sectionId }
                val progressBySectionId = sectionBySectionId.mapValues {
                    val entries = entriesBySectionId[it.key] ?: listOf()
                    PracticeEntry.calculateProgress(entries, it.value.initialTempo, it.value.goalTempo)
                }
                val avgProgress = progressBySectionId.toList().fold(0.0f, {acc, pair -> acc + pair.second }) / sections.size
                progressLoaded.postValue(true)
                avgProgress
            }
        }
        }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var songsRepository: SongRepository
    @Inject
    lateinit var sectionRepository: SectionRepository
    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository

    fun setSong(song: Song) {
        songId.postValue(song.songId)
    }

    fun deleteSong() {
        song.value?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    songsRepository.delete(it)
                }
            }
        }
    }

    fun deletePracticeEntry(entry: PracticeEntry) {
        practiceEntryRepository.delete(entry)
    }

    fun restorePracticeEntry(entry: PracticeEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                practiceEntryRepository.add(entry)
            }
        }
    }

    fun reorderSections(reorderedSections: List<Section>) {
        val updatedOrder = reorderedSections.mapIndexed { i, s -> s.copy(order = i+1)}
        sectionsLoaded.postValue(false)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sectionRepository.update(updatedOrder)
                sectionsLoaded.postValue(true)
            }
        }
    }

}
