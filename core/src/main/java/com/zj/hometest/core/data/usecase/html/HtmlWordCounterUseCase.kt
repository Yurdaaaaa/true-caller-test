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

        val startTime = System.nanoTime()

        val words = input.splitToSequence(' ', '\n', '\r', '\t') // faster than regex
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
            .toList()

        val availableProcessors = Runtime.getRuntime().availableProcessors()
        val coreUtilization = 0.70 // 70% -> don't be too greedy
        val maxChunkCount = 4

        val effectiveProcessors = (availableProcessors * coreUtilization)
            .toInt()
            .coerceIn(1, maxChunkCount)

        val chunks = getChunks(words, effectiveProcessors)
        LOG.d("HtmlWordCounterUseCase Words: ${words.size}, Chunks: ${chunks.size}")

        return Observable.fromIterable(chunks)
            .flatMap(
                { chunk ->
                    Observable.fromCallable {
                        val localWordCount = mutableMapOf<String, Int>()
                        for (word in chunk) {
                            localWordCount[word] = localWordCount.getOrDefault(word, 0) + 1
                        }
                        localWordCount
                    }.subscribeOn(computationThreadScheduler)
                },
                effectiveProcessors // maxConcurrency
            )
            .reduce { accMap, chunkMap ->
                for ((word, count) in chunkMap) {
                    accMap[word] = accMap.getOrDefault(word, 0) + count
                }
                accMap
            }
            .toObservable()
            .map { wordCountMap ->
                val sorted = wordCountMap.entries
                    .sortedByDescending { it.value }
                    .associateTo(LinkedHashMap()) { it.toPair() }

                val endTime = System.nanoTime()
                val durationMillis = (endTime - startTime) / 1_000_000
                LOG.d("HtmlWordCounterUseCase took $durationMillis ms")

                sorted
            }
    }

    private fun getChunks(words: List<String>, effectiveProcessors: Int): List<List<String>> {
        val len = words.size
        return when {
            len < 4_000 -> listOf(words)
            len < 10_000 -> {
                LOG.d("HtmlWordCounterUseCase getChunks chunk size: 2")
                words.chunked(len / 2)
            }
            len < 15_000 -> {
                LOG.d("HtmlWordCounterUseCase getChunks chunk size: 3")
                words.chunked(len / 3)
            }
            else -> {
                val chunkSize = maxOf(1, len / effectiveProcessors)
                LOG.d("HtmlWordCounterUseCase getChunks using $effectiveProcessors cores, chunk size: $chunkSize")
                words.chunked(chunkSize)
            }
        }
    }
}