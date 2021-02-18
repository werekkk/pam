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
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.sound.Sound
import jwernikowski.pam_lab.sound.SoundPlayer

class MetronomePracticeFragment : Fragment() {

    private lateinit var binding: FragmentMetronomePracticeBinding
    private lateinit var viewModel: MetronomePracticeViewModel

    private lateinit var player: SoundPlayer

    companion object {
        const val TAG = "metronome_practice_fragment"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        player = MainActivity.component.soundPlayer

        viewModel.onRatedListener = {entry -> run{
            undoSnackbar(entry)
        }}
        observeViewModel()
        initMetronomeView()
    }

    fun setSong(song: Song) {
        viewModel.song.postValue(song)
    }

    fun setSection(section: Section) {
        viewModel.section.postValue(section)
        viewModel.minBpm.value = section.initialTempo
        viewModel.maxBpm.value = section.goalTempo
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMetronomePracticeBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this).get(MetronomePracticeViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initSeekBar()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.metronomeView.shutdown()
    }

    private fun observeViewModel() {
        viewModel.lastPracticeEntry.observe(viewLifecycleOwner) {
            viewModel.bpm.postValue(it?.tempo ?: viewModel.section.value!!.initialTempo)
        }
        viewModel.bpm.observe(viewLifecycleOwner) {
            binding.bpm.text = it.toString()
            if (viewModel.minBpm.value != null && viewModel.maxBpm.value != null)
            binding.tempoSeekBar.progress = (100 * ((it - viewModel.minBpm.value!!).toFloat() / (viewModel.maxBpm.value!! - viewModel.minBpm.value!!))).toInt()
        }
    }

    private fun initMetronomeView() {
        binding.metronomeView.setOnClickListener { viewModel.isOn.apply { value = !value!! } }
        binding.metronomeView.onMetronomeTickListener = { player.play(Sound.WOOD) }

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

}
