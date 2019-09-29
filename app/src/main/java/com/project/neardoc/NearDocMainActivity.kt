package com.project.neardoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.project.neardoc.utils.IConnectionStateMonitor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class NearDocMainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var iConnectionStateMonitor: IConnectionStateMonitor
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.near_by_main_layout)
        this.navController = Navigation.findNavController(this, R.id.container)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return this.dispatchingAndroidInjector
    }

    override fun onStart() {
        super.onStart()
        monitorConnectionSetting()
    }
    private fun monitorConnectionSetting() {
        this.iConnectionStateMonitor.getObserver().observe(this, Observer {isNetAvailable ->
            if (isNetAvailable) {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Lost", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
