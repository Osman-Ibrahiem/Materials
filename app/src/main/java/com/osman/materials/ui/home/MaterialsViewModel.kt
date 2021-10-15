package com.osman.materials.ui.home

import androidx.lifecycle.ViewModel
import com.osman.materials.domain.BaseVS
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MaterialsViewModel @Inject internal constructor(
    private val materialsProcessor: MaterialsProcessor
) : ViewModel() {

    private var intentsSubject: PublishSubject<MaterialsIntent> = PublishSubject.create()
    private val statesSubject: Observable<BaseVS> = compose()

    private fun compose(): Observable<BaseVS> {
        return intentsSubject
            .compose(materialsProcessor.actionProcessor)
            .replay(1)
            .autoConnect(0)
    }

    fun processIntents(intents: Observable<MaterialsIntent>) {
        intents.subscribe(intentsSubject)
    }

    fun states(): Observable<BaseVS> {
        return statesSubject
    }

    override fun onCleared() {
        materialsProcessor.dispose()
        super.onCleared()
    }
}