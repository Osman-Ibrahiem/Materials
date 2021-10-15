package com.osman.materials.domain.interactor.download_material

import com.osman.materials.data.datasource.remote.model.MaterialFile
import com.osman.materials.domain.BaseVS

class DownloadMaterialsVS(
    val materialFile: MaterialFile,
    val position: Int,
    val url: String
) : BaseVS.Success()