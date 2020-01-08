package jwernikowski.pam_lab.ui.songs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import jwernikowski.pam_lab.MainActivity
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.db.data.SongDao
import jwernikowski.pam_lab.utils.ErrorText
import kotlinx.android.synthetic.main.item_song.*

class NewSongDialogFragment : DialogFragment() {

    companion object {
        val TAG = "NewSongDialogFragment"
    }

    private lateinit var nameEditText: EditText
    private lateinit var initialTempoEditText: EditText
    private lateinit var goalTempoEditText: EditText

    private lateinit var viewModel: SongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_new_song, container)

        nameEditText = root.findViewById(R.id.nameEditText)
        initialTempoEditText = root.findViewById(R.id.initialTempoEditText)
        goalTempoEditText = root.findViewById(R.id.goalTempoEditText)
        parentFragment?.let {
            viewModel = ViewModelProviders.of(it).get(SongsViewModel::class.java)
        }

        initTextFields()
        initButtons(root)

        return root
    }

    private fun initTextFields() {
        initNameField()
        initInitialTempoField()
        initGoalTempoField()
    }

    private fun initNameField() {
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkSongName()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun initInitialTempoField() {
        initialTempoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkInitialTempo()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun initGoalTempoField() {
        goalTempoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkGoalTempo()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    private fun checkSongName(): Boolean {
        if (nameEditText.editableText?.length == 0) {
            nameEditText.error = (getString(R.string.enter_song_name))
            return false
        }
        return true
    }

    private fun checkInitialTempo(): Boolean {
        if (initialTempoEditText.editableText?.length == 0) {
            initialTempoEditText.error = (getString(R.string.enter_initial_tempo))
            return false
        } else if (!checkTempoRange(getInitialTempo())) {
            initialTempoEditText.error = ErrorText.tempoOutOfRange(context)
            return false
        }
        return true
    }

    private fun getInitialTempo(): Int {
        return Integer.parseInt(initialTempoEditText.editableText.toString())
    }

    private fun checkGoalTempo(): Boolean {
        if (goalTempoEditText.editableText?.length == 0) {
            goalTempoEditText.error = resources.getString(R.string.enter_goal_tempo)
            return false
        } else if (!checkTempoRange(getGoalTempo())) {
            goalTempoEditText.error = ErrorText.tempoOutOfRange(context)
            return false
        }
        if (!checkInitialTempo())
            return false
        if (getGoalTempo() < getInitialTempo()) {
            goalTempoEditText.error = resources.getString(R.string.goal_higher_than_initial)
            return false
        }
        return true
    }

    private fun getGoalTempo(): Int {
        return Integer.parseInt(goalTempoEditText.editableText.toString())
    }

    private fun checkTempoRange(tempo: Int): Boolean {
        return tempo >= resources.getInteger(R.integer.min_tempo) && tempo <= resources.getInteger(R.integer.max_tempo)
    }

    private fun initButtons(root: View) {
        root.findViewById<Button>(R.id.cancel).setOnClickListener { view -> dismiss() }
        root.findViewById<Button>(R.id.add).setOnClickListener { view ->
            run {
                if (checkAddSong())
                    addSong()
            }
        }
    }

    private fun checkAddSong(): Boolean {
        return checkSongName() && checkInitialTempo() && checkGoalTempo()
    }

    private fun addSong() {
        if (checkAddSong()) {
            viewModel.insert(getSong())
            Toast.makeText(context, "Song added", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun getSong(): Song {
        return Song(getSongName(), getInitialTempo(), getGoalTempo())
    }

    private fun getSongName(): String {
        return nameEditText.editableText.toString()
    }

}