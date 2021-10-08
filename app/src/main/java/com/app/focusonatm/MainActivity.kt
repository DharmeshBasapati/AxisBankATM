package com.app.focusonatm

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.focusonatm.databinding.ActivityMainBinding
import com.app.focusonatm.room.builder.DatabaseBuilder
import com.app.focusonatm.room.entity.Bank
import com.app.focusonatm.room.entity.Transactions

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupObserver()

        binding.apply {

            rvTransactions.layoutManager = LinearLayoutManager(this@MainActivity)

            btnWithdraw.setOnClickListener {
                mainViewModel!!.validateAmount()
            }

            edtWithdrawAmount.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(edtWithdrawAmount.windowToken, 0)
                }
            }

        }

        mainViewModel.getWithdrawAmount().observe(this, {
            updateNotesAndTransactionsUI(Integer.parseInt(it))
            binding.edtWithdrawAmount.clearFocus()
        })

    }

    private fun setupViewModel() {

        mainViewModel = ViewModelProvider(
            this, ViewModelFactory(DatabaseBuilder.getDBInstance(applicationContext).focusDao())
        ).get(MainViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        mainViewModel.setupInitialBankDetails()
        mainViewModel.fetchTransactionsListFromDB()

    }

    private fun setupObserver() {

        mainViewModel.getBankData().observe(this, {
            binding.bankNotesLayout.bankData = it
        })

        mainViewModel.getTransactionsList().observe(this, { transactionsList ->

            val trans = transactionsList.last()
            val bank = Bank(
                2,
                trans.transAmount,
                trans.notesOf100,
                trans.notesOf200,
                trans.notesOf500,
                trans.notesOf2000
            )
            binding.lastTransactionLayout.bankData = bank

            transactionsList?.let {
                if (it.isNotEmpty()) {
                    updateTransactionsList(it)
                }
            }

        })

    }

    private fun updateNotesAndTransactionsUI(withdrawAmount: Int) {
        mainViewModel.updateBankDetails(withdrawAmount)
        mainViewModel.addNewTransactionToDB(withdrawAmount)
    }

    private fun updateTransactionsList(list: List<Transactions>) {
        binding.apply {
            tvLastTransactions.visibility = View.VISIBLE
            lastTransactionLayout.cvNotes.visibility = View.VISIBLE

            tvYourTransactions.visibility = View.VISIBLE
            cvYourTransactions.visibility = View.VISIBLE

            val transactionsAdapter = TransactionsAdapter(list)
            rvTransactions.adapter = transactionsAdapter

            transactionsAdapter.notifyItemRangeChanged(0, list.size)
        }
    }

}