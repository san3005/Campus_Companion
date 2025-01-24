package com.maggie.rapidsync.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maggie.rapidsync.R
import com.maggie.rapidsync.model.pojo.GpaCourse

class GpaCourseAdapter(
    private val courses: List<GpaCourse>,
    private val deleteCourse: (GpaCourse) -> Unit
) :
    RecyclerView.Adapter<GpaCourseAdapter.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gpa_course, parent, false)
        return CourseViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentItem = courses[position]
        holder.textViewCourseName.text = currentItem.courseName
        holder.textViewGrade.text = currentItem.courseGrade + " " + currentItem.courseCredit
        holder.deleteButton.setOnClickListener {
            deleteCourse(currentItem)
        }
    }

    override fun getItemCount() = courses.size

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCourseName: TextView = itemView.findViewById(R.id.textViewCourseName)
        val textViewGrade: TextView = itemView.findViewById(R.id.textViewGrade)
        val deleteButton: ImageButton = itemView.findViewById(R.id.imageButtonDelete)
    }
}