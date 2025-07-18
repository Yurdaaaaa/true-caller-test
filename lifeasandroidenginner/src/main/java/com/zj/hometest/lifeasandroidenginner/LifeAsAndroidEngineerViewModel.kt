package com.zj.hometest.lifeasandroidenginner

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zj.hometest.core.data.usecase.html.Html15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlEvery15thCharacterUseCase
import com.zj.hometest.core.data.usecase.html.HtmlPageFetchLifeAsAndroidEngineerUseCase
import com.zj.hometest.core.data.usecase.html.HtmlWordCounterUseCase
import com.zj.hometest.core.net.TrueCallerNetworkManager.DownloadHtmlEvent
import com.zj.hometest.core.ui.ViewModel
import com.zj.hometest.core.util.LOG
import com.zj.hometest.core.util.ext.accept
import com.zj.hometest.core.util.ext.throwingSubscribe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.plusAssign

class LifeAsAndroidEngineerViewModel(
    private val navigator: LifeAsAndroidEngineerNavigator,
    private val fifteenthCharacterUseCase: Html15thCharacterUseCase,
    private val every15CharacterUseCase: HtmlEvery15thCharacterUseCase,
    private val wordCounterUseCase: HtmlWordCounterUseCase,
    private val fetchLifeAsDeveloperHtmlUseCase: HtmlPageFetchLifeAsAndroidEngineerUseCase,
    private val mainThreadScheduler: Scheduler,
    private val computationThreadScheduler: Scheduler
) : ViewModel<Nothing>() {

    private val uiEventRelay = BehaviorRelay.createDefault(UiEventState())
    val uiEventObservable: Observable<UiEventState>
        get() = uiEventRelay

    private fun fetchHtmlData() {
        disposables += fetchLifeAsDeveloperHtmlUseCase.execute()
            .observeOn(mainThreadScheduler)
            .throwingSubscribe { event ->
                when (event) {
                    is DownloadHtmlEvent.Started -> {
                        LOG.d("Started request")
                        uiEventRelay.accept { it.copy(event = UiState.Phase.Loading) }
                    }
                    is DownloadHtmlEvent.Success -> {
                        LOG.d("Success request ${event.body}")
                        parseHtml(event.body)
                    }
                    is DownloadHtmlEvent.Error -> {
                        LOG.e("Error request ${event.error}")
                        uiEventRelay.accept { it.copy(event = UiState.Error.ErrorHtmlFetch) }
                    }
                }
            }
    }

    private fun parseHtml(html: String) {
        getFifteenthCharacter(html)
        getEvery15thCharacter(html)
        getWordCounts(html)
    }

    private fun getFifteenthCharacter(html: String) {
        disposables += fifteenthCharacterUseCase.execute(html)
            .observeOn(mainThreadScheduler)
            .subscribe({ char ->
                LOG.d("15th character: $char")
                uiEventRelay.accept { it.copy(event = UiState.Data.ShowFifteenthCharacter(char)) }
            }, { error ->
                LOG.e("Error in char15UseCase: $error")
                uiEventRelay.accept { it.copy(event = UiState.Error.ErrorFifteenthCharacter) }
            })
    }

    private fun getEvery15thCharacter(html: String) {
        disposables += every15CharacterUseCase.execute(html)
            .observeOn(mainThreadScheduler)
            .subscribe({ listOfChars ->
                LOG.d("Every 15th character: ${listOfChars.joinToString()}")
                uiEventRelay.accept { it.copy(event = UiState.Data.ShowEvery15thCharacter(listOfChars)) }
            }, { error ->
                LOG.e("Error in every15thUseCase: $error")
                uiEventRelay.accept { it.copy(event = UiState.Error.ErrorEvery15thCharacter) }
            })
    }

    private fun getWordCounts(html: String) {
        println("input size: ${html.length}")
        disposables += wordCounterUseCase.execute(html)
            .subscribeOn(computationThreadScheduler) // tokenization, reduce, map wont run on UI thread but on computation thread
            .observeOn(mainThreadScheduler)
            .subscribe({ wordCountMap ->
                LOG.d("Word counts: ${wordCountMap.entries.joinToString { "${it.key}=${it.value}" }}")
                uiEventRelay.accept { it.copy(event = UiState.Data.ShowWordCount(wordCountMap)) }
            }, { error ->
                LOG.e("Error in wordCountUseCase: $error")
                uiEventRelay.accept { it.copy(event = UiState.Error.ErrorWordCount) }
            })
    }

    fun loadDataButtonClicked() {
        fetchHtmlData()
    }

    fun retryButtonClicked() {
        fetchHtmlData()
    }

    fun forceInitialStateClicked() {
        uiEventRelay.accept { it.copy(event = UiState.Phase.Initial) }
    }

    fun onAnotherScreenClicked() {
        navigator.goToAnotherScreen()
    }
}

data class UiEventState(
    val event: UiState = UiState.Phase.Initial
)

sealed class UiState {

    sealed class Phase : UiState() {
        object Initial : Phase()
        object Loading : Phase()
    }

    sealed class Data : UiState() {
        data class ShowFifteenthCharacter(val char: Char) : Data()
        data class ShowEvery15thCharacter(val listOfChars: ArrayList<Char>) : Data()
        data class ShowWordCount(val countsMap: Map<String, Int>) : Data()
    }

    sealed class Error : UiState() {
        object ErrorFifteenthCharacter : Error()
        object ErrorEvery15thCharacter : Error()
        object ErrorWordCount : Error()
        object ErrorHtmlFetch : Error()
    }
}

