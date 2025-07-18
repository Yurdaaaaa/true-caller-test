package com.zj.hometest.lifeasandroidenginner.adapter.worldcounter.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.hometest.core.data.model.WordCounterListItem
import com.zj.hometest.lifeasandroidengineer.R

open class WorldCounterViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val countTextView: TextView = view.findViewById(R.id.count)
    private val wordTextView: TextView = view.findViewById(R.id.word)

    fun bind(dataItem: WordCounterListItem.DataItem) {
        countTextView.setTextColor(itemView.context.getColor(com.zj.hometest.core.R.color.black))
        countTextView.text = itemView.context.getString(R.string.position_text_semicolon, dataItem.count)

        wordTextView.setTextColor(itemView.context.getColor(com.zj.hometest.core.R.color.black))
        wordTextView.text = dataItem.word
    }
}