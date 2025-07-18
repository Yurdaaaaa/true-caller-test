package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.HtmlUseCaseSingle
import io.reactivex.Scheduler
import io.reactivex.Single

class Html15thCharacterUseCase(
    private val computationThreadScheduler: Scheduler
) : HtmlUseCaseSingle<String, Char> {

    override fun execute(input: String): Single<Char> {
        return Single.fromCallable {
            input.getOrNull(14) ?: '❌'
        }.subscribeOn(computationThreadScheduler)
    }
}