package com.osman.materials.domain.interactor.get_materials

import com.osman.materials.domain.BaseVS
import com.osman.materials.domain.model.Material

class GetMaterialsVS(
    val materialEntities: List<Material>
) : BaseVS.Success()