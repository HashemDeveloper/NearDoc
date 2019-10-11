package com.project.neardoc.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.model.UsernameRes
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
import org.mockito.*
import org.mockito.Mockito.`when`
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class RegistrationViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()
    @Mock
    lateinit var iNearDocRemoteRepo: INearDocRemoteRepo

    @InjectMocks
    var sut = RegistrationViewModel()

    private var testUsernameObservable: Observable<UsernameRes>?= null

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
               return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, false)
            }
        }
        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }

    @Test
    fun testUsernameExists_yes() {
        val usernameRes = UsernameRes("Junia")
        this.testUsernameObservable = Observable.just(usernameRes)
        `when`(this.iNearDocRemoteRepo.getUsernames(any(), any())).thenReturn(this.testUsernameObservable)
        this.sut.checkIfUsernameExists("Junia", "whatever")
        assertEquals(false, this.sut.getLoadingLiveData().value)
        assertEquals(true, this.sut.getUsernameExistsLiveData().value)
        assertEquals("Junia", this.sut.getUsernameLiveData().value)
    }
    @Test
    fun testUsernameExis_no() {
        this.testUsernameObservable = Observable.error(Throwable())
        `when`(this.iNearDocRemoteRepo.getUsernames(any(), any())).thenReturn(this.testUsernameObservable)
        this.sut.checkIfUsernameExists("Junia", "whatever")
        assertEquals(null, this.sut.getUsernameExistsLiveData().value)
    }
}