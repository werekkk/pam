package jwernikowski.pam_lab.ui.fragment.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.repository.RhythmLineRepository
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.dto.RhythmLineDto
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.utils.Tempo
import javax.inject.Inject

open class MetronomeViewViewModel : ViewModel() {

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var rhythmRepository: RhythmRepository
    @Inject
    lateinit var rhythmLineRepository: RhythmLineRepository

    val isOn: MutableLiveData<Boolean> = MutableLiveData(false)
    val bpm: MutableLiveData<Int> = MutableLiveData()
    val rhythmId: MutableLiveData<Long?> = MutableLiveData()

    val rhythm = Transformations.switchMap(rhythmId) {
        it?.let { newId ->
            Transformations.map(rhythmRepository.getById(newId)) {
                if (it == null) rhythmId.postValue(null)
                it
            }
        } ?: MutableLiveData(Rhythm.DEFAULT_RHYTHM)
    }

    val rhythms: LiveData<List<Rhythm>> = rhythmRepository.getAll()

    val chosenRhythmLines = Transformations.switchMap(rhythm) {
        it?.let {
            if (it == Rhythm.DEFAULT_RHYTHM) {
                MutableLiveData(RhythmLineDto.DEFAULT_RHYTHM_LINES)
            } else {
                Transformations.map(rhythmLineRepository.getByRhythmId(it.rhythmId)) {
                    RhythmLineDto.fromRhythmLines(it)
                }
            }
        } ?: MutableLiveData(RhythmLineDto.DEFAULT_RHYTHM_LINES)
    }

    protected fun initViewModel(bpm: Int?, rhythmId: Long?) {
        this.bpm.value = bpm ?: 140
        this.rhythmId.value = rhythmId
    }

    open fun setRhythm(rhythmId: Long?) {
        this.rhythmId.value = rhythmId
    }

    fun increaseBpm(value: Int) {
        bpm.value?.let {
            val newBpm = coerceBpm(it + value)
            bpm.value = newBpm
        }
    }

    open fun coerceBpm(newBpm: Int): Int {
        return newBpm.coerceIn(Tempo.MIN_BPM, Tempo.MAX_BPM)
    }

}