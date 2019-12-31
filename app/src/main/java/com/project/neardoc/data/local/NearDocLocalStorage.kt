package com.project.neardoc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.neardoc.data.local.searchdocdaos.*
import com.project.neardoc.model.Specialty
import com.project.neardoc.model.localstoragemodels.*

@Database(
    entities = [UserPersonalInfo::class, StepCountDurationList::class, StepCountData::class, DocProfile::class, DocProfileLanguage::class, Doc::class,
    DocRatings::class, DocPractice::class, Specialty::class],
    version = 6,
    exportSchema = false
)
abstract class NearDocLocalStorage : RoomDatabase() {
    companion object {
        val DB_NAME = "ACTIVITY_INFORMATION"
        @Volatile
        private var instance: NearDocLocalStorage? = null
        private val LOCK = Any()
        @JvmStatic
        val MIGRATOIN_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("drop table IF EXISTS duration_list")
                database.execSQL("create table duration_list(email TEXT not null, id TEXT primary key not null, time INTEGER not null)")
                database.execSQL("drop table IF EXISTS step_count_data")
                database.execSQL("create table step_count_data(id TEXT primary key not null, data_inserted_time INTEGER not null, calories_burned_result INTEGER not null)")
                database.execSQL("drop table IF EXISTS personal_info")
                database.execSQL("create table personal_info(personal_info_id TEXT primary key not null, email TEXT not null, weight INTEGER not null, height INTEGER not null, age INTEGER not null, gender TEXT not null )")
            }
        }

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildNearDocLocalStorage(context).also {
                instance = it
            }
        }

        private fun buildNearDocLocalStorage(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NearDocLocalStorage::class.java,
            DB_NAME
        ).addMigrations(MIGRATOIN_5_6).build()
    }

    abstract fun getUserPersonalInfoDao(): IUserInfoDao
    abstract fun getStepCountDurationListDao(): IStepCountDurationListDao
    abstract fun getStepCountDataDao(): IStepCountDataDao
    abstract fun getDocDao(): IDocDao
    abstract fun getDocRatingDao(): IDocRatingDao
    abstract fun getDocProfileDao(): IDocProfileDao
    abstract fun getDocProfileLanguageDao(): IDocProfileLanguageDao
    abstract fun getDocPracticeDao(): IDocPracticeDao
    abstract fun getDocSpecialityDao(): IDocSpecialityDao
}