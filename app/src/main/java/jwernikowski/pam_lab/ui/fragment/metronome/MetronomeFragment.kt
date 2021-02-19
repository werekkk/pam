package jwernikowski.pam_lab.ui.fragment.metronome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import jwernikowski.pam_lab.ui.activity.MainActivity
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.FragmentMetronomeBinding
import jwernikowski.pam_lab.db.data.entity.Rhythm
import jwernikowski.pam_lab.sound.SoundPlayer
import jwernikowski.pam_lab.ui.dialog.rhythm_change.ChangeRhythmDialogFragment
import jwernikowski.pam_lab.ui.view.metronome.MetronomeView


class MetronomeFragment : Fragment() {

    private lateinit var binding: FragmentMetronomeBinding
    private lateinit var viewModel: MetronomeViewModel

    private lateinit var player: SoundPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        player = MainActivity.component.soundPlayer

        viewModel = ViewModelProvider(this).get(MetronomeViewModel::class.java)

        binding = FragmentMetronomeBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.changeRhythmBtn.setOnClickListener { onChangeRhythmClicked() }
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
        viewModel.persistPreviousTempoAndRhythm()
    }

    private fun observeViewModel() {
        viewModel.bpm.observe(viewLifecycleOwner) {
            binding.tempoSeekBar.progress = (100 * ((it - MetronomeViewModel.MIN_BPM).toFloat() / (MetronomeViewModel.MAX_BPM - MetronomeViewModel.MIN_BPM))).toInt()
        }
        viewModel.rhythm.observe(viewLifecycleOwner) { binding.metronomeView.rhythm = it ?: Rhythm.DEFAULT_RHYTHM }
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
        binding.tempoSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.bpm.value = ((MetronomeViewModel.MAX_BPM - MetronomeViewModel.MIN_BPM) * (progress.toFloat() / 100) + MetronomeViewModel.MIN_BPM).toInt()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
    }

    private fun onChangeRhythmClicked() {
        ChangeRhythmDialogFragment { setRhythm(it) }
            .show(parentFragmentManager, ChangeRhythmDialogFragment.TAG)
    }

    private fun setRhythm(rhythm: Rhythm) {
        viewModel.setRhythm(rhythm)
    }

}