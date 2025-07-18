package com.zj.hometest.lifeasandroidenginner.adapter.every15thchar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zj.hometest.core.data.model.Every15thCharacterListItem
import com.zj.hometest.core.data.model.Every15thCharacterListItem.*
import com.zj.hometest.core.util.diffutil.Diff
import com.zj.hometest.core.util.diffutil.DiffUtilAdapter
import com.zj.hometest.core.util.diffutil.FLAG_CHANGE__ANONYMOUS
import com.zj.hometest.core.util.diffutil.ListDiffer
import com.zj.hometest.core.util.diffutil.applyDiff
import com.zj.hometest.lifeasandroidengineer.R
import com.zj.hometest.lifeasandroidenginner.adapter.every15thchar.holder.EmptyViewHolder
import com.zj.hometest.lifeasandroidenginner.adapter.every15thchar.holder.ErrorViewHolder
import com.zj.hometest.lifeasandroidenginner.adapter.every15thchar.holder.Every15thCharacterViewHolder

private val TYPE_DATA_ITEM = R.layout.data_item_every_15h_char
private val TYPE_EMPTY_LIST = R.layout.empty_item_every_15th_char
private val TYPE_ERROR_ITEM = R.layout.error_item_every_15th_char

interface IEvery15CharacterAdapter<T> {
    fun setData(
        list: List<Every15thCharacterListItem>,
        preCalculate: () -> Int,
        onCalculated: (Diff<Every15thCharacterListItem>, Int) -> Unit)
    fun isNotEmpty(): Boolean
    fun isEmpty(): Boolean
    fun clearDiffer()
    fun clearData()
}

class Every15thCharacterAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    IEvery15CharacterAdapter<Every15thCharacterListItem> {

    private val listDiffer = Every15thCharacterListDiffer()
    private val layoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Any>()

    override fun setData(list: List<Every15thCharacterListItem>, preCalculate: () -> Int, onCalculated: (Diff<Every15thCharacterListItem>, Int) -> Unit) {
        println("Every15thCharacterAdapter setData size: ${list.size} list: $list")

        listDiffer.calculate(list) { diff ->
            val cached = preCalculate()
            items = diff.list
            applyDiff(diff.result)
            onCalculated(diff, cached)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATA_ITEM -> {
                val view = layoutInflater.inflate(TYPE_DATA_ITEM, parent, false)
                Every15thCharacterViewHolder(view)
            }
            TYPE_EMPTY_LIST -> {
                val view = layoutInflater.inflate(TYPE_EMPTY_LIST, parent, false)
                EmptyViewHolder(view)
            }
            TYPE_ERROR_ITEM -> {
                val view = layoutInflater.inflate(TYPE_ERROR_ITEM, parent, false)
                ErrorViewHolder(view)
            }
            else -> error("Unknown view type '$viewType'")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val viewType = getItemViewType(position)) {
            TYPE_DATA_ITEM -> {
                (holder as Every15thCharacterViewHolder).bind((items[position] as DataItem))
            }
            TYPE_EMPTY_LIST -> (holder as EmptyViewHolder).bind()
            TYPE_ERROR_ITEM -> (holder as ErrorViewHolder).bind()
            else -> error("Unknown view type '$viewType'")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is DataItem -> TYPE_DATA_ITEM
            is EmptyItem -> TYPE_EMPTY_LIST
            is ErrorItem -> TYPE_ERROR_ITEM
            else -> error("Unknown type '$item'")
        }
    }

    override fun getItemCount() = items.size

    override fun isNotEmpty(): Boolean {
        return items.isNotEmpty()
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun clearDiffer() {
        listDiffer.clear()
    }

    override fun clearData() {
        items = listOf()
        notifyDataSetChanged()
    }
}

internal class Every15thCharacterListDiffer : ListDiffer<Every15thCharacterListItem>(
    areIdenticalPredicate = { old, new ->
        when {
            old is DataItem && new is DataItem -> old.index == new.index && old.char == new.char
            old is ErrorItem && new is ErrorItem -> true
            old is EmptyItem && new is EmptyItem -> true
            else -> false
        }
    },
    areEqualPredicate = { old, new ->
        when {
            old is DataItem && new is DataItem -> messageItemEquals(old, new)
            old is ErrorItem && new is ErrorItem -> true
            old is EmptyItem && new is EmptyItem -> true
            else -> old == new
        }
    },
    payloadSelector = { old, new ->
        when {
            old is DataItem && new is DataItem -> Every15thCharacterDataItemDiffUtilAdapter.getChangePayload(old, new)
            else -> null
        }
    },
    detectMoves = true
)

private fun messageItemEquals(old: DataItem, new: DataItem): Boolean {
    return Every15thCharacterDataItemDiffUtilAdapter.areEqual(old, new)
}

object Every15thCharacterDataItemDiffUtilAdapter : DiffUtilAdapter<DataItem>(supportsPayloads = true) {
    override fun fullRebindChanges(l: DataItem, r: DataItem): Int {
        var flags = super.fullRebindChanges(l, r)

        if (l.char != r.char) flags = flags or FLAG_CHANGE__ANONYMOUS

        return flags
    }
}