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

    private var currentNotesOf100: Int = 0
    private var currentNotesOf200: Int = 0
    private var currentNotesOf500: Int = 0
    private var currentNotesOf2000: Int = 0

    private var notesOf100 = 0
    private var notesOf200 = 0
    private var notesOf500 = 0
    private var notesOf2000 = 0

    private var totalAmountInBank = 0

    val edtWithdrawAmount = MutableLiveData<String>()

    val btnWithdrawAmountText = MutableLiveData("Withdraw")

    private val validWithdrawAmount = MutableLiveData<String>()

    val errorMessage = MutableLiveData("")

    private val bankData = MutableLiveData<Bank>()

    private val transactionsData = MutableLiveData<List<Transactions>>()

    fun getWithdrawAmount(): LiveData<String> = validWithdrawAmount

    fun getBankData(): LiveData<Bank> = bankData

    fun getTransactionsList(): LiveData<List<Transactions>> = transactionsData

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = if (s.isNotEmpty()) {
        btnWithdrawAmountText.postValue("Withdraw Rs.$s")
    } else {
        btnWithdrawAmountText.postValue("Withdraw")
    }

    fun validateAmount() {

        if (edtWithdrawAmount.value != null && edtWithdrawAmount.value!!.isNotEmpty()) {

            val withdrawAmount = Integer.parseInt(edtWithdrawAmount.value!!)

            if (withdrawAmount <= totalAmountInBank) {

                if (withdrawAmount != 0 && withdrawAmount % 100 == 0) {

                    withDrawCalculator(withdrawAmount)
                    edtWithdrawAmount.postValue("")
                    validWithdrawAmount.postValue(withdrawAmount.toString())
                    errorMessage.value = ""

                } else {
                    errorMessage.value = "Please enter amount in multiples of 100."
                }

            } else {
                errorMessage.value =
                    "Unable to withdraw due to insufficient balance in your account."
            }
        } else {
            errorMessage.value = "Please enter withdraw amount."
        }
    }

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

    private fun withDrawCalculator(withdrawAmount: Int) {

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