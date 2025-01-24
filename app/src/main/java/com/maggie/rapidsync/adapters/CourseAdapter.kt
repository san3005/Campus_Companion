package com.maggie.rapidsync.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maggie.rapidsync.R
import com.maggie.rapidsync.model.pojo.Course

class CourseAdapter(
    private val options: List<Course>, private val isMyCourses: Boolean,
    private val myCourses: List<Course>,
    private val onOptionSelected: (Course) -> Unit
) :
    RecyclerView.Adapter<CourseAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_course_slot, parent, false)
        return OptionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val currentOption = options[position]
        holder.name.text = currentOption.subject.name
        holder.schedule.text =
            currentOption.days + " " + currentOption.startTime + " - " + currentOption.endTime
        holder.instructor.text = currentOption.instructor.username
        holder.available.text =
            currentOption.currentStudents.toString() + "/" + currentOption.maxStudents
        holder.code.text = currentOption.subject.code

        if (isMyCourses)
            holder.enrolled.text = "Un-enroll"
        else {
            holder.enrolled.text = "Enroll"
//            if (myCourses.contains(currentOption)) {
//                holder.enrolled.text = "Enrolled"
//                holder.enrolled.setBackgroundColor(holder.enrolled.context.getColor(R.color.text_lyt))
//            }
        }

        holder.enrolled.isEnabled =
            currentOption.currentStudents != currentOption.maxStudents
                    && (!myCourses.contains(currentOption))

        holder.enrolled.setOnClickListener {
            onOptionSelected(currentOption)
        }
    }

    override fun getItemCount() = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val schedule: TextView = itemView.findViewById(R.id.schedule)
        val instructor: TextView = itemView.findViewById(R.id.instructor)
        val available: TextView = itemView.findViewById(R.id.availableSlots)
        val code: TextView = itemView.findViewById(R.id.code)
        val enrolled: TextView = itemView.findViewById(R.id.buttonBookSlot)

    }
}
