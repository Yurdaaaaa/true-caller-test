package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.HtmlUseCaseObservable
import com.zj.hometest.core.util.LOG
import io.reactivex.Observable
import io.reactivex.Scheduler

class HtmlWordCounterUseCase(
    private val computationThreadScheduler: Scheduler
) : HtmlUseCaseObservable<String, Map<String, Int>> {

    override fun execute(input: String): Observable<Map<String, Int>> {

        if (input.isBlank()) return Observable.just(emptyMap())

        val words = input.splitToSequence(' ', '\n', '\r', '\t') // this should be faster than regex
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
            .toList()

        val availableProcessors = Runtime.getRuntime().availableProcessors()
        val coreUtilization = 0.70 // 70% -> dont be too greedy
        val maxChunkCount = 4

        val effectiveProcessors = (availableProcessors * coreUtilization)
            .toInt()
            .coerceIn(1, maxChunkCount)

        val chunks = getChunks(words, effectiveProcessors)

        return Observable.fromIterable(chunks)
            .flatMap(
                { chunk ->
                    Observable.fromCallable {
                        val map = mutableMapOf<String, Int>()
                        for (word in chunk) {
                            map[word] = map.getOrDefault(word, 0) + 1
                        }
                        map
                    }.subscribeOn(computationThreadScheduler)
                },
                effectiveProcessors // second param maxConcurrency
            )
            .reduce { accMap, chunkMap -> // first chunkMap is taken as initial map in accumulation, then we are merging the rest
                for ((word, count) in chunkMap) {
                    accMap[word] = accMap.getOrDefault(word, 0) + count // is word present it adds count otherwise default 0 + count
                }
                accMap
            }
            .toObservable()
            .map { wordCountMap ->
                wordCountMap.entries
                    .sortedByDescending { it.value }
                    .associateTo(LinkedHashMap()) { it.toPair() }  // linkedHashMap preserves order
            }
    }

    private fun getChunks(words: List<String>, effectiveProcessors: Int): List<List<String>> {
        val len = words.size
        return when {
            len < 3_000 -> listOf(words) // would otherwise end up as too much people in the kitchen
            len < 10_000 -> {
                LOG.d("getChunks chunk size: 2")
                words.chunked(len / 2) // max 2 chunks
            }
            else -> {
                val chunkSize = maxOf(1, len / effectiveProcessors)
                LOG.d("getChunks using $effectiveProcessors cores, chunk size: $chunkSize")

                words.chunked(chunkSize)
            }
        }
    }
}