package jwernikowski.pam_lab.ui.dialog.song_edit_name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.ui.activity.song_details.SongDetailsViewModel

class EditSongNameDialogFragment(val viewModel: SongDetailsViewModel) : DialogFragment() {

    companion object {
        val TAG = "EditSongNameDialogFragment"
    }

    private lateinit var editedName: EditText
    lateinit var onSongEdited: (Song) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_edit_song_name, container, false)

        initButtons(root)
        initEditText(root)
        return root
    }

    fun initButtons(root: View) {
        root.findViewById<Button>(R.id.cancel).setOnClickListener { v -> run{dismiss()} }
        root.findViewById<Button>(R.id.edit).setOnClickListener { v -> run{
            var song = viewModel.song.value
            song?.let {
                val editedSong = Song(
                    editedName.editableText.toString(),
                    song.hasSections
                )
                song.songId?.let {
                    editedSong.songId = song.songId
                    viewModel.updateSong(editedSong)
                }
                dismiss()
            }
        } }
    }

    fun initEditText(root: View) {
        editedName = root.findViewById(R.id.editedName)
        editedName.setText(viewModel.song.value?.name)
    }
}
