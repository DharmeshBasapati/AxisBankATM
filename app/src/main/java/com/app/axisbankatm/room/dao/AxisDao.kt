package com.app.axisbankatm.room.dao

import androidx.room.*
import com.app.axisbankatm.room.entity.Bank
import com.app.axisbankatm.room.entity.Transactions

@Dao
interface AxisDao {

    @Query("Select * from bank")
    fun getBankDetails(): Bank

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNotesToBank(bank: Bank)

    @Query("Select * from transactions")
    fun getAllTransactions(): List<Transactions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTransactions(transactions: Transactions)

}