package com.osman.nagwa_task.data

data class Material(
    val id: Int,
    val title: String,
    val url: String,
    var isPlaying: Boolean = false
)
