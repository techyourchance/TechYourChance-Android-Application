package com.techyourchance.android.screens.benchmarks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techyourchance.android.R
import com.techyourchance.android.screens.common.toolbar.MyToolbar

class BenchmarksViewMvcImpl(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
): BenchmarksViewMvc() {

    private val toolbar: MyToolbar
    private val destinationsRecycler: RecyclerView
    private val destinationsAdapter: DestinationsAdapter

    init {
        setRootView(layoutInflater.inflate(R.layout.layout_list_of_destinations, parent, false))

        destinationsRecycler = findViewById(R.id.recycler)
        destinationsRecycler.layoutManager = LinearLayoutManager(context)
        destinationsAdapter = DestinationsAdapter(context)
        destinationsRecycler.adapter = destinationsAdapter

        toolbar = findViewById(R.id.toolbar)

        toolbar.setTitle(getString(R.string.screen_user_interfaces))

        toolbar.setNavigateUpListener {
            listeners.map { it.onBackClicked() }
        }
    }

    override fun bindDestinations(destinations: List<FromBenchmarksDestination>) {
        destinationsAdapter.bindDestinations(destinations)
    }

    // ---------------------------------------------------------------------------------------------
    // Inner classes
    // ---------------------------------------------------------------------------------------------

    inner class DestinationsAdapter(
        private val context: Context,
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val VIEW_TYPE_REGULAR = 0

        private val destinations = mutableListOf<FromBenchmarksDestination>()

        fun bindDestinations(destinationDetails: List<FromBenchmarksDestination>) {
            this.destinations.clear()
            this.destinations.addAll(destinationDetails)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return destinations.size
        }

        override fun getItemViewType(position: Int): Int {
            return VIEW_TYPE_REGULAR
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_REGULAR -> {
                    val view = LayoutInflater.from(context).inflate(R.layout.layout_home_destination_item, parent, false)
                    DestinationViewHolder(view)
                }
                else -> {
                    throw RuntimeException("unsupported view type: $viewType")
                }
            }
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            when (viewHolder.itemViewType) {
                VIEW_TYPE_REGULAR -> {
                    (viewHolder as DestinationViewHolder).apply {
                        val destination = destinations[position]
                        viewHolder.txtDestinationTitle.text = destination.title
                        viewHolder.view.setOnClickListener {
                            listeners.map { it.onDestinationClicked(destination) }
                        }
                    }
                }
                else -> {
                    throw RuntimeException("unsupported view type: ${viewHolder.itemViewType}")
                }
            }

        }

    }

    class DestinationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtDestinationTitle: TextView = view.findViewById(R.id.txtDestinationTitle)
    }
}