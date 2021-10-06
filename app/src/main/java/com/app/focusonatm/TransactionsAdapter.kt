package com.app.focusonatm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.focusonatm.databinding.RowItemTransactionsBinding
import com.app.focusonatm.room.entity.Transactions

class TransactionsAdapter(private val transactionsList: List<Transactions>) :
    RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
    inner class ViewHolder(val rowItemTransactionsBinding: RowItemTransactionsBinding) :
        RecyclerView.ViewHolder(rowItemTransactionsBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RowItemTransactionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(transactionsList[position]) {

                rowItemTransactionsBinding.lnrHeaders.visibility = View.GONE
                if (position == 0) {
                    rowItemTransactionsBinding.lnrHeaders.visibility = View.VISIBLE
                }

                rowItemTransactionsBinding.tvATMAmount.text = transAmount.toString()
                rowItemTransactionsBinding.tv100Notes.text = notesOf100.toString()
                rowItemTransactionsBinding.tv200Notes.text = notesOf200.toString()
                rowItemTransactionsBinding.tv500Notes.text = notesOf500.toString()
                rowItemTransactionsBinding.tv2000Notes.text = notesOf2000.toString()

            }
        }
    }

    override fun getItemCount() = transactionsList.size
}