package com.maggie.rapidsync.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.maggie.rapidsync.R
import com.maggie.rapidsync.model.pojo.Appointment
import com.maggie.rapidsync.model.pojo.GroupedAppointment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class AppointmentAdapter(
    private val options: List<GroupedAppointment>,
    private val toBook: Boolean,
    private val onOptionSelected: (Appointment) -> Unit
) :
    RecyclerView.Adapter<AppointmentAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.appointment, parent, false)
        return OptionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val currentOption = options[position]
        holder.date.text = currentOption.date
        val adapter = currentOption.appointments?.let {
            AppointmentScheduleAdapter(
                it,
                toBook,
                onOptionSelected
            )
        }
        holder.pager.adapter = adapter
        holder.dotsIndicator.attachTo(holder.pager)

    }

    override fun getItemCount() = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.appointmentDate)
        val pager: ViewPager2 = itemView.findViewById(R.id.eventsViewPager)
        val dotsIndicator: DotsIndicator = itemView.findViewById(R.id.dots_indicator)
    }
}
