package jwernikowski.pam_lab.ui.songs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.Song
import jwernikowski.pam_lab.ui.song_details.SongDetailsActivity

class SongsFragment : Fragment() {

    private lateinit var viewModel: SongsViewModel

    private lateinit var songsRecyclerView: RecyclerView
    private lateinit var viewAdapter: SongsAdapter
    private lateinit var viewManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.songs_fragment, container, false)

        initRecyclerView(root)
        initFloatingActionButton(root)

        return root
    }

    private fun initRecyclerView(root: View) {
        viewManager = LinearLayoutManager(context)

        viewAdapter = SongsAdapter { song -> run{
            startSongDetailsActivity(song)
        }}

        songsRecyclerView = root.findViewById<RecyclerView>(R.id.songs_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val divider = DividerItemDecoration(songsRecyclerView.context, viewManager.orientation)
        songsRecyclerView.addItemDecoration(divider)
    }

    private fun startSongDetailsActivity(song: Song) {
        val intent = Intent(context, SongDetailsActivity::class.java)
        intent.putExtra(SongDetailsActivity.SONG_TAG, song)
        startActivity(intent)
    }

    private fun initFloatingActionButton(root: View) {
        root.findViewById<View>(R.id.add_song_fab).setOnClickListener {
            displayNewSongDialog()
        }
    }

    private fun displayNewSongDialog() {
        NewSongDialogFragment().show(parentFragmentManager, NewSongDialogFragment.TAG)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SongsViewModel::class.java)
        viewModel.getAllSongs()
            .observe(viewLifecycleOwner, Observer { newSongs -> run{
                viewAdapter.setSongs(newSongs)
            }})
    }

}
