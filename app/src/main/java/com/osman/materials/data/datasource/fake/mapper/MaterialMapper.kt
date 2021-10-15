package com.osman.materials.data.datasource.fake.mapper

import com.osman.materials.data.Mapper
import com.osman.materials.data.datasource.fake.model.MaterialFake
import com.osman.materials.domain.model.Material
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Map a [MaterialFake] to and from a [Material] instance when data is moving between
 * this later and the Data layer
 */
@Singleton
open class MaterialMapper @Inject constructor() : Mapper<MaterialFake?, Material> {

    /**
     * Map an instance of a [MaterialFake] to a [Material] model
     */
    override fun mapFromRemote(type: MaterialFake?): Material {
        return Material(
            type?.id ?: 0,
            type?.name ?: "",
            type?.url ?: "",
            when (type?.type) {
                "VIDEO" -> Material.Type.VIDEO
                "PDF" -> Material.Type.PDF
                else -> Material.Type.UNKNOWN
            }
        )
    }
}
