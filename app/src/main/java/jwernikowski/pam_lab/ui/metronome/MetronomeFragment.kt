package jwernikowski.pam_lab.ui.metronome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.rhythm.Rhythm
import jwernikowski.pam_lab.sound.Sound
import jwernikowski.pam_lab.sound.SoundPlayer


open class MetronomeFragment : Fragment() {

    private lateinit var viewModel: MetronomeViewModel

    private lateinit var bpmText: TextView
    private lateinit var metronomeView: MetronomeView
    private lateinit var tempoSeekBar: SeekBar

    private lateinit var player: SoundPlayer

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(MetronomeViewModel::class.java)
        player = MainActivity.component.soundPlayer
        observeViewModel()
        initMetronomeView()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.metronome_fragment, container, false)

        bpmText = root.findViewById(R.id.bpm)
        metronomeView = root.findViewById(R.id.metronomeView)
        tempoSeekBar = root.findViewById(R.id.tempoSeekBar)

        initButtons(root)
        initSeekBar()
        return root
    }

    fun setRhythm(rhythm: Rhythm) {
        viewModel.rhythm = rhythm
        metronomeView.rhythm = rhythm
    }

    private fun observeViewModel() {
        viewModel.bpm.observe(this, Observer {
            bpmText.text = it.toString()
            tempoSeekBar.progress = (100 * ((it - MetronomeViewModel.MIN_BPM).toFloat() / (MetronomeViewModel.MAX_BPM - MetronomeViewModel.MIN_BPM))).toInt()
        })
    }

    private fun initMetronomeView() {
        metronomeView.setOnClickListener { view -> run{
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

        metronomeView.onMetronomeTickListener = { index ->
            viewModel.chosenRhythmLines.value?.let {
                player.playRhythmLines(it, index)
            }
        }

        viewModel.isOn.observe(this, Observer { isOn -> run{
            if (isOn)
                metronomeView.turnOn()
            else
                metronomeView.turnOff()
        } })
        viewModel.bpm.observe(this, Observer { bpm -> metronomeView.bpm = bpm })
    }

    private fun initButtons(root: View) {
        root.findViewById<Button>(R.id.plus1btn).setOnClickListener { viewModel.increaseBpm(1) }
        root.findViewById<Button>(R.id.plus4btn).setOnClickListener { viewModel.increaseBpm(4) }
        root.findViewById<Button>(R.id.minus1btn).setOnClickListener { viewModel.increaseBpm(-1) }
        root.findViewById<Button>(R.id.minus4btn).setOnClickListener { viewModel.increaseBpm(-4) }
        root.findViewById<Button>(R.id.changeRhythmBtn).setOnClickListener { onChangeRhythmClicked() }
    }

    private fun initSeekBar() {
        tempoSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
        ChangeRhythmDialogFragment(this).show(fragmentManager, ChangeRhythmDialogFragment.TAG)
    }

}