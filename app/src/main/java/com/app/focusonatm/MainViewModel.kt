package com.app.focusonatm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.focusonatm.room.dao.FocusDao
import com.app.focusonatm.room.entity.Bank
import com.app.focusonatm.room.entity.Transactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val focusDao: FocusDao) : ViewModel() {

    var currentNotesOf100: Int = 0
    var currentNotesOf200: Int = 0
    var currentNotesOf500: Int = 0
    var currentNotesOf2000: Int = 0

    var notesOf100 = 0
    var notesOf200 = 0
    var notesOf500 = 0
    var notesOf2000 = 0

    var totalAmountInBank = 0

    private val bankData = MutableLiveData<Bank>()

    private val transactionsData = MutableLiveData<List<Transactions>>()

    fun getBankData(): LiveData<Bank> = bankData

    fun getTransactionsList(): LiveData<List<Transactions>> = transactionsData

    fun setupInitialBankDetails() {

        viewModelScope.launch(Dispatchers.IO) {

            val initialBankData = focusDao.getBankDetails()

            if (initialBankData == null) {

                focusDao.addNotesToBank(
                    Bank(
                        BasicBankData.BANK_ID,
                        BasicBankData.BANK_BALANCE,
                        BasicBankData.NOTES_OF_100,
                        BasicBankData.NOTES_OF_200,
                        BasicBankData.NOTES_OF_500,
                        BasicBankData.NOTES_OF_2000
                    )
                )

            }

            val updatedBankData = focusDao.getBankDetails()

            notesOf100 = updatedBankData.notesOf100
            notesOf200 = updatedBankData.notesOf200
            notesOf500 = updatedBankData.notesOf500
            notesOf2000 = updatedBankData.notesOf2000
            totalAmountInBank = updatedBankData.totalAmount

            bankData.postValue(updatedBankData)

        }

    }

    fun fetchTransactionsListFromDB() {

        viewModelScope.launch(Dispatchers.IO) {

            transactionsData.postValue(focusDao.getAllTransactions())

        }

    }

    fun addNewTransactionToDB(withdrawAmount: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            focusDao.addTransactions(
                Transactions(
                    transAmount = withdrawAmount,
                    notesOf100 = currentNotesOf100,
                    notesOf200 = currentNotesOf200,
                    notesOf500 = currentNotesOf500,
                    notesOf2000 = currentNotesOf2000
                )
            )
            clearCurrentNotes()
            fetchTransactionsListFromDB()
        }

    }

    fun updateBankDetails(withdrawAmount: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            totalAmountInBank -= withdrawAmount

            focusDao.addNotesToBank(
                Bank(
                    BasicBankData.BANK_ID,
                    totalAmountInBank,
                    notesOf100,
                    notesOf200,
                    notesOf500,
                    notesOf2000
                )
            )
            setupInitialBankDetails()
        }

    }

    fun withDrawCalculator(withdrawAmount: Int) {

        var updatedAmount = withdrawAmount

        when {
            updatedAmount >= 2000 && notesOf2000 > 0 -> {
                updatedAmount -= 2000
                notesOf2000 -= 1
                currentNotesOf2000 += 1
                withDrawCalculator(updatedAmount)
            }
            updatedAmount >= 500 && notesOf500 > 0 -> {
                updatedAmount -= 500
                notesOf500 -= 1
                currentNotesOf500 += 1
                withDrawCalculator(updatedAmount)
            }
            updatedAmount >= 200 && notesOf200 > 0 -> {
                updatedAmount -= 200
                notesOf200 -= 1
                currentNotesOf200 += 1
                withDrawCalculator(updatedAmount)
            }
            updatedAmount >= 100 && notesOf100 > 0 -> {
                updatedAmount -= 100
                notesOf100 -= 1
                currentNotesOf100 += 1
                withDrawCalculator(updatedAmount)
            }
        }
    }

    private fun clearCurrentNotes() {
        currentNotesOf2000 = 0
        currentNotesOf500 = 0
        currentNotesOf200 = 0
        currentNotesOf100 = 0
    }

}