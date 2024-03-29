package jwer.pam.ui.activity.song_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import jwer.pam.R
import jwer.pam.db.data.entity.Section
import jwer.pam.ui.view.ColoredPercentageTextView
import java.util.*
import kotlin.collections.ArrayList

class SectionAdapter(
    val onPracticeClicked: (Section) -> Unit,
    val onItemClicked: (Section) -> Unit
): RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    var sections: List<Section> = ArrayList()
    get() = field
    set(value) {
        if (!hasOnlySectionOrderChanged(field, value)) {
            field = value
            notifyDataSetChanged()
        }
    }

    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                if (itemCount == 1) {
                    Collections.swap(sections, fromPosition, toPosition)
                }
            }
        })
    }

    private fun hasOnlySectionOrderChanged(oldSections: List<Section>, newSections: List<Section>): Boolean {
        return oldSections.map { it.copy(order = 1) }.sortedBy { it.sectionId } ==
         newSections.map { it.copy(order = 1) }.sortedBy { it.sectionId }
    }

    class SectionViewHolder(
        itemView: View,
        val onPracticeClicked: (Section) -> Unit,
        val onItemClicked: (Section) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val sectionName: TextView = itemView.findViewById(R.id.section_name)
        private val sectionProgress: ColoredPercentageTextView = itemView.findViewById(R.id.section_progress)
        private val practiceBtn: Button = itemView.findViewById(R.id.practice_btn)

        private lateinit var _section: Section

        var section: Section
            get() = _section
            set(s) {
                _section = s
                sectionName.text = s.name
                sectionProgress.setPercentage(s.previousProgress)
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
        val vh =
            SectionViewHolder(
                layout,
                onPracticeClicked,
                onItemClicked
            )
        return vh
    }

    override fun getItemCount(): Int = sections.size

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.section = sections[position]
    }

}