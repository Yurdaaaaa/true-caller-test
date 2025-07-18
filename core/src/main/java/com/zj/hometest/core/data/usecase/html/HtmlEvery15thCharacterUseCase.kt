package com.zj.hometest.core.data.usecase.html

import com.zj.hometest.core.data.usecase.base.UseCaseSingle
import io.reactivex.Scheduler
import io.reactivex.Single

class HtmlEvery15thCharacterUseCase(
    private val computationThreadScheduler: Scheduler
) : UseCaseSingle<String, ArrayList<Char>> {

    override fun execute(input: String): Single<ArrayList<Char>> {
        return Single.fromCallable {
            val initialCapacity = input.length / 15 // to avoid unnecessary reallocation

            val list = ArrayList<Char>(initialCapacity)
            input.forEachIndexed { index, c ->
                if ((index + 1) % 15 == 0) list.add(c)
            }
            list
        }.subscribeOn(computationThreadScheduler)
    }
}