package com.hamzakhalid.i210704
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hamzakhalid.integration.R

class AppointmentAdapter(
    private val appointmentList: MutableList<Appointment>
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         val nameTextView: TextView = itemView.findViewById(R.id.Name3)
         val descriptionTextView: TextView = itemView.findViewById(R.id.Description3)
         val dateTextView: TextView = itemView.findViewById(R.id.textdate1)
         val timeTextView: TextView = itemView.findViewById(R.id.texttime1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.appointment_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointmentList[position]
        // Bind appointment data to views
        holder.nameTextView.text = appointment.mentorName
       holder.descriptionTextView.text = appointment.mentorDescription
        holder.dateTextView.text = appointment.date
        holder.timeTextView.text = appointment.timeSlot
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

}