package com.osman.materials.domain

import com.osman.materials.data.datasource.remote.model.MyRemoteException
import java.io.IOException
import java.net.SocketTimeoutException

sealed class BaseVS {

    var type = 0

    object Loading : BaseVS()

    open class Success : BaseVS() {
        var code = 200
        var message: String? = ""
    }

    class Error(val error: Throwable) : BaseVS() {
        var message: String? = when (error) {
            is SocketTimeoutException, is java.net.UnknownHostException,
            is java.net.ConnectException, is IOException -> "No Internet connection"
            is MyRemoteException -> error.errorMessage
            else -> error.localizedMessage
        }
        var code = when (error) {
            is SocketTimeoutException, is java.net.ConnectException, is IOException,
            is java.net.UnknownHostException -> NO_CONNECTION_CODE
            is MyRemoteException -> error.code
            else -> 0
        }

        companion object {
            const val NO_CONNECTION_CODE = 2002
        }
    }

    object Empty : BaseVS()

    object Idle : BaseVS()
}