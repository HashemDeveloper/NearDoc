package com.project.neardoc.utils.heartbeats

interface IImageProcessor {
    fun decodeYUV420SPtoRedAvg(yuv420sp: ByteArray?, width: Int, height: Int): Int
}