package com.app.axisbankatm.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank")
data class Bank(
    @PrimaryKey val bankId: Int,
    val totalAmount: Int,
    val notesOf100: Int,
    val notesOf200: Int,
    val notesOf500: Int,
    val notesOf2000: Int,
)
