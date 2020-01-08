package jwernikowski.pam_lab.ui.rhythm_details

import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.models.RhythmDto
import jwernikowski.pam_lab.models.RhythmLineDto
import jwernikowski.pam_lab.sound.SoundPlayer
import jwernikowski.pam_lab.utils.Converters
import jwernikowski.pam_lab.utils.ErrorText
import jwernikowski.pam_lab.utils.Tempo
import kotlinx.android.synthetic.main.activity_rhythm_details.*
import javax.inject.Inject

class RhythmDetailsActivity : AppCompatActivity() {

    companion object {
        val RHYTHM_ID_TAG = "rhythm"
    }

    @Inject
    lateinit var player: SoundPlayer
    private lateinit var viewModel: RhythmDetailsViewModel
    private var isPlaying = false
    private var editTextTempoChange = false
    private var saveIconId = R.drawable.ic_add_white_24dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.component.inject(this)
        volumeControlStream = AudioManager.STREAM_MUSIC
        setContentView(R.layout.activity_rhythm_details)

        viewModel = ViewModelProviders.of(this).get(RhythmDetailsViewModel::class.java)
        loadRhythm()
        init()
    }

    override fun onBackPressed() {
        resetBtn.callOnClick()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, 0, Menu.NONE, "")
        var item = menu?.getItem(0)
        item?.setIcon(saveIconId)
        item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        item?.setOnMenuItemClickListener { item -> let{
            onSaveClicked()
            true
        } }
        return super.onCreateOptionsMenu(menu)
    }

    private fun onSaveClicked() {
        if (checkFields()) {
            viewModel.rhythmLinesDto.value = rhythmDesigner.rhythmLines
            viewModel.rhythmDto.value!!.name = nameEditText.editableText.toString()
            viewModel.rhythmDto.value!!.meter = viewModel.meter.value!!
            viewModel.rhythmDto.value!!.defaultBpm = viewModel.tempo.value!!
            viewModel.save()
            finish()
        }
    }

    private fun checkFields(): Boolean {
        if (nameEditText.text.length < 3) {
            nameEditText.error = getString(R.string.rhythm_name_too_short)
            return false
        }
        if (rhythmDesigner.isRhythmEmpty()) {
            Toast.makeText(this, getString(R.string.rhythm_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loadRhythm() {
        var id: Long? = intent.extras?.getLong(RHYTHM_ID_TAG)
        var title = getString(R.string.new_rhythm)
        id?.let {
            viewModel.loadRhythm(it)
            title = getString(R.string.edit_rhythm)
            saveIconId = R.drawable.ic_save_24dp
        } ?: viewModel.newRhythm()
        supportActionBar?.title = title
    }

    private fun init() {
        rhythmDesigner.player = player

        viewModel.rhythmDto.observe(this, Observer {
            nameEditText.setText(it.name)
        })

        tempoEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (tempoEditText.isFocused && s!!.isNotEmpty() && !editTextTempoChange) {
                    val enteredTempo = s.toString().toInt()
                    if (enteredTempo != Tempo.truncate(enteredTempo))
                        tempoEditText.error = ErrorText.tempoOutOfRange(this@RhythmDetailsActivity)
                    editTextTempoChange = true
                    Log.i("12345","sending new val to vm")
                    viewModel.tempo.value = Tempo.truncate(enteredTempo)
                    editTextTempoChange = false
                } else if (s!!.isEmpty()) {
                    tempoEditText.error = getString(R.string.enter_tempo)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        viewModel.rhythmLinesDto.observe(this, Observer { newLines -> run{onRhythmLinesChanged(newLines)} })
        viewModel.tempo.observe(this, Observer { newTempo: Int -> run{onTempoChanged(newTempo)} })

        initSeekBar()
        initMeter()
        initButtons()
    }

    private fun onRhythmLinesChanged(newLines: List<RhythmLineDto>) {
        rhythmDesigner.rhythmLines = newLines
    }

    private fun onTempoChanged(newTempo: Int) {
        if (!editTextTempoChange)
            tempoEditText.setText(newTempo.toString())
        tempoSeekBar.progress = Tempo.bpmToProgress(newTempo)
        rhythmDesigner.bpm = newTempo
    }

    override fun onDestroy() {
        resetBtn.callOnClick()
        super.onDestroy()
    }

    private fun initSeekBar() {
        tempoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.tempo.value = Tempo.progressToBpm(progress)
                    tempoEditText.error = null
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
    }

    private fun initMeter() {
        viewModel.meter.observe(this, Observer { newMeter -> run{
            meterEditText.setText(newMeter.toString())
            resetBtn.callOnClick()
            rhythmDesigner.setMeter(newMeter)
        } })
        meterEditText.setOnClickListener { run {displayEditMeterDialog()} }
    }

    private fun initButtons() {
        playPauseBtn.setOnClickListener { run{
            when(isPlaying) {
                true -> {rhythmDesigner.pause(); playPauseBtn.setImageResource(R.drawable.play)}
                false -> {rhythmDesigner.play(); playPauseBtn.setImageResource(R.drawable.pause)}
            }
            isPlaying = !isPlaying
        } }
        resetBtn.setOnClickListener{ run{
            if (isPlaying)
                playPauseBtn.callOnClick()
            rhythmDesigner.reset()
        } }
    }

    private fun displayEditMeterDialog() {
        val dialog = EditMeterDialogFragment(viewModel)
        dialog.show(supportFragmentManager, EditMeterDialogFragment.TAG)
    }
}
