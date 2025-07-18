package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.HtmlUseCaseObservable
import io.reactivex.Observable
import io.reactivex.Scheduler

class HtmlWordCounterUseCase(
    private val computationThreadScheduler: Scheduler
) : HtmlUseCaseObservable<String, Map<String, Int>> {

    override fun execute(input: String): Observable<Map<String, Int>> {
        val words = input.splitToSequence(' ', '\n', '\r', '\t') // this should be faster than regex
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
            .toList()

        val chunkSize = 10_000
        val chunks = words.chunked(chunkSize)

        return Observable.fromIterable(chunks)
            .flatMap(
                { chunk ->
                    Observable.fromCallable {
                        val map = mutableMapOf<String, Int>()
                        for (word in chunk) {
                            if (map.containsKey(word)) {
                                map[word] = map[word]!! + 1 // increment
                            } else {
                                map[word] = 1 // first occurrence
                            }
                        }
                        map
                    }.subscribeOn(computationThreadScheduler)
                },
                Runtime.getRuntime().availableProcessors()
            )
            .reduce { acc, chunkMap -> // first chunkMap is taken as initial map in accumulation, then we are merging the rest
                for ((k, count) in chunkMap) {
                    if (acc.containsKey(k)) {
                        acc[k] = acc[k]!! + count // count whats already in map + chunk count
                    } else {
                        acc[k] = count
                    }
                }
                acc
            }
            .toObservable()
            .map { wordCountMap ->
                wordCountMap.entries
                    .sortedByDescending { it.value }
                    .associateTo(LinkedHashMap()) { it.toPair() }  // linkedHashMap preserves order
            }
    }
}