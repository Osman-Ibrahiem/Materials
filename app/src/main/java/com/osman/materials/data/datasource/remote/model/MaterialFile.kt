package com.osman.materials.data.datasource.remote.model

import java.io.File

data class MaterialFile(
    val progress: Int = 0,
    val totalSize: Long = 0,
    val file: File,
)
