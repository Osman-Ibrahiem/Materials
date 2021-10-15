package com.osman.materials.ui.home

sealed class MaterialsIntent {

    object GetMaterials : MaterialsIntent()
    class DownloadMaterial(val position: Int, val url: String) : MaterialsIntent()

}
