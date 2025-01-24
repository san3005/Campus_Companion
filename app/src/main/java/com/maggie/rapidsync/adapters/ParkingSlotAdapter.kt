package com.maggie.rapidsync.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maggie.rapidsync.R
import com.maggie.rapidsync.model.pojo.Option
import com.maggie.rapidsync.model.pojo.ParkingSlot

class ParkingSlotAdapter(
    private val options: List<ParkingSlot>,
    private val isAlreadyBooked: Boolean,
    private val onOptionSelected: (ParkingSlot) -> Unit
) :
    RecyclerView.Adapter<ParkingSlotAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_parking_slot, parent, false)
        return OptionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val currentOption = options[position]
        holder.location.text = "Location: ${currentOption.location}"
        holder.speciality.text = "Speciality: ${currentOption.speciality}"

        holder.id.text =
            currentOption.id.subSequence(currentOption.id.length - 2, currentOption.id.length)

        if (isAlreadyBooked) {
            holder.book.isEnabled = false
            holder.book.setTextColor(Color.GRAY)
        } else {
            holder.book.isEnabled = true
        }
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
