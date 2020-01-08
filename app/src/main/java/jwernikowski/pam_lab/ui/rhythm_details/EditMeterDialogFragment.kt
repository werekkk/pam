package jwernikowski.pam_lab.ui.rhythm_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import jwernikowski.pam_lab.R
import kotlinx.android.synthetic.main.dialog_meter.*

class EditMeterDialogFragment(val viewModel: RhythmDetailsViewModel): DialogFragment() {

    companion object {
        val TAG = "EditMeterDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_meter, container, false)
        init(root)
        return root
    }

    fun init(root: View) {
        viewModel.meter.observe(this, Observer { meter -> run{
            measureText.text = meter.measure.toString()
            lengthText.text = meter.length.toString()
        } })

        root.findViewById<ImageButton>(R.id.measureUpBtn).setOnClickListener { viewModel.updateMeter(1, 0)}
        root.findViewById<ImageButton>(R.id.measureDownBtn).setOnClickListener { viewModel.updateMeter(-1, 0)}
        root.findViewById<ImageButton>(R.id.lengthUpBtn).setOnClickListener { viewModel.updateMeter(0, 1)}
        root.findViewById<ImageButton>(R.id.lengthDownBtn).setOnClickListener { viewModel.updateMeter(0, -1)}
    }
}