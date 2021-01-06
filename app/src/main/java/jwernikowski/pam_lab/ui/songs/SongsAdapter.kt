package jwernikowski.pam_lab.ui.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.Song

class SongsAdapter(val clickListener: (Song) -> Unit) :
    RecyclerView.Adapter<SongsAdapter.SongsViewHolder>() {

    private var songs: List<Song> = ArrayList()

    class SongsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName: TextView = itemView.findViewById(R.id.song_name)

        private lateinit var _song: Song

        var song: Song
            get() = _song
            set(s) {
                _song = s
                songName.text = s.name
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.item_song,
            parent,
            false
        ) as ViewGroup
        val vh =  SongsViewHolder(layout)
        vh.itemView.setOnClickListener {
            run{
            clickListener(vh.song)
        } }
        return vh
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        holder.song = songs[position]
    }

    fun setSongs(songs: List<Song>) {
        this.songs = songs
        notifyDataSetChanged()
    }

}