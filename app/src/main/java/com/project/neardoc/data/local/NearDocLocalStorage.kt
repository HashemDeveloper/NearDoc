package com.project.neardoc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.neardoc.model.localstoragemodels.StepCountDurationList
import com.project.neardoc.model.localstoragemodels.UserPersonalInfo

@Database(entities = [UserPersonalInfo::class, StepCountDurationList::class], version = 4, exportSchema = false)
abstract class NearDocLocalStorage: RoomDatabase() {
    companion object {
        val DB_NAME = "ACTIVITY_INFORMATION"
        @Volatile
        private var instance: NearDocLocalStorage?= null
        private val LOCK = Any()
        @JvmStatic
        val MIGRATOIN_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("drop table IF EXISTS duration_list")
                database.execSQL("create table duration_list(email TEXT not null, id TEXT primary key not null, time INTEGER not null)")
            }
        }
        operator fun invoke(context: Context) = instance?: synchronized(LOCK) {
            instance ?: buildNearDocLocalStorage(context).also {
                instance = it
            }
        }
        private fun buildNearDocLocalStorage(context: Context) = Room.databaseBuilder(
         context.applicationContext,
            NearDocLocalStorage::class.java,
            DB_NAME
        ).addMigrations(MIGRATOIN_3_4).build()
    }
    abstract fun getUserPersonalInfoDao(): IUserInfoDao
    abstract fun getStepCountDurationListDao(): IStepCountDurationListDao
}