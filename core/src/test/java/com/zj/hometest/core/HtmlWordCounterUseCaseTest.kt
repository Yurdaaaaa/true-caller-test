package com.zj.hometest.core

import com.zj.hometest.core.data.usecase.html.HtmlWordCounterUseCase
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class HtmlWordCounterUseCaseTest {

    private lateinit var useCase: HtmlWordCounterUseCase
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        useCase = HtmlWordCounterUseCase(testScheduler)
    }

    @Test
    fun `htmlUniqueWorldCounterCaseInSensitiveTest`() {
        val html = "<p> TRUECALLER Hello World </p> TrueCALLER truecaller"
        val expected = mapOf(
            "<p>" to 1,
            "truecaller" to 3,
            "hello" to 1,
            "world" to 1,
            "</p>" to 1
        )

        val testObserver = useCase.execute(html).test()
        testScheduler.triggerActions()

        testObserver.assertNoErrors()
        testObserver.assertValue(expected)
    }
}