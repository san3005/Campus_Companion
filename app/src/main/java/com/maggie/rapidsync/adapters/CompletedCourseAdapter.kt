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
import com.maggie.rapidsync.model.pojo.CourseEnrollment

class CompletedCourseAdapter(
    private val options: List<CourseEnrollment>
) :
    RecyclerView.Adapter<CompletedCourseAdapter.OptionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_course_completed, parent, false)
        return OptionViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val currentOption = options[position]
        holder.name.text = currentOption.course.subject.name
        holder.schedule.text = currentOption.feedback
        holder.instructor.text = currentOption.course.instructor.username
        holder.code.text = currentOption.grade
    }

    override fun getItemCount() = options.size

    inner class OptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val schedule: TextView = itemView.findViewById(R.id.schedule)
        val instructor: TextView = itemView.findViewById(R.id.instructor)
        val code: TextView = itemView.findViewById(R.id.code)
    }
}
