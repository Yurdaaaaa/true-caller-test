package com.zj.hometest.core.data.model

sealed class WordCounterListItem {
    object ErrorItem : WordCounterListItem()
    data class DataItem(val word: String, val count: Int) : WordCounterListItem()
    object EmptyItem : WordCounterListItem()
}
