package com.project.neardoc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.neardoc.model.localstoragemodels.UserPersonalInfo

@Database(entities = [UserPersonalInfo::class], version = 1, exportSchema = false)
abstract class NearDocLocalStorage: RoomDatabase() {
    companion object {
        val DB_NAME = "ACTIVITY_INFORMATION"
        @Volatile
        private var instance: NearDocLocalStorage?= null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK) {
            instance ?: buildNearDocLocalStorage(context).also {
                instance = it
            }
        }
        private fun buildNearDocLocalStorage(context: Context) = Room.databaseBuilder(
         context.applicationContext,
            NearDocLocalStorage::class.java,
            DB_NAME
        ).build()
    }
    abstract fun getUserPersonalInfoDao(): IUserInfoDao
}