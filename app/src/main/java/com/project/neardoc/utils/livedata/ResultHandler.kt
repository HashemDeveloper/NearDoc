package com.project.neardoc.utils.livedata

data class ResultHandler<out T>(val status: ResultStatus, val data: T?, val message: String?) {
    enum class ResultStatus {
        LOADING,
        SUCCESS,
        ERROR
    }
    companion object {
        fun <T> success(data: T): ResultHandler<T> {
            return ResultHandler(ResultStatus.SUCCESS, data, null)
        }
        fun <T> onError(data: T?= null, message: String?): ResultHandler<T> {
            return ResultHandler(ResultStatus.ERROR, data, message)
        }
        fun <T> onLoading(data: T?= null): ResultHandler<T> {
            return ResultHandler(ResultStatus.LOADING, data, null)
        }
    }
}