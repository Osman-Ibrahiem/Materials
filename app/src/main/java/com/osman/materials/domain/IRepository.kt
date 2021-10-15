package com.osman.materials.domain

import com.osman.materials.domain.interactor.download_material.DownloadMaterialsVS
import com.osman.materials.domain.interactor.get_materials.GetMaterialsVS
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface IRepository {

    fun getMaterials(): Single<GetMaterialsVS>

    fun downloadFile(url: String, position: Int): Flowable<DownloadMaterialsVS>
}