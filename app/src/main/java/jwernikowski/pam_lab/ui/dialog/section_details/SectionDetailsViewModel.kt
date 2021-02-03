package jwernikowski.pam_lab.ui.dialog.section_details

import androidx.lifecycle.*
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.repository.PracticeEntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SectionDetailsViewModel : ViewModel() {

    private val _section: MutableLiveData<Section> = MutableLiveData()

    val section: LiveData<Section> = _section

    val practiceEntries: LiveData<List<PracticeEntry>> =
        Transformations.switchMap(section) {
            practiceEntryRepository.getBySectionId(it.sectionId)
        }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var practiceEntryRepository: PracticeEntryRepository

    fun setSection(newSection: Section) {
        _section.postValue(newSection)
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

}