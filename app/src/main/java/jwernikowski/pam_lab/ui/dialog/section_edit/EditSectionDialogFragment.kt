package jwernikowski.pam_lab.ui.dialog.section_edit

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.DialogEditSectionBinding
import jwernikowski.pam_lab.db.data.entity.Section
import jwernikowski.pam_lab.utils.ErrorText
import java.lang.IllegalStateException

class EditSectionDialogFragment(private val section: Section) : DialogFragment() {

    companion object {
        val TAG = "edit_section_name"
    }

    private lateinit var binding: DialogEditSectionBinding
    private lateinit var viewModel: EditSectionViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DialogEditSectionBinding.inflate(layoutInflater, null, false)

            AlertDialog.Builder(it)
                .setView(binding.root)
                .setTitle(R.string.edit_section)
                .setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
                .setPositiveButton(R.string.save) { _, _ -> onSave() }
                .create()
        } ?: throw IllegalStateException()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(EditSectionViewModel::class.java)
        viewModel.setSection(section)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        observeViewModel()

        return binding.root
    }

    private fun onSave() {
        viewModel.handleSave()
    }

    private fun onCancel() {
        dismiss()
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
    }
}