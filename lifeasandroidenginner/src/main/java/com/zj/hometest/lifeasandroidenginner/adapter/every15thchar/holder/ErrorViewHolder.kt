package com.zj.hometest.lifeasandroidenginner.adapter.every15thchar.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.hometest.lifeasandroidengineer.R

open class ErrorViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val text: TextView = view.findViewById(R.id.errorText)

    fun bind() {
        text.setTextColor(itemView.context.getColor(com.zj.hometest.core.R.color.red))
    }
}