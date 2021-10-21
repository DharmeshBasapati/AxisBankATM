package com.app.axisbankatm.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.axisbankatm.room.entity.Bank
import com.app.axisbankatm.room.entity.Transactions
import com.app.axisbankatm.room.dao.AxisDao

@Database(entities = [Transactions::class,Bank::class], version = 1)
abstract class AxisDatabase: RoomDatabase() {

    abstract fun axisDao() : AxisDao

}