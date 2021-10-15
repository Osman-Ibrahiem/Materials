package com.osman.materials.domain.interactor.get_materials

import com.osman.materials.domain.IRepository
import com.osman.materials.domain.definteractor.SingleUseCase
import com.osman.materials.domain.diinterfaces.excuter.PostExecutionThread
import com.osman.materials.domain.diinterfaces.excuter.ThreadExecutor
import io.reactivex.Single
import javax.inject.Inject

class GetMaterialsUseCase @Inject constructor(
    val repository: IRepository,
    threadExecutor: ThreadExecutor,
    postExecutionThread: PostExecutionThread
) : SingleUseCase<GetMaterialsVS, Void>(threadExecutor, postExecutionThread) {

    override fun buildUseCaseObservable(params: Void?): Single<GetMaterialsVS> {
        return repository.getMaterials()
    }

}