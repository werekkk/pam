package jwer.pam.ui.dialog.song_edit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import jwer.pam.R
import jwer.pam.databinding.DialogEditSongBinding
import jwer.pam.db.data.entity.Song
import jwer.pam.utils.ErrorText
import java.lang.IllegalStateException

class EditSongDialogFragment(val song: Song) : DialogFragment() {

    companion object {
        val TAG = "EditSongDialogFragment"
    }

    private lateinit var binding: DialogEditSongBinding
    private lateinit var viewModel: EditSongViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogEditSongBinding.inflate(layoutInflater, null, false)

            AlertDialog.Builder(it)
                .setView(binding.root)
                .setTitle(R.string.edit_song)
                .setPositiveButton(R.string.save) { _, _ -> onSaveSong() }
                .setNegativeButton(R.string.cancel) { _, _ -> Unit}
                .create()
        } ?: throw IllegalStateException()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(EditSongViewModel::class.java)
        viewModel.setSong(song)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.songNameNotEmpty.observe(viewLifecycleOwner, Observer {
            if(!it && binding.nameEditText.isDirty) binding.nameEditText.error = getString(R.string.enter_song_name)
        })
        viewModel.initialTempoInRange.observe(viewLifecycleOwner, Observer {
            if(!it) binding.initialTempoEditText.error = ErrorText.tempoOutOfRange(context)
        })
        viewModel.goalTempoInRange.observe(viewLifecycleOwner, Observer {
            if(!it) binding.goalTempoEditText.error = ErrorText.tempoOutOfRange(context)
        })
        viewModel.initialTempoSmallerThanGoal.observe(viewLifecycleOwner, Observer {
            if(!it) binding.goalTempoEditText.error = getString(R.string.goal_higher_than_initial)
        })
    }

    private fun onSaveSong() {
        if (viewModel.isDataValid()) {
            val oldHasSections = viewModel.song.value!!.hasSections
            val newHasSections = viewModel.hasSections.value!!
            if (oldHasSections != newHasSections) {
                displaySectionChangeWarning(newHasSections)
            } else {
                viewModel.handleSaveSong()
            }
        }
    }

    private fun displaySectionChangeWarning(newHasSections: Boolean) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.warning)
            .setMessage(when(newHasSections) {
                true -> R.string.change_to_sections_warning_message
                false -> R.string.change_to_no_sections_warning_message
            })
            .setPositiveButton(R.string.save) { _, _ -> viewModel.handleSaveSong() }
            .setNegativeButton(R.string.cancel) { _, _ -> Unit}
            .show()
    }

}
