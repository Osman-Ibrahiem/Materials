package com.osman.materials.data.datasource

import com.osman.materials.data.datasource.remote.model.MaterialFile
import com.osman.materials.domain.model.Material
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface IRemoteDataSource {

    fun getMaterials(): Single<List<Material>>

    fun downloadFile(url: String): Flowable<MaterialFile>
}