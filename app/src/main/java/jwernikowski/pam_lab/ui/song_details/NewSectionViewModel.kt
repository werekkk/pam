package jwernikowski.pam_lab.ui.song_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.repository.SectionRepository
import jwernikowski.pam_lab.utils.Tempo
import jwernikowski.pam_lab.utils.checkValidators
import jwernikowski.pam_lab.utils.validateLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewSectionViewModel : ViewModel() {

    private val _dismissView: MutableLiveData<Unit> = MutableLiveData()
    private var saving = false

    val song: MutableLiveData<Song> = MutableLiveData()
    val existingSections: MutableLiveData<List<Section>> = MutableLiveData()

    val sectionName: MutableLiveData<String> = MutableLiveData()
    val initialTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_INITIAL)
    val goalTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_GOAL)
    val dismissView: LiveData<Unit> = _dismissView

    val sectionNameNotEmpty = validateLiveData(sectionName, ::isSectionNameValid)
    val initialTempoInRange = validateLiveData(initialTempo, ::isTempoValid)
    val goalTempoInRange = validateLiveData(goalTempo, ::isTempoValid)
    val initialTempoSmallerThanGoal = validateLiveData(goalTempo, {initialTempo.value == null || initialTempo.value!! <= it})

    private val addSectionValidators = arrayOf(sectionNameNotEmpty, initialTempoInRange, goalTempoInRange, initialTempoInRange)

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var sectionRepository: SectionRepository

    fun onAddSection() {
        if (isDataValid() && !saving) {
            saving = true
            val newSection = enteredSectionData()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    sectionRepository.add(newSection)
                    _dismissView.postValue(null)
                    saving = false
                }
            }
        }
    }

    private fun enteredSectionData(): Section {
        val section = Section(sectionName.value!!, initialTempo.value!!, goalTempo.value!!, lastSectionOrderNumber() + 1)
        section.songId = song.value!!.songId
        return section
    }

    private fun lastSectionOrderNumber(): Int {
        return existingSections.value!!.let {
            return if (it.isEmpty())
                1
            else it.last().order + 1
        }
    }

    private fun isDataValid(): Boolean {
        return checkValidators(addSectionValidators)
    }

    fun onCancel() {
        _dismissView.postValue(null)
    }

    private fun isSectionNameValid(sectionName: String?): Boolean {
        return sectionName != null && sectionName.isNotEmpty()
    }

    private fun isTempoValid(tempo: Int): Boolean {
        return tempo >= Tempo.MIN_BPM && tempo <= Tempo.MAX_BPM
    }

}