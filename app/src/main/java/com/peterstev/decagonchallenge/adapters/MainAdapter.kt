package com.peterstev.decagonchallenge.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.peterstev.decagonchallenge.R
import com.peterstev.decagonchallenge.models.UserData
import com.peterstev.decagonchallenge.utils.longToDate

class MainAdapter(private val list: MutableList<UserData>) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(View.inflate(parent.context, R.layout.main_item_view, parent))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = list[position]
        holder.bindItems(data)
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username = itemView.findViewById<AppCompatTextView>(R.id.item_username)
        private val createdAt = itemView.findViewById<AppCompatTextView>(R.id.item_created_at)
        private val submissionCount =
            itemView.findViewById<AppCompatTextView>(R.id.item_submission_count)

        fun bindItems(item: UserData) {
            username.text = item.username
            submissionCount.text = item.submission_count.toString()
            createdAt.text = longToDate(item.created_at ?: 0)
        }
    }
}