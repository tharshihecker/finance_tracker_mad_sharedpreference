package com.example.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.app.R
import com.example.app.models.Transaction
import com.example.app.utils.SharedPrefsHelper

class AddIncomeActivity : AppCompatActivity() {

    private lateinit var tvCurrentIncome: TextView
    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button

    // Track editing state and ID
    private var isEditing = false
    private var editingTransactionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_income)

        // UI init
        tvCurrentIncome = findViewById(R.id.tvCurrentIncome)
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        datePicker = findViewById(R.id.datePicker)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)

        // Check if we're editing an existing transaction
        isEditing = intent.getBooleanExtra("isEditing", false)
        if (isEditing) {
            editingTransactionId = intent.getLongExtra("transactionId", -1L)
            // Load existing transaction
            val existing = SharedPrefsHelper.getTransactions(this)
                .firstOrNull { it.id == editingTransactionId }
            existing?.let {
                etTitle.setText(it.title)
                etAmount.setText(it.amount.toString())
                // Date stored as "day-month-year"
                val parts = it.date.split("-")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1  // DatePicker months are 0-based
                    val year = parts[2].toInt()
                    datePicker.updateDate(year, month, day)
                }
            }
        }

        // Show current income and actual total expenses
        updateIncomeAndExpenses()

        // Save income logic
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val amountText = etAmount.text.toString().trim()

            if (title.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "Please provide a title and amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Format date as day-month-year
            val date = "${datePicker.dayOfMonth}-${datePicker.month + 1}-${datePicker.year}"

            if (isEditing && editingTransactionId != -1L) {
                // Update existing
                val transactions = SharedPrefsHelper.getTransactions(this).toMutableList()
                val index = transactions.indexOfFirst { it.id == editingTransactionId }
                if (index != -1) {
                    transactions[index] = Transaction(
                        id = editingTransactionId,
                        title = title,
                        amount = amount,
                        category = "Income",
                        date = date
                    )
                    SharedPrefsHelper.saveAllTransactions(this, transactions)
                    Toast.makeText(this, "Income updated", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Create new
                val newTransaction = Transaction(
                    id = System.currentTimeMillis(),
                    title = title,
                    amount = amount,
                    category = "Income",
                    date = date
                )
                SharedPrefsHelper.saveTransaction(this, newTransaction)
                Toast.makeText(this, "Income saved", Toast.LENGTH_SHORT).show()
            }

            updateIncomeAndExpenses()
            finish() // Go back after saving
        }

        // Navigate back
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateIncomeAndExpenses() {
        val currentIncome = SharedPrefsHelper.getIncome(this)
        val totalExpenses = SharedPrefsHelper.getExpenses(this)
        tvCurrentIncome.text = "Current Income: Rs.$currentIncome\nTotal Spent: Rs.$totalExpenses"
    }
}
