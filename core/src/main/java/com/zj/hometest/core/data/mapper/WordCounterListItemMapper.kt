package com.zj.hometest.core.data.mapper

import com.zj.hometest.core.data.model.WordCounterListItem

object WordCounterListItemMapper {

    fun mapItems(
        list: Map<String, Int>,
        addError: Boolean
    ): MutableList<WordCounterListItem> {

        val listItems = mutableListOf<WordCounterListItem>()

        if (addError) {
            listItems.add(WordCounterListItem.ErrorItem)
            return listItems
        }

        processWords(list, listItems)

        if (listItems.isEmpty()) {
            listItems.add(WordCounterListItem.EmptyItem)
        }

        return listItems
    }

    private fun processWords(map: Map<String, Int>, listItems: MutableList<WordCounterListItem>) {
        map.forEach { (word, count) ->
            listItems.add(WordCounterListItem.DataItem(word, count))
        }
    }
}