package com.app.axisbankatm.room.builder

import android.content.Context
import androidx.room.Room
import com.app.axisbankatm.room.database.AxisDatabase

object DatabaseBuilder {

    private var dbInstance: AxisDatabase? = null

    fun getDBInstance(context: Context): AxisDatabase {
        if (dbInstance == null) {
            synchronized(AxisDatabase::class) {
                dbInstance = buildAxisDB(context)
            }
        }
        return dbInstance!!
    }

    private fun buildAxisDB(context: Context): AxisDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AxisDatabase::class.java,
            "AxisDatabase"
        ).build()
    }

}