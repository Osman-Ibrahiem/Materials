package com.osman.materials.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

open class RemoteResponse {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("status_code")
    var statusCode: Int? = null

    @SerializedName("code")
    var code: String? = null

    @SerializedName("status")
    var status: Int? = null
}