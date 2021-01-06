package jwernikowski.pam_lab.ui.song_practice


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import jwernikowski.pam_lab.MainActivity

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.FragmentMetronomePracticeBinding
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.sound.Sound
import jwernikowski.pam_lab.sound.SoundPlayer

class MetronomePracticeFragment : Fragment() {

    private lateinit var binding: FragmentMetronomePracticeBinding
    private lateinit var viewModel: MetronomePracticeViewModel

    private lateinit var player: SoundPlayer

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
        viewModel.lastPracticeEntry.observe(viewLifecycleOwner, Observer {
            viewModel.bpm.postValue(it?.tempo ?: viewModel.section.value!!.initialTempo)
        })
        viewModel.bpm.observe(viewLifecycleOwner, Observer {
            binding.bpm.text = it.toString()
            if (viewModel.minBpm.value != null && viewModel.maxBpm.value != null)
            binding.tempoSeekBar.progress = (100 * ((it - viewModel.minBpm.value!!).toFloat() / (viewModel.maxBpm.value!! - viewModel.minBpm.value!!))).toInt()
        })
    }

    private fun initMetronomeView() {
        binding.metronomeView.setOnClickListener {
            run{
            viewModel.isOn.let { isOn ->
                isOn.value.let { value ->
                    if (value != null) {
                        isOn.value = !value
                    } else {
                        isOn.value = true
                    }
                }
            }
        } }

        binding.metronomeView.onMetronomeTickListener = {
            player.play(Sound.WOOD)
        }

        viewModel.isOn.observe(viewLifecycleOwner, Observer { isOn -> run{
            if (isOn)
                binding.metronomeView.turnOn()
            else
                binding.metronomeView.turnOff()
        } })
        viewModel.bpm.observe(viewLifecycleOwner, Observer { bpm -> binding.metronomeView.bpm = bpm })
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
        val sb = Snackbar.make(binding.layout, R.string.entry_added, Snackbar.LENGTH_LONG)
        sb.setAction(R.string.undo) {
            viewModel.removeEntry(entry)
        }
        sb.show()
    }

}
