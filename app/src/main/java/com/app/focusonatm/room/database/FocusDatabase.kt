package com.app.focusonatm.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.focusonatm.room.entity.Bank
import com.app.focusonatm.room.entity.Transactions
import com.app.focusonatm.room.dao.FocusDao

@Database(entities = [Transactions::class,Bank::class], version = 1)
abstract class FocusDatabase: RoomDatabase() {

    abstract fun focusDao() : FocusDao

}