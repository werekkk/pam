package jwernikowski.pam_lab.ui.activity.rhythm_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.data.entity.RhythmLine
import jwernikowski.pam_lab.db.repository.RhythmLineRepository
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.dto.RhythmDto
import jwernikowski.pam_lab.dto.RhythmLineDto
import jwernikowski.pam_lab.utils.Tempo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RhythmDetailsViewModel : ViewModel() {

    companion object {
        val MAX_BPM = Tempo.MAX_BPM
        val MIN_BPM = Tempo.MIN_BPM
    }

    init {
        MainActivity.component.inject(this)
    }

    @Inject
    lateinit var rhythmRepository: RhythmRepository
    @Inject
    lateinit var rhythmLineRepository: RhythmLineRepository

    val rhythmId: MutableLiveData<Int> = MutableLiveData()

    val rhythmDto: MutableLiveData<RhythmDto> = MutableLiveData()
    val rhythmLinesDto: MutableLiveData<List<RhythmLineDto>> = MutableLiveData()

    val tempo: MutableLiveData<Int> = MutableLiveData()

    private val _meter: MutableLiveData<Meter> = MutableLiveData()
    val meter : LiveData<Meter>
        get() = _meter

    fun updateMeter(measureChange: Int, lengthChange: Int) {
        _meter.value?.let {
            val newMeter = Meter(it.measure + measureChange, it.length + lengthChange)
            newMeter.truncate()
            _meter.value = newMeter
            rhythmLinesDto.value = RhythmLineDto.default(newMeter)
        }
    }

    fun newRhythm() {
        rhythmDto.value = RhythmDto()
        rhythmLinesDto.value = RhythmLineDto.default(Meter.default())
        tempo.value = rhythmDto.value!!.defaultBpm
        _meter.value = Meter.default()
    }

    fun loadRhythm(rhythmId: Long) {
        viewModelScope.launch {
            lateinit var rhythm: Rhythm
            lateinit var rhythmLines: List<RhythmLine>
            withContext(Dispatchers.IO) {
                rhythm = rhythmRepository.getById(rhythmId)
                rhythmLines = rhythmLineRepository.getByRhythmId(rhythmId)
                withContext(Dispatchers.Main) {
                    rhythmDto.value = RhythmDto(rhythm!!)
                    tempo.value = rhythmDto.value!!.defaultBpm
                    _meter.value = rhythmDto.value!!.meter
                    rhythmLinesDto.value = RhythmLineDto.fromRhythmLines(rhythmLines!!)
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                rhythmDto.value?.let {
                    rhythmLinesDto.value?.let { lines -> run{
                        var id: Long?
                        if (it.rhythmId == null) {
                            id = rhythmRepository.add(
                                Rhythm(
                                    it.name,
                                    it.meter,
                                    it.defaultBpm
                                )
                            )
                        } else {
                            val rhythm =
                                Rhythm(
                                    it.name,
                                    it.meter,
                                    it.defaultBpm
                                )
                            id = it.rhythmId!!
                            rhythm.rhythmId = id
                            rhythmRepository.update(rhythm)
                        }
                        updateLines(id, lines)
                    }
                    }
                }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                rhythmDto.value?.let {
                    val rhythm = Rhythm(
                        it.name,
                        it.meter,
                        it.defaultBpm
                    )
                    rhythm.rhythmId = it.rhythmId!!
                    rhythmRepository.delete(rhythm)
                }
            }
        }
    }

    private fun updateLines(rhythmId: Long, dtoLines: List<RhythmLineDto>) {
        rhythmLineRepository.deleteByRhythmId(rhythmId)
        val lines = dtoLines.map { line ->
            RhythmLine(
                rhythmId,
                line.sound,
                line.beats.clone()
            )
        }
        rhythmLineRepository.addAll(*lines.toTypedArray())
    }
}