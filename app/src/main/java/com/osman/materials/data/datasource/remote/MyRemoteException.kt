package com.osman.materials.data.datasource.remote

import com.google.gson.Gson
import com.osman.materials.data.datasource.remote.model.RemoteResponse
import okhttp3.ResponseBody

fun ResponseBody.getMessage():String{
    val messageText = this.string()
    try {
        val model: RemoteResponse = Gson().fromJson(messageText, RemoteResponse::class.java)
        return model.message ?: ""
    }catch (e:Exception){
        return messageText
    }
}
fun ResponseBody.getMessage(model:RemoteResponse):String{
    return model.message?:""
}