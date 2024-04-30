package com.trace.gtrack.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.trace.gtrack.R

class MaterialItemAdapter(
    val onClick: (String?) -> Unit,
    private val lstMaterialCode: List<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_VIEW_TYPE_ITEM = 0
    private val ITEM_VIEW_TYPE_PROGRESS_BAR = 1
    private var showProgressBar = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater =
            LayoutInflater.from(parent.context)
        return if (viewType == ITEM_VIEW_TYPE_ITEM) {
            ItemViewHolder(
                inflater.inflate(R.layout.material_code_item, parent, false)
            )
        } else {
            LoadHolder(
                inflater.inflate(R.layout.row_load, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemViewHolder) {
            holder.bind(lstMaterialCode[position])
        } else if (holder is LoadHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return if (showProgressBar) {
            lstMaterialCode.size + 1 // Add 1 for the progress bar
        } else {
            lstMaterialCode.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < lstMaterialCode.size) {
            ITEM_VIEW_TYPE_ITEM
        } else {
            ITEM_VIEW_TYPE_PROGRESS_BAR
        }
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


    inner class LoadHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        fun bind() {
            itemView.visibility = if (showProgressBar) View.VISIBLE else View.GONE
        }
    }

    fun showProgressBarNotify(show: Boolean) {
        showProgressBar = show
        notifyDataSetChanged()
    }

    /*fun updateSearchMaterialCodeList() {
        *//*AppProgressDialog.hide()
        if (lstMaterialCode.isNotEmpty()) {
            this.lstMaterialCode.addAll(lstMaterialCode)
        }*//*
        notifyDataSetChanged()
    }*/
}