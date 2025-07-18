package com.zj.hometest.core.data.usecase.base

import io.reactivex.Observable

interface NetworkUseCaseObservable<O> {
    fun execute(): Observable<O>
}