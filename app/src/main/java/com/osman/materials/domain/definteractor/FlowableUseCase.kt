package com.osman.materials.domain.definteractor

import com.osman.materials.domain.diinterfaces.excuter.PostExecutionThread
import com.osman.materials.domain.diinterfaces.excuter.ThreadExecutor
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Abstract class for a UseCase that returns an instance of a [Single].
 */
abstract class FlowableUseCase<T, in Params> constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread
) {

    private val disposables = CompositeDisposable()

    /**
     * Builds a [Single] which will be used when the current [FlowableUseCase] is executed.
     */
    protected abstract fun buildUseCaseObservable(params: Params? = null): Flowable<T>

    /**
     * Executes the current use case.
     */
    open fun isOnUiThread(): Boolean {
        return false
    }

    open fun execute(params: Params? = null): Flowable<T> {
        return this.buildUseCaseObservable(params)
            .subscribeOn(
                if (isOnUiThread()) postExecutionThread.scheduler else Schedulers.from(
                    threadExecutor
                )
            )
            .observeOn(postExecutionThread.scheduler)
//             .doOnError { e->
//                 if(e is MyRemoteException){
//                 }else{
//                     throw MyRemoteException(0,"من فضلك تأكد من اتصالك بالانترنت")
//                 }
//             }

//        addDisposable(single.subscribeWith(singleObserver))
    }

    /**
     * Dispose from current [CompositeDisposable].
     */
    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }

    /**
     * Dispose from current [CompositeDisposable].
     */
    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

}