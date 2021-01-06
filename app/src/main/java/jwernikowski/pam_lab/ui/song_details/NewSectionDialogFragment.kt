package jwernikowski.pam_lab.ui.song_details

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.DialogNewSectionBinding
import jwernikowski.pam_lab.db.data.Section
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.utils.ErrorText

class NewSectionDialogFragment(private val song: Song, private val existingSections: List<Section>) : DialogFragment() {

    companion object {
        val TAG = "new_section"
    }

    private lateinit var binding: DialogNewSectionBinding
    private lateinit var viewModel: NewSectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogNewSectionBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this).get(NewSectionViewModel::class.java)
        viewModel.song.postValue(song)
        viewModel.existingSections.postValue(existingSections)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.sectionNameNotEmpty.observe(viewLifecycleOwner, Observer {
            if(!it && binding.nameEditText.isDirty) binding.nameEditText.error = getString(R.string.enter_section_name)
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
        })
    }

}