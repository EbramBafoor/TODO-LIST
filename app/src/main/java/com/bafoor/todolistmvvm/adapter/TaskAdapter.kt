package com.bafoor.todolistmvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bafoor.todolistmvvm.R
import com.bafoor.todolistmvvm.data.Task
import kotlinx.android.synthetic.main.fragment_task_item.view.*
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter(private val listener : OnItemClickListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val currentTask = differ.currentList[position]
        holder.itemView.check_box_complete.isChecked = currentTask.completed
        holder.itemView.task_item_tv.text = currentTask.name
        holder.itemView.task_item_tv.paint.isStrikeThruText = currentTask.completed
        holder.itemView.label_priority.isVisible = currentTask.important

        holder.itemView.rv_task_item.setOnClickListener {
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(currentTask)
        }

        holder.itemView.check_box_complete.setOnClickListener {
            if (position != RecyclerView.NO_POSITION)
                listener.onCheckBoxClick(currentTask, holder.itemView.check_box_complete.isChecked)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener{
        fun  onItemClick(task : Task)
        fun onCheckBoxClick(task: Task, isChecked : Boolean)
    }

}


