package jwernikowski.pam_lab.ui.dialog.song_new

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.DialogNewSongBinding
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.utils.ErrorText

class NewSongDialogFragment(
    private val onSongCreated: (Song) -> Unit
): DialogFragment() {

    companion object {
        val TAG = "NewSongDialogFragment"
    }

    private lateinit var binding: DialogNewSongBinding
    private lateinit var viewModel: NewSongViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogNewSongBinding.inflate(inflater, container, false)

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

        viewModel.dismissView.observe(viewLifecycleOwner, Observer {
            dismiss()
            if (it != null) onSongCreated(it)
        })
    }

}