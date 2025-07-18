package com.zj.hometest.core.data.model

sealed class Every15thCharacterListItem {
    object ErrorItem : Every15thCharacterListItem()
    data class DataItem(val char: Char, val index: Int, val htmlPositionIndex: Int) : Every15thCharacterListItem()
    object EmptyItem : Every15thCharacterListItem()
}
