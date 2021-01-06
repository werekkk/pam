package jwernikowski.pam_lab.ui.song_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jwernikowski.pam_lab.R
import jwernikowski.pam_lab.db.data.Section

class SectionAdapter(
    val onPracticeClicked: (Section) -> Unit,
    val onItemClicked: (Section) -> Unit
): RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    private var sections: List<Section> = ArrayList()

    class SectionViewHolder(
        itemView: View,
        val onPracticeClicked: (Section) -> Unit,
        val onItemClicked: (Section) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val sectionName: TextView = itemView.findViewById(R.id.section_name)
        private val practiceBtn: Button = itemView.findViewById(R.id.practice_btn)

        private lateinit var _section: Section

        var section: Section
            get() = _section
            set(s) {
                _section = s
                sectionName.text = s.name
            }

        init {
            practiceBtn.setOnClickListener { onPracticeClicked(section) }
            itemView.setOnClickListener { onItemClicked(section) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.item_section,
            parent,
            false
        ) as ViewGroup
        val vh = SectionViewHolder(layout, onPracticeClicked, onItemClicked)
        return vh
    }

    override fun getItemCount(): Int = sections.size

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.section = sections[position]
    }

    fun setSections(sections: List<Section>) {
        this.sections = sections
        notifyDataSetChanged()
    }
}