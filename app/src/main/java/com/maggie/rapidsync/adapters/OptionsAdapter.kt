package com.maggie.rapidsync.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maggie.rapidsync.R
import com.maggie.rapidsync.model.pojo.Option

class OptionsAdapter(
    private val options: List<Option>,
    private val onOptionSelected: (Option) -> Unit
) :
    RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return OptionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val currentOption = options[position]
        holder.textViewOption.text = currentOption.title
        holder.image.setImageResource(currentOption.icon)
        holder.bind(currentOption, onOptionSelected)
    }

    override fun getItemCount() = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewOption: TextView = itemView.findViewById(R.id.textViewTitle)
        val image : ImageView = itemView.findViewById(R.id.imageView)

        fun bind(option: Option, onOptionSelected: (Option) -> Unit) {
            itemView.setOnClickListener { onOptionSelected(option) }
        }
    }
}
