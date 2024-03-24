package com.hamzakhalid.i210704

//data class MentorReview(val mentorName: String, val feedback: String)
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hamzakhalid.integration.R

class MentorReviewAdapter(
    private val ReviewList: MutableList<MentorReview>
) : RecyclerView.Adapter<MentorReviewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.name1)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorReviewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = ReviewList[position]
        // Bind appointment data to views
        holder.nameTextView.text = review.mentorName
        holder.descriptionTextView.text = review.feedback
    }

    override fun getItemCount(): Int {
        return ReviewList.size
    }

}