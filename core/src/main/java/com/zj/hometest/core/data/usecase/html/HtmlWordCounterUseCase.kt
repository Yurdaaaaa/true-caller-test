package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.UseCaseSingle
import io.reactivex.Scheduler
import io.reactivex.Single

class HtmlWordCounterUseCase(
    private val computationThreadScheduler: Scheduler
) : UseCaseSingle<String, Map<String, Int>> {

    override fun execute(input: String): Single<Map<String, Int>> {
        return Single.fromCallable {
            Regex("\\S+").findAll(input)
                .map { it.value.lowercase() }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .toMap()
        }.subscribeOn(computationThreadScheduler)
    }
}