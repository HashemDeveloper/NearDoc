package com.project.neardoc.view.fragments


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.project.neardoc.R
import com.project.neardoc.di.Injectable
import com.project.neardoc.utils.heartbeats.HeartBeatIndicatorType
import com.project.neardoc.utils.widgets.HeartBeatsView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_heart_beat.*


class HeartBeat : Fragment(), Injectable, HeartBeatsView.OnRequestHeartBeatIndicator {

    private var heartBeatView: HeartBeatsView?= null
    private var indicatorType: HeartBeatIndicatorType?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
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
        fragment_heart_beat_heart_view_id?.let {
            this.heartBeatView = it
        }
        fragment_heart_beat_back_bt_id?.let {
            it.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.accountPage)
            }
        }
        disableBackButton(view)
        setIndicatorType(HeartBeatIndicatorType.DEFAULT)
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

    override fun requestHeartBeatIndicator(): Bitmap {
        setIndicatorType(HeartBeatIndicatorType.GREEN)
        val indicatorIcon: Bitmap? = when (getIndicatorType()) {
            HeartBeatIndicatorType.GREEN -> {
                BitmapFactory.decodeResource(resources, R.drawable.ic_heart_rate_24_green)
            }
            HeartBeatIndicatorType.RED -> {
                BitmapFactory.decodeResource(resources, R.drawable.ic_heart_rate_24_red)
            }
            HeartBeatIndicatorType.DEFAULT -> {
                BitmapFactory.decodeResource(resources, R.drawable.heart_rate_24_white)
            }
        }
        return indicatorIcon!!
    }
}
