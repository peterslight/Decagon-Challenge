package com.peterstev.decagonchallenge.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peterstev.decagonchallenge.R
import com.peterstev.decagonchallenge.models.UserData
import com.peterstev.decagonchallenge.utils.longToDate


class MainAdapter(
    private val list: MutableList<UserData>,
    private val recyclerView: RecyclerView,
    private val fetchMore: FetchMore
) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var totalItems = 0
    private var lastVisibleItemsPosition = -1
    private var itemThreshold = 5
    private var isLoading: Boolean = false

    init {
        val manager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItems = manager.itemCount
                lastVisibleItemsPosition = manager.findLastVisibleItemPosition()
                if (!isLoading && totalItems <= (lastVisibleItemsPosition + itemThreshold)) {
                    fetchMore.load()
                    isLoading = true
                }
            }
        })
    }

    fun stopLoading() {
        isLoading = false
    }

    interface FetchMore {
        fun load()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.main_item_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = list[position]
        holder.bindItems(data)
    }

    fun updateList(newList: List<UserData>) {
        list.addAll(newList)
        notifyDataSetChanged()
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username = itemView.findViewById<AppCompatTextView>(R.id.item_username)
        private val createdAt = itemView.findViewById<AppCompatTextView>(R.id.item_created_at)
        private val aboutMe = itemView.findViewById<AppCompatTextView>(R.id.item_about_me)
        private val submissionCount =
            itemView.findViewById<AppCompatTextView>(R.id.item_submission_count)

        fun bindItems(item: UserData) {
            username.text = item.username
            aboutMe.text = item.about
            submissionCount.text = item.submission_count.toString()
            createdAt.text = longToDate(item.created_at ?: 0)
        }
    }
}