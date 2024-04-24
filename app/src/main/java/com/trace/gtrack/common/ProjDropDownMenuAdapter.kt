package com.trace.gtrack.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.trace.gtrack.R
import com.trace.gtrack.data.network.response.ProjectKeysResponse

class ProjDropDownMenuAdapter(
    private var list: List<ProjectKeysResponse>?,
    val onClick: (ProjectKeysResponse?) -> Unit,
) :
    RecyclerView.Adapter<ProjDropDownMenuAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProjDropDownMenuAdapter.ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dd_menu_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list?.size ?: -1
    }

    override fun onBindViewHolder(holder: ProjDropDownMenuAdapter.ItemViewHolder, position: Int) {
        list?.get(position)?.let { holder.bind(it) }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var tvTitle: AppCompatTextView = view.findViewById(R.id.tv_status)
        fun bind(item: ProjectKeysResponse) {
            tvTitle.text = item.ProjectName
            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }

}