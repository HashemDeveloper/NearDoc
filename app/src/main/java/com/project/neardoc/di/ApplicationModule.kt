package com.project.neardoc.di

import android.content.Context
import com.project.neardoc.NearDocApp
import com.project.neardoc.data.local.ISharedPrefService
import com.project.neardoc.data.local.IUserInfoDao
import com.project.neardoc.data.local.LocalDBService
import com.project.neardoc.data.local.SharedPrefService
import com.project.neardoc.data.local.remote.INearDocRemoteRepo
import com.project.neardoc.data.local.remote.NearDocRemoteRepo
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.rxauth.RxAuthentication
import com.project.neardoc.rxeventbus.IRxEventBus
import com.project.neardoc.rxeventbus.RxEventBus
import com.project.neardoc.utils.*
import com.project.neardoc.utils.calories.CalorieBurnedCalculator
import com.project.neardoc.utils.calories.ICalorieBurnedCalculator
import com.project.neardoc.utils.networkconnections.ConnectionStateMonitor
import com.project.neardoc.utils.networkconnections.IConnectionStateMonitor
import com.project.neardoc.utils.notifications.INotificationBuilder
import com.project.neardoc.utils.notifications.INotificationScheduler
import com.project.neardoc.utils.notifications.NotificationBuilder
import com.project.neardoc.utils.notifications.NotificationScheduler
import com.project.neardoc.utils.sensors.*
import com.project.neardoc.utils.widgets.INearDockMessageViewer
import com.project.neardoc.utils.widgets.NearDockMessageViewer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {
    @Singleton
    @Provides
    fun provideContext(nearDocApp: NearDocApp): Context {
        return nearDocApp
    }
    @Singleton
    @Provides
    fun provideRxAuthentication(rxAuthentication: RxAuthentication): IRxAuthentication{
        return rxAuthentication
    }
    @Singleton
    @Provides
    fun provideIConnectionStateMonitor(connectionStateMonitor: ConnectionStateMonitor): IConnectionStateMonitor {
        return connectionStateMonitor
    }
    @Singleton
    @Provides
    fun provideSharedPrefService(context: Context): SharedPrefService {
        return SharedPrefService.invoke(context)
    }
    @Singleton
    @Provides
    fun provideSharedPref(sharedPrefService: SharedPrefService): ISharedPrefService {
        return sharedPrefService
    }
    @Singleton
    @Provides
    fun provideRxEventBus(rxEventBus: RxEventBus): IRxEventBus{
        return rxEventBus
    }
    @Singleton
    @Provides
    fun provideNearDocRemoteRep(nearDocRemoteRepo: NearDocRemoteRepo): INearDocRemoteRepo {
        return nearDocRemoteRepo
    }
    @Singleton
    @Provides
    fun provideLocationService(locationService: LocationService): ILocationService {
        return locationService
    }
    @Singleton
    @Provides
    fun provideUserStateService(userStateService: UserStateService): IUserStateService {
        return userStateService
    }
    @Singleton
    @Provides
    fun provideNearDockSnackBar(nearDockMessageViewer: NearDockMessageViewer): INearDockMessageViewer {
        return nearDockMessageViewer
    }
    @Singleton
    @Provides
    fun provideDeviceSensors(deviceSensors: DeviceSensors): IDeviceSensors {
        return deviceSensors
    }
    @Singleton
    @Provides
    fun provideTemperatureSensor(tempSensor: TempSensor): ITempSensor {
        return tempSensor
    }
    @Singleton
    @Provides
    fun provideLightSensor(lightSensor: LightSensor): ILightSensor {
        return lightSensor
    }
    @Singleton
    @Provides
    fun provideStepCountSensor(stepCounter: StepCountSensor): IStepCountSensor {
        return stepCounter
    }
    @Singleton
    @Provides
    fun provideNotificationScheduler(notificationScheduler: NotificationScheduler): INotificationScheduler {
        return notificationScheduler
    }
    @Singleton
    @Provides
    fun provideNotificationBuilder(notificationBuilder: NotificationBuilder): INotificationBuilder {
        return notificationBuilder
    }
    @Singleton
    @Provides
    fun provideCalorieBurnCalculator(calorieBurnedCalculator: CalorieBurnedCalculator): ICalorieBurnedCalculator {
        return calorieBurnedCalculator
    }
    @Singleton
    @Provides
    fun provideUserInfoDb(dbService: LocalDBService): IUserInfoDao {
        return dbService.getUserInfoDao()
    }
}