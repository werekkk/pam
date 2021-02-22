package jwer.pam.ui.dialog.section_new

import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import jwer.pam.R
import jwer.pam.databinding.DialogNewSectionBinding
import jwer.pam.db.data.entity.Section
import jwer.pam.db.data.entity.Song
import jwer.pam.utils.ErrorText

class NewSectionDialogFragment(private val song: Song, private val existingSections: List<Section>) : DialogFragment() {

    companion object {
        val TAG = "new_section"
    }

    private lateinit var binding: DialogNewSectionBinding
    private lateinit var viewModel: NewSectionViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogNewSectionBinding.inflate(layoutInflater, null, false)

            AlertDialog.Builder(it)
                .setView(binding.root)
                .setTitle(R.string.add_section)
                .setPositiveButton(R.string.add, { dialog, which -> viewModel.onAddSection() })
                .setNegativeButton(R.string.cancel, { dialog, which -> viewModel.onCancel() })
                .create()
        } ?: throw IllegalStateException()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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