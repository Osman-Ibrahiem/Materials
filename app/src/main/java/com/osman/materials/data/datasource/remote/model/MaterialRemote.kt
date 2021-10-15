package com.osman.materials.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

data class MaterialRemote(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("name")
    val name: String?
)
