package com.zj.hometest.core.util

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DisableLOGRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                LOG.disable()
                base.evaluate()
            }
        }
    }
}