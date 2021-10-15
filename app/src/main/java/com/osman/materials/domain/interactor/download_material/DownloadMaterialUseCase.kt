package com.osman.materials.domain.interactor.download_material

import com.osman.materials.domain.IRepository
import com.osman.materials.domain.definteractor.FlowableUseCase
import com.osman.materials.domain.definteractor.SingleUseCase
import com.osman.materials.domain.diinterfaces.excuter.PostExecutionThread
import com.osman.materials.domain.diinterfaces.excuter.ThreadExecutor
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class DownloadMaterialUseCase @Inject constructor(
    val repository: IRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : FlowableUseCase<DownloadMaterialsVS, Pair<Int, String>>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Pair<Int, String>?): Flowable<DownloadMaterialsVS> {
        return repository.downloadFile(params?.second ?: "", params?.first ?: 0)
    }

}