package com.zj.hometest.core

import com.zj.hometest.core.data.usecase.html.Html15thCharacterUseCase
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class Html15thCharacterUseCaseTest {

    private lateinit var useCase: Html15thCharacterUseCase
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        useCase = Html15thCharacterUseCase(testScheduler)
    }

    @Test
    fun `execute should return 15th character`() {
        val input = "<p>truecaller</p>"
        val expectedChar = '/' // index 14

        val testObserver = useCase.execute(input).test()
        testScheduler.triggerActions()

        testObserver.assertNoErrors()
        testObserver.assertValue(expectedChar)
    }

    @Test
    fun `execute should return an exception if input is shorter than 15 characters`() {
        val input = "not long text" // shorter than 15 chars

        val testObserver = useCase.execute(input).test()
        testScheduler.triggerActions()

        testObserver.assertError(IndexOutOfBoundsException::class.java)
        testObserver.assertNotComplete()
        testObserver.assertNoValues()
    }
}