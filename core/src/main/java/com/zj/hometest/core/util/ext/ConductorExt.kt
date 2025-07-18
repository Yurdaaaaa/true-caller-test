package com.zj.hometest.core.util.ext

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.zj.hometest.core.ui.noAnimTransaction
import com.zj.hometest.core.ui.noAnimTransactionTagged

fun Router.peekBackstackTop(): Controller? {
    return this.backstack.firstOrNull()?.controller
}

fun Router.noAnimSetRoot(controller: Controller) {
    setRoot(noAnimTransaction(controller))
}

fun Router.noAnimSetRootTagged(controller: Controller, controllerTag: String) {
    setRoot(noAnimTransactionTagged(controller, controllerTag))
}