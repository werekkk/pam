package jwernikowski.pam_lab.ui.fragment.current_rhythm_info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.FragmentCurrentRhythmInfoBinding
import jwernikowski.pam_lab.db.data.entity.Rhythm

class CurrentRhythmInfoFragment : Fragment() {

    private lateinit var binding: FragmentCurrentRhythmInfoBinding
    private lateinit var viewModel: CurrentRhythmInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CurrentRhythmInfoViewModel::class.java)

        binding = FragmentCurrentRhythmInfoBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    fun onNewRhythm(newRhythm: Rhythm?) {
        viewModel.currentRhythm.postValue(newRhythm)
    }

}