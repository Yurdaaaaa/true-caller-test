package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.HtmlUseCaseSingle
import io.reactivex.Scheduler
import io.reactivex.Single

class Html15thCharacterUseCase(
    private val computationThreadScheduler: Scheduler
) : HtmlUseCaseSingle<String, Char> {

    override fun execute(input: String): Single<Char> {
        return Single.defer {
            input.getOrNull(14)?.let { char ->
                Single.just(char)
            } ?: Single.error(IndexOutOfBoundsException("Input string is too short."))
        }.subscribeOn(computationThreadScheduler)
    }

}