package jwernikowski.pam_lab.ui.dialog.section_details

import androidx.lifecycle.*
import jwernikowski.pam_lab.db.data.dao.SectionDao
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.helper_entity.SectionProgressUpdate
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import jwernikowski.pam_lab.db.repository.SectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SectionDetailsViewModel : ViewModel() {

    private val currentSectionId: MutableLiveData<Long> = MutableLiveData()

    val section: LiveData<Section> =
        Transformations.switchMap(currentSectionId) {
            sectionRepository.getBySectionId(it)
        }

    val practiceEntries: LiveData<List<PracticeEntry>> =
        Transformations.switchMap(section) {
            practiceEntriesLoaded.postValue(false)
            val entries = practiceEntryRepository.getBySectionId(it.sectionId)
            practiceEntriesLoaded.postValue(true)
            entries
        }

    val practiceEntriesLoaded = MutableLiveData(false)

    val progress: LiveData<Int> =
        Transformations.map(practiceEntries) { e ->
            section.value?.let {
                (PracticeEntry.calculateProgress(e, it.initialTempo, it.goalTempo)*100).toInt()
            } ?: 0
        }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository

    @Inject
    lateinit var sectionRepository: SectionRepository

    fun setSection(newSection: Section) {
        currentSectionId.postValue(newSection.sectionId)
    }

    fun deletePracticeEntry(practiceEntry: PracticeEntry) {
        practiceEntryRepository.delete(practiceEntry)
    }

    fun restorePracticeEntry(entry: PracticeEntry) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                practiceEntryRepository.add(entry)
            }
        }
    }

    fun handleDeleteSection() {
        section.value?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    sectionRepository.delete(it)
                }
            }
        }
    }

    fun updateProgress() {
        val section = section.value
        val progress = progress.value ?: 0
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                section?.let {
                    sectionRepository.progressUpdate(
                        SectionProgressUpdate(it.sectionId, progress)
                    )
                }
            }
        }
    }

}