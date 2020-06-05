package com.peterstev.decagonchallenge


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.peterstev.decagonchallenge.adapters.MainAdapter
import com.peterstev.decagonchallenge.models.DataModel
import com.peterstev.decagonchallenge.models.UserData
import com.peterstev.decagonchallenge.utils.getRetrofit
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

    private var currPage: Int = 0
    private val authorList = mutableListOf<UserData>()
    private var totalItems = 0
    private var totalPages = 0

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
            //
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(authorList, recyclerView, this)
        recyclerView.adapter = adapter
        getData(1)
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
                    triggerKey = USERNAMES
                    true
                }
                R.id.popup_created_at -> {
                    toggleBottomSheetState()
                    triggerKey = CREATED_AT
                    true
                }
                R.id.popup_highest_comment -> {
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
                                currPage = page
                                totalItems = data.total!!
                                totalPages = data.total_pages!!
                            }
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
        if (totalPages > currPage) {
            getData(currPage.plus(1))
        }
    }
}