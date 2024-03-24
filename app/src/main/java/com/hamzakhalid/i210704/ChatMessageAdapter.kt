package com.hamzakhalid.i210704

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hamzakhalid.integration.R

class ChatMessageAdapter(
    private val MentorList: MutableList<Mentor>,
    private val onItemClick: (String) -> Unit // Click listener
) : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.Name)
        init {
            // Set click listener for each item
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val mentor = MentorList[position]
                    onItemClick.invoke(mentor.name) // Pass mentor's name to the click listener
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Mentors = MentorList[position]
        // Bind appointment data to views
        holder.nameTextView.text = Mentors.name
    }

    override fun getItemCount(): Int {
        return MentorList.size
    }
    fun updateMentors(newMentors: List<Mentor>) {
        MentorList.clear()
        MentorList.addAll(newMentors)
        notifyDataSetChanged()
    }

}