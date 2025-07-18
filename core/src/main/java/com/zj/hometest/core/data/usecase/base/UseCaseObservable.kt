package com.zj.hometest.core.data.usecase.base

import io.reactivex.Observable

interface UseCaseObservable<O> {
    fun execute(): Observable<O>
}