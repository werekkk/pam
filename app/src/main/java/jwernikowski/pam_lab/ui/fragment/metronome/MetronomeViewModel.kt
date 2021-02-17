package jwernikowski.pam_lab.ui.fragment.metronome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.repository.RhythmLineRepository
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.dto.RhythmLineDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class MetronomeViewModel : ViewModel() {

    companion object {
        const val MAX_BPM = 300
        const val MIN_BPM = 10
    }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var rhythmRepository: RhythmRepository
    @Inject
    lateinit var rhythmLineRepository: RhythmLineRepository

    private val _bpm: MutableLiveData<Int> = MutableLiveData()
    val isOn: MutableLiveData<Boolean> = MutableLiveData(false)
    val rhythms: LiveData<List<Rhythm>> = rhythmRepository.getAll()

    var rhythm: Rhythm = Rhythm.DEFAULT_RHYTHM
    set(value) {
        field = value
        _bpm.value = value.defaultBpm
        if (value == Rhythm.DEFAULT_RHYTHM)
            chosenRhythmLines.value = RhythmLineDto.DEFAULT_RHYTHM_LINES
        else {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val newLines = RhythmLineDto.fromRhythmLines(rhythmLineRepository.getByRhythmId(value.rhythmId))
                    withContext(Dispatchers.Main) {
                        chosenRhythmLines.value = newLines
                    }
                }
            }
        }
    }
    val chosenRhythmLines: MutableLiveData<List<RhythmLineDto>> = MutableLiveData()


    val bpm: MutableLiveData<Int>
    get() = _bpm

    init {
        _bpm.value = 140
        isOn.value = false
        chosenRhythmLines.value = RhythmLineDto.DEFAULT_RHYTHM_LINES
    }

    fun increaseBpm(value: Int) {
        _bpm.value?.let {
            val newBpm = it + value
            if (newBpm < MIN_BPM)
                _bpm.value =
                    MIN_BPM
            else if (newBpm > MAX_BPM)
                _bpm.value =
                    MAX_BPM
            else
                _bpm.value = newBpm
        }
    }

}