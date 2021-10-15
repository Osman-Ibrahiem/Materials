package com.osman.materials.data.datasource

import com.osman.materials.domain.model.Material
import io.reactivex.Single

interface IFakeDataSource {

    fun getMaterials(): Single<List<Material>>

}