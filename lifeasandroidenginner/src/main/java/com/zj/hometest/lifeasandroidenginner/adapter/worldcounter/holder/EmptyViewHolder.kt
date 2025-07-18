package com.zj.hometest.lifeasandroidenginner.adapter.worldcounter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zj.hometest.lifeasandroidengineer.R

open class EmptyViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val icon: ImageView = view.findViewById(R.id.iconEmpty)
    private val emptyText: TextView = view.findViewById(R.id.emptyTextView)
    private val emptySubtitleText: TextView = view.findViewById(R.id.emptySubtitleTextView)

    fun bind() {
        val iconColorState = ContextCompat.getColorStateList(itemView.context, com.zj.hometest.core.R.color.black)
        icon.imageTintList = iconColorState

        emptyText.setTextColor(itemView.context.getColor(com.zj.hometest.core.R.color.black))
        emptySubtitleText.setTextColor(itemView.context.getColor(com.zj.hometest.core.R.color.black))
    }
}