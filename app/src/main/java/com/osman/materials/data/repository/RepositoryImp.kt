package com.osman.materials.data.repository

import android.content.Context
import android.os.Environment
import com.osman.materials.data.datasource.IFakeDataSource
import com.osman.materials.data.datasource.IRemoteDataSource
import com.osman.materials.domain.IRepository
import com.osman.materials.domain.diinterfaces.AppContext
import com.osman.materials.domain.interactor.download_material.DownloadMaterialsVS
import com.osman.materials.domain.interactor.get_materials.GetMaterialsVS
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import javax.inject.Inject

class RepositoryImp @Inject constructor(
    @AppContext val context: Context,
    val remoteDataSource: IRemoteDataSource,
    val fakeDataSource: IFakeDataSource
) : IRepository {

    override fun getMaterials(): Single<GetMaterialsVS> {
        return remoteDataSource.getMaterials()
            .map {
                it.map { material ->
                    val fileName = File(material.url).name
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absoluteFile,
                        fileName
                    )
                    if (file.isFile && file.exists()) {
                        material.url = file.absolutePath
                        material.isLocale = true
                    }
                    material
                }
            }
            .map(::GetMaterialsVS)
    }

    override fun downloadFile(url: String, position: Int): Flowable<DownloadMaterialsVS> {
        return remoteDataSource.downloadFile(url)
            .map { DownloadMaterialsVS(it, position, url) }
    }
}