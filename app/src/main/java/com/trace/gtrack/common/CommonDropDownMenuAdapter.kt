package com.trace.gtrack.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.trace.gtrack.R

class CommonDropDownMenuAdapter(
    private var list: List<String>,
    val onClick: (String) -> Unit,
) :
    RecyclerView.Adapter<CommonDropDownMenuAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommonDropDownMenuAdapter.ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dd_menu_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CommonDropDownMenuAdapter.ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var tvTitle: AppCompatTextView = view.findViewById(R.id.tv_status)
        fun bind(item: String) {
            tvTitle.text = item
            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }

}