package com.app.focusonatm.room.dao

import androidx.room.*
import com.app.focusonatm.room.entity.Bank
import com.app.focusonatm.room.entity.Transactions

@Dao
interface FocusDao {

    @Query("Select * from bank")
    fun getBankDetails(): Bank

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNotesToBank(bank: Bank)

    @Query("Select * from transactions")
    fun getAllTransactions(): List<Transactions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTransactions(transactions: Transactions)

}