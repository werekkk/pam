package jwernikowski.pam_lab.ui.fragment.metronome

import androidx.lifecycle.*
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.repository.RhythmLineRepository
import jwernikowski.pam_lab.db.repository.RhythmRepository
import jwernikowski.pam_lab.db.repository.SharedPreferencesRepository
import jwernikowski.pam_lab.dto.RhythmLineDto
import jwernikowski.pam_lab.ui.fragment.shared.MetronomeViewViewModel
import javax.inject.Inject

open class MetronomeViewModel : MetronomeViewViewModel() {

    companion object {
        const val MAX_BPM = 300
        const val MIN_BPM = 10
    }

    init {
        MainActivity.component.inject(this)
        initViewModel(
            sharedPreferencesRepository.previousTempo,
            sharedPreferencesRepository.previousRhythmId
        )
    }

    @Inject
    lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    override fun setRhythm(rhythm: Rhythm) {
        super.setRhythm(rhythm)
        bpm.value = rhythm.defaultBpm
    }

    fun persistPreviousTempoAndRhythm() {
        sharedPreferencesRepository.previousTempo = bpm.value
        val previousRhythmId = rhythm.value!!.rhythmId.let { if (it == 0L) null else it }
        sharedPreferencesRepository.previousRhythmId = previousRhythmId
    }

}