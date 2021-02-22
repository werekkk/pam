package jwer.pam.ui.fragment.metronome

import jwer.pam.ui.activity.MainActivity
import jwer.pam.db.data.entity.Rhythm
import jwer.pam.db.repository.SharedPreferencesRepository
import jwer.pam.ui.fragment.shared.MetronomeViewViewModel
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

    fun setRhythm(rhythm: Rhythm) {
        super.setRhythm(rhythm.rhythmId)
        bpm.value = rhythm.defaultBpm
    }

    fun persistPreviousTempoAndRhythm() {
        sharedPreferencesRepository.previousTempo = bpm.value
        val previousRhythmId = rhythm.value?.rhythmId.let { if (it == 0L) null else it }
        sharedPreferencesRepository.previousRhythmId = previousRhythmId
    }

}