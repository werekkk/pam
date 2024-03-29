package jwer.pam.ui.activity.rhythm_details

import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import jwer.pam.ui.activity.MainActivity
import jwer.pam.R
import jwer.pam.databinding.ActivityRhythmDetailsBinding
import jwer.pam.dto.RhythmLineDto
import jwer.pam.sound.SoundPlayer
import jwer.pam.ui.dialog.meter_edit.EditMeterDialogFragment
import jwer.pam.utils.ErrorText
import jwer.pam.utils.Tempo
import javax.inject.Inject

class RhythmDetailsActivity : AppCompatActivity() {

    companion object {
        const val RHYTHM_ID_TAG = "rhythm"
    }

    @Inject
    lateinit var player: SoundPlayer

    private lateinit var binding: ActivityRhythmDetailsBinding
    private lateinit var viewModel: RhythmDetailsViewModel

    private var isPlaying = false
    private var editTextTempoChange = false
    private var id: Long? = null
    private var saveIconId = R.drawable.ic_add_white_24dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.component.inject(this)

        binding = ActivityRhythmDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RhythmDetailsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        loadRhythm()
        init()
    }

    override fun onDestroy() {
        binding.resetBtn.callOnClick()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuRes = R.menu.menu_rhythm_details_new
        if (id != null)
            menuRes = R.menu.menu_rhythm_details_edit
        menuInflater.inflate(menuRes, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> onSaveClicked()
            R.id.add -> onSaveClicked()
            R.id.delete -> onDeleteClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (unsavedChanges()) onUnsavedChanges() else finish()
    }

    private fun unsavedChanges(): Boolean {
        return viewModel.rhythmDto.value!!.name != binding.nameEditText.editableText.toString() ||
                viewModel.rhythmDto.value!!.meter != viewModel.meter.value!! ||
                viewModel.rhythmDto.value!!.defaultBpm != viewModel.tempo.value!! ||
                viewModel.initialRhythmLinesDto != binding.rhythmDesigner.rhythmLines
    }

    private fun onSaveClicked() {
        if (checkFields()) {
            viewModel.rhythmLinesDto.value = binding.rhythmDesigner.rhythmLines
            viewModel.rhythmDto.value!!.name = binding.nameEditText.editableText.toString()
            viewModel.rhythmDto.value!!.meter = viewModel.meter.value!!
            viewModel.rhythmDto.value!!.defaultBpm = viewModel.tempo.value!!
            viewModel.save()
            binding.rhythmDesigner.pause()
            finish()
        }
    }

    private fun onDeleteClicked() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_rhythm)
            .setMessage(R.string.delete_rhythm_sure)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.delete()
                binding.rhythmDesigner.pause()
                finish()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> Unit}
            .show()
    }

    private fun onUnsavedChanges() {
        AlertDialog.Builder(this)
            .setTitle(R.string.unsaved_changes)
            .setMessage(R.string.unsaved_rhythm_warning_message)
            .setPositiveButton(R.string.save) { _, _ ->
                onSaveClicked()
            }
            .setNegativeButton(R.string.dont_save) { _, _ -> finish() }
            .setNeutralButton(R.string.cancel) { _, _ -> }
            .show()
    }

    private fun checkFields(): Boolean {
        if (binding.nameEditText.text.length < 3) {
            binding.nameEditText.error = getString(R.string.rhythm_name_too_short)
            return false
        }
        if (binding.rhythmDesigner.isRhythmEmpty()) {
            Toast.makeText(this, getString(R.string.rhythm_empty), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loadRhythm() {
        id = intent.extras?.getLong(RHYTHM_ID_TAG)
        var title = getString(R.string.new_rhythm)
        id?.let {
            viewModel.loadRhythm(it)
            title = getString(R.string.edit_rhythm)
            saveIconId = R.drawable.ic_save_24dp
        } ?: viewModel.newRhythm()
        supportActionBar?.title = title
    }

    private fun init() {
        volumeControlStream = AudioManager.STREAM_MUSIC

        viewModel.rhythmDto.observe(this, Observer {
            binding.nameEditText.setText(it.name)
        })

        binding.tempoEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.tempoEditText.isFocused && s!!.isNotEmpty() && !editTextTempoChange) {
                    val enteredTempo = s.toString().toInt()
                    if (enteredTempo != Tempo.truncate(enteredTempo))
                        binding.tempoEditText.error = ErrorText.tempoOutOfRange(this@RhythmDetailsActivity)
                    editTextTempoChange = true
                    viewModel.tempo.value = Tempo.truncate(enteredTempo)
                    editTextTempoChange = false
                } else if (s!!.isEmpty()) {
                    binding.tempoEditText.error = getString(R.string.enter_tempo)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

        })

        viewModel.rhythmLinesDto.observe(this) { onRhythmLinesChanged(it) }
        viewModel.tempo.observe(this) { onTempoChanged(it)}

        initSeekBar()
        initMeter()
        initButtons()
    }

    private fun onRhythmLinesChanged(newLines: List<RhythmLineDto>) {
        binding.rhythmDesigner.rhythmLines = newLines
    }

    private fun onTempoChanged(newTempo: Int) {
        if (!editTextTempoChange)
            binding.tempoEditText.setText(newTempo.toString())
        binding.tempoSeekBar.progress = Tempo.bpmToProgress(newTempo)
        binding.rhythmDesigner.bpm = newTempo
    }

    private fun initSeekBar() {
        binding.tempoSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.tempo.value = Tempo.progressToBpm(progress)
                    binding.tempoEditText.error = null
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
    }

    private fun initMeter() {
        viewModel.meter.observe(this, Observer { newMeter -> run{
            binding.meterEditText.setText(newMeter.toString())
            binding.resetBtn.callOnClick()
            binding.rhythmDesigner.setMeter(newMeter)
        } })
        binding.meterEditText.setOnClickListener { run {displayEditMeterDialog()} }
    }

    private fun initButtons() {
        binding.playPauseBtn.setOnClickListener { run{
            when(isPlaying) {
                true -> {binding.rhythmDesigner.pause(); binding.playPauseBtn.setImageResource(R.drawable.play)}
                false -> {binding.rhythmDesigner.play(player); binding.playPauseBtn.setImageResource(R.drawable.pause)}
            }
            isPlaying = !isPlaying
        } }
        binding.resetBtn.setOnClickListener{ run{
            if (isPlaying)
                binding.playPauseBtn.callOnClick()
            binding.rhythmDesigner.reset()
        } }
    }

    private fun displayEditMeterDialog() {
        EditMeterDialogFragment(viewModel.meter.value!!) { viewModel.handleNewMeter(it) }
            .show(supportFragmentManager, EditMeterDialogFragment.TAG)
    }
}
