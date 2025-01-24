package com.maggie.rapidsync.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maggie.rapidsync.R
import com.maggie.rapidsync.model.pojo.ParkingPlan

class ParkingPlanAdapter(
    private val options: List<ParkingPlan>,
    private val isBooked: Boolean,
    private val onOptionSelected: (ParkingPlan) -> Unit
) :
    RecyclerView.Adapter<ParkingPlanAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_parking_slot, parent, false)
        return OptionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val currentOption = options[position]
        holder.location.text = currentOption.name
        holder.speciality.text = currentOption.description
        holder.id.text = "$${currentOption.price}"
        holder.book.text = "Purchase plan"

        holder.book.isEnabled = !isBooked

        holder.book.setOnClickListener {
            onOptionSelected(currentOption)
        }
    }

    override fun getItemCount() = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.textViewLocation)
        val speciality: TextView = itemView.findViewById(R.id.textViewSpeciality)
        val id: TextView = itemView.findViewById(R.id.textViewId)
        val book: TextView = itemView.findViewById(R.id.buttonBookSlot)
    }
}
