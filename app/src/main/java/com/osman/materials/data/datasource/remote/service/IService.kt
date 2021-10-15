package com.osman.materials.data.datasource.remote.service

import com.osman.materials.data.datasource.remote.model.MaterialRemote
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface IService {

    @GET("movies")
    fun getMaterials(
    ): Single<Response<List<MaterialRemote>>>

    @GET
    @Streaming
    fun downloadFile(
        @Url url: String
    ): Flowable<Response<ResponseBody>>

}

