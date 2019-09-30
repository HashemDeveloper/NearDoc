package com.project.neardoc.rxeventbus

import io.reactivex.Observable

interface IRxEventBus {
    fun post(event: Any)
    fun <T> observable(eventClass: Class<T>): Observable<T>
}