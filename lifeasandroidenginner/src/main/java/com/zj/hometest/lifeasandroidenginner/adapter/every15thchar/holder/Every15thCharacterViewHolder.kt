package com.zj.hometest.lifeasandroidenginner.adapter.every15thchar.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.hometest.core.data.model.Every15thCharacterListItem.*
import com.zj.hometest.lifeasandroidengineer.R

open class Every15thCharacterViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val htmlIndexPositionTextView: TextView = view.findViewById(R.id.htmlIndexPositionTextView)
    private val characterTextView: TextView = view.findViewById(R.id.emptyTextView)

    fun bind(dataItem: DataItem) {
        htmlIndexPositionTextView.setTextColor(itemView.context.getColor(com.zj.hometest.core.R.color.black))
        htmlIndexPositionTextView.text = itemView.context.getString(R.string.position_text_semicolon, dataItem.htmlPositionIndex)

        characterTextView.setTextColor(itemView.context.getColor(com.zj.hometest.core.R.color.black))
        characterTextView.text = dataItem.char.toString()
    }
}