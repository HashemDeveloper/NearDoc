package com.project.neardoc.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.model.BetterDocApiHealthRes
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class SearchPageViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()
    @Mock
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo

    @InjectMocks
    var sut = SearchPageViewModel()
    private var testApiHealthObservable: Observable<BetterDocApiHealthRes>?= null
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }
    @Before
    fun setupRxSchedulers() {
        val immediate = object : Scheduler() {
            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run()}, false)
            }
        }
        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
    }
    fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }


    @Test
    fun checkBetterDocApiHealth_healthy() {
        val betterDocApiHealthRes = BetterDocApiHealthRes("OK", "API_VERSION", "BUILD_VERSION")
        this.testApiHealthObservable = Observable.just(betterDocApiHealthRes)
        `when`(this.iNearDocRemoteRepo.checkBetterDocApiHealth(any(), any())).thenReturn(this.testApiHealthObservable)
        this.sut.checkBetterDocApiHealth("API_KEY")
        assertEquals(true, this.sut.getStatusOkLiveData().value)
    }

    @Test
    fun checkBetterDocApiHealth_not_healthy() {
        this.testApiHealthObservable = Observable.error(Throwable())
        `when`(this.iNearDocRemoteRepo.checkBetterDocApiHealth(any(), any())).thenReturn(this.testApiHealthObservable)
        this.sut.checkBetterDocApiHealth("API_KEY")
        assertEquals(false, this.sut.getStatusOkLiveData().value)
    }
}