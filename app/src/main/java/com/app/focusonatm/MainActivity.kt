package com.app.focusonatm

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.focusonatm.databinding.ActivityMainBinding
import com.app.focusonatm.room.builder.DatabaseBuilder
import com.app.focusonatm.room.dao.FocusDao
import com.app.focusonatm.room.entity.Bank
import com.app.focusonatm.room.entity.Transactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var transactions: List<Transactions>
    private var currentNotesOf100: Int = 0
    private var currentNotesOf200: Int = 0
    private var currentNotesOf500: Int = 0
    private var currentNotesOf2000: Int = 0
    private lateinit var focusDao: FocusDao
    private var bankDataFromDB: Bank? = null
    private lateinit var binding: ActivityMainBinding
    var notesOf100 = 0
    var notesOf200 = 0
    var notesOf500 = 0
    var notesOf2000 = 0

    var totalAmountInBank = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBank()

        binding.btnWithdraw.setOnClickListener {

            if (binding.edtWithdrawAmount.text.toString().isNotEmpty()) {

                val withdrawAmount = Integer.parseInt(binding.edtWithdrawAmount.text.toString())
                Log.d("TAG", "onCreate: Withdraw Amount - $withdrawAmount")

                if (withdrawAmount <= totalAmountInBank) {
                    if (withdrawAmount.toString().endsWith("00")) {
                        calculator(withdrawAmount)
                        updatedNotesUI(withdrawAmount)
                        binding.edtWithdrawAmount.text?.clear()
                    } else {
                        Toast.makeText(
                            this,
                            "Please enter 100, 200, 500, 2000 related amount.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this,
                        "You cant withdraw currently.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            } else {
                return@setOnClickListener
            }


        }

    }

    private fun setupBank() {

        GlobalScope.launch(Dispatchers.IO) {

            focusDao = DatabaseBuilder.getDBInstance(applicationContext).focusDao()
            bankDataFromDB = focusDao.getBankDetails()
            Log.d("TAG", "setupBank: $bankDataFromDB")
            if (bankDataFromDB == null) {
                focusDao.addNotesToBank(
                    Bank(
                        1,
                        50000,
                        75,
                        50,
                        25,
                        10
                    )
                )
                bankDataFromDB = focusDao.getBankDetails()

            }

            transactions = ArrayList()
            transactions = focusDao.getAllTransactions()

        }.invokeOnCompletion {

            GlobalScope.launch(Dispatchers.Main) {

                bankDataFromDB?.let { it1 -> setupNotesUI(it1) }

                if (transactions.isNotEmpty()) {
                    transactions.let { it2 -> setupLastTransactionUI(it2[it2.size - 1]) }
                }

                updateTransactionsList()
            }

        }


    }

    private fun updatedNotesUI(withdrawAmount: Int) {

        GlobalScope.launch(Dispatchers.IO) {
            totalAmountInBank -= withdrawAmount
            focusDao.addNotesToBank(
                Bank(
                    1,
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
            Log.d(
                "TAG",
                "updatedNotesUI: Last Transaction = ${transactions[transactions.size - 1]}"
            )
        }.invokeOnCompletion {


            GlobalScope.launch(Dispatchers.Main) {

                bankDataFromDB?.let { it1 -> setupNotesUI(it1) }

                if (transactions.isNotEmpty()) {
                    transactions.let { it2 -> setupLastTransactionUI(it2[it2.size - 1]) }
                }


                currentNotesOf2000 = 0
                currentNotesOf500 = 0
                currentNotesOf200 = 0
                currentNotesOf100 = 0

                //Add or Update RV
                updateTransactionsList()
            }

        }
    }

    private fun updateTransactionsList() {

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)

        val transactionsAdapter = TransactionsAdapter(transactions)

        binding.rvTransactions.adapter = transactionsAdapter

        transactionsAdapter.notifyDataSetChanged()

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

    private fun calculator(withdrawAmount: Int) {

        var updatedAmount = withdrawAmount
        Log.d("TAG", "updatedAmount: $updatedAmount")

        when {
            updatedAmount >= 2000 && notesOf2000 > 0 -> {
                Log.d("TAG", "calculator: In 2000")
                updatedAmount -= 2000
                notesOf2000 -= 1
                currentNotesOf2000 += 1
                calculator(updatedAmount)
            }
            updatedAmount >= 500 && notesOf500 > 0 -> {
                Log.d("TAG", "calculator: In 500")
                updatedAmount -= 500
                notesOf500 -= 1
                currentNotesOf500 += 1
                calculator(updatedAmount)
            }
            updatedAmount >= 200 && notesOf200 > 0 -> {
                Log.d("TAG", "calculator: In 200")
                updatedAmount -= 200
                notesOf200 -= 1
                currentNotesOf200 += 1
                calculator(updatedAmount)
            }
            updatedAmount >= 100 && notesOf100 > 0 -> {
                Log.d("TAG", "calculator: In 100")
                updatedAmount -= 100
                notesOf100 -= 1
                currentNotesOf100 += 1
                calculator(updatedAmount)
            }
            updatedAmount in 1..99 -> {
                Log.d("TAG", "calculator: Less than 100")
                Toast.makeText(
                    this,
                    "Please enter 100, 200, 500, 2000 related amount",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}