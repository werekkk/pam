package jwernikowski.pam_lab.ui.dialog.section_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.repository.SectionRepository
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.utils.Tempo
import jwernikowski.pam_lab.utils.checkValidators
import jwernikowski.pam_lab.utils.validateLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditSectionViewModel : ViewModel() {

    val section = MutableLiveData<Section>()
    val sectionName: MutableLiveData<String> = MutableLiveData()
    val initialTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_INITIAL)
    val goalTempo: MutableLiveData<Int> = MutableLiveData(Tempo.DEFAULT_GOAL)

    val sectionNameNotEmpty = validateLiveData(sectionName, { Section.isSectionNameValid(it) })
    val initialTempoInRange = validateLiveData(initialTempo, { Tempo.isTempoValid(it) })
    val goalTempoInRange = validateLiveData(goalTempo, { Tempo.isTempoValid(it)} )
    val initialTempoSmallerThanGoal = validateLiveData(goalTempo, {initialTempo.value == null || initialTempo.value!! <= it})

    private val addSectionValidators = arrayOf(sectionNameNotEmpty, initialTempoInRange, goalTempoInRange, initialTempoInRange)

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var sectionRepository: SectionRepository

    fun setSection(section: Section) {
        this.section.postValue(section)
        this.sectionName.postValue(section.name)
        this.initialTempo.postValue(section.initialTempo)
        this.goalTempo.postValue(section.goalTempo)
    }

    fun handleSave() {
        if (isDataValid()) {
            val updatedSection = enteredSectionData()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    sectionRepository.update(updatedSection)
                }
            }
        }
    }

    private fun enteredSectionData(): Section {
        val updatedSection = section.value!!.copy(
            name = sectionName.value!!,
            initialTempo = initialTempo.value!!,
            goalTempo = goalTempo.value!!
        )
        return updatedSection
    }

    private fun isDataValid(): Boolean {
        return checkValidators(addSectionValidators)
    }

}