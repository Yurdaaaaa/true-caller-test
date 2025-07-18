package com.zj.hometest.core

import com.zj.hometest.core.data.usecase.html.HtmlEvery15thCharacterUseCase
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class HtmlEvery15thCharacterUseCaseTest {

    private lateinit var useCase: HtmlEvery15thCharacterUseCase
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        useCase = HtmlEvery15thCharacterUseCase(testScheduler)
    }

    @Test
    fun every15thCharacterLongTextUseCaseTest() {
        val input = "<p>truecaller</p><b>TRUECALLER</b>"
        val expected = arrayListOf('/', 'R') // index 14 = '/', index 29 = 'R'

        val testObserver = useCase.execute(input).test()
        testScheduler.triggerActions()

        testObserver.assertNoErrors()
        testObserver.assertValue(expected)
    }

    @Test
    fun every15thCharacterShotTextUseCase() {
        val input = "short string"
        val expected = arrayListOf<Char>()

        val testObserver = useCase.execute(input).test()
        testScheduler.triggerActions()

        testObserver.assertNoErrors()
        testObserver.assertValue(expected)
    }
}