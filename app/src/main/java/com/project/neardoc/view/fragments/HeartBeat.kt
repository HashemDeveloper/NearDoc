package com.project.neardoc.view.fragments


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.project.neardoc.BuildConfig
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.di.viewmodel.ViewModelFactory
import com.project.neardoc.utils.heartbeats.HeartBeatIndicatorType
import com.project.neardoc.utils.heartbeats.IImageProcessor
import com.project.neardoc.utils.widgets.HeartBeatsView
import com.project.neardoc.viewmodel.HeartBeatViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_heart_beat.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class HeartBeat : Fragment(), Injectable, HeartBeatsView.OnRequestHeartBeatIndicator,
    SurfaceHolder.Callback, Camera.PreviewCallback {
    companion object {
        @JvmStatic
        private val TAG: String = HeartBeat::class.java.canonicalName!!
        @JvmStatic
        private val processing = AtomicBoolean(false)
        @JvmStatic
        private val averageArraySize: Int = 4
        @JvmStatic
        private val averageArray: IntArray = IntArray(averageArraySize)
        @JvmStatic
        private val beatsArraySize: Int = 3
        @JvmStatic
        private val beatsArray: IntArray = IntArray(beatsArraySize)
    }

    @Inject
    lateinit var iImageProcessor: IImageProcessor
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val heartBeatViewModel: HeartBeatViewModel by viewModels {
        this.viewModelFactory
    }
    private var preview: SurfaceView? = null
    private var previewHolder: SurfaceHolder? = null
    private var camera: Camera? = null
    private var heartBeatView: HeartBeatsView? = null
    private var indicatorType: HeartBeatIndicatorType? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var averageIndex: Int = 0
    private var beatsIndex: Int = 0
    private var beats: Double = 0.0
    private var startTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        (context!!.getSystemService(Context.POWER_SERVICE) as PowerManager).let {
            this.wakeLock = it.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$TAG: DoNotDimScreen")
        }
        this.heartBeatViewModel.init()
        this.heartBeatViewModel.getUserImageLiveData().observe(activity!!, userDataLiveDataObserver())
    }
    private fun userDataLiveDataObserver(): Observer<String> {
        return Observer {
            if (it.isNotEmpty()) {
                Glide.with(this.context!!).load(it).into(fragment_heart_beat_user_image_view_id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_heart_beat, container, false)
    }

    private fun setIndicatorType(indicatorType: HeartBeatIndicatorType) {
        this.indicatorType = indicatorType
    }

    private fun getIndicatorType(): HeartBeatIndicatorType {
        return this.indicatorType!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setIndicatorType(HeartBeatIndicatorType.DEFAULT)
        fragment_heart_beat_surface_view_id?.let {
            this.preview = it
        }
        this.preview?.let {
            this.previewHolder = it.holder
        }
        this.previewHolder?.addCallback(this)
        this.previewHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        fragment_heart_beat_heart_view_id?.let {
            this.heartBeatView = it
        }
        fragment_heart_beat_back_bt_id?.let { bt ->
            bt.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.accountPage)
            }
        }
        disableBackButton(view)
        this.heartBeatView!!.setHeartBeatIndicatorListener(this)
    }

    private fun disableBackButton(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener true
                }
            }
            false
        })
    }

    override fun onResume() {
        super.onResume()
        this.wakeLock!!.acquire(1000 * 60 * 5)
        this.camera = Camera.open()
        this.startTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        this.wakeLock!!.release()
        this.camera!!.setPreviewCallback(null)
        this.camera!!.stopPreview()
        this.camera!!.release()
        this.camera = null
    }

    override fun onDestroy() {
        super.onDestroy()
        this.heartBeatViewModel.getUserImageLiveData().removeObserver(userDataLiveDataObserver())
    }

    override fun requestHeartBeatIndicator(): Bitmap {
        val indicatorIcon: Bitmap? = when (getIndicatorType()) {
            HeartBeatIndicatorType.GREEN -> {
                BitmapFactory.decodeResource(resources, R.drawable.ic_heart_rate_24_green)
            }
            HeartBeatIndicatorType.RED -> {
                BitmapFactory.decodeResource(resources, R.drawable.ic_heart_violet_24)
            }
            HeartBeatIndicatorType.DEFAULT -> {
                BitmapFactory.decodeResource(resources, R.drawable.heart_rate_24_white)
            }
        }
        return indicatorIcon!!
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        val paramters: Camera.Parameters = this.camera!!.parameters
        paramters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        val size: Camera.Size = getSmallestPreviewSize(width, height, paramters)!!
        paramters.setPreviewSize(size.width, size.height)
        this.camera!!.parameters = paramters
        this.camera!!.startPreview()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        try {
            this.camera!!.setPreviewDisplay(this.previewHolder)
            this.camera!!.setPreviewCallback(this)
        } catch (ex: Exception) {
            if (ex.localizedMessage != null) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "Exception on surface created: ${ex.localizedMessage!!}")
                }
            }
        }
    }

    private fun getSmallestPreviewSize(
        width: Int,
        height: Int,
        parameters: Camera.Parameters
    ): Camera.Size? {
        var result: Camera.Size? = null
        for (size: Camera.Size in parameters.supportedPreviewSizes) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size
                } else {
                    val resultArea = result.width * result.height
                    val newArea = size.width * size.height
                    if (newArea < resultArea) result = size
                }
            }
        }
        return result
    }

    override fun onPreviewFrame(data: ByteArray?, cam: Camera?) {
        if (data == null) throw NullPointerException()
        val size: Camera.Size = cam!!.parameters.previewSize ?: throw NullPointerException()

        if (!processing.compareAndSet(false, true)) return

        val width = size.width
        val height = size.height

        val imgAvg: Int = this.iImageProcessor.decodeYUV420SPtoRedAvg(data.clone(), width, height)

        if (imgAvg == 0 || imgAvg == 255) {
            processing.set(false)
            return
        }

        var averageArrayAvg = 0
        var averageArrayCnt = 0
        for (i in averageArray.indices) {
            if (averageArray.get(i) > 0) {
                averageArrayAvg += averageArray[i]
                averageArrayCnt++
            }
        }

        val rollingAverage: Int = if (averageArrayCnt > 0) averageArrayAvg / averageArrayCnt else 0
        var newType: HeartBeatIndicatorType = getIndicatorType()
        if (imgAvg < rollingAverage) {
            newType = HeartBeatIndicatorType.RED
            if (newType != getIndicatorType()) {
                beats++
            }
        } else if (imgAvg > rollingAverage) {
            newType = HeartBeatIndicatorType.GREEN
        }

        if (averageIndex == averageArraySize) {
            averageIndex = 0
        }

        averageArray[averageIndex] = imgAvg
        averageIndex++

        // Transitioned from one state to another to the same
        if (newType != getIndicatorType()) {
            setIndicatorType(newType)
            this.heartBeatView!!.postInvalidate()
        }

        val endTime: Long = System.currentTimeMillis()
        val totalTimeInSecs: Double =
            (endTime - startTime) / 1000.0
        if (totalTimeInSecs >= 10) {
            val bps: Double = beats / totalTimeInSecs
            val dpm = (bps * 60.0).toInt()
            if (dpm < 30 || dpm > 180) {
                startTime = System.currentTimeMillis()
                beats = 0.0
                processing.set(false)
                return
            }
            if (beatsIndex == beatsArraySize) {
                beatsIndex = 0
            }
            beatsArray[beatsIndex] = dpm
            beatsIndex++
            var beatsArrayAvg = 0
            var beatsArrayCnt = 0
            for (i: Int in beatsArray.indices) {
                if (beatsArray.get(i) > 0) {
                    beatsArrayAvg += beatsArray[i]
                    beatsArrayCnt++
                }
            }
            val beatsAvg = beatsArrayAvg / beatsArrayCnt
            fragment_heart_beat_result_view_id?.let {
                it.text = beatsAvg.toString()
            }
            startTime = System.currentTimeMillis()
            beats = 0.0
        }
        processing.set(false)
    }
}
