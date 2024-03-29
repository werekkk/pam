package jwer.pam.ui.fragment.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jwer.pam.R
import jwer.pam.db.data.entity.Song
import jwer.pam.ui.view.ColoredPercentageTextView

class SongsAdapter(val clickListener: (Song) -> Unit) :
    RecyclerView.Adapter<SongsAdapter.SongsViewHolder>() {

    private var songs: List<Song> = ArrayList()

    class SongsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val songName: TextView = itemView.findViewById(R.id.song_name)
        private val songProgress: ColoredPercentageTextView = itemView.findViewById(R.id.song_progress)

        private lateinit var _song: Song

        var song: Song
            get() = _song
            set(s) {
                _song = s
                songName.text = s.name
                songProgress.setPercentage(song.previousProgress)
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