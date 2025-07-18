package com.zj.hometest.core.data.usecase.base

import io.reactivex.Single

interface HtmlUseCaseSingle<I, O> {
    fun execute(input: I): Single<O>
}