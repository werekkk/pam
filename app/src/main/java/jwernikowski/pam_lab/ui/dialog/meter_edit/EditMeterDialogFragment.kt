package jwernikowski.pam_lab.ui.dialog.meter_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import jwernikowski.pam_lab.databinding.DialogMeterBinding
import jwernikowski.pam_lab.db.data.Meter
import jwernikowski.pam_lab.ui.activity.rhythm_details.RhythmDetailsViewModel

class EditMeterDialogFragment(private val initialMeter: Meter, private val onNewMeter: (Meter) -> Unit): DialogFragment() {

    companion object {
        val TAG = "EditMeterDialogFragment"
    }

    private lateinit var binding: DialogMeterBinding
    private lateinit var viewModel: EditMeterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(EditMeterViewModel::class.java)

        binding = DialogMeterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        init()
        return binding.root
    }

    fun init() {
        viewModel.setMeter(initialMeter)
        viewModel.meterChange.observe(this) { onNewMeter(it) }
    }

}