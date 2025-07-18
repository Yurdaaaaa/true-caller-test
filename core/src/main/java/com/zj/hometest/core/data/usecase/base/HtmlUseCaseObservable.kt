package com.zj.hometest.core.data.usecase.base

import io.reactivex.Observable

interface HtmlUseCaseObservable<I, O> {
    fun execute(input: I): Observable<O>
}