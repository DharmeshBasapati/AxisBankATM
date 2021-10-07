package com.app.focusonatm

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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

        binding.btnWithdraw.setOnClickListener {

            mainViewModel.validateAmount()

        }

        binding.edtWithdrawAmount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtWithdrawAmount.windowToken, 0)
            }
        }

        mainViewModel.getWithdrawAmount().observe(this, {

            updateNotesAndTransactionsUI(Integer.parseInt(it))
            binding.edtWithdrawAmount.clearFocus()

        })

        mainViewModel.getErrorMessage().observe(this, {

            showErrorMessage(it)

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

            it?.let { bankData -> setupNotesUI(bankData) }

        })

        mainViewModel.getTransactionsList().observe(this, {

            it?.let {
                if (it.isNotEmpty()) {
                    setupLastTransactionUI(it[it.size - 1])
                    updateTransactionsList(it)
                }
            }

        })

    }

    private fun showErrorMessage(errorMessage: String) {
        binding.textFieldWithdrawAmount.helperText = errorMessage
        binding.textFieldWithdrawAmount.setHelperTextColor(ColorStateList.valueOf(Color.RED))
    }

    private fun updateNotesAndTransactionsUI(withdrawAmount: Int) {
        mainViewModel.updateBankDetails(withdrawAmount)
        mainViewModel.addNewTransactionToDB(withdrawAmount)
    }

    private fun updateTransactionsList(list: List<Transactions>) {
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        val transactionsAdapter = TransactionsAdapter(list)
        binding.rvTransactions.adapter = transactionsAdapter
        transactionsAdapter.notifyItemRangeChanged(0, list.size)
    }

    private fun setupLastTransactionUI(lastTransaction: Transactions) {
        binding.tvATMAmountLT.text = lastTransaction.transAmount.toString()
        binding.tv100NotesLT.text = lastTransaction.notesOf100.toString()
        binding.tv200NotesLT.text = lastTransaction.notesOf200.toString()
        binding.tv500NotesLT.text = lastTransaction.notesOf500.toString()
        binding.tv2000NotesLT.text = lastTransaction.notesOf2000.toString()

        binding.tvLastTransactions.visibility = View.VISIBLE
        binding.cvLastTransactions.visibility = View.VISIBLE

        binding.tvYourTransactions.visibility = View.VISIBLE
        binding.cvYourTransactions.visibility = View.VISIBLE
    }

    private fun setupNotesUI(bank: Bank) {
        binding.tvATMAmount.text = bank.totalAmount.toString()
        binding.tv100Notes.text = bank.notesOf100.toString()
        binding.tv200Notes.text = bank.notesOf200.toString()
        binding.tv500Notes.text = bank.notesOf500.toString()
        binding.tv2000Notes.text = bank.notesOf2000.toString()
    }

}