package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.HtmlUseCaseObservable
import com.zj.hometest.core.util.LOG
import io.reactivex.Observable
import io.reactivex.Scheduler

class HtmlWordCounterUseCase(
    private val computationThreadScheduler: Scheduler
) : HtmlUseCaseObservable<String, Map<String, Int>> {

    private val MIN_NUMBER_OF_CORES = 1
    private val MAX_NUMBER_OF_CORES = 4

    override fun execute(input: String): Observable<Map<String, Int>> {

        if (input.isBlank()) return Observable.just(emptyMap())

        val startTime = System.nanoTime()

        val availableProcessors = Runtime.getRuntime().availableProcessors()
        val coreUtilization = 0.70 // 70% -> don't be too greedy
        val effectiveCores = (availableProcessors * coreUtilization)
            .toInt()
            .coerceIn(MIN_NUMBER_OF_CORES, MAX_NUMBER_OF_CORES)

        return Observable.fromCallable {
            val words = input.splitToSequence(' ', '\n', '\r', '\t')
                .filter { it.isNotBlank() }
                .map { it.lowercase() }
                .toList()

            val chunks = getChunks(words, effectiveCores)
            LOG.d("HtmlWordCounterUseCase Words: ${words.size}, Chunks: ${chunks.size}")

            chunks
        }
        .flatMap { chunks ->
            Observable.fromIterable(chunks)
        }
        .flatMap(
            { chunk ->
                Observable.fromCallable {
                    val chunkMap = mutableMapOf<String, Int>()
                    for (word in chunk) {
                        chunkMap[word] = chunkMap.getOrDefault(word, 0) + 1
                    }
                    chunkMap
                }.subscribeOn(computationThreadScheduler)
            },
            effectiveCores // maxConcurrency
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

    // this solves too much people in the kitchen
    private fun getChunks(words: List<String>, effectiveCores: Int): List<List<String>> {
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
                val chunkSize = maxOf(MIN_NUMBER_OF_CORES, len / effectiveCores)
                LOG.d("HtmlWordCounterUseCase getChunks using $effectiveCores cores, chunk size: $chunkSize")
                words.chunked(chunkSize)
            }
        }
    }
}