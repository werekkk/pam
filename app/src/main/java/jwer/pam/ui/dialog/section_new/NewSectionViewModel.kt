package jwer.pam.ui.dialog.section_new

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jwer.pam.ui.activity.MainActivity
import jwer.pam.db.data.entity.Section
import jwer.pam.db.data.entity.Song
import jwer.pam.db.repository.SectionRepository
import jwer.pam.utils.Tempo
import jwer.pam.utils.checkValidators
import jwer.pam.utils.validateLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewSectionViewModel : ViewModel() {

    private val _dismissView: MutableLiveData<Unit> = MutableLiveData()

    val song: MutableLiveData<Song> = MutableLiveData()
    val existingSections: MutableLiveData<List<Section>> = MutableLiveData()

    val sectionName: MutableLiveData<String> = MutableLiveData()
    val initialTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_INITIAL)
    val goalTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_GOAL)
    val dismissView: LiveData<Unit> = _dismissView

    val sectionNameNotEmpty = validateLiveData(sectionName) { Section.isSectionNameValid(it) }
    val initialTempoInRange = validateLiveData(initialTempo) { Tempo.isTempoValid(it) }
    val goalTempoInRange = validateLiveData(goalTempo) { Tempo.isTempoValid(it)}
    val initialTempoSmallerThanGoal = validateLiveData(goalTempo) {initialTempo.value == null || initialTempo.value!! <= it}

    private val addSectionValidators = arrayOf(sectionNameNotEmpty, initialTempoInRange, goalTempoInRange, initialTempoSmallerThanGoal)

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var sectionRepository: SectionRepository

    fun onAddSection() {
        if (isDataValid()) {
            val newSection = enteredSectionData()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    sectionRepository.add(newSection)
                    _dismissView.postValue(null)
                }
            }
        }
    }

    private fun enteredSectionData(): Section {
        val section = Section(
            sectionName.value!!,
            initialTempo.value!!,
            goalTempo.value!!,
            lastSectionOrderNumber() + 1
        )
        section.songId = song.value!!.songId
        return section
    }

    private fun lastSectionOrderNumber(): Int {
        return existingSections.value!!.let {
            return if (it.isEmpty())
                0
            else it.last().order
        }
    }

    private fun isDataValid(): Boolean {
        return checkValidators(addSectionValidators)
    }

    fun onCancel() {
        _dismissView.postValue(null)
    }

}