package com.example.to_do_list.ui.list

import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputBinding
import android.widget.Adapter
import androidx.core.view.NestedScrollingParent
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_list.data.Task
import com.example.to_do_list.databinding.ItemTaskBinding


class TasksAdapter(private val listener: onItemClickListener) : ListAdapter<Task,TasksAdapter.TaskViewHolder >(DiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder{
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int){
        val currentItem = getItem(position)
        holder.bind(currentItem)

    }
    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position!= RecyclerView.NO_POSITION)
                    {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
                cb1.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION)
                    {
                        val task = getItem(position)
                        listener.onCheckBoxClicked(task, cb1.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task){
            binding.apply {
                cb1.isChecked = task.completed
                tv1.text = task.name
                tv1.paint.isStrikeThruText = task.completed
                taskPriority.isVisible = task.imprtance

            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClicked(task: Task, isChecked: Boolean)
    }

    class DiffCallback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem

    }

}