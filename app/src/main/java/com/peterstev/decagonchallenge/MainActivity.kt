package com.peterstev.decagonchallenge

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = main_list
        fab = main_fab_filter
        fab.setOnClickListener {
            showFilterOptions()
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter =

    }

    private fun showFilterOptions() {
        val options = PopupMenu(this, fab)
        options.inflate(R.menu.menu_popup)
        options.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.popup_active_authors -> {
                    true
                }
                R.id.popup_highest_comment -> {
                    true
                }
                R.id.popup_created_at -> {
                    true
                }
                else -> false
            }
        }
        options.show()
    }
}
