package jwernikowski.pam_lab.ui.fragment.metronome_practice


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import jwernikowski.pam_lab.ui.activity.MainActivity

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.FragmentMetronomePracticeBinding
import jwernikowski.pam_lab.db.data.entity.PracticeEntry
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.sound.Sound
import jwernikowski.pam_lab.sound.SoundPlayer
import jwernikowski.pam_lab.ui.dialog.rhythm_change.ChangeRhythmDialogFragment
import jwernikowski.pam_lab.ui.fragment.current_rhythm_info.CurrentRhythmInfoFragment

class MetronomePracticeFragment : Fragment() {

    private lateinit var binding: FragmentMetronomePracticeBinding
    private lateinit var viewModel: MetronomePracticeViewModel

    private lateinit var player: SoundPlayer

    companion object {
        const val TAG = "metronome_practice_fragment"
    }

    fun setSong(song: Song) {
        viewModel.song.postValue(song)
    }

    fun setSection(section: Section) {
        viewModel.setSection(section)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        player = MainActivity.component.soundPlayer

        viewModel = ViewModelProvider(this).get(MetronomePracticeViewModel::class.java)
        viewModel.init(this)

        binding = FragmentMetronomePracticeBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.setRhythmBtn.setOnClickListener { onChangeRhythmClicked() }
        observeViewModel()
        initMetronomeView()
        initSeekBar()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.metronomeView.shutdown()
    }

    override fun onPause() {
        super.onPause()
        viewModel.persistPreviousBpmAndRhythm()
    }

    private fun observeViewModel() {
        val currentRhythmInfo = childFragmentManager.findFragmentById(R.id.current_rhythm_info)
        currentRhythmInfo as CurrentRhythmInfoFragment
        viewModel.onRatedListener = { undoSnackbar(it) }
        viewModel.bpm.observe(viewLifecycleOwner) {
            binding.tempoSeekBar.progress = (100 * ((it - viewModel.minBpm.value!!).toFloat() / (viewModel.maxBpm.value!! - viewModel.minBpm.value!!))).toInt()
        }
        viewModel.rhythm.observe(viewLifecycleOwner) {
            binding.metronomeView.rhythm = it ?: Rhythm.DEFAULT_RHYTHM
            currentRhythmInfo.onNewRhythm(it)
        }
        viewModel.chosenRhythmLines.observe(viewLifecycleOwner) {}
    }

    private fun initMetronomeView() {
        binding.metronomeView.setOnClickListener { viewModel.isOn.apply { value = !value!! } }

        binding.metronomeView.onMetronomeTickListener = { index ->
            viewModel.chosenRhythmLines.value?.let {
                player.playRhythmLines(it, index)
            }
        }

        viewModel.isOn.observe(viewLifecycleOwner) {
            binding.metronomeView.apply { if (it) turnOn() else turnOff() }
        }
        viewModel.bpm.observe(viewLifecycleOwner) { binding.metronomeView.bpm = it }
    }

    private fun initSeekBar() {
        binding.tempoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.bpm.value = ((viewModel.maxBpm.value!! - viewModel.minBpm.value!!) * (progress.toFloat() / 100) + viewModel.minBpm.value!!).toInt()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
    }

    private fun undoSnackbar(entry: PracticeEntry) {
        Snackbar.make(binding.layout, R.string.entry_added, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { viewModel.removeEntry(entry) }
            .show()
    }

    private fun onChangeRhythmClicked() {
        ChangeRhythmDialogFragment({ setRhythm(it) }, viewModel.rhythmId.value ?: 0)
            .show(parentFragmentManager, ChangeRhythmDialogFragment.TAG)
    }

    private fun setRhythm(rhythm: Rhythm) {
        viewModel.setRhythm(rhythm.rhythmId)
    }

}
