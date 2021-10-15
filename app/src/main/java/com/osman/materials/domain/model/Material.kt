package com.osman.materials.domain.model

data class Material(
    var id: Int,
    var title: String,
    var url: String,
    var type: Type,
    var isLocale: Boolean = false
) {
    enum class Type(val value: Int) {
        UNKNOWN(0),
        VIDEO(1),
        PDF(2)
    }
}
