package com.example.app.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.models.Transaction
import com.example.app.utils.NotificationHelper
import com.example.app.utils.SharedPrefsHelper

class ManageTransactionActivity : AppCompatActivity() {

    private lateinit var summaryLayout: LinearLayout
    private lateinit var emptyText: TextView
    private lateinit var btnBack: Button
    private lateinit var clearAllButton: Button
    private lateinit var transactions: MutableList<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_transaction)

        // Bind views
        summaryLayout   = findViewById(R.id.categorySummaryLayout)
        emptyText       = findViewById(R.id.tvNoData)
        btnBack         = findViewById(R.id.btnBack)
        clearAllButton  = findViewById(R.id.clearAllButton)

        // Back navigates up
        btnBack.setOnClickListener { finish() }

        // Clear all handler
        clearAllButton.setOnClickListener {
            SharedPrefsHelper.clearAllTransactions(this)
            Toast.makeText(this, "All transactions deleted", Toast.LENGTH_SHORT).show()

            // Show notification
            NotificationHelper.sendBudgetAlert(
                context = this,
                message = "All transactions have been reset."
            )

            transactions.clear()
            displayTransactions()
        }


        // Load and show
        transactions = SharedPrefsHelper.getTransactions(this).toMutableList()
        displayTransactions()
    }

    private fun displayTransactions() {
        // First clear out any old views
        summaryLayout.removeAllViews()

        if (transactions.isEmpty()) {
            // --- EMPTY STATE ---
            clearAllButton.isEnabled = false

            // Re-style & re-add the emptyText view (since removeAllViews() took it out)
            emptyText.apply {
                // Make it full width, centered, bold, big
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // Directly using 32dp, converted to pixels
                    topMargin = (32 * resources.displayMetrics.density).toInt()
                }
                textSize = 40f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(resources.getColor(R.color.primaryText, null))
                gravity = Gravity.CENTER
                visibility = View.VISIBLE
            }
            summaryLayout.addView(emptyText)

            return
        } else {
            clearAllButton.isEnabled = true
            emptyText.visibility = View.GONE
        }

        // --- TRANSACTION LIST ---
        val grouped = transactions.groupBy { it.category }
        for ((category, txnList) in grouped) {
            // Category header (optional: you can also bold this if you like)
            val categoryTitle = TextView(this).apply {
                text = "$category:"
                textSize = 25f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(resources.getColor(R.color.primaryText, null))
            }
            summaryLayout.addView(categoryTitle)

            // Each transaction
            txnList.forEach { txn ->
                val itemLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 24)
                }

                // ↑–– Bigger, bold transaction details
                val txnText = TextView(this).apply {
                    text = "• ${txn.title} | Rs. ${"%.2f".format(txn.amount)} | ${txn.date}"
                    textSize = 25f
                    setTypeface(typeface, Typeface.BOLD)
                    setTextColor(resources.getColor(R.color.primaryText, null))
                }

                val buttonLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 8, 0, 0)
                }

                val editBtn = Button(this).apply {
                    text = "Edit"
                    textSize = 18f
                    setTypeface(typeface, Typeface.BOLD)
                    setPadding(24, 16, 24, 16)
                    setTextColor(getColor(R.color.accent))// Add padding for a bigger button

                    setOnClickListener {
                        // Check if the transaction is income type
                        val isIncome = txn.category.equals("income", ignoreCase = true)

                        val intent = if (isIncome) {
                            Intent(this@ManageTransactionActivity, AddIncomeActivity::class.java)
                        } else {
                            Intent(this@ManageTransactionActivity, AddTransactionActivity::class.java)
                        }

                        intent.putExtra("isEditing", true)
                        intent.putExtra("transactionId", txn.id)
                        startActivity(intent)
                    }
                }


                val deleteBtn = Button(this).apply {
                    text = "Delete"
                    textSize = 18f // Increase text size
                    setTypeface(typeface, Typeface.BOLD) // Make text bold
                    setPadding(24, 16, 24, 16)
                    setTextColor(getColor(R.color.accent))// Add padding for a bigger button
                    setOnClickListener {
                        transactions.remove(txn)
                        SharedPrefsHelper.saveAllTransactions(this@ManageTransactionActivity, transactions)
                        Toast.makeText(this@ManageTransactionActivity, "Transaction deleted", Toast.LENGTH_SHORT).show()
                        displayTransactions()
                    }
                }


                buttonLayout.addView(editBtn)
                buttonLayout.addView(deleteBtn)

                itemLayout.addView(txnText)
                itemLayout.addView(buttonLayout)
                summaryLayout.addView(itemLayout)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh after returning from Add/Edit
        transactions = SharedPrefsHelper.getTransactions(this).toMutableList()
        displayTransactions()
    }
}
