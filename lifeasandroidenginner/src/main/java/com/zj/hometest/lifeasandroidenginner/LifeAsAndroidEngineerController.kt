package com.zj.hometest.lifeasandroidenginner

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zj.hometest.core.data.mapper.Every15thCharacterListItemMapper
import com.zj.hometest.core.data.mapper.WordCounterListItemMapper
import com.zj.hometest.core.di.AppComponent
import com.zj.hometest.core.ui.BaseController
import com.zj.hometest.core.ui.ViewBinding
import com.zj.hometest.core.util.LOG
import com.zj.hometest.core.util.ext.bundleOf
import com.zj.hometest.core.util.ext.isDarkMode
import com.zj.hometest.core.util.ext.throwingSubscribe
import com.zj.hometest.core.view.TintToolbar
import com.zj.hometest.lifeasandroidengineer.R
import com.zj.hometest.lifeasandroidenginner.adapter.every15thchar.Every15thCharacterAdapter
import com.zj.hometest.lifeasandroidenginner.adapter.every15thchar.IEvery15CharacterAdapter
import com.zj.hometest.lifeasandroidenginner.adapter.worldcounter.IWorldCounterAdapter
import com.zj.hometest.lifeasandroidenginner.adapter.worldcounter.WordCounterAdapter
import com.zj.hometest.lifeasandroidenginner.di.DaggerLifeAsAndroidEngineerControllerComponent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class LifeAsAndroidEngineerController : BaseController<LifeAsAndroidEngineerViewModel, LifeAsAndroidEngineerBinding, Nothing>,
    LifeAsAndroidEngineerNavigator {

    constructor() : this(bundleOf {
    })

    constructor(bundle: Bundle) : super(bundle)

    override fun hasLightStatusBar() = true
    override fun hasLightNavBar() = true
    override fun hasUpButton() = false

    override fun layoutRes(): Int = R.layout.controller_life_as_android_enginner

    override fun onCreateViewBinding(view: View): LifeAsAndroidEngineerBinding = LifeAsAndroidEngineerBinding(view)

    override fun onCreateViewModel(
        appComponent: AppComponent,
        savedState: Nothing?
    ): LifeAsAndroidEngineerViewModel {

        return DaggerLifeAsAndroidEngineerControllerComponent.factory()
            .create(
                appComponent = appComponent,
                controller = this
            )
            .lifeAsAndroidEngineerViewModel
    }

    override fun onSetupToolbar(toolbar: TintToolbar) {
        super.onSetupToolbar(toolbar)
        val context = toolbar.context

        toolbar.inflateMenu(R.menu.menu_life_as_android_dev)
        toolbar.setTitle(context.getString(R.string.title_life_as_android_engineer))
        if (appComponent.context.resources.configuration.isDarkMode) {
            toolbar.setTitleTextColor(context.getColor(com.zj.hometest.core.R.color.white))
        } else {
            toolbar.setTitleTextColor(context.getColor(com.zj.hometest.core.R.color.black))
        }
    }

    override fun handleMenuItemClicked(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_initial_state -> {
                viewModel.forceInitialStateClicked()
                true
            }
            else -> super.handleMenuItemClicked(item)
        }
    }

    override fun onViewCreated(
        activity: Activity,
        viewBinding: LifeAsAndroidEngineerBinding,
        viewModel: LifeAsAndroidEngineerViewModel,
        savedViewState: Bundle?
    ) {
        createEvery15CharacterAdapter(activity, viewBinding)
        createWordCounterAdapter(activity, viewBinding)

        viewBinding.let {
            it.loadDataButton.setOnClickListener {
                viewModel.loadDataButtonClicked()
            }

            it.retryButton.setOnClickListener {
                viewModel.retryButtonClicked()
            }
        }
    }

    override fun onViewAttached(
        activity: Activity,
        viewBinding: LifeAsAndroidEngineerBinding,
        viewModel: LifeAsAndroidEngineerViewModel,
        disposables: CompositeDisposable
    ) {

        val uiEventObservable = viewModel.uiEventObservable.share()

        disposables += uiEventObservable
            .filter { it.event is  UiState.Phase }
            .subscribeOn(Schedulers.io())
            .observeOn(appComponent.mainThreadScheduler)
            .throwingSubscribe {
                LOG.d("uiEvent phase: ${it.event}")

                when(it.event) {
                    UiState.Phase.Initial -> {
                        viewBinding.phaseLayout.visibility = View.VISIBLE
                        viewBinding.dataLayout.visibility = View.GONE
                        viewBinding.errorHtmlFetchLayout.visibility = View.GONE
                        viewBinding.loadDataButton.visibility = View.VISIBLE
                        viewBinding.progressBar.visibility = View.GONE
                    }
                    UiState.Phase.Loading -> {
                        viewBinding.phaseLayout.visibility = View.VISIBLE
                        viewBinding.dataLayout.visibility = View.GONE
                        viewBinding.errorHtmlFetchLayout.visibility = View.GONE
                        viewBinding.loadDataButton.visibility = View.GONE
                        viewBinding.progressBar.visibility = View.VISIBLE
                    }
                    else -> {
                        LOG.e("uiEvent Phase: wrong event: ${it.event}")
                    }
                }
            }

        disposables += uiEventObservable
            .filter { it.event is UiState.Data }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(appComponent.mainThreadScheduler)
            .throwingSubscribe {
                LOG.d("uiEvent data: ${it.event}")

                viewBinding.phaseLayout.visibility = View.GONE
                viewBinding.errorHtmlFetchLayout.visibility = View.GONE
                viewBinding.dataLayout.visibility = View.VISIBLE

                when(val event = it.event) {
                    is UiState.Data.ShowEvery15thCharacter -> {
                        mapEvery15thCharacterItems(viewBinding, event.listOfChars)
                    }
                    is UiState.Data.ShowFifteenthCharacter -> {
                        viewBinding.fifteenthCharacterTextView.text = event.char.toString()
                    }
                    is UiState.Data.ShowWordCount -> {
                        mapWorldCounterItems(viewBinding, event.countsMap)
                    }
                    else -> {
                        LOG.e("uiEvent success: wrong event: ${it.event}")
                    }
                }
            }

        disposables += uiEventObservable
            .filter { it.event is UiState.Error }
            .subscribeOn(Schedulers.io())
            .observeOn(appComponent.mainThreadScheduler)
            .throwingSubscribe {
                LOG.e("uiEvent errors: ${it.event}")

                when(it.event) {
                    UiState.Error.ErrorEvery15thCharacter -> {
                        mapEvery15thCharacterItems(viewBinding, ArrayList(), true)
                    }
                    UiState.Error.ErrorFifteenthCharacter -> {
                        viewBinding.fifteenthCharacterTextView.text = activity.getString(R.string.error_item_15_th_char)
                    }
                    UiState.Error.ErrorWordCount -> {
                        mapWorldCounterItems(viewBinding, mapOf(), true)
                    }
                    UiState.Error.ErrorHtmlFetch -> {
                        viewBinding.phaseLayout.visibility = View.GONE
                        viewBinding.dataLayout.visibility = View.GONE
                        viewBinding.errorHtmlFetchLayout.visibility = View.VISIBLE
                    }
                    else -> {
                        LOG.e("uiEvent errors: wrong event: ${it.event}")
                    }
                }
            }
    }

    private fun mapEvery15thCharacterItems(viewBinding: LifeAsAndroidEngineerBinding, listOfChars: ArrayList<Char>, showError: Boolean = false) {
        val adapter = viewBinding.recyclerViewEvery15thCharacter.adapter as IEvery15CharacterAdapter<*>

        disposables += Observable.fromCallable {
                listOfChars
            }
            .flatMap { list ->
                Observable.fromCallable {
                    Every15thCharacterListItemMapper.mapItems(list, showError)
                }.subscribeOn(Schedulers.computation())
            }
            .observeOn(appComponent.mainThreadScheduler)
            .throwingSubscribe { items ->
                LOG.d("every 15 characters items size: ${items.size}")
                adapter.setData(items,
                    preCalculate = {
                        0
                    }
                ) { diff, scroll ->

                }
            }
    }

    private fun mapWorldCounterItems(viewBinding: LifeAsAndroidEngineerBinding, wordMap: Map<String, Int>, showError: Boolean = false) {
        val adapter = viewBinding.recyclerViewWordCount.adapter as IWorldCounterAdapter<*>

        disposables += Observable.fromCallable {
            wordMap
        }
        .flatMap { list ->
            Observable.fromCallable {
                WordCounterListItemMapper.mapItems(wordMap, showError)
            }.subscribeOn(Schedulers.computation())
        }
        .observeOn(appComponent.mainThreadScheduler)
        .throwingSubscribe { items ->
            LOG.d("world counter items size: ${items.size}")
            adapter.setData(items,
                preCalculate = {
                    0
                }
            ) { diff, scroll ->

            }
        }
    }

    private fun createEvery15CharacterAdapter(
        activity: Activity,
        viewBinding: LifeAsAndroidEngineerBinding
    ) {
        viewBinding.recyclerViewEvery15thCharacter.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter =  Every15thCharacterAdapter(activity)
        }
    }

    private fun createWordCounterAdapter(
        activity: Activity,
        viewBinding: LifeAsAndroidEngineerBinding
    ) {
        viewBinding.recyclerViewWordCount.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter =  WordCounterAdapter(activity)
        }
    }

    override fun goToAnotherScreen() {
        // go to another screen
    }
}

class LifeAsAndroidEngineerBinding(view: View) : ViewBinding(view) {

    val phaseLayout: LinearLayout = view.findViewById(R.id.phaseLayout)
    val loadDataButton: Button = view.findViewById(R.id.loadDataButton)
    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

    val errorHtmlFetchLayout: LinearLayout = view.findViewById(R.id.errorHtmlFetchLayout)
    val retryButton: Button = view.findViewById(R.id.retryButton)

    val dataLayout: LinearLayout = view.findViewById(R.id.dataLayout)
    val fifteenthCharacterTextView: TextView = view.findViewById(R.id.fifteenthCharacterTextView)

    val recyclerViewEvery15thCharacter: RecyclerView = view.findViewById(R.id.recyclerViewEvery15thCharacter)
    val recyclerViewWordCount: RecyclerView = view.findViewById(R.id.recyclerViewWordCount)
}

interface LifeAsAndroidEngineerNavigator {
    fun goToAnotherScreen()
}