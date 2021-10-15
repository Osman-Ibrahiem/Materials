package com.osman.materials.data.datasource.remote.mapper

import com.osman.materials.data.Mapper
import com.osman.materials.data.datasource.remote.model.MaterialRemote
import com.osman.materials.domain.model.Material
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Map a [MaterialRemote] to and from a [Material] instance when data is moving between
 * this later and the Data layer
 */
@Singleton
open class MaterialMapper @Inject constructor() : Mapper<MaterialRemote?, Material> {

    /**
     * Map an instance of a [MaterialRemote] to a [Material] model
     */
    override fun mapFromRemote(type: MaterialRemote?): Material {
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
