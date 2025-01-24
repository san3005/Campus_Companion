package com.maggie.rapidsync.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maggie.rapidsync.R
import com.maggie.rapidsync.model.pojo.Appointment
import com.maggie.rapidsync.model.pojo.MealPlan
import com.maggie.rapidsync.model.pojo.User

class AppointmentScheduleAdapter(
    private val options: List<Appointment>,
    private val toBook: Boolean,
    private val onOptionSelected: (Appointment) -> Unit
) :
    RecyclerView.Adapter<AppointmentScheduleAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.appointment_schedule, parent, false)
        return OptionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val currentOption = options[position]
        holder.teacherName.text = currentOption.teacher?.username
        holder.date.text = currentOption.appointmentSchedule?.date
        holder.time.text =
            currentOption.appointmentSchedule?.startTime + " - " + currentOption.appointmentSchedule?.endTime
        if (toBook) {
            if (currentOption.appointmentSchedule?.status == "Available")
                holder.book.text = "Request appointment"
        } else {
            if (currentOption.appointmentSchedule?.status == "Requested")
                holder.book.text = "Cancel request"
            else {
                holder.book.text = "Cancel appointment"
            }
            holder.book.setBackgroundColor(Color.RED)
        }

        holder.book.setOnClickListener {
            onOptionSelected(currentOption)
        }
    }

    override fun getItemCount() = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teacherName: TextView = itemView.findViewById(R.id.teacher_name)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)
        val book: TextView = itemView.findViewById(R.id.buttonBookSlot)
    }
}
