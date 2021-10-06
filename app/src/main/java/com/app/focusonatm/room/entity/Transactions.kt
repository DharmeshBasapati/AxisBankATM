package com.app.focusonatm.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transactions(
    @PrimaryKey(autoGenerate = true)
    val transId: Int = 0,
    val transAmount: Int,
    val notesOf100: Int,
    val notesOf200: Int,
    val notesOf500: Int,
    val notesOf2000: Int,
) {



}
