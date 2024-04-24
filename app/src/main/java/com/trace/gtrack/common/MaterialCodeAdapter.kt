package com.trace.gtrack.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.trace.gtrack.R

class MaterialCodeAdapter(
    val onClick: (String?) -> Unit,
) :
    RecyclerView.Adapter<MaterialCodeAdapter.ItemViewHolder>() {
    private var lstMaterialCode: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MaterialCodeAdapter.ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.material_code_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lstMaterialCode.size
    }

    override fun onBindViewHolder(holder: MaterialCodeAdapter.ItemViewHolder, position: Int) {
        holder.bind(lstMaterialCode[position])
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

    fun updateSearchMaterialCodeList(lstMaterialCode: List<String>) {
        AppProgressDialog.hide()
        if (lstMaterialCode.isNotEmpty()) {
            this.lstMaterialCode.addAll(lstMaterialCode)
        } else {
            this.lstMaterialCode = ArrayList()
        }
        notifyDataSetChanged()
    }
}