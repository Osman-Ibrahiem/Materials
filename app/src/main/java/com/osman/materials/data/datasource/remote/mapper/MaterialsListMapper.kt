package com.osman.materials.data.datasource.remote.mapper

import com.osman.materials.data.Mapper
import com.osman.materials.data.datasource.remote.model.MaterialRemote
import com.osman.materials.domain.model.Material
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Map a [Iterable<MaterialEntity>] to and from a [List<Material>] instance when data is moving between
 * this later and the Data layer
 */
@Singleton
open class MaterialsListMapper @Inject constructor(
    private val materialMapper: MaterialMapper,
) : Mapper<Iterable<MaterialRemote?>?, List<Material>> {

    /**
     * Map an instance of a [Iterable<MaterialEntity>] to a [List<Material>] model
     */
    override fun mapFromRemote(type: Iterable<MaterialRemote?>?): List<Material> {
        return type?.map(materialMapper::mapFromRemote) ?: ArrayList()
    }
}
