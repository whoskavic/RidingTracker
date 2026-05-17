package com.avishena.ridingtracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.data.model.RidingSession

@Database(
    entities = [RidingSession::class, LocationPoint::class],
    version = 1,
    exportSchema = false
)
abstract class RidingDatabase : RoomDatabase() {

    abstract fun ridingDao(): RidingDao

    companion object {
        @Volatile
        private var INSTANCE: RidingDatabase? = null

        fun getInstance(context: Context): RidingDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RidingDatabase::class.java,
                    "riding_tracker.db"
                ).build().also { INSTANCE = it }
            }
    }
}
