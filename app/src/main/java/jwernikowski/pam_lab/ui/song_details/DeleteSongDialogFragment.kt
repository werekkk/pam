package jwernikowski.pam_lab.ui.song_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import jwernikowski.pam_lab.R

class DeleteSongDialogFragment(val viewModel: SongDetailsViewModel) : DialogFragment() {

    companion object {
        val TAG = "DeleteSongDialogFragment"
    }

    lateinit var onDelete: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_delete_song, container, false)
        initButtons(root)
        return root
    }

    fun initButtons(root: View) {
        root.findViewById<Button>(R.id.cancel).setOnClickListener { v -> run{dismiss()} }
        root.findViewById<Button>(R.id.delete).setOnClickListener { v -> run{
            viewModel.deleteSong()
            onDelete()
            dismiss()
        } }
    }
}
