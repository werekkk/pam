package jwernikowski.pam_lab.ui.fragment.songs

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.databinding.FragmentSongsBinding
import jwernikowski.pam_lab.db.data.entity.Song
import jwernikowski.pam_lab.ui.activity.song_details.SongDetailsActivity
import jwernikowski.pam_lab.ui.dialog.song_new.NewSongDialogFragment

class SongsFragment : Fragment() {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var viewModel: SongsViewModel

    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var viewAdapter: SongsAdapter
    private lateinit var viewManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SongsViewModel::class.java)

        binding = FragmentSongsBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initRecyclerView()
        initFloatingActionButton()

        viewModel.allSongs.observe(viewLifecycleOwner) { viewAdapter.setSongs(it) }

        return binding.root
    }

    private fun initRecyclerView() {
        viewManager = LinearLayoutManager(context)

        viewAdapter = SongsAdapter { song -> run{
            startSongDetailsActivity(song)
        }}

        binding.songsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    binding.addSongFab.apply { if (dy > 0) hide() else show() }
                }
            })
        }

        val divider = DividerItemDecoration(binding.songsRecyclerView.context, viewManager.orientation)
        binding.songsRecyclerView.addItemDecoration(divider)
    }

    private fun startSongDetailsActivity(song: Song) {
        val intent = Intent(context, SongDetailsActivity::class.java)
        intent.putExtra(SongDetailsActivity.SONG_TAG, song)
        startActivity(intent)
    }

    private fun initFloatingActionButton() {
        binding.addSongFab.setOnClickListener { displayNewSongDialog() }
    }

    private fun displayNewSongDialog() {
        NewSongDialogFragment {source ->
            source.observe(viewLifecycleOwner) {
                startSongDetailsActivity(it)
                source.removeObservers(viewLifecycleOwner)
            }
        }.show(parentFragmentManager, NewSongDialogFragment.TAG)
    }

}
