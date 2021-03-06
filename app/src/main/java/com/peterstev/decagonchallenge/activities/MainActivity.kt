package com.peterstev.decagonchallenge.activities


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.peterstev.decagonchallenge.R
import com.peterstev.decagonchallenge.adapters.MainAdapter
import com.peterstev.decagonchallenge.models.DataModel
import com.peterstev.decagonchallenge.models.UserData
import com.peterstev.decagonchallenge.utils.getRetrofit
import com.peterstev.decagonchallenge.utils.longToDate
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "MainActivity"
private const val USERNAMES = 1
private const val CREATED_AT = 2

class MainActivity : AppCompatActivity(), MainAdapter.FetchMore {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: ExtendedFloatingActionButton
    private lateinit var adapter: MainAdapter
    private lateinit var progress: ProgressBar
    private lateinit var etThreshold: TextInputEditText

    private var currPage: Int = 1
    private var totalItems = 0
    private var totalPages = 0
    private var usersList = mutableListOf<UserData>()

    private lateinit var sheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var sheetView: NestedScrollView

    private var triggerKey = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = main_list
        fab = main_fab_filter
        progress = main_progress
        sheetView = nestedScrollView
        sheetBehavior = BottomSheetBehavior.from(sheetView)
        etThreshold = et_filter_threshold

        fab.setOnClickListener {
            showFilterOptions()
        }

        main_btn_filter.setOnClickListener {
            val threshold = etThreshold.text.toString().trim()
            if (threshold.isNotEmpty()) {
                if (triggerKey == USERNAMES) listFilterResults(getUsernames(threshold.toInt()))
                else if (triggerKey == CREATED_AT) listFilterResults(
                    getUsernamesSortedByRecordDate(
                        threshold.toInt()
                    )
                )
            } else {
                Toast.makeText(this, "Enter a threshold value to filter by", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(emptyList<UserData>().toMutableList(), recyclerView, this)
        recyclerView.adapter = adapter
        getData(currPage)
    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    private fun toggleBottomSheetState() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (sheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun showFilterOptions() {
        val options = PopupMenu(this, fab)
        options.inflate(R.menu.menu_popup)
        options.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.popup_active_authors -> {
                    toggleBottomSheetState()
                    triggerKey =
                        USERNAMES
                    true
                }
                R.id.popup_created_at -> {
                    toggleBottomSheetState()
                    triggerKey =
                        CREATED_AT
                    true
                }
                R.id.popup_highest_comment -> {
                    Toast.makeText(this, getUsernameWithHighestCommentCount(), Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                else -> false
            }
        }
        options.show()
    }

    private fun getData(page: Int) {
        getRetrofit(this).apply {
            progress.visibility = View.VISIBLE
            getUsers(page).enqueue(object : Callback<DataModel> {
                override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
                    progress.visibility = View.GONE
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data?.data != null)
                            adapter.updateList(data.data).apply {
                                adapter.stopLoading()
                                currPage++
                                totalItems = data.total!!
                                totalPages = data.total_pages!!
                                usersList.addAll(data.data)
                            }
                        if (data!!.data!!.isNotEmpty()) fab.show()
                    }
                }

                override fun onFailure(call: Call<DataModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                    Snackbar.make(fab, t.message!!, Snackbar.LENGTH_INDEFINITE).setAction("Retry") {
                        getData(page)
                    }.show()
                    Log.d(TAG, "onFailure: ${t.stackTrace}")
                }

            })

        }
    }

    override fun load() {
        if (totalPages >= currPage) {
            getData(currPage)
        } else Toast.makeText(this, "all caught up", Toast.LENGTH_SHORT).show()
    }

    //authors sorted by when their record was created
    private fun getUsernamesSortedByRecordDate(threshold: Int): List<String> {
        usersList.sortBy {
            it.created_at
        }
        val sortedList = mutableListOf<String>()
        usersList.forEachIndexed { index, userData ->
            if (index.plus(1) > threshold)
                return@forEachIndexed
            sortedList.add("${userData.username} (${longToDate(userData.created_at!!)}) ")
        }
        return sortedList
    }

    //most active authors(using submission_count as the criteria)
    private fun getUsernames(threshold: Int): List<String> {
        usersList.sortByDescending {
            it.submission_count
        }
        val sortedList = mutableListOf<String>()
        usersList.forEachIndexed { index, userData ->
            if (index.plus(1) > threshold)
                return@forEachIndexed
            sortedList.add("${userData.username}(${userData.submission_count}) ")
        }
        return sortedList
    }

    private fun getUsernameWithHighestCommentCount(): String {
        val highestUser = usersList.distinct().maxBy {
            it.comment_count!!
        }
        return "${highestUser!!.username} (${highestUser.comment_count})"
    }

    private fun listFilterResults(result: List<String>) {
        val listAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            result
        )
        AlertDialog.Builder(this)
            .setTitle("Results")
            .setAdapter(listAdapter) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Okay") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

}