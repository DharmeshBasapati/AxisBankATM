package com.app.focusonatm

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.focusonatm.BasicBankData.BANK_BALANCE
import com.app.focusonatm.BasicBankData.BANK_ID
import com.app.focusonatm.BasicBankData.NOTES_OF_100
import com.app.focusonatm.BasicBankData.NOTES_OF_200
import com.app.focusonatm.BasicBankData.NOTES_OF_2000
import com.app.focusonatm.BasicBankData.NOTES_OF_500
import com.app.focusonatm.databinding.ActivityMainBinding
import com.app.focusonatm.room.builder.DatabaseBuilder
import com.app.focusonatm.room.dao.FocusDao
import com.app.focusonatm.room.entity.Bank
import com.app.focusonatm.room.entity.Transactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var currentNotesOf100: Int = 0
    private var currentNotesOf200: Int = 0
    private var currentNotesOf500: Int = 0
    private var currentNotesOf2000: Int = 0

    var notesOf100 = 0
    var notesOf200 = 0
    var notesOf500 = 0
    var notesOf2000 = 0

    var totalAmountInBank = 0

    private var bankDataFromDB: Bank? = null
    private lateinit var focusDao: FocusDao
    private lateinit var transactions: List<Transactions>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBank()

        binding.btnWithdraw.setOnClickListener {

            if (binding.edtWithdrawAmount.text.toString().isNotEmpty()) {

                val withdrawAmount = Integer.parseInt(binding.edtWithdrawAmount.text.toString())

                if (withdrawAmount <= totalAmountInBank) {

                    if (withdrawAmount.toString().endsWith("00")) {

                        withDrawCalculator(withdrawAmount)
                        updatedNotesUI(withdrawAmount)

                        binding.edtWithdrawAmount.text?.clear()
                        binding.textFieldWithdrawAmount.helperText = null

                    } else {

                        showErrorMessage(getString(R.string.msg_enter_amount_in_multiples_of_100))

                    }

                } else {

                    showErrorMessage(getString(R.string.msg_insufficient_balance))

                }

            } else {

                showErrorMessage(getString(R.string.msg_enter_withdraw_amount))

            }


        }

    }

    private fun showErrorMessage(errorMessage: String) {
        binding.textFieldWithdrawAmount.helperText = errorMessage
        binding.textFieldWithdrawAmount.setHelperTextColor(ColorStateList.valueOf(Color.RED))
    }

    private fun setupBank() {

        GlobalScope.launch(Dispatchers.IO) {

            focusDao = DatabaseBuilder.getDBInstance(applicationContext).focusDao()

            bankDataFromDB = focusDao.getBankDetails()

            if (bankDataFromDB == null) {

                focusDao.addNotesToBank(
                    Bank(
                        BANK_ID,
                        BANK_BALANCE,
                        NOTES_OF_100,
                        NOTES_OF_200,
                        NOTES_OF_500,
                        NOTES_OF_2000
                    )
                )

                bankDataFromDB = focusDao.getBankDetails()

            }

            transactions = ArrayList()
            transactions = focusDao.getAllTransactions()

        }.invokeOnCompletion {

            GlobalScope.launch(Dispatchers.Main) {

                bankDataFromDB?.let { bankData -> setupNotesUI(bankData) }

                if (transactions.isNotEmpty()) {
                    setupLastTransactionUI(transactions[transactions.size - 1])
                    updateTransactionsList()
                }

            }

        }


    }

    private fun updatedNotesUI(withdrawAmount: Int) {

        GlobalScope.launch(Dispatchers.IO) {

            totalAmountInBank -= withdrawAmount

            focusDao.addNotesToBank(
                Bank(
                    BANK_ID,
                    totalAmountInBank,
                    notesOf100,
                    notesOf200,
                    notesOf500,
                    notesOf2000
                )
            )

            bankDataFromDB = focusDao.getBankDetails()

            focusDao.addTransactions(
                Transactions(
                    transAmount = withdrawAmount,
                    notesOf100 = currentNotesOf100,
                    notesOf200 = currentNotesOf200,
                    notesOf500 = currentNotesOf500,
                    notesOf2000 = currentNotesOf2000
                )
            )

            transactions = ArrayList()
            transactions = focusDao.getAllTransactions()

        }.invokeOnCompletion {

            GlobalScope.launch(Dispatchers.Main) {

                bankDataFromDB?.let { it1 -> setupNotesUI(it1) }

                if (transactions.isNotEmpty()) {
                    setupLastTransactionUI(transactions[transactions.size - 1])
                    updateTransactionsList()
                }

                currentNotesOf2000 = 0
                currentNotesOf500 = 0
                currentNotesOf200 = 0
                currentNotesOf100 = 0

            }

        }
    }

    private fun updateTransactionsList() {

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)

        val transactionsAdapter = TransactionsAdapter(transactions)

        binding.rvTransactions.adapter = transactionsAdapter

        transactionsAdapter.notifyItemRangeChanged(0, transactions.size)

    }

    private fun setupLastTransactionUI(lastTransaction: Transactions) {

        binding.tvLastTransactions.visibility = View.VISIBLE
        binding.cvLastTransactions.visibility = View.VISIBLE

        binding.tvYourTransactions.visibility = View.VISIBLE
        binding.cvYourTransactions.visibility = View.VISIBLE

        binding.tvATMAmountLT.text = lastTransaction.transAmount.toString()
        binding.tv100NotesLT.text = lastTransaction.notesOf100.toString()
        binding.tv200NotesLT.text = lastTransaction.notesOf200.toString()
        binding.tv500NotesLT.text = lastTransaction.notesOf500.toString()
        binding.tv2000NotesLT.text = lastTransaction.notesOf2000.toString()
    }

    private fun setupNotesUI(bank: Bank) {

        totalAmountInBank = bank.totalAmount
        notesOf100 = bank.notesOf100
        notesOf200 = bank.notesOf200
        notesOf500 = bank.notesOf500
        notesOf2000 = bank.notesOf2000

        binding.tvATMAmount.text = totalAmountInBank.toString()
        binding.tv100Notes.text = notesOf100.toString()
        binding.tv200Notes.text = notesOf200.toString()
        binding.tv500Notes.text = notesOf500.toString()
        binding.tv2000Notes.text = notesOf2000.toString()

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

}