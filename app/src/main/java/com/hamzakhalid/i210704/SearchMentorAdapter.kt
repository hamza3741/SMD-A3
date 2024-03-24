package com.hamzakhalid.i210704

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hamzakhalid.integration.R

class SearchMentorAdapter(
    private val mentorList: MutableList<Mentor>,
    private val itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<SearchMentorAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nameTextView: TextView = itemView.findViewById(R.id.lin2)
        val descriptionTextView: TextView = itemView.findViewById(R.id.lin3)
        val statusTextView: TextView = itemView.findViewById(R.id.lin4)
        val rateTextView: TextView = itemView.findViewById(R.id.rate3)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener?.onItemClick(mentorList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_mentor, parent, false)
        return ViewHolder(view)
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

    // Interface for handling item clicks
    interface OnItemClickListener {
        fun onItemClick(mentor: Mentor)
    }
}
