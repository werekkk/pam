package jwernikowski.pam_lab.ui.rhythm_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.DialogMeterBinding

class EditMeterDialogFragment(val viewModel: RhythmDetailsViewModel): DialogFragment() {

    companion object {
        val TAG = "EditMeterDialogFragment"
    }

    private lateinit var binding: DialogMeterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMeterBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        init()
        return binding.root
    }

    fun init() {
        viewModel.meter.observe(this, Observer { meter -> run{
            binding.measureText.text = meter.measure.toString()
            binding.lengthText.text = meter.length.toString()
        } })
    }
}