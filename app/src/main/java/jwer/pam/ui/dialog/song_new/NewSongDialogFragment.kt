package jwer.pam.ui.dialog.song_new

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import jwer.pam.R
import jwer.pam.databinding.DialogNewSongBinding
import jwer.pam.db.data.entity.Song
import jwer.pam.utils.ErrorText
import java.lang.IllegalStateException

class NewSongDialogFragment(
    private val onSongCreated: (LiveData<Song>) -> Unit
): DialogFragment() {

    companion object {
        val TAG = "NewSongDialogFragment"
    }

    private lateinit var binding: DialogNewSongBinding
    private lateinit var viewModel: NewSongViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogNewSongBinding.inflate(layoutInflater, null, false)

            AlertDialog.Builder(it)
                .setView(binding.root)
                .setTitle(R.string.add_song)
                .setPositiveButton(R.string.add) { _, _ -> viewModel.onAddSong() }
                .setNegativeButton(R.string.cancel) { _, _ -> Unit }
                .create()
        } ?: throw IllegalStateException()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(NewSongViewModel::class.java)

        binding.newSongViewModel = viewModel
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
        viewModel.createdSong.observe(viewLifecycleOwner, Observer {
            onSongCreated(it)
        })
    }

}