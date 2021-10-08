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
            rowItemTransactionsBinding.apply {
                if (position == 0)
                    lnrHeaders.visibility = View.VISIBLE
                else lnrHeaders.visibility =
                    View.GONE
                transactions = transactionsList[position]
            }
        }
    }

    override fun getItemCount() = transactionsList.size
}