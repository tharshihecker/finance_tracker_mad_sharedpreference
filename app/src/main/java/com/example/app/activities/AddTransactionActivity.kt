package com.example.app.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.models.Transaction
import com.example.app.utils.NotificationHelper
import com.example.app.utils.SharedPrefsHelper

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var tvCurrentBudget: TextView
    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var datePicker: DatePicker
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnBack: Button

    private val categories = arrayOf("Food", "Transport", "Bills", "Entertainment", "Other")
    private var currentTransaction: Transaction? = null
    private var isEditing: Boolean = false
    private var transactionId: Long = -1L

    // Launcher for notification permission
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // UI Initialization
        tvCurrentBudget = findViewById(R.id.tvCurrentBudget)
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        datePicker = findViewById(R.id.datePicker)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Spinner setup
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter

        // Budget and Expense info
        updateBudgetAndExpenses()

        // Check if in edit mode
        isEditing = intent.getBooleanExtra("isEditing", false)
        transactionId = intent.getLongExtra("transactionId", -1L)

        if (isEditing && transactionId != -1L) {
            val transactions = SharedPrefsHelper.getTransactions(this)
            currentTransaction = transactions.find { it.id == transactionId }

            currentTransaction?.let { txn ->
                etTitle.setText(txn.title)
                etAmount.setText(txn.amount.toString())
                spinnerCategory.setSelection(categories.indexOf(txn.category))

                val parts = txn.date.split("-") // Format: dd-MM-yyyy
                if (parts.size == 3) {
                    datePicker.updateDate(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                }

                btnDelete.visibility = Button.VISIBLE
            }
        } else {
            btnDelete.visibility = Button.GONE
        }

        // Save transaction
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val amountText = etAmount.text.toString().trim()

            if (title.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = spinnerCategory.selectedItem.toString()
            val date = "${datePicker.dayOfMonth}-${datePicker.month + 1}-${datePicker.year}"

            if (isEditing && currentTransaction != null) {
                val updatedTransaction = currentTransaction!!.copy(
                    title = title, amount = amount, category = category, date = date
                )
                SharedPrefsHelper.updateTransaction(this, updatedTransaction)
                Toast.makeText(this, getString(R.string.transaction_updated), Toast.LENGTH_SHORT).show()
            } else {
                val newTransaction = Transaction(
                    id = System.currentTimeMillis(), title = title, amount = amount,
                    category = category, date = date
                )
                SharedPrefsHelper.saveTransaction(this, newTransaction)
                Toast.makeText(this, getString(R.string.transaction_saved), Toast.LENGTH_SHORT).show()
            }

            checkIfBudgetExceeded()
            finish()
        }

        // Delete transaction
        btnDelete.setOnClickListener {
            currentTransaction?.let {
                SharedPrefsHelper.deleteTransaction(this, it)
                Toast.makeText(this, getString(R.string.transaction_deleted), Toast.LENGTH_SHORT).show()
                checkIfBudgetExceeded()
                finish()
            }
        }

        // Check notification permissions
        checkNotificationPermission()
    }

    private fun updateBudgetAndExpenses() {
        val currentBudget = SharedPrefsHelper.getBudget(this)
        val totalExpenses = SharedPrefsHelper.getExpenses(this)
        tvCurrentBudget.text = "Current Budget: Rs.$currentBudget\nTotal Spent: Rs.$totalExpenses"
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkIfBudgetExceeded() {
        val totalExpenses = SharedPrefsHelper.getExpenses(this)
        val budget = SharedPrefsHelper.getBudget(this)

        updateBudgetAndExpenses()

        if (totalExpenses > budget) {
            val exceededAmount = totalExpenses - budget
            val message = "Your total expenses (Rs.$totalExpenses) have exceeded your budget (Rs.$budget)! " +
                    "You have exceeded by Rs.$exceededAmount."
            checkAndSendNotification(message)
        } else if (totalExpenses >= 0.9 * budget) {
            val remaining = budget - totalExpenses
            val message = "Warning: You are about to reach your budget limit!\n" +
                    "Only Rs.$remaining remaining from your budget of Rs.$budget."
            checkAndSendNotification(message)
        }
    }

    private fun checkAndSendNotification(message: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationHelper.sendBudgetAlert(this, message)
        } else {
            Log.w("AddTransactionActivity", "Notification permission not granted")
        }
    }
}
