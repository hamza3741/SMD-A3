package com.hamzakhalid.i210704
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hamzakhalid.integration.R

class MentorAdapter(private val mentorList: MutableList<Mentor>) :
    RecyclerView.Adapter<MentorAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.lin2)
        val descriptionTextView: TextView = itemView.findViewById(R.id.lin3)
        val statusTextView: TextView = itemView.findViewById(R.id.lin4)
        val rateTextView: TextView = itemView.findViewById(R.id.rate3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.mentor_item, parent, false)
        return ViewHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(mentor: Mentor)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mentor = mentorList[position]
        holder.nameTextView.text = mentor.name
        holder.descriptionTextView.text = mentor.description
        holder.statusTextView.text = mentor.status
        holder.rateTextView.text = mentor.rate
    }

    override fun getItemCount(): Int {
        return mentorList.size
    }
    fun updateMentors(newMentors: List<Mentor>) {
        mentorList.clear()
        mentorList.addAll(newMentors)
        notifyDataSetChanged()
    }
}