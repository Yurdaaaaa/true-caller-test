package com.zj.hometest.lifeasandroidenginner.adapter.worldcounter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zj.hometest.core.data.model.WordCounterListItem
import com.zj.hometest.core.data.model.WordCounterListItem.*
import com.zj.hometest.core.util.diffutil.Diff
import com.zj.hometest.core.util.diffutil.DiffUtilAdapter
import com.zj.hometest.core.util.diffutil.FLAG_CHANGE__ANONYMOUS
import com.zj.hometest.core.util.diffutil.ListDiffer
import com.zj.hometest.core.util.diffutil.applyDiff
import com.zj.hometest.lifeasandroidengineer.R
import com.zj.hometest.lifeasandroidenginner.adapter.worldcounter.holder.EmptyViewHolder
import com.zj.hometest.lifeasandroidenginner.adapter.worldcounter.holder.ErrorViewHolder
import com.zj.hometest.lifeasandroidenginner.adapter.worldcounter.holder.WorldCounterViewHolder

private val TYPE_DATA_ITEM = R.layout.data_item_word_counter
private val TYPE_EMPTY_LIST = R.layout.empty_item_word_counter
private val TYPE_ERROR_ITEM = R.layout.error_item_word_counter

interface IWorldCounterAdapter<T> {
    fun setData(
        list: List<WordCounterListItem>,
        preCalculate: () -> Int,
        onCalculated: (Diff<WordCounterListItem>, Int) -> Unit)
    fun isNotEmpty(): Boolean
    fun isEmpty(): Boolean
    fun clearDiffer()
    fun clearData()
}

class WordCounterAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    IWorldCounterAdapter<WordCounterListItem> {

    private val listDiffer = WorldCounterListDiffer()
    private val layoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Any>()

    override fun setData(list: List<WordCounterListItem>, preCalculate: () -> Int, onCalculated: (Diff<WordCounterListItem>, Int) -> Unit) {
        println("WordCounterAdapter setData size: ${list.size} list: $list")

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
                WorldCounterViewHolder(view)
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
                (holder as WorldCounterViewHolder).bind((items[position] as DataItem))
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

internal class WorldCounterListDiffer : ListDiffer<WordCounterListItem>(
    areIdenticalPredicate = { old, new ->
        when {
            old is DataItem && new is DataItem -> old.word == new.word && old.count == new.count
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
            old is DataItem && new is DataItem -> WorldCounterDataItemDiffUtilAdapter.getChangePayload(old, new)
            else -> null
        }
    },
    detectMoves = true
)

private fun messageItemEquals(old: DataItem, new: DataItem): Boolean {
    return WorldCounterDataItemDiffUtilAdapter.areEqual(old, new)
}

object WorldCounterDataItemDiffUtilAdapter : DiffUtilAdapter<DataItem>(supportsPayloads = true) {
    override fun fullRebindChanges(l: DataItem, r: DataItem): Int {
        var flags = super.fullRebindChanges(l, r)

        if (l.word != r.word) flags = flags or FLAG_CHANGE__ANONYMOUS

        return flags
    }
}