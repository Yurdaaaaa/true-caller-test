package com.zj.hometest.core.data.mapper

import com.zj.hometest.core.data.model.Every15thCharacterListItem
import com.zj.hometest.core.data.model.Every15thCharacterListItem.*

object Every15thCharacterListItemMapper {

    fun mapItems(
        list: ArrayList<Char>,
        addError: Boolean
    ): MutableList<Every15thCharacterListItem> {

        val listItems = mutableListOf<Every15thCharacterListItem>()

        if (addError) {
            listItems.add(ErrorItem)
            return listItems
        }

        processCharacters(list, listItems)

        if (listItems.isEmpty()) {
            listItems.add(EmptyItem)
        }

        return listItems
    }

    private fun processCharacters(list: List<Char>, listItems: MutableList<Every15thCharacterListItem>) {
        list.forEachIndexed { index, char ->
            val htmlPositionIndex = (index+1)*15
            listItems.add(DataItem(char, index, htmlPositionIndex))
        }
    }
}