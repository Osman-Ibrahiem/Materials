package com.osman.materials.data.datasource.remote.source

import android.os.Environment
import com.osman.materials.data.datasource.IRemoteDataSource
import com.osman.materials.data.datasource.remote.getMessage
import com.osman.materials.data.datasource.remote.mapper.MaterialsListMapper
import com.osman.materials.data.datasource.remote.model.MaterialFile
import com.osman.materials.data.datasource.remote.model.MyRemoteException
import com.osman.materials.data.datasource.remote.service.IService
import com.osman.materials.domain.model.Material
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlin.math.roundToInt


class RemoteDataSourceImp @Inject constructor(
    val service: IService,
    val materialsListMapper: MaterialsListMapper
) : IRemoteDataSource {

    override fun getMaterials(): Single<List<Material>> {
        return service.getMaterials().map {
            if (it.isSuccessful) {
                materialsListMapper.mapFromRemote(it.body())
                    .map { material ->
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
            } else {
                throw MyRemoteException(it.code(), it.errorBody()?.string() ?: it.message())
            }
        }
    }

    override fun downloadFile(url: String): Flowable<MaterialFile> {
        return service.downloadFile(url).flatMap {
            if (it.isSuccessful && it.body() != null) {
                val fileName = File(url).name
                saveFile(it.body()!!, fileName)
            } else {
                throw MyRemoteException(it.code(), it.errorBody()?.string() ?: it.message())
            }
        }
    }

    private fun saveFile(response: ResponseBody, fileName: String): Flowable<MaterialFile> {
        return Flowable.create({ subscriber ->
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {

                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absoluteFile,
                    fileName
                )

                inputStream = response.byteStream()
                val length = response.contentLength()

                outputStream = FileOutputStream(file)
                val data = ByteArray(1024)

                subscriber.onNext(MaterialFile(0, length, file))
                var total: Long = 0
                var count: Int
                while (inputStream.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    val progress = (total * 100.0 / length).roundToInt()
                    subscriber.onNext(MaterialFile(progress, length, file))
                    outputStream.write(data, 0, count)
                }
                subscriber.onNext(MaterialFile(100, length, file))
                outputStream.flush()
                outputStream.close()
                inputStream.close()

            } catch (e: Exception) {
                e.printStackTrace()
                subscriber.onError(e)
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (ex: Exception) {
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (e: Exception) {
                    }
                }
            }
            subscriber.onComplete()
        }, BackpressureStrategy.BUFFER)
    }


}