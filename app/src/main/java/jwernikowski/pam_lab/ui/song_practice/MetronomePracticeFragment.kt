package jwernikowski.pam_lab.ui.song_practice


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import jwernikowski.pam_lab.MainActivity

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.PracticeEntry
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.sound.Sound
import jwernikowski.pam_lab.sound.SoundPlayer
import jwernikowski.pam_lab.ui.metronome.MetronomeView

class MetronomePracticeFragment : Fragment() {

    private lateinit var song: Song

    private lateinit var player: SoundPlayer

    private lateinit var viewModel: SongPracticeViewModel
    private lateinit var layout: View
    private lateinit var bpmText: TextView
    private lateinit var metronomeView: MetronomeView
    private lateinit var tempoSeekBar: SeekBar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        player = MainActivity.component.soundPlayer
        viewModel = ViewModelProviders.of(this).get(SongPracticeViewModel::class.java)

        viewModel.onRatedListener = {entry -> run{
            undoSnackbar(entry)
        }}
        observeViewModel()
        initMetronomeView()
    }

    fun setSong(song: Song) {
        this.song = song
        viewModel.song.value = song
        viewModel.maxBpm.value = song.goalTempo
        viewModel.minBpm.value = song.initialTempo
    }

    fun setBpm(bpm: Int) {
        viewModel.bpm.value = bpm
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_metronome_practice, container, false)

        bpmText = root.findViewById(R.id.bpm)
        metronomeView = root.findViewById(R.id.metronomeView)
        layout = root.findViewById(R.id.layout)
        tempoSeekBar = root.findViewById(R.id.tempoSeekBar)

        initTempoButtons(root)
        initRatingButtons(root)
        initSeekBar()
        return root
    }

    private fun observeViewModel() {
        viewModel.bpm.observe(this, Observer {
            bpmText.text = it.toString()
            if (viewModel.minBpm.value != null && viewModel.maxBpm.value != null)
            tempoSeekBar.progress = (100 * ((it - viewModel.minBpm.value!!).toFloat() / (viewModel.maxBpm.value!! - viewModel.minBpm.value!!))).toInt()
        })
    }

    private fun initMetronomeView() {
        metronomeView.setOnClickListener {
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

        metronomeView.onMetronomeTickListener = {
            player.play(Sound.WOOD)
        }

        viewModel.isOn.observe(this, Observer { isOn -> run{
            if (isOn)
                metronomeView.turnOn()
            else
                metronomeView.turnOff()
        } })
        viewModel.bpm.observe(this, Observer { bpm -> metronomeView.bpm = bpm })
    }

    private fun initTempoButtons(root: View) {
        root.findViewById<Button>(R.id.plus1btn).setOnClickListener { viewModel.increaseBpm(1) }
        root.findViewById<Button>(R.id.plus4btn).setOnClickListener { viewModel.increaseBpm(4) }
        root.findViewById<Button>(R.id.minus1btn).setOnClickListener { viewModel.increaseBpm(-1) }
        root.findViewById<Button>(R.id.minus4btn).setOnClickListener { viewModel.increaseBpm(-4) }
    }

    private fun initRatingButtons(root: View) {
        root.findViewById<ImageButton>(R.id.rate1).setOnClickListener { viewModel.addRating(PracticeEntry.Rating.VERY_LOW) }
        root.findViewById<ImageButton>(R.id.rate2).setOnClickListener { viewModel.addRating(PracticeEntry.Rating.LOW) }
        root.findViewById<ImageButton>(R.id.rate3).setOnClickListener { viewModel.addRating(PracticeEntry.Rating.MEDIUM) }
        root.findViewById<ImageButton>(R.id.rate4).setOnClickListener { viewModel.addRating(PracticeEntry.Rating.HIGH) }
        root.findViewById<ImageButton>(R.id.rate5).setOnClickListener { viewModel.addRating(PracticeEntry.Rating.VERY_HIGH) }
    }

    private fun initSeekBar() {
        tempoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.bpm.value = ((viewModel.maxBpm.value!! - viewModel.minBpm.value!!) * (progress.toFloat() / 100) + viewModel.minBpm.value!!).toInt()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { }

        })
    }

    fun undoSnackbar(entry: PracticeEntry) {
        val sb = Snackbar.make(layout, R.string.entry_added, Snackbar.LENGTH_LONG)
        sb.setAction(R.string.undo, {_ -> run{
            viewModel.removeEntry(entry)
        }})
        sb.show()
    }

}
