package com.osman.materials.data.datasource.fake.source

import com.osman.materials.data.datasource.IFakeDataSource
import com.osman.materials.data.datasource.fake.mapper.MaterialsListMapper
import com.osman.materials.data.datasource.fake.model.MaterialFake
import com.osman.materials.domain.model.Material
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FakeDataSourceImp @Inject constructor(
    val materialsListMapper: MaterialsListMapper
) : IFakeDataSource {

    override fun getMaterials(): Single<List<Material>> {
        val list = arrayListOf(
            MaterialFake(
                1,
                "VIDEO",
                "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
                "Video 1"
            ),
            MaterialFake(
                2,
                "VIDEO",
                "https://bestvpn.org/html5demos/assets/dizzy.mp4",
                "Video 2"
            ),
            MaterialFake(
                3,
                "PDF",
                "https://kotlinlang.org/docs/kotlin-reference.pdf",
                "PDF 3"
            ),
            MaterialFake(
                4,
                "PDF",
                "https://www.cs.cmu.edu/afs/cs.cmu.edu/user/gchen/www/download/java/LearnJava.pdf",
                "PDF 4"
            ),
        )
        return Single.just(list)
            .delay(2, TimeUnit.SECONDS)
            .map(materialsListMapper::mapFromRemote)
    }

}