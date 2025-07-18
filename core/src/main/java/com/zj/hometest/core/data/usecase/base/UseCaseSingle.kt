package com.zj.hometest.core.data.usecase.base

import io.reactivex.Single

interface UseCaseSingle<I, O> {
    fun execute(input: I): Single<O>
}