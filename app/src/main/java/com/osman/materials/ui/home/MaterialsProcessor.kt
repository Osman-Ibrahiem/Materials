package com.osman.materials.ui.home

import com.osman.materials.domain.BaseVS
import com.osman.materials.domain.interactor.download_material.DownloadMaterialUseCase
import com.osman.materials.domain.interactor.get_materials.GetMaterialsUseCase
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject

class MaterialsProcessor @Inject constructor(
    private val getMaterialsUseCase: GetMaterialsUseCase,
    private val downloadMaterialUseCase: DownloadMaterialUseCase
) {

    private val getMaterialsProcessor =
        ObservableTransformer<MaterialsIntent.GetMaterials, BaseVS> { it ->
            it.switchMap {
                getMaterialsUseCase.execute()
                    .toObservable()
                    .map { if (it.materialEntities.isNullOrEmpty()) BaseVS.Empty else it }
                    .map { it.apply { type = -1 } }
                    .onErrorReturn { e -> BaseVS.Error(e).apply { type = -1 } }
                    .startWith(BaseVS.Loading.apply { type = -1 })
            }
        }

    private val downloadMaterialProcessor =
        ObservableTransformer<MaterialsIntent.DownloadMaterial, BaseVS> { it ->
            it.switchMap { intent ->
                downloadMaterialUseCase.execute(intent.position to intent.url)
                    .toObservable()
                    .map { (it as BaseVS).apply { type = intent.position } }
                    .onErrorReturn { e -> BaseVS.Error(e).apply { type = intent.position } }
                    .startWith(BaseVS.Loading.apply { type = intent.position })
            }
        }

    var actionProcessor = ObservableTransformer<MaterialsIntent, BaseVS> { it ->
        it.publish {
            Observable.merge(
                it.ofType(MaterialsIntent.GetMaterials::class.java)
                    .compose(getMaterialsProcessor),
                it.ofType(MaterialsIntent.DownloadMaterial::class.java)
                    .compose(downloadMaterialProcessor)
            ).mergeWith(it.filter { a ->
                a !is MaterialsIntent.GetMaterials &&
                        a !is MaterialsIntent.DownloadMaterial
            }.flatMap {
                Observable.error(IllegalArgumentException("Unknown Action type"))
            })
        }
    }

    fun dispose() {
        getMaterialsUseCase.dispose()
    }
}