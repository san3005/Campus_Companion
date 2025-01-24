package com.maggie.rapidsync.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.maggie.rapidsync.R
import com.maggie.rapidsync.commons.show
import com.maggie.rapidsync.model.pojo.UniversityEvent

class UniversityEventAdapter(
    private val options: List<UniversityEvent>,
    private val onOptionClick: (UniversityEvent) -> Unit
) :
    RecyclerView.Adapter<UniversityEventAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_university_event, parent, false)
        return OptionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.name.text = option.name
        holder.description.text = option.description
        holder.price.text = option.date
        holder.date.text = option.time + ", " + option.location
        if (option.imageUrl != null && option.imageUrl.isNotEmpty()) {
            holder.image.show()
            holder.image.load(option.imageUrl)
        }
        holder.itemView.setOnClickListener {
            onOptionClick(option)
        }
    }

    override fun getItemCount() = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById<TextView>(R.id.title)
        val description: TextView = itemView.findViewById<TextView>(R.id.description)
        val price: TextView = itemView.findViewById<TextView>(R.id.date)
        val date: TextView = itemView.findViewById<TextView>(R.id.time)
        val image: ImageView = itemView.findViewById<ImageView>(R.id.image)

    }
}
